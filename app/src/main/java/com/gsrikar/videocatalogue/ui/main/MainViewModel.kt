package com.gsrikar.videocatalogue.ui.main

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gsrikar.videocatalogue.BuildConfig
import com.gsrikar.videocatalogue.R
import com.gsrikar.videocatalogue.db.AppDb.Companion.appDb
import com.gsrikar.videocatalogue.db.FavEntity
import com.gsrikar.videocatalogue.model.VideoInfo


class MainViewModel : ViewModel() {

    companion object {
        // Log cat tag
        private val TAG = MainViewModel::class.java.simpleName

        // True for debug builds and false otherwise
        private val DBG = BuildConfig.DEBUG
    }

    /**
     * Contains the status text
     */
    val statusTextMutableLiveData = MutableLiveData<String>()

    private val videoListMutableLiveData = MutableLiveData<List<VideoInfo>>()

    /**
     * Contains the video list
     */
    val videoListLiveData: LiveData<List<VideoInfo>>
        get() = videoListMutableLiveData

    /**
     * List of favorites
     */
    private var favList: List<FavEntity>? = null

    /**
     * Favorite Menu item
     */
    private var menuItem: MenuItem? = null

    /**
     * Current Visible Uri item
     */
    private var currentUri: Uri? = null

    /**
     * Recycler view scroll listener
     */
    val scrollListener =
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollChanged(getCurrentPosition(recyclerView))
            }
        }

    private fun scrollChanged(currentPosition: Int) {
        // Get the current position
        if (DBG) Log.d(TAG, "Scrolled to $currentPosition")
        val videoList = videoListMutableLiveData.value
        val uri = videoList?.get(currentPosition)?.uri
        if (DBG) Log.d(TAG, "Size: ${videoList?.size}")
        if (DBG) Log.d(TAG, "Uri: $uri")
        updateCurrentUri(uri)
        val index = favList?.indexOf(FavEntity(uri.toString()))
        if (DBG) Log.d(TAG, "Index: $index")
        if (index != null && index >= 0) {
            updateMenuColor()
        } else {
            resetMenuColor()
        }
    }

    private fun updateCurrentUri(uri: Uri?) {
        currentUri = uri
    }

    fun getCurrentUri(): Uri? {
        return currentUri
    }

    /**
     * Insert the uri to the database
     */
    suspend fun insert(uri: Uri) {
        appDb?.favDao()?.insert(FavEntity(uri.toString()))
    }

    /**
     * Return the position of the current item
     */
    fun getCurrentPosition(recyclerView: RecyclerView) =
        (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()

    /**
     * Query for the media list
     */
    fun queryProvider(contentResolver: ContentResolver?) {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE
        )

        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        generateList(
            contentResolver?.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
        )
    }

    private fun generateList(cursor: Cursor?) {
        val videoList = mutableListOf<VideoInfo>()
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

            while (it.moveToNext()) {
                // Get values of columns for a given video
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val size = it.getInt(sizeColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Stores column values and the contentUri in a local object
                videoList += VideoInfo(contentUri, name, size)
            }
        }
        if (DBG) Log.d(TAG, "Video Size: ${videoList.size}")
        // Update the live data
        videoListMutableLiveData.value = videoList
    }

    /**
     * Update the fav icon
     */
    fun setMenuItem(menu: Menu) {
        menuItem = menu.findItem(R.id.favItem)
    }

    /**
     * Update the menu item
     */
    fun updateMenuColor() {
        getFavIcon()?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Color.MAGENTA,
                BlendModeCompat.SRC_IN
            )
        menuItem?.icon = getFavIcon()
    }

    /**
     * Update the menu item
     */
    fun resetMenuColor() {
        getFavIcon()?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                Color.WHITE,
                BlendModeCompat.SRC_IN
            )
        menuItem?.icon = getFavIcon()
    }

    /**
     * Return the fav icon
     */
    private fun getFavIcon(): Drawable? {
        return menuItem?.icon
    }

    fun getStoragePermission() = Manifest.permission.READ_EXTERNAL_STORAGE

    /**
     * Show that no permission was given or list is empty
     */
    fun updateStatusText(text: String) {
        statusTextMutableLiveData.value = text
    }

    suspend fun queryList() {
        favList = appDb?.favDao()?.query()
    }

}
