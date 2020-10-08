package com.gsrikar.videocatalogue.ui.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.gsrikar.videocatalogue.model.VideoInfo


/**
 * Diff Call back
 */
class VideoDiffCallback(
    private val oldList: ArrayList<VideoInfo>,
    private val newList: List<VideoInfo>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].uri == newList[newItemPosition].uri
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}