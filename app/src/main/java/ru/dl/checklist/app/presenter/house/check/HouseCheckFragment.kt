package ru.dl.checklist.app.presenter.house.check

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import ru.dl.checklist.R
import ru.dl.checklist.app.ext.collectLatestLifecycleFlow
import ru.dl.checklist.app.ext.createBitmapFromResult
import ru.dl.checklist.app.ext.getViewModel
import ru.dl.checklist.app.ext.viewLifecycleLazy
import ru.dl.checklist.databinding.FragmentHouseCheckBinding
import ru.dl.checklist.domain.model.HouseCheckDomain
import timber.log.Timber

class HouseCheckFragment : Fragment(R.layout.fragment_house_check) {
    companion object {
        private const val CODE_IMG_GALLERY = 111
        private const val PERMISSIONS_REQUEST_CODE = 10
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val args: HouseCheckFragmentArgs by navArgs()
    private val binding by viewLifecycleLazy(FragmentHouseCheckBinding::bind)
    private val viewModel: HouseCheckViewModel by lazy {
        getViewModel { HouseCheckViewModel() }
    }
    private var houseCheckAdapter = HouseCheckAdapter(
        onNoClick = ::onNoClick,
        onYesClick = ::onYesClick,
        onClickAddPhoto = ::onClickAddPhoto,
    )
    private var itemId: Long = 0
    private fun onClickAddPhoto(item: HouseCheckDomain) {
        itemId = item.id
        loadFileFromDevice()
    }

    private fun loadFileFromDevice() {
        when (ContextCompat.checkSelfPermission(
            requireContext(), CAMERA_PERMISSION
        )) {
            PackageManager.PERMISSION_GRANTED -> addPhotoFromIntent()
            else -> requestPermissionLauncher.launch(CAMERA_PERMISSION)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imageBitmap = data.createBitmapFromResult(requireActivity())
            imageBitmap?.let {
//                Timber.i(it.allocationByteCount.toString())
                viewModel.event(HouseChecksContract.Event.OnChangeAttachment(itemId, imageBitmap))
            }
        }
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

    private fun onNoClick(item: HouseCheckDomain) {
        viewModel.event(HouseChecksContract.Event.OnNoClick(item))
    }

    private fun onYesClick(item: HouseCheckDomain) {
        viewModel.event(HouseChecksContract.Event.OnYesClick(item))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uuidArgs = args.houseChecklistUUID
        Timber.i("UUID ${args.houseChecklistUUID}")
        initUI()
        initViewModelObservers()
        viewModel.event(HouseChecksContract.Event.OnRefresh)
    }

    private fun initUI() {
        with(binding.viewPager2) {
            adapter = houseCheckAdapter
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val item = houseCheckAdapter.getItemAtPosition(position)
                    viewModel.event(HouseChecksContract.Event.OnChangeSlide(position, item))
                }
            })
            binding.btnNext.setOnClickListener {
                binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem + 1, true)
            }
            binding.btnPrev.setOnClickListener {
                binding.viewPager2.setCurrentItem(binding.viewPager2.currentItem - 1, true)
            }
        }
        binding.fabSend.setOnClickListener {
            viewModel.event(HouseChecksContract.Event.OnSend)
        }
    }

    private fun initViewModelObservers() {
        collectLatestLifecycleFlow(viewModel.effect) {
            when (it) {
                is HouseChecksContract.Effect.ShowMessage -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        collectLatestLifecycleFlow(viewModel.state) { state ->
            Timber.i("Пришёл новый список состояний")
            houseCheckAdapter.updateList(state.list)
            if (state.list.isNotEmpty()) {
                viewModel.event(
                    HouseChecksContract.Event.OnCheckPhoto(
                        binding.viewPager2.currentItem,
                        houseCheckAdapter.getItemAtPosition(binding.viewPager2.currentItem)
                    )
                )
            }
            binding.btnNext.visibility = (if (state.isNextVisible) View.VISIBLE else View.INVISIBLE)
            binding.fabSend.visibility = (if (state.isSendVisible) View.VISIBLE else View.GONE)
            binding.btnPrev.isEnabled = state.isPrevEnabled
            binding.btnNext.isEnabled = state.isNextEnabled
            binding.fabSend.isEnabled = state.isNextEnabled
        }
    }
}