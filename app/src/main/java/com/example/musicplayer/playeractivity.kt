package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.com.example.musicplayer.exitApplication
import com.example.musicplayer.com.example.musicplayer.favouriteChecker
import com.example.musicplayer.com.example.musicplayer.formatDuration
import com.example.musicplayer.com.example.musicplayer.getImgArt
import com.example.musicplayer.com.example.musicplayer.music
import com.example.musicplayer.com.example.musicplayer.musicservice
import com.example.musicplayer.com.example.musicplayer.setSongPosition
import com.example.musicplayer.databinding.ActivityPlayeractivityBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class playeractivity : AppCompatActivity() , ServiceConnection ,MediaPlayer.OnCompletionListener{

    companion object{
        lateinit var musicListPA : ArrayList<music>
        var songposition :Int = 0
        var isplaying : Boolean = false
        var Musicservice : musicservice? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayeractivityBinding
        var repeat : Boolean = false
        var min15 : Boolean = false
        var min30 : Boolean = false
        var min60 : Boolean = false
        var nowPlayingId : String = ""
        var isfavourite : Boolean = false
        var fIndex : Int = -1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayeractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ContextCompat.startForegroundService(this , intent)
        if(intent.data?.scheme.contentEquals("content")){
            val intentService = Intent(this,musicservice::class.java)
            bindService(intentService,this, BIND_AUTO_CREATE)
            startService(intentService)
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            Glide.with(this)
                .load(getImgArt(musicListPA[songposition].path))
                .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
                .into(binding.songimagePA)
            binding.songnamePA.text = musicListPA[songposition].title
        }
        else initializelayout()
        binding.backbtnPA.setOnClickListener{finish()}
        binding.playpausePA.setOnClickListener {
            if(isplaying) pausemusic()
            else playmusic()
        }
        binding.previousbtnPA.setOnClickListener { prevNextSong(false) }
        binding.nextbtnPA.setOnClickListener { prevNextSong(true) }
        binding.seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) Musicservice!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repeatPA.setOnClickListener{
            if(!repeat){
                repeat = true
                binding.repeatPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }
            else{
                repeat = false
                binding.repeatPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }
        }
        @Suppress("Depreciation")
        binding.equilizerPA.setOnClickListener{
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, Musicservice!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,13)
            }
            catch (e : Exception){
                Toast.makeText(this,"Equilizer not supported on this device",Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerPA.setOnClickListener{
            val timer = min15 || min30 || min60
            if(!timer){
                showBottomSheetDialog()
            }
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("stop timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
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
        binding.sharePA.setOnClickListener{
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songposition].path))
            startActivity(Intent.createChooser(shareIntent,"shanring music file"))
        }
        binding.favbtnPA.setOnClickListener {
            if(isfavourite){
                isfavourite = false
                binding.favbtnPA.setImageResource(R.drawable.favourite_open)
                favouriteActivity.favouriteSongs.removeAt(fIndex)
            }
            else {
                isfavourite = true
                binding.favbtnPA.setImageResource(R.drawable.favourite_icon)
                favouriteActivity.favouriteSongs.add(musicListPA[songposition])
            }
        }
    }

    private fun setlayout(){
        fIndex = favouriteChecker(musicListPA[songposition].id)
        Glide.with(this)
            .load(musicListPA[songposition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
            .into(binding.songimagePA)
        binding.songnamePA.text = musicListPA[songposition].title
        if (repeat){
            binding.repeatPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        }
        if(min15 || min30 || min60){
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        }
        if (isfavourite) binding.favbtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favbtnPA.setImageResource(R.drawable.favourite_open)
    }

    private fun createMediaPlayer(){
        try{
            if(Musicservice!!.mediaPlayer == null) Musicservice!!.mediaPlayer = MediaPlayer()
            Musicservice!!.mediaPlayer!!.reset()
            Musicservice!!.mediaPlayer!!.setDataSource(musicListPA[songposition].path)
            Musicservice!!.mediaPlayer!!.prepare()
            Musicservice!!.mediaPlayer!!.start()
            isplaying = true
            binding.playpausePA.setIconResource(R.drawable.pause)
            Musicservice!!.showNotification(R.drawable.pause)
            binding.tvseekbarPAstart.text = formatDuration(Musicservice!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvseekbarPAend.text = formatDuration(Musicservice!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = Musicservice!!.mediaPlayer!!.duration
            Musicservice!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = musicListPA[songposition].id
        }
        catch(e : Exception){ return }
    }
    private fun initializelayout(){
        songposition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "musicAdapterSearch" ->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setlayout()
                createMediaPlayer()
            }
            "musicAdapter" ->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setlayout()
                createMediaPlayer()
            }
            "mainActivity" ->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setlayout()
                createMediaPlayer()
            }
            "NowPlaying" ->{
                setlayout()
                binding.tvseekbarPAstart.text = formatDuration(Musicservice!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvseekbarPAend.text = formatDuration(Musicservice!!.mediaPlayer!!.duration.toLong())
                binding.seekBarPA.progress = Musicservice!!.mediaPlayer!!.currentPosition
                binding.seekBarPA.max = Musicservice!!.mediaPlayer!!.duration
                if(isplaying) binding.playpausePA.setIconResource(R.drawable.pause)
                else binding.playpausePA.setIconResource(R.drawable.play)
            }
            "favouriteAdapter" ->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(favouriteActivity.favouriteSongs)
                setlayout()
                createMediaPlayer()
            }
            "FavouriteShuffle" ->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(favouriteActivity.favouriteSongs)
                musicListPA.shuffle()
                setlayout()
                createMediaPlayer()
            }
            "PlaylistDetailsAdapter"->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(playlistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist)
                setlayout()
                createMediaPlayer()
            }
            "PlaylistDetailsShuffle"->{
                val intent = Intent(this,musicservice::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(favouriteActivity.favouriteSongs)
                musicListPA.shuffle()
                setlayout()
                createMediaPlayer()
            }
        }
    }
    private fun playmusic(){
        binding.playpausePA.setIconResource(R.drawable.pause)
        Musicservice!!.showNotification(R.drawable.pause)
        isplaying = true
        Musicservice!!.mediaPlayer!!.start()
    }
    private fun pausemusic(){
        binding.playpausePA.setIconResource(R.drawable.play)
        Musicservice!!.showNotification(R.drawable.play)
        isplaying = false
        Musicservice!!.mediaPlayer!!.pause()
    }
    private fun prevNextSong(increment: Boolean){
        if(increment)
        {
            setSongPosition(increment = true)
            setlayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment = false)
            setlayout()
            createMediaPlayer()
        }
    }



    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as musicservice.MyBinder
        Musicservice = binder.currentService()
        createMediaPlayer()
        Musicservice!!.seekBarSetup()
        Musicservice!!.audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        Musicservice!!.audioManager.requestAudioFocus(Musicservice,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Musicservice = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try { setlayout() }catch (e : Exception){return}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK){
            return
        }
    }
    private fun showBottomSheetDialog(){
        var dialog = BottomSheetDialog(this@playeractivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will pause after 15 mins",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min15 = true
            Thread{Thread.sleep((15 * 60000).toLong())
            if (min15) exitApplication()}.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will pause after 30 mins",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min30 = true
            Thread{Thread.sleep((30 * 60000).toLong())
                if (min30) exitApplication()}.start()

            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will pause after 60 mins",Toast.LENGTH_SHORT).show()
            binding.timerPA.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min60 = true
            Thread{Thread.sleep((60 * 60000).toLong())
                if (min60) exitApplication()}.start()

            dialog.dismiss()
        }
    }
    private fun getMusicDetails(contentUri : Uri) : music{
        var cursor: Cursor? = null
        try{
            val projection = arrayOf(MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION , MediaStore.Audio.Media.TITLE)
            cursor = this.contentResolver.query(contentUri, projection, null, null, null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val TitleColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }!!
            val title = TitleColumn?.let { cursor.getString(it) }!!
            return music(id = "Unknown", title = title, album = "Unknown", artist = "Unknown", duration = duration , artUri = "Unknown",path = path.toString())
        }finally{
              cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(musicListPA[songposition].id == "Unknown" && !isplaying){
            exitApplication()
        }
    }
}