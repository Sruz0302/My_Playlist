package com.sruz.myplaylist

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sruz.myplaylist.adapter.PlayListAdapter
import com.sruz.myplaylist.database.PlayListDatabase
import com.sruz.myplaylist.database.model.PlayList
import com.sruz.myplaylist.databinding.ActivityMainBinding
import android.media.RingtoneManager

import android.media.Ringtone
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var mBinidng: ActivityMainBinding
    var mediaPlayer: MediaPlayer? = null
    private lateinit var vm: MainActivityVm
    private lateinit var adapter: PlayListAdapter
    private val playListDataBase by lazy { PlayListDatabase.getInstance(this).playListDao() }
    private var ringtoneManager: Ringtone? = null
    private var playing:Boolean=false
    private var linearLayoutManager:LinearLayoutManager?=null
    private var currentItem :Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBinidng = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        vm = ViewModelProviders.of(this)[MainActivityVm::class.java]
        setUpRecyClerView()



        vm.getAllNotes().observe(this, Observer {
            Log.i("Notes observed", "$it")

            adapter.submitList(it)
        })
        mBinidng.btnAddPlayList.setOnClickListener {
            val intent_upload = Intent()
            intent_upload.type = "audio/*"
            intent_upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent_upload.action = Intent.ACTION_GET_CONTENT
            intent_upload.flags =
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            resultLauncher.launch(intent_upload)

        }

        mBinidng.recyclerPlayList.adapter = adapter

    }

    private fun setUpRecyClerView() {

        mBinidng.recyclerPlayList.layoutManager = LinearLayoutManager(this)
        mBinidng.recyclerPlayList.setHasFixedSize(true)

        adapter = PlayListAdapter(
            this
        ) { clickedPlayList ->
            if(!clickedPlayList.isPlaying){
                currentItem= clickedPlayList.id!!
                vm.update(

                    PlayList(
                        clickedPlayList.title,
                        clickedPlayList.duration,
                        true,
                        clickedPlayList.uri
                    )
                )
                val uri = Uri.parse(clickedPlayList.uri)
                ringtoneManager?.stop()
                ringtoneManager = RingtoneManager.getRingtone(applicationContext, uri)
                if(!playing){
                    playing=true
                    if (!ringtoneManager!!.isPlaying) {
                        ringtoneManager!!.play()
                        playing=true

                    } else {
                        ringtoneManager!!.stop()
                        playing=false
                    }
                }else{
                    ringtoneManager!!.stop()
                    playing=false
                }


            }

        }
        adapter.setListener(attachmentListener)


        mBinidng.recyclerPlayList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visiblePosition: Int = (mBinidng.recyclerPlayList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (visiblePosition > -1) {
                    val firstElementPosition: Int = (mBinidng.recyclerPlayList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val v: View? = (mBinidng.recyclerPlayList.layoutManager as LinearLayoutManager).findViewByPosition(visiblePosition)
                    //do something
                    var visibleChild: View = mBinidng.recyclerPlayList.getChildAt(0)
                    val firstChild: Int = mBinidng.recyclerPlayList.getChildAdapterPosition(visibleChild)
                    visibleChild = mBinidng.recyclerPlayList.getChildAt(mBinidng.recyclerPlayList.childCount - 1)
                    val lastChild: Int = mBinidng.recyclerPlayList.getChildAdapterPosition(visibleChild)
                    if(currentItem in firstChild .. lastChild){
                       Log.e("visible","yes")
                    }else{
                        if(ringtoneManager!!.isPlaying){
                           ringtoneManager!!.stop()
                        }
                    }
                    println("first visible child is: $firstChild")
                    println("last visible child is: $lastChild")

                }
            }
        })

        mBinidng.recyclerPlayList.adapter = adapter
    }
    var attachmentListener: PlayListAdapter.PlayListListener =
        object : PlayListAdapter.PlayListListener {
            override fun onPlayButtonClicked(position: Int, clickedPlayList: PlayList, image: ImageView, seekBar: SeekBar) {
                if(!clickedPlayList.isPlaying){
                    currentItem= clickedPlayList.id!!
                    vm.update(

                        PlayList(
                            clickedPlayList.title,
                            clickedPlayList.duration,
                            true,
                            clickedPlayList.uri
                        )
                    )
                    val uri = Uri.parse(clickedPlayList.uri)
                    ringtoneManager?.stop()
                    ringtoneManager = RingtoneManager.getRingtone(applicationContext, uri)
                    if(!playing){
                        playing=true
                        if (!ringtoneManager!!.isPlaying) {
                            ringtoneManager!!.play()
                            playing=true
                            image.setImageResource(R.drawable.ic_pause)

                        } else {
                            ringtoneManager!!.stop()
                            playing=false
                            image.setImageResource(R.drawable.ic_play)

                        }
                    }else{
                        ringtoneManager!!.stop()
                        playing=false
                        image.setImageResource(R.drawable.ic_play)

                    }


                }else{
                    image.setImageResource(R.drawable.ic_play)
                }

            }

            override fun onPauseButtonClicked(
                position: Int,
                playList: PlayList,
                image: ImageView,
                seekBar: SeekBar
            ) {
                TODO("Not yet implemented")
            }


        }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data


                result.data?.let { returnUri ->
                    if (data != null) {
                        if (data.clipData != null) {
                            for (i in 0 until data.clipData!!.itemCount) {
                                val uri: Uri? = data.clipData!!.getItemAt(i).uri
                                if (uri != null) {
                                    contentResolver.query(
                                        uri,
                                        null, null, null, null
                                    )?.use { cursor ->
                                        cursor.moveToFirst()
                                        val nameIndex =
                                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                        cursor.moveToFirst()
                                        val playList = PlayList(
                                            cursor.getString(nameIndex),
                                            "10",
                                            false,
                                            uri.toString()
                                        )
                                        vm.insert(playList)

                                    }
                                }
                            }
                        } else {
                            val uri: Uri? = data.data
                            if (uri != null) {

                                contentResolver.query(
                                    uri,
                                    null, null, null, null
                                )?.use { cursor ->
                                    val nameIndex =
                                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    cursor.moveToFirst()
                                    val playList = PlayList(
                                        cursor.getString(nameIndex),
                                        "10",
                                        false,
                                        uri.toString()
                                    )

                                    vm.insert(playList)


                                }
                            }
                        }
                    }

                }
            }
        }


}
