package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.com.example.musicplayer.MusicPlaylist
import com.example.musicplayer.com.example.musicplayer.exitApplication
import com.example.musicplayer.com.example.musicplayer.music
import com.example.musicplayer.com.example.musicplayer.musicAdapter
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var Musicadapter: musicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<music>
        lateinit var musicListSearch : ArrayList<music>
        var search : Boolean = false
        var themeIndex : Int = 0
        val currentTheme = arrayOf(R.style.coolPink , R.style.coolBlue , R.style.coolpurple , R.style.coolgreen , R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav , R.style.coolblueNav , R.style.coolpurpleNav , R.style.coolgreenNav , R.style.coolblackNav)
        val currentGradient = arrayOf(R.drawable.gradient_pink , R.drawable.gradient_blue , R.drawable.gradient_purple , R.drawable.gradient_green , R.drawable.gradient_black)
        var sortOrder : Int = 0
        val sortingList = arrayOf(MediaStore.Audio.Media.DATE_ADDED , MediaStore.Audio.Media.TITLE , MediaStore.Audio.Media.SIZE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex", 0)
        setTheme(currentTheme[themeIndex])
//        setTheme(R.style.coolPink)
        requestRuntimePermission()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar : androidx.appcompat.widget.Toolbar  = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open ,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission()) {
            initializelayout()
            favouriteActivity.favouriteSongs = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs",null)
            val typeToken = object : TypeToken<ArrayList<music>>() {}.type
            if(jsonString != null){
                val data : ArrayList<music> = GsonBuilder().create().fromJson(jsonString , typeToken)
                favouriteActivity.favouriteSongs.addAll(data)
            }
            playlistActivity.musicPlaylist = MusicPlaylist()
            val jsonStringPlaylist = editor.getString("MusicPlaylist",null)
            if(jsonStringPlaylist != null){
                val dataPlaylist : MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist , MusicPlaylist::class.java)
                playlistActivity.musicPlaylist = dataPlaylist
            }
        }



        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this,playeractivity::class.java)
            intent.putExtra("index" , 0)
            intent.putExtra("class","mainActivity")
            startActivity(intent)
        }
        binding.favouritesbtn.setOnClickListener {
            val intent = Intent(this,favouriteActivity::class.java)
            startActivity(intent)
        }
        binding.playlistbtn.setOnClickListener {
            val intent = Intent(this,playlistActivity::class.java)
            startActivity(intent)
        }
        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navFeedback -> startActivity(Intent(this@MainActivity,FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
                R.id.navAbout -> startActivity(Intent(this@MainActivity,AboutActivity::class.java))
                R.id.navExit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            exitApplication()
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
            true
        }


    }



    private fun requestRuntimePermission() :Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 13)
                return false
            }
        }else{
            //android 13 or Higher permission request
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted",Toast.LENGTH_SHORT).show()
                initializelayout()
            }
//            else ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializelayout(){
        search = false
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        val musicList = ArrayList<String>()
        var sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder",0)
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this)
        Musicadapter = musicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = Musicadapter
        binding.totalcount.text = buildString {
            append("Total Songs : ")
            append(Musicadapter.itemCount)
        }
    }

    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<music>{
        val tempList = ArrayList<music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)

        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null,
            sortingList[sortOrder] + " DESC", null)
        if(cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC,artUri = artUriC)
                    val file = File(music.path)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
        return true
        return super.onOptionsItemSelected(item)

    }
    @Suppress("DEPRECATION")
    override fun onDestroy() {
        super.onDestroy()
        if(!playeractivity.isplaying && playeractivity.Musicservice != null){
            exitApplication()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //for storing favourites data using shaared prefrences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favouriteActivity.favouriteSongs)
        editor.putString("FavouriteSongs",jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(playlistActivity.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
        var sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder",0)
        if(sortOrder != sortValue){
            sortOrder = sortValue
            MusicListMA = getAllAudio()
            Musicadapter.updateMusicList(MusicListMA)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu,menu)
        findViewById<LinearLayout>(R.id.linearLayoutNav)?.setBackgroundResource(currentGradient[themeIndex])
        var searchView = menu?.findItem(R.id.searchview)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean = true
            override fun onQueryTextChange(p0: String?): Boolean {
                musicListSearch = ArrayList()
                if(p0 != null){
                    val userInput = p0.lowercase()
                    for(song in MusicListMA){
                        if (song.title.lowercase().contains(userInput)){
                            musicListSearch.add(song)
                        }
                    }
                    search = true
                    Musicadapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}