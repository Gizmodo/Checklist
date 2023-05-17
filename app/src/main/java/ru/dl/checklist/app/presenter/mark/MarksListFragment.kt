package ru.dl.checklist.app.presenter.mark

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.createBitmapFromResult
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentMarksListBinding
import timber.log.Timber

class MarksListFragment : Fragment(R.layout.fragment_marks_list) {
    private val args: MarksListFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentMarksListBinding::bind)
    private val viewModel: MarksListViewModel by lazy {
        getViewModel { MarksListViewModel() }
    }
    private var markListAdapter = MarkListAdapter(mutableListOf(), ::onCardUIEvent)

    private fun onCardUIEvent(markCardUIEvent: MarkCardUIEvent) {
        when (markCardUIEvent) {
            is MarkCardUIEvent.Attach -> {
                Timber.i("Показать выбор фото")
                loadFileFromDevice()
            }

            is MarkCardUIEvent.No -> {
                Timber.i("Выбран ответ: ${markCardUIEvent.item.answer}")
                viewModel.onEvent(MarkListEvent.ChangeAnswer(markCardUIEvent.item))
            }

            is MarkCardUIEvent.Yes -> {
                Timber.i("Выбран ответ: ${markCardUIEvent.item.answer}")
                viewModel.onEvent(MarkListEvent.ChangeAnswer(markCardUIEvent.item))
            }

            is MarkCardUIEvent.Comment -> {
                Timber.i("Новый комментарий: ${markCardUIEvent.text}")
                viewModel.onEvent(MarkListEvent.ChangeComment(markCardUIEvent.item))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity())
            imageBitmap?.let {
                Timber.i(it.allocationByteCount.toString())
            }
//            viewModel.sendFileRequest(image = imageBitmap!!)
//            Glide.with(requireContext()).load(imageBitmap).into(binding.imgPreview)
        }
    }

    private fun loadFileFromDevice() {
        val cameraIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf { intent ->
            intent.resolveActivity(requireActivity().packageManager) != null
        }

        val galleryIntent = Intent(Intent.ACTION_PICK).apply { this.type = "image/*" }

        val intentChooser = Intent(Intent.ACTION_CHOOSER).apply {
            this.putExtra(Intent.EXTRA_INTENT, galleryIntent)
            cameraIntent?.let { intent ->
                this.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS,
                    arrayListOf(intent).toTypedArray<Parcelable>()
                )
            }
            this.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.gallery_title))
        }

        startActivityForResult(intentChooser, CODE_IMG_GALLERY)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Takes the user to the success fragment when permission is granted
                loadFileFromDevice()
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val zoneId = args.zoneId.toLong()
        Timber.i(zoneId.toString())
        viewModel.onEvent(MarkListEvent.SetZoneId(zoneId))
        initUI()
        initViewModelObservers()
        viewModel.onEvent(MarkListEvent.LoadMarkListByZone)
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.markListEvent) {
            Timber.i("Collected data from MarksListFragment")
            Timber.d(it.toString())
            markListAdapter.updateList(it)
        }
    }

    private fun initUI() {
        with(binding.rvMarkList) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = markListAdapter
        }
    }

    companion object {
        private const val CODE_IMG_GALLERY = 111
        private const val PERMISSIONS_REQUEST_CODE = 10
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}