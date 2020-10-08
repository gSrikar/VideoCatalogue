package com.gsrikar.videocatalogue.ui.main

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSnapHelper
import com.gsrikar.videocatalogue.BuildConfig
import com.gsrikar.videocatalogue.R
import com.gsrikar.videocatalogue.databinding.MainFragmentBinding
import com.gsrikar.videocatalogue.model.VideoInfo
import com.gsrikar.videocatalogue.ui.main.adapter.VideoAdapter
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    companion object {
        // Log cat tag
        private val TAG = MainFragment::class.java.simpleName

        // True for debug builds and false otherwise
        private val DBG = BuildConfig.DEBUG

        // Request code used while requesting storage permissions
        private const val REQUEST_STORAGE_PERMISSION = 3299
    }

    init {
        setHasOptionsMenu(true)
    }

    /**
     * Instance of [MainViewModel]
     */
    private val viewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    /**
     * Instance of [VideoAdapter]
     */
    private val adapter = VideoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MainFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setListener()
        setAdapter()

        // Check storage permissions
        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                viewModel.getStoragePermission()
            ) == PERMISSION_GRANTED
        ) {
            // Query the video list
            queryList()
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(viewModel.getStoragePermission()),
            REQUEST_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            permissionGrantedCheck(permissions)
        }
    }

    private fun permissionGrantedCheck(permissions: Array<out String>) {
        when {
            isPermissionGranted(permissions) -> {
                // Query the video list
                queryList()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                viewModel.getStoragePermission()
            ) -> requestPermissions()
            else -> viewModel.updateStatusText(requireContext().getString(R.string.no_permission_text))
        }
    }

    private fun isPermissionGranted(permissions: Array<out String>): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(requireContext(), it) != PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun setListener() {
        viewModel.videoListLiveData.observe(viewLifecycleOwner) {
            videoListChanged(it)
        }
        videoRecyclerView.addOnScrollListener(viewModel.scrollListener)
    }

    private fun videoListChanged(videoInfoList: List<VideoInfo>) {
        if (videoInfoList.isEmpty()) {
            viewModel.updateStatusText(requireContext().getString(R.string.empty_list))
        } else {
            adapter.updateVideoList(videoInfoList)
        }
    }

    private fun setAdapter() {
        videoRecyclerView.adapter = adapter
        LinearSnapHelper().apply {
            attachToRecyclerView(videoRecyclerView)
        }
    }

    private fun queryList() {
        lifecycleScope.launch {
            viewModel.queryProvider(context?.contentResolver)
            viewModel.queryList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
        // Save the fav icon
        viewModel.setMenuItem(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favItem -> {
                favClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun favClicked() {
        // Get the current uri
        val uri = viewModel.getCurrentUri()
        if (DBG) Log.d(TAG, "Uri clicked: $uri")
        if (uri != null) {
            saveFavUri(uri)
            viewModel.updateMenuColor()
        }
    }

    private fun saveFavUri(uri: Uri) {
        lifecycleScope.launch {
            viewModel.insert(uri)
        }
    }

}