package com.gsrikar.videocatalogue.ui.main.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.android.synthetic.main.exo_video_list_item.view.*


/**
 * Holds reference to the views populated by [VideoAdapter]
 */
class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var currPlayer: SimpleExoPlayer? = null
    var prevPlayer: SimpleExoPlayer? = null

    /**
     * Player view
     */
    val playerView: StyledPlayerView = itemView.playerView

    /**
     * Play the video
     */
    private fun playVideo() {
        stopPrevPlay()
        // Start the playback
        currPlayer?.play()
    }

    private fun stopPrevPlay() {
        prevPlayer?.let {
            it.stop()
            it.release()
        }
        prevPlayer = currPlayer
    }

}