package com.example.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.com.example.musicplayer.MusicPlaylist
import com.example.musicplayer.com.example.musicplayer.Playlist
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistDislogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import playlistViewAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class playlistActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPlaylistBinding
    private lateinit var adapter : playlistViewAdapter

    companion object{
        var musicPlaylist : MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@playlistActivity,2,)
        adapter = playlistViewAdapter(context = this@playlistActivity , playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adapter
        binding.backbtnPL.setOnClickListener{finish()}
        binding.addplaylistbtn.setOnClickListener { customAlertDialog() }
    }
    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this@playlistActivity).inflate(R.layout.add_playlist_dislog,binding.root,false)
        val binder = AddPlaylistDislogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add") { dialog, _ ->
                val playListName = binder.playlistName.text
                val createdBy = binder.yourName.text
                if(playListName != null && createdBy != null){
                    if(playListName.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playListName.toString(),createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }
    private fun addPlaylist(name : String , createdBy : String){
        var playlistexist = false
        for(i in musicPlaylist.ref){
            if(name == i.name){
                playlistexist = true
                break
            }
        }
        if(playlistexist) Toast.makeText(this,"Playlist Exist",Toast.LENGTH_SHORT).show()
        else{
            val tmepPlaylist = Playlist()
            tmepPlaylist.name = name
            tmepPlaylist.playlist = ArrayList()
            tmepPlaylist.createdby = createdBy
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tmepPlaylist.createdon = sdf.format(calendar)
            musicPlaylist.ref.add(tmepPlaylist)
            adapter.refreshPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}