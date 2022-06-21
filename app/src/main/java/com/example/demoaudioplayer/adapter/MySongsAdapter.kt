package com.example.demoaudioplayer.adapter

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.demoaudioplayer.R
import com.example.demoaudioplayer.listener.MediaPlayerPreparedListener
import com.example.demoaudioplayer.listener.RecyclerViewListener
import com.example.demoaudioplayer.model.AudioModel
import com.example.demoaudioplayer.player.MyMediaPlayer
import com.example.demoaudioplayer.view.activity.main.MainActivity
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.audio_item.view.*
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 *Name: MySongsAdapter
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This is the recyclerview adapter class.
 **/
class MySongsAdapter(private val audioSong: ArrayList<AudioModel>, mainActivity: MainActivity) : Item<ViewHolder>() {
    lateinit var myMediaPlayer: MyMediaPlayer
    lateinit var seekBar: SeekBar
    lateinit var btnPlay: ImageButton
    lateinit var btnPause: ImageButton
    lateinit var tvCurrentTime: TextView
    lateinit var tvDuration: TextView
    lateinit var tvSongName: TextView
    lateinit var tvArtistName: TextView
    lateinit var ivImageAlbum: ImageView
    var context: Context? = mainActivity
    var pos:Int?=null
    override fun getLayout(): Int = R.layout.audio_item

    override fun bind(viewHolder: ViewHolder, position: Int) {
        btnPlay = viewHolder.itemView.btnPlay
        btnPause = viewHolder.itemView.btnPause
        seekBar = viewHolder.itemView.seekBar
        tvCurrentTime = viewHolder.itemView.txtStartTime
        tvDuration = viewHolder.itemView.txtSongTime
        tvSongName = viewHolder.itemView.txtSongName
        tvArtistName = viewHolder.itemView.txtSongArtist
        ivImageAlbum= viewHolder.itemView.ivAlbum

        myMediaPlayer = MyMediaPlayer.getInstance(viewHolder.itemView.context)
        tvSongName.setSelected(true);
        tvSongName.text=audioSong.get(position).aAlbum
        tvArtistName.text=audioSong.get(position).aArtist
        ivImageAlbum.setImageBitmap(audioSong.get(position).aAlbumArt)

        pos=position
        if (myMediaPlayer.isPlaying(position)) {
            btnPlay.visibility = GONE
            btnPause.visibility = VISIBLE

        } else {
            btnPlay.visibility = VISIBLE
            btnPause.visibility = GONE
            seekBar.progress = 0
        }


        btnPlay.setOnClickListener {
            btnPlay.visibility = GONE
            btnPause.visibility = VISIBLE
            MyGroupAdapter.currentPosition = position
            myMediaPlayer.playResource(position, audioSong.get(position), seekBar.progress)
            myMediaPlayer.setViews(
                btnPlay = btnPlay,
                btnPause = btnPause,
                seekBar = seekBar,
                duration = tvDuration,
                currentTime = tvCurrentTime,
                songName=tvSongName,
                songArtist=tvArtistName)

            myMediaPlayer.setMediaPlayerPrepared(object : MediaPlayerPreparedListener {
                override fun onPrepared(mp: MediaPlayer) {
                    startUpdater(position)
                    val finalTime = mp.duration
                    tvDuration.text = String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                finalTime.toLong()
                            )
                        )
                    )
                }

            })

        }

        btnPause.setOnClickListener {
            it.visibility = GONE
            btnPlay.visibility = VISIBLE
            myMediaPlayer.pauseResource()
            stopUpdater()
            MyGroupAdapter.currentPosition = -1
        }

        MyGroupAdapter.setRecyclerViewListener(object : RecyclerViewListener {
            override fun onViewAttached(holder: ViewHolder?) {
                startUpdater(position)
                Log.d("XXXX", "I am attached")
            }

            override fun onViewRecycled(holder: ViewHolder) {
                btnPause.visibility = GONE
                btnPlay.visibility = VISIBLE
                myMediaPlayer.pauseResource()
                stopUpdater()
                MyGroupAdapter.currentPosition = -1
                Log.d("XXXX", "I am Removed")
            }

        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {
                    if (myMediaPlayer.player != null && myMediaPlayer.isPlaying(position))
                        myMediaPlayer.player!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    /**
     *Name: MySongsAdapter startUpdater()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will start playback and update seconds into textview.
     **/
    private fun startUpdater(position: Int) {
        if (MyMediaPlayer.handler == null) {
            MyMediaPlayer.handler = Handler()
        }

        MyMediaPlayer.task = object : Runnable {
            override fun run() {
                if (myMediaPlayer.isPlaying(position)) {
                    seekBar.max = myMediaPlayer.player!!.duration

                    val startTime = myMediaPlayer.player!!.currentPosition
                    tvCurrentTime.text = String.format(
                        "%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                startTime.toLong()
                            )
                        )
                    )
                    seekBar.progress = startTime
                    MyMediaPlayer.handler?.postDelayed(this, 50)
                } else {
                    seekBar.max = myMediaPlayer.player!!.duration
                    seekBar.progress = 0
                    stopUpdater()
                }

            }
        }

        MyMediaPlayer.handler?.postDelayed(MyMediaPlayer.task!!, 50)
    }

    /**
     *Name: MySongsAdapter stopUpdater()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will stop update of media player when click on pause or reached to end of song.
     **/
    private fun stopUpdater() {
        MyMediaPlayer.handler?.removeCallbacks(MyMediaPlayer.task!!, null)
        MyMediaPlayer.handler = null
        MyMediaPlayer.task = null
    }

    /**
     *Name: MySongsAdapter stopMedia()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will stop media player when activity goes into pause state
     **/
    fun stopMedia(){
        try{
            if(MyMediaPlayer.getInstance(context!!).getPlayer()!=null){
                MyMediaPlayer.getInstance(context!!).getPlayer()!!.pause()
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}