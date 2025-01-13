
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlaylistDetails
import com.example.musicplayer.R
import com.example.musicplayer.com.example.musicplayer.Playlist
import com.example.musicplayer.databinding.PlaylistviewBinding
import com.example.musicplayer.playlistActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class playlistViewAdapter(private val context : Context, private var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<playlistViewAdapter.MyHolder>() {
    class MyHolder(binding: PlaylistviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistimg
        val name = binding.playlistname
        val root = binding.root
        val delete = binding.playlistDlt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): playlistViewAdapter.MyHolder {
        return MyHolder(PlaylistviewBinding.inflate(LayoutInflater.from(context) ,parent,false))
    }


    override fun onBindViewHolder(holder: playlistViewAdapter.MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to dlt playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    playlistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
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
        holder.root.setOnClickListener {
            val intent = Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if(playlistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(playlistActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splashscreen).centerCrop())
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(playlistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}