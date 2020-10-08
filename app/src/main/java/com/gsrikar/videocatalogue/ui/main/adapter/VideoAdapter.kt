package com.gsrikar.videocatalogue.ui.main.adapter


import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.gsrikar.videocatalogue.BuildConfig
import com.gsrikar.videocatalogue.R
import com.gsrikar.videocatalogue.model.VideoInfo
import com.gsrikar.videocatalogue.ui.main.MainFragment


/**
 * Populates [MainFragment] with views held by [VideoHolder]
 */
class VideoAdapter : RecyclerView.Adapter<VideoHolder>() {

    companion object {
        // Log cat tag
        private val TAG = VideoAdapter::class.java.simpleName

        // True for debug builds and false otherwise
        private val DBG = BuildConfig.DEBUG
    }

    /**
     * List contains video information
     */
    private val videoList = arrayListOf<VideoInfo>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): VideoHolder {
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.exo_video_list_item, p0, false)
        return VideoHolder(view)
    }

    override fun onBindViewHolder(p0: VideoHolder, p1: Int) {
        val uri = videoList[p1].uri
        if (DBG) Log.d(TAG, "At $p1 Uri: $uri")
        val playerView = p0.playerView
        val context = playerView.context
        val player = getPlayer(context)
        p0.currPlayer = player
        playerView.setShowPreviousButton(false)
        playerView.setShowNextButton(false)
        playerView.player = player
        preparePlay(player, uri)
    }

    private fun getPlayer(context: Context) =
        SimpleExoPlayer.Builder(context)
            .build()

    /**
     * Prepare the player and play the video
     */
    private fun preparePlay(player: Player, uri: Uri) {
        val firstItem = MediaItem.fromUri(uri)
        // Add the media items to be played.
        player.addMediaItem(firstItem)
        // Prepare the player
        player.prepare()
    }

    override fun getItemCount() = videoList.size

    /**
     * Update the list
     */
    fun updateVideoList(videoList: List<VideoInfo>) {
        val diffCallback = VideoDiffCallback(this.videoList, videoList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Empty List
        this.videoList.clear()
        // Add the list
        this.videoList.addAll(videoList)
        // Push the updates
        diffResult.dispatchUpdatesTo(this)
    }

}