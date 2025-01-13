package com.example.musicplayer.com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.ApplicationClass
import com.example.musicplayer.R
import com.example.musicplayer.nowplaying
import com.example.musicplayer.playeractivity

@Suppress("DEPRECATION")
class NotificationReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> if (playeractivity.musicListPA.size > 1) prevNextSong(
                increment = false,
                context = context!!
            )

            ApplicationClass.PLAY -> if (playeractivity.isplaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> if (playeractivity.musicListPA.size > 1) prevNextSong(
                increment = true,
                context = context!!
            )

            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun playMusic() {
        playeractivity.isplaying = true
        playeractivity.Musicservice!!.mediaPlayer!!.start()
        playeractivity.Musicservice!!.showNotification(R.drawable.pause )
        playeractivity.binding.playpausePA.setIconResource(R.drawable.pause)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        try {
            nowplaying.binding.playpauseNP.setIconResource(R.drawable.play)
        } catch (_: Exception) {
        }
    }

    private fun pauseMusic() {
        playeractivity.isplaying = false
        playeractivity.Musicservice!!.mediaPlayer!!.pause()
        playeractivity.Musicservice!!.showNotification(R.drawable.play )
        playeractivity.binding.playpausePA.setIconResource(R.drawable.play)
        //for handling app crash during notification play - pause btn (While app opened through intent)
        try {
            nowplaying.binding.playpauseNP.setIconResource(R.drawable.play)
        } catch (_: Exception) {
        }
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        playeractivity.Musicservice!!.createMediaPlayer()
        Glide.with(context)
            .load(playeractivity.musicListPA[playeractivity.songposition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
            .into(playeractivity.binding.songimagePA)
        playeractivity.binding.songnamePA.text =
            playeractivity.musicListPA[playeractivity.songposition].title
        Glide.with(context)
            .load(playeractivity.musicListPA[playeractivity.songposition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
            .into(nowplaying.binding.songImgNP)
        nowplaying.binding.songNameNP.text =
            playeractivity.musicListPA[playeractivity.songposition].title
        playMusic()
        playeractivity.fIndex = favouriteChecker(playeractivity.musicListPA[playeractivity.songposition].id)
        if(playeractivity.isfavourite) playeractivity.binding.favbtnPA.setImageResource(R.drawable.favourite_icon)
        else playeractivity.binding.favbtnPA.setImageResource(R.drawable.favourite_open)
    }
}