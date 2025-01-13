package com.example.musicplayer.com.example.musicplayer

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import com.example.musicplayer.favouriteActivity
import com.example.musicplayer.playeractivity
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class music(val id:String , val title:String , val album :String ,
                 val artist: String , val duration : Long = 0,
                 val path: String,val artUri : String)


class Playlist{
    lateinit var name: String
    lateinit var playlist : ArrayList<music>
    lateinit var createdby : String
    lateinit var createdon : String
}
class MusicPlaylist{
    var ref : ArrayList<Playlist> = ArrayList()
}

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
            - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}
fun getImgArt(path : String) : ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
fun setSongPosition(increment: Boolean) {
    if(!playeractivity.repeat){
        if (increment) {
            if (playeractivity.musicListPA.size - 1 == playeractivity.songposition)
                playeractivity.songposition = 0
            else ++playeractivity.songposition
        } else {
            if (0 == playeractivity.songposition)
                playeractivity.songposition = playeractivity.musicListPA.size - 1
            else --playeractivity.songposition
        }
    }
}

@Suppress("DEPRECATION")
fun exitApplication(){
    if (playeractivity.Musicservice != null) {
        playeractivity.Musicservice!!.audioManager.abandonAudioFocus(playeractivity.Musicservice)
        playeractivity.Musicservice!!.stopForeground(true)
        playeractivity.Musicservice!!.mediaPlayer!!.release()
        playeractivity.Musicservice = null
    }
    exitProcess(1)
}
fun favouriteChecker(id:String) : Int{
    playeractivity.isfavourite = false
    favouriteActivity.favouriteSongs.forEachIndexed { index, music ->
        if(id == music.id){
            playeractivity.isfavourite = true
            return index
        }
    }
    return -1
}
fun checkPlaylist(playlist: ArrayList<music>): ArrayList<music> {
    val indicesToRemove = mutableListOf<Int>()

    playlist.forEachIndexed { index, music ->
        if (!File(music.path).exists()) indicesToRemove.add(index)
    }

    indicesToRemove.sortDescending()
    indicesToRemove.forEach { index -> playlist.removeAt(index) }
    return playlist
}
