package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.com.example.musicplayer.setSongPosition
import com.example.musicplayer.databinding.FragmentNowplayingBinding
import com.example.musicplayer.playeractivity.Companion.musicListPA
import com.example.musicplayer.playeractivity.Companion.songposition

@SuppressLint("StaticFieldLeak")
class nowplaying : Fragment() {
    companion object{
        lateinit var binding : FragmentNowplayingBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_nowplaying, container, false)
        binding = FragmentNowplayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playpauseNP.setOnClickListener{
            if(playeractivity.isplaying) pausemusic() else playmusic()
        }
        binding.nextbtnLP.setOnClickListener{
            setSongPosition(increment = true)
            playeractivity.Musicservice!!.createMediaPlayer()
            Glide.with(this)
                .load(musicListPA[songposition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = musicListPA[songposition].title
            playeractivity.Musicservice!!.showNotification(R.drawable.pause)
            playmusic()
        }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), playeractivity::class.java)
            intent.putExtra("index" , playeractivity.songposition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(playeractivity.Musicservice != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true
            Glide.with(this)
                .load(musicListPA[songposition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = playeractivity.musicListPA[playeractivity.songposition].title
            if(playeractivity.isplaying) binding.playpauseNP.setIconResource(R.drawable.pause)
            else binding.playpauseNP.setIconResource(R.drawable.play)
        }
    }
    private fun playmusic(){
        playeractivity.Musicservice!!.mediaPlayer!!.start()
        binding.playpauseNP.setIconResource(R.drawable.pause)
        playeractivity.Musicservice!!.showNotification(R.drawable.pause)
        playeractivity.binding.nextbtnPA.setIconResource(R.drawable.pause)
        playeractivity.isplaying = true
    }
    private fun pausemusic(){
        playeractivity.Musicservice!!.mediaPlayer!!.pause()
        binding.playpauseNP.setIconResource(R.drawable.play)
        playeractivity.Musicservice!!.showNotification(R.drawable.play)
        playeractivity.binding.nextbtnPA.setIconResource(R.drawable.play)
        playeractivity.isplaying = false
    }

}