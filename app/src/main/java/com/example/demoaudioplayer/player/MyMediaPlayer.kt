package com.example.demoaudioplayer.player

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.example.demoaudioplayer.adapter.MyGroupAdapter
import com.example.demoaudioplayer.listener.MediaPlayerPreparedListener
import com.example.demoaudioplayer.model.AudioModel
import com.google.android.exoplayer2.Player

/**
 *Name: MyMediaPlayer
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This is cutom media player class for managing the media player.
 **/
class MyMediaPlayer {
    private var currentTime: TextView? = null
    private var duration: TextView? = null
    var player: MediaPlayer? = null
    private var context: Context? = null
    private var btnPlay: ImageButton? = null
    private var btnPause: ImageButton? = null
    private var seekBar: SeekBar? = null
    private var tvSongName: TextView?=null
    private var tvArtistName: TextView?=null
    private var mediaPlayerPreparedListener: MediaPlayerPreparedListener? = null
    var id: Int = -1
    companion object {

        private var instance: MyMediaPlayer? = null

        var handler: Handler? = null
        var task: Runnable? = null


        fun getInstance(context: Context): MyMediaPlayer {
            if (instance == null) {

                instance = MyMediaPlayer()
                instance?.context = context
            }
            return instance!!
        }
    }

    fun setMediaPlayerPrepared(mediaPlayerPreparedListener: MediaPlayerPreparedListener) {
        this.mediaPlayerPreparedListener = mediaPlayerPreparedListener
    }

    fun playResource(id: Int, audioResource: AudioModel, progress: Int = 0) {
        if (player != null) {
            if (player!!.isPlaying) {
                player!!.stop()
                player!!.reset()
                btnPause?.visibility = GONE
                btnPlay?.visibility = VISIBLE
                seekBar?.progress = 0
            }
            player = null
        }

//        player = MediaPlayer.create(context, audioResource)
        player = MediaPlayer.create(context, Uri.parse(audioResource.aPath))
        instance?.id = id
        player?.start()
        player?.seekTo(progress)
        player?.setOnPreparedListener {
            seekBar?.max = it.duration
            it.seekTo(seekBar!!.progress)
            mediaPlayerPreparedListener?.onPrepared(it)
        }

        player?.setOnCompletionListener {
            btnPause?.visibility = GONE
            btnPlay?.visibility = VISIBLE
            seekBar?.progress = 0
            it.stop()
            MyGroupAdapter.currentPosition = -1
        }
    }

    /**
     *Name: MyMediaPlayer pauseResource()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will pause the playback.
     **/
    fun pauseResource() {
        player?.pause()
    }

    /**
     *Name: MyMediaPlayer isPlaying()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will return if song is playing or not by checking seconds.
     **/
    fun isPlaying(audioId: Int): Boolean {
        if (player != null && audioId == id) {
            return player!!.isPlaying
        }
        return false
    }

    /**
     *Name: MyMediaPlayer setViews()
     *<br>  Created By Arpan Mehta on 21-06-22
     *<br>  Modified By Arpan Mehta on 21-06-22
     *<br>  Purpose: This method will set views.
     **/
    fun setViews(
        btnPlay: ImageButton?,
        btnPause: ImageButton?,
        seekBar: SeekBar?,
        currentTime: TextView?,
        duration: TextView?,
        songName:TextView?,
        songArtist:TextView?,
    ) {
        this.btnPlay = btnPlay
        this.btnPause = btnPause
        this.seekBar = seekBar
        this.currentTime = currentTime
        this.duration = duration
        this.tvSongName = songName
        this.tvArtistName = songArtist
    }

    @JvmName("getPlayer1")
    fun getPlayer(): MediaPlayer? {
        return player
    }
}