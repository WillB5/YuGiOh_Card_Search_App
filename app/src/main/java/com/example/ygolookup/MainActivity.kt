package com.example.ygolookup


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitButton = findViewById<Button>(R.id.submitButton)
        val cameraButton = findViewById<Button>(R.id.cameraButton)
        val imageView = findViewById<ImageView>(R.id.imageView)


        submitButton.setOnClickListener{
            val editText = findViewById<EditText>(R.id.cardName)
            val intent = Intent(this@MainActivity, Activity2::class.java)
            intent.putExtra("cardName", editText.text.toString())
            startActivity(intent)
        }

        cameraButton.setOnClickListener{
            
        }
    }
}


