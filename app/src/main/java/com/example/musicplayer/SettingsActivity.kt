package com.example.musicplayer

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.com.example.musicplayer.exitApplication
import com.example.musicplayer.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Settings"
        when(MainActivity.themeIndex){
            0 -> binding.coolpinkSettings.setBackgroundColor(Color.YELLOW)
            1 -> binding.coolblueSettings.setBackgroundColor(Color.YELLOW)
            2 -> binding.purple500Settings.setBackgroundColor(Color.YELLOW)
            3 -> binding.coolgreenSettings.setBackgroundColor(Color.YELLOW)
            4 -> binding.whiteSettings.setBackgroundColor(Color.YELLOW)
        }
        binding.coolpinkSettings.setOnClickListener { saveTheme(0) }
        binding.coolblueSettings.setOnClickListener { saveTheme(1) }
        binding.purple500Settings.setOnClickListener { saveTheme(2) }
        binding.coolgreenSettings.setOnClickListener { saveTheme(3) }
        binding.whiteSettings.setOnClickListener { saveTheme(4) }
        binding.versionName.text = setVersionDetails()
        binding.sortBtn.setOnClickListener {
            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
            var currentSort = MainActivity.sortOrder
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sorting")
                .setPositiveButton("OK"){ _, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort){ _,which->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }
    private fun saveTheme(index: Int){
        if(MainActivity.themeIndex != index){
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do you want to apply theme?")
                .setPositiveButton("Yes"){ _, _ ->
                    exitApplication()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }
    private fun setVersionDetails():String{
        return "Version Name: 1.0"
    }
}