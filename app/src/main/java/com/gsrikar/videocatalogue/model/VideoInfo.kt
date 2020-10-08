package com.gsrikar.videocatalogue.model

import android.net.Uri


/**
 * Contains the details about the video
 */
data class VideoInfo(
    val uri: Uri,
    val name: String,
    val size: Int
)
