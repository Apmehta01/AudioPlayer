package com.example.demoaudioplayer.model

import android.graphics.Bitmap

/**
 *Name: AudioModel
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This model class of Audio.
 **/
data class AudioModel(
    var aPath: String? = null,
    var aAlbum: String? = null,
    var aAlbumArt: Bitmap? = null,
    var aArtist: String? = null
)