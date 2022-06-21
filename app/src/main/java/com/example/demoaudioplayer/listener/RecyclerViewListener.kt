package com.example.demoaudioplayer.listener

import com.xwray.groupie.ViewHolder

/**
 *Name: RecyclerViewListener
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This is recyclerview listener for attached and recycled event.
 **/
interface RecyclerViewListener{
    fun onViewAttached(holder: ViewHolder?)

    fun onViewRecycled(holder: ViewHolder)
}