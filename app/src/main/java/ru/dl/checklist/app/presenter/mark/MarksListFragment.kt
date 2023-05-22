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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.createBitmapFromResult
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.DialogCommentBinding
import ru.dl.checklist.databinding.FragmentMarksListBinding
import ru.dl.checklist.domain.model.MarkDomainWithCount
import timber.log.Timber

class MarksListFragment : Fragment(R.layout.fragment_marks_list) {
    private val args: MarksListFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentMarksListBinding::bind)
    private val viewModel: MarksListViewModel by lazy { getViewModel { MarksListViewModel() } }
    private lateinit var dialog: AlertDialog
    private var itemId: Long = 0
    private var markListAdapter = MarkListAdapter(
        onCardUIEvent = ::onCardUIEvent,
        onChangeAnswer = ::onClickAnswer,
        onClickAddComment = ::onClickAddComment,
        onClickAddPhoto = ::onClickAddPhoto
    )

    private fun onClickAddPhoto(item: MarkDomainWithCount) {
        itemId = item.id
        loadFileFromDevice()
    }

    private fun onClickAddComment(item: MarkDomainWithCount) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val bind = DialogCommentBinding.inflate(layoutInflater)
        bind.edtComment.setText(item.comment)

        builder.setView(bind.root)
            .setCancelable(true)
            .setTitle(R.string.dialog_comment_title)
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                val newComment = bind.edtComment.text.toString()
                viewModel.onEvent(MarkListEvent.ChangeComment(item.id, newComment))
            }
        dialog = builder.show()
    }

    private fun onClickAnswer(markId: Long, answer: Float) {

        Timber.i("Select answer ${answer} for id ${markId} ")
        viewModel.onEvent(MarkListEvent.ChangeAnswer(markId, answer))
    }

    private fun onCardUIEvent(markCardUIEvent: MarkCardUIEvent) {
        /*when (markCardUIEvent) {
            is MarkCardUIEvent.ChangeAttach -> {
                Timber.i("Показать выбор фото")
                loadFileFromDevice()
            }

            is MarkCardUIEvent.ChangeAnswer -> {
                Timber.i("Смена ответа через Event")
                Timber.i("markCardUIEvent.item.answer: ${markCardUIEvent.item.answer}")
                Timber.i("markCardUIEvent.item: ${markCardUIEvent.item}")
                viewModel.onEvent(MarkListEvent.ChangeAnswer(markCardUIEvent.item))
            }

            is MarkCardUIEvent.ChangeComment -> {
                Timber.i("Новый комментарий: ${markCardUIEvent.item.comment}")
                viewModel.onEvent(MarkListEvent.ChangeComment(markCardUIEvent.item))
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity())
            imageBitmap?.let {
                Timber.i(it.allocationByteCount.toString())
                viewModel.onEvent(MarkListEvent.ChangeAttachment(markId = itemId, imageBitmap))
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            when {
                isGranted -> addPhotoFromIntent()
                else -> Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_permission),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    private fun loadFileFromDevice() {
        when (ContextCompat.checkSelfPermission(requireContext(), CAMERA_PERMISSION)) {
            PackageManager.PERMISSION_GRANTED -> addPhotoFromIntent()
            else -> requestPermissionLauncher.launch(CAMERA_PERMISSION)
        }
    }

    private fun addPhotoFromIntent() {
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
                loadFileFromDevice()
            } else {
                Toast.makeText(context, "Разрешения не предоставлены", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEvent(MarkListEvent.SetZoneId(args.zoneId.toLong()))
        initUI()
        initViewModelObservers()
        viewModel.onEvent(MarkListEvent.LoadMarkListByZone)
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.markListEvent) {
            Timber.i("Получены показатели через Flow. Кол-во: ${it.size}")
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
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}