package com.example.cekipal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.cekipal.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding//*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)//*
        setContentView(binding.root) //xml içine erişebilmek için her sayfaya * ile beşirtilen tanımlamalar yapılmalıdır.

        Handler().postDelayed({
            var intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
        },2500)
    }
    //süre ve tasarım geliştirilecek/değiştirilecektir.

}