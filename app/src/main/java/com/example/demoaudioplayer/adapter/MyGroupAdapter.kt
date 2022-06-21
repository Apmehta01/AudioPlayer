package com.example.demoaudioplayer.adapter

import com.example.demoaudioplayer.listener.RecyclerViewListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

/**
 *Name: MyGroupAdapter
 *<br>  Created By Arpan Mehta on 21-06-22
 *<br>  Modified By Arpan Mehta on 21-06-22
 *<br>  Purpose: This is the adapter class for managing recyclerview events.
 **/
class MyGroupAdapter : GroupAdapter<ViewHolder>() {
    companion object {
        var currentPosition: Int = -1
        private var recyclerViewListener: RecyclerViewListener? = null
        fun setRecyclerViewListener(recyclerViewListener: RecyclerViewListener?) {
            this.recyclerViewListener = recyclerViewListener
        }
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        if (holder != null && currentPosition == getAdapterPosition(holder.item)) {
            recyclerViewListener?.onViewAttached(holder)
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        if (currentPosition == getAdapterPosition(holder.item)) {
            recyclerViewListener?.onViewRecycled(holder)
        }
        super.onViewRecycled(holder)
    }
}