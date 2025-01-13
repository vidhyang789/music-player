package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.com.example.musicplayer.musicAdapter
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding : ActivityPlaylistDetailsBinding
    lateinit var adapter : musicAdapter

    companion object{
        var currentPlaylistPos : Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        playlistActivity.musicPlaylist.ref[currentPlaylistPos].playlist = checkPlaylist(playlistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)
        currentPlaylistPos = intent.extras?.get("index") as Int
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = musicAdapter(this,playlistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backbtnPLD.setOnClickListener {
            finish()
        }
        binding.shufflebtnPD.setOnClickListener {
            val intent = Intent(this, playeractivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addbtnPD.setOnClickListener{
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove All")
                .setMessage("Do you want to Remove All Songs from playlist")
                .setPositiveButton("Yes") { dialog, _ ->
                    playlistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistnamePD.text = playlistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On : \n${playlistActivity.musicPlaylist.ref[currentPlaylistPos].createdon}" +
                "  -- ${playlistActivity.musicPlaylist.ref[currentPlaylistPos].createdby}"
        if(adapter.itemCount > 0){
            Glide.with(this)
                .load(playlistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
                .into(binding.plalistImgPD)
            binding.shufflebtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(playlistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
    }
}