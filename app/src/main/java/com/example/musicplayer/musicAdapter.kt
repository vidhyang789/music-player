package com.example.musicplayer.com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity
import com.example.musicplayer.PlaylistDetails
import com.example.musicplayer.R
import com.example.musicplayer.databinding.MusicviewBinding
import com.example.musicplayer.playeractivity
import com.example.musicplayer.playlistActivity

class musicAdapter(private val context : Context, private var musicList: ArrayList<music> ,private val playlistDetails: Boolean = false , private val selectionActivity: Boolean = false) : RecyclerView.Adapter<musicAdapter.MyHolder>() {
    class MyHolder(binding: MusicviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songnameMV
        val album = binding.songalbumMV
        val image = binding.imageMV
        val duration = binding.songduration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): musicAdapter.MyHolder {
        return MyHolder(MusicviewBinding.inflate(LayoutInflater.from(context) ,parent,false))
    }

    override fun onBindViewHolder(holder: musicAdapter.MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
            .into(holder.image)
        when{
            playlistDetails->{
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter" , pos = position)
                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(getColor(context, R.color.cool_pink))
                    else
                        holder.root.setBackgroundColor(getColor(context, R.color.black))
                }
            }
            else ->{
                holder.root.setOnClickListener {
                    when{
                        MainActivity.search -> sendIntent("musicAdapterSearch",pos = position)
                        musicList[position].id == playeractivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = playeractivity.songposition)
                        else-> sendIntent(ref = "musicAdapter",pos = position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList : ArrayList<music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref : String , pos:Int){
        val intent = Intent(context, playeractivity::class.java)
        intent.putExtra("index" , pos)
        intent.putExtra("class",ref)
        startActivity(context,intent,null)
    }
    private fun addSong(song : music) : Boolean{
        playlistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                playlistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        playlistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = playlistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }


}