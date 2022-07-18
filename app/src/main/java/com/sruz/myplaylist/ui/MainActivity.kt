package com.sruz.myplaylist.ui

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sruz.myplaylist.R
import com.sruz.myplaylist.adapter.PlayListAdapter
import com.sruz.myplaylist.database.PlayListDatabase
import com.sruz.myplaylist.database.model.PlayList
import com.sruz.myplaylist.databinding.ActivityMainBinding
import com.sruz.myplaylist.helpers.CustomToast
import com.sruz.myplaylist.helpers.Utils
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var vm: MainActivityVm
    private lateinit var adapter: PlayListAdapter
    private var ringtoneManager: Ringtone? = null
    private var playing:Boolean=false
    private var currentItem :Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        vm = ViewModelProviders.of(this)[MainActivityVm::class.java]
        setUpRecyclerView()

        vm.getAllNotes().observe(this, Observer {
            adapter.submitList(it)
        })
        mBinding.btnAddPlayList.setOnClickListener {
            if(Utils.isNetworkAvailable()){
                val intentUpload = Intent()
                intentUpload.type = "audio/*"
                intentUpload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intentUpload.action = Intent.ACTION_GET_CONTENT
                intentUpload.flags =
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                resultLauncher.launch(intentUpload)

            }else{
                CustomToast.makeToast("No internet connection")
            }

        }

        mBinding.recyclerPlayList.adapter = adapter

    }

    private fun setUpRecyclerView() {

        mBinding.recyclerPlayList.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerPlayList.setHasFixedSize(true)

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
                    playing = if (!ringtoneManager!!.isPlaying) {
                        ringtoneManager!!.play()
                        true

                    } else {
                        ringtoneManager!!.stop()
                        false
                    }
                }else{
                    ringtoneManager!!.stop()
                    playing=false
                }


            }

        }
        adapter.setListener(attachmentListener)


        mBinding.recyclerPlayList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visiblePosition: Int = (mBinding.recyclerPlayList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (visiblePosition > -1) {
                    (mBinding.recyclerPlayList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    val v: View? = (mBinding.recyclerPlayList.layoutManager as LinearLayoutManager).findViewByPosition(visiblePosition)
                    //do something
                    var visibleChild: View = mBinding.recyclerPlayList.getChildAt(0)
                    val firstChild: Int = mBinding.recyclerPlayList.getChildAdapterPosition(visibleChild)
                    visibleChild = mBinding.recyclerPlayList.getChildAt(mBinding.recyclerPlayList.childCount - 1)
                    val lastChild: Int = mBinding.recyclerPlayList.getChildAdapterPosition(visibleChild)
                    if(currentItem in firstChild .. lastChild){
                       Log.e("visible","yes")
                    }else{
                        if(ringtoneManager!=null)
                        if(ringtoneManager!!.isPlaying){
                           ringtoneManager!!.stop()
                        }
                    }


                }
            }
        })

        mBinding.recyclerPlayList.adapter = adapter
    }
    private var attachmentListener: PlayListAdapter.PlayListListener =
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

            }


        }


    private var resultLauncher =
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
                                        val mp: MediaPlayer = MediaPlayer.create(this, uri)
                                        val duration: Int = mp.duration
                                        val finalDuration: Long = duration.toLong()
                                        val myDuration: String = String.format(
                                            "%d min, %d sec",
                                            TimeUnit.MILLISECONDS.toMinutes(finalDuration),
                                            TimeUnit.MILLISECONDS.toSeconds(finalDuration) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalDuration))
                                        )
                                        mp.release()
                                        val nameIndex =
                                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                        cursor.moveToFirst()
                                        val playList = PlayList(
                                            cursor.getString(nameIndex),
                                            myDuration,
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
                                    val mp: MediaPlayer = MediaPlayer.create(this, uri)
                                    val duration: Int = mp.duration
                                    val finalDuration: Long = duration.toLong()
                                    val myDuration: String = String.format(
                                        "%d min, %d sec",
                                        TimeUnit.MILLISECONDS.toMinutes(finalDuration),
                                        TimeUnit.MILLISECONDS.toSeconds(finalDuration) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalDuration))
                                    )
                                    mp.release()
                                    val nameIndex =
                                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    cursor.moveToFirst()
                                    val playList = PlayList(
                                        cursor.getString(nameIndex),
                                        myDuration,
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

    override fun onDestroy() {
        super.onDestroy()
        ringtoneManager?.stop()
    }



}
