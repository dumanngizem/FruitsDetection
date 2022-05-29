package com.example.cekipal.bLL

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.cekipal.view.bitmap
import com.example.cekipal.bLL.classifier.ImageClassifier
import java.io.IOException

class Detection_Async (val context : Context, val resultFunc : (predict : String?) -> Unit) : AsyncTask<Void, Void, String?>()  {
    private var imageClassifier : ImageClassifier? = null

    override fun onPreExecute() {
        super.onPreExecute()
        try {
            imageClassifier = ImageClassifier(context)
        } catch (e: IOException) {
            Log.e("Image Classifier Error", "ERROR: $e")
        }
    }

    override fun doInBackground(vararg p0: Void?): String{
        val predictions: List<ImageClassifier.Recognition> = imageClassifier!!.recognizeImage(
            bitmap, 0
        )

        // creating a list of string to display in list view
        val predictionsList: MutableList<String> = ArrayList()
        for (recog in predictions) {
            predictionsList.add(recog.confidence.toString() + "  : " + recog.name)
        }
        return predictionsList[0]
    }

    override fun onPostExecute(predict: String?) {
        super.onPostExecute(predict)
        resultFunc(predict)
    }
}