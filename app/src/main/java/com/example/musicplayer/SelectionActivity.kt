package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity.Companion.MusicListMA
import com.example.musicplayer.com.example.musicplayer.musicAdapter
import com.example.musicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: musicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        setContentView(binding.root)
        binding.SelectionRV.setItemViewCacheSize(30)
        binding.SelectionRV.setHasFixedSize(true)
        binding.SelectionRV.layoutManager = LinearLayoutManager(this)
        adapter = musicAdapter(this, MusicListMA, selectionActivity = true)
        binding.SelectionRV.adapter = adapter
        binding.backbtnSA.setOnClickListener { finish() }
        //for search View
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.musicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.musicListSearch)
                }
                return true
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        //for black theme checking
//        if(MainActivity. == 4)
//        {
//            binding.searchViewSA.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
//        }
//    }
}