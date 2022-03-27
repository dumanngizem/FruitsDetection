package com.example.cekipal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cekipal.databinding.ActivityMainBinding
import com.example.cekipal.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    lateinit var binding : ActivitySelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}