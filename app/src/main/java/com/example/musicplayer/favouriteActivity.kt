package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.com.example.musicplayer.checkPlaylist
import com.example.musicplayer.com.example.musicplayer.music
import com.example.musicplayer.databinding.ActivityFavouriteBinding
import favouriteAdapter

class favouriteActivity : AppCompatActivity() {


    private lateinit var binding : ActivityFavouriteBinding
    private lateinit var adapter : favouriteAdapter
    companion object{
        var favouriteSongs: ArrayList<music> = ArrayList()
        var favouritesChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favouriteSongs = checkPlaylist(favouriteSongs)
        favouriteSongs = (favouriteSongs)
        binding.backbtnFA.setOnClickListener { finish() }
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this, 4)
        adapter = favouriteAdapter(this, favouriteSongs)
        binding.favouriteRV.adapter = adapter

        favouritesChanged = false

        if(favouriteSongs.size < 1) binding.shuffleBtnFA.visibility = View.INVISIBLE

        if(favouriteSongs.isNotEmpty()) binding.instructionFV.visibility = View.GONE

        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, playeractivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if(favouritesChanged) {
            adapter.updateFavourites(favouriteSongs)
            favouritesChanged = false
        }
    }

//    private lateinit var binding : ActivityFavouriteBinding
//    private lateinit var adapter : favouriteAdapter
//
//    companion object{
//        var fovouriteSongs: ArrayList<music> = ArrayList()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setTheme(R.style.Theme_MusicPlayer)
//        binding = ActivityFavouriteBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        binding.backbtnFA.setOnClickListener{finish()}
//        binding.favouriteRV.setHasFixedSize(true)
//        binding.favouriteRV.setItemViewCacheSize(13)
//        binding.favouriteRV.layoutManager = GridLayoutManager(this,4)
//        adapter = favouriteAdapter(this, fovouriteSongs)
//        binding.favouriteRV.adapter = adapter
//    }
}