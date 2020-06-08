package com.shyam.smsspamclassifier

import android.content.Context
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Preprocessor(val context: Context) {
    private val stopWordsFile = "stopwords.txt"
    private val vocabularyFile = "vocabulary.json"
    private val sequenceLength = 100
    private var stopWords = HashSet<String>()
    private var vocabulary = HashMap<String, Int>()
    private val client = OkHttpClient()
    private lateinit var callback: outputCallback

    init {
        loadData()
    }
    private fun loadData(){
        val fin1 = context.assets.open(stopWordsFile)
        for(sw in fin1.readBytes().toString(Charsets.UTF_8).split(","))
            stopWords.add(sw)
        val fin2 = context.assets.open(vocabularyFile)
        val jsonObject = JSONObject(fin2.readBytes().toString(Charsets.UTF_8))
        for(key in jsonObject.keys())
            vocabulary[key] = jsonObject.getString(key).toInt()
    }
    //preprocessing starts on calling this function
    fun preprocess(msg: String){
        var msg = removeSpecialCharacters(msg)
        msg = removeStopWords(msg)
        //log(StanfordLemmatizer().lemmatize(msg).toString()) //needs to be fixed
        lemmatize(msg)   //temporary fix
    }
    //on lemmatisation completion
    fun continuePreprocessing(msg: String){
        val tokenizedData = tokenize(msg)
        callback.onPreprocessed(padSequences(tokenizedData))
    }
    //callback required to call the classifier and update UI
    fun setCallback(callback: outputCallback){
        this.callback = callback
    }
    interface outputCallback{
        fun onPreprocessed(preprocessedMsg: FloatArray)
    }
    private fun removeSpecialCharacters(msg: String): String{
        //removes special characters '!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~';
        val newMsg = msg.replace("\\p{Punct}".toRegex(), "").toLowerCase()
        return newMsg
    }
    private fun removeStopWords(msg: String): String{
        //removes stop words
        var nonStopwords = ArrayList<String>()
        for(word in msg.split(" "))
            if(!stopWords.contains(word))
                nonStopwords.add(word)
        return nonStopwords.joinToString(separator = " ")
    }
    private fun tokenize(msg: String): FloatArray{
        //word -> token
        var tokenized = ArrayList<Float>()
        for(word in msg.split(" "))
            if(vocabulary.containsKey(word))
                tokenized.add(vocabulary[word]!!.toFloat())
            else
                tokenized.add(1.0f)
        return tokenized.toFloatArray()
    }
    private fun padSequences(data: FloatArray): FloatArray{
        //pad/truncate sequences
        if(data.size > sequenceLength)
            return data.dropLast(data.size - sequenceLength).toFloatArray()
        var paddedData = data.toMutableList()
        for(i in 1..(sequenceLength - data.size)) paddedData.add(0.0f)
        return paddedData.toFloatArray()
    }
    //temporary alternative for Stanford lemmatizer
    private fun lemmatize(msg: String){
        //Make a API request to receive NLTK lemmatizer output
        val url = "http://192.168.0.158:5000/lemmatize?text=$msg"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                log(e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                val result = response.body()?.string().toString()
                val jsonObject = JSONObject(result)
                val lemmatized = jsonObject.get("Result").toString()
                continuePreprocessing(lemmatized)
            }
        })
    }
    private fun removeLinks(msg: String): String{
        /*Not implemented*/
        return ""
    }
    private fun log(msg: String){
        Log.d("TFLITE OP", msg);
    }
}