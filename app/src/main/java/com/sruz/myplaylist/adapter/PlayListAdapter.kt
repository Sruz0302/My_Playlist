package com.sruz.myplaylist.adapter


import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sruz.myplaylist.R
import com.sruz.myplaylist.database.model.PlayList
import com.sruz.myplaylist.helpers.CustomToast
import com.sruz.myplaylist.utils.GetDuration
import java.lang.Exception
import java.util.concurrent.TimeUnit


class PlayListAdapter(private val context: Context,private val onItemClickListener: (PlayList) -> Unit) :
    ListAdapter<PlayList, PlayListAdapter.NoteHolder>(diffCallback) {

    //    var context: Context? = null
    private var listener: PlayListListener?=null
    var mediaPlayer:MediaPlayer?=null


    fun setListener (playListListener: PlayListListener){
        this.listener=playListListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_play_list, parent,
            false
        )
        return NoteHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        with(getItem(position)) {
            holder.tvTitle.text = title
            holder.tvDuration.text = duration
            Log.e("uri", uri.toString())
            try {
                if(Uri.parse(uri)!=null){
                    val mp: MediaPlayer = MediaPlayer.create(context, Uri.parse(uri))
                    val duration: Int = mp.duration
                    val finalDuration: Long = duration.toLong()
                    val myDuration: String = String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(finalDuration),
                        TimeUnit.MILLISECONDS.toSeconds(finalDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalDuration))
                    )
                    mp.release()
                    Log.e("duration", duration.toString())
                    Log.e("formatted", myDuration)

                    holder.tvDuration.text=myDuration
                    holder.seekBar.max=duration
                }
            }catch (e:Exception){

            }


            holder.imgBtnPlay.setOnClickListener() {
                if(!isPlaying){
                    onItemClickListener(getItem(position))

                }

            }
        }
    }

    fun getPlayListAt(position: Int) = getItem(position)


    inner class NoteHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        val tvTitle: TextView = itemView.findViewById(R.id.tvAudioName)
        val tvDuration: TextView = itemView.findViewById(R.id.tvTime)
        val imgBtnPlay: ImageView = itemView.findViewById(R.id.imgPlayBtn)
        val seekBar: SeekBar = itemView.findViewById(R.id.mSeekBar)


    }

    interface PlayListListener {
        fun onPlayButtonClicked(position: Int, playList: PlayList, image: ImageView,seekBar: SeekBar)
        fun onPauseButtonClicked(position: Int, playList: PlayList,image: ImageView,seekBar: SeekBar)
    }
}
private val diffCallback = object : DiffUtil.ItemCallback<PlayList>() {
    override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList) =
        oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList) =
        oldItem.title == newItem.title
                && oldItem.title == newItem.title
                && oldItem.duration == newItem.duration
}

