package com.tuananh.app.musicapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tuananh.app.musicapp.data.model.Song
import com.tuananh.app.musicapp.databinding.ItemSongBinding

class SongAdapter: RecyclerView.Adapter<SongAdapter.MyViewHolder>() {

    private lateinit var songList: MutableList<Song>
    private lateinit var onItemClickListener: (View?, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rowItemBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val myHolder = MyViewHolder(rowItemBinding)
        return myHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song)
    }

    override fun getItemCount(): Int {
        if(::songList.isInitialized) {
            return songList.size
        }
        return 0
    }

    fun setOnItemClickListener(onItemClickListener: (View?, Int) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSongList(songList: MutableList<Song>) {
        this.songList = songList
    }

    inner class MyViewHolder(val rowItemBinding: ItemSongBinding) : RecyclerView.ViewHolder(rowItemBinding.root), View.OnClickListener {
        fun bind(song: Song) {
            rowItemBinding.textTitle.text = song.title
            rowItemBinding.textAuthor.text = song.artist
            rowItemBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(::onItemClickListener.isInitialized) {
                onItemClickListener(v, adapterPosition)
            }
        }
    }
}
