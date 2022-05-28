package com.example.cekipal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cekipal.databinding.ActivityDetectionScreenBinding

class DetectionScreen : AppCompatActivity() {
    lateinit var binding : ActivityDetectionScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
    }

    private fun initializeViews(){
        binding = ActivityDetectionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.takenPhoto.setImageBitmap(bitmap)
        Detection_Async(this, this::resultFunc).execute()
    }

    fun resultFunc (predict : String?)
    {
        if(predict != null){
            binding.tvPredictResult.text = predict

        }
    }
}