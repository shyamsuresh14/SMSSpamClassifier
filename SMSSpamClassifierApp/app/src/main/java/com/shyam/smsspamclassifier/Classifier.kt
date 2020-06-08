package com.shyam.smsspamclassifier

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/*Loads the model and classifies the given the sequences*/
class Classifier(val context: Context) {
    val MODEL_FILE_NAME = "3_model_cnn.tflite"
    var interpreter: Interpreter

    init{
        interpreter = Interpreter(loadModel())
    }

    @Throws(IOException::class)
    private fun loadModel() : MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(MODEL_FILE_NAME);
        val fin = FileInputStream(fileDescriptor.fileDescriptor);
        val fileChannel: FileChannel = fin.channel;
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength);
    }

    /*input float array is the preprocessed SMS message using Preprocessor*/
    fun classify(input: FloatArray) : String{
        val output = arrayOf(floatArrayOf(0.0f))
        interpreter.run(arrayOf(input), output)
        val op = output[0][0]
        log("output: $op")
        return if (op > 0.5) "Spam" else "Ham"
    }

    fun close(){
        interpreter.close()
    }

    private fun log(msg: String){
        Log.d("TFLITE OP", msg);
    }
}