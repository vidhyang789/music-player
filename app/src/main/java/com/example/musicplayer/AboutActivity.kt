package com.example.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_about)
        val toolbar : androidx.appcompat.widget.Toolbar  = findViewById(R.id.toolbarAB)
        setSupportActionBar(toolbar)
        binding.Abouttxt.text= "Developed By : Vidhyang Jain" +
                "\n\nIf you want to provide feedback i will love to hear that"
    }
//    private fun aboutText() : String{
//        return "Developed By : Vidhyang Jain" +
//                "\n\nIf you want to provide feedback i will love to hear that"
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.aboutmenu, menu)
        return true
    }

}