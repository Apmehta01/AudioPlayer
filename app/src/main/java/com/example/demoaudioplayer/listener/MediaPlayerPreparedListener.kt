package com.example.demoaudioplayer.listener

import android.media.MediaPlayer

/**
 *Name: MediaPlayerPreparedListener
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This is listener of getting media player prepad event.
 **/
interface MediaPlayerPreparedListener {
    fun onPrepared(mp: MediaPlayer)
}