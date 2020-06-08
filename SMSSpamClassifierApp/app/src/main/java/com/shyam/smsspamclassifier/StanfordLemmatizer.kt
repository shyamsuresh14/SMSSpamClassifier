package com.shyam.smsspamclassifier

import edu.stanford.nlp.ling.CoreAnnotations.*
import edu.stanford.nlp.pipeline.Annotation
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.*

/*Currently not used as it did not yield the required results*/
/*Perform Lemmatization on the device*/
class StanfordLemmatizer {
    protected var pipeline: StanfordCoreNLP

    fun lemmatize(documentText: String?): List<String> {
        val lemmas: MutableList<String> =
            LinkedList()
        // Create an empty Annotation just with the given text
        val document =
            Annotation(documentText)
        // run all Annotators on this text
        pipeline.annotate(document)
        // Iterate over all of the sentences found
        val sentences =
            document.get(
                SentencesAnnotation::class.java
            )
        for (sentence in sentences) {
            // Iterate over all tokens in a sentence
            for (token in sentence.get(
                TokensAnnotation::class.java
            )) {
                // Retrieve and add the lemma for each word into the
                // list of lemmas
                lemmas.add(token.get(LemmaAnnotation::class.java))
            }
        }
        return lemmas
    }

    /*companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Starting Stanford Lemmatizer")
            val S = Scanner(System.`in`)
            val text = S.next()
            val slem = StanfordLemmatizer()
            println(slem.lemmatize(text))
        }
    }*/

    init {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        val props: Properties
        props = Properties()
        props["annotators"] = "tokenize, ssplit, pos, lemma"
        pipeline = StanfordCoreNLP(props)
    }
}