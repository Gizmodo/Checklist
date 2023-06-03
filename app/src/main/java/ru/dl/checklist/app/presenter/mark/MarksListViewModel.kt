package ru.dl.checklist.app.presenter.mark

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.dl.checklist.app.app.App
import ru.dl.checklist.domain.model.MarkDomainWithCount
import ru.dl.checklist.domain.usecase.AddPhotoToMarkUseCase
import ru.dl.checklist.domain.usecase.GetMarkListByZone
import ru.dl.checklist.domain.usecase.UpdateMarkUseCase
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class MarksListViewModel : ViewModel() {
    private var zoneId: Long = 0
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }
    private val _listChannel = Channel<List<MarkDomainWithCount>>()
    val markListEvent = _listChannel.receiveAsFlow().distinctUntilChanged()

    @Inject
    lateinit var getMarkListByZoneLazy: dagger.Lazy<GetMarkListByZone>

    @Inject
    lateinit var updateMarkUseCase: dagger.Lazy<UpdateMarkUseCase>

    @Inject
    lateinit var addPhotoToMarkUseCase: dagger.Lazy<AddPhotoToMarkUseCase>

    init {
        App.appComponent.inject(this)
    }

    fun onEvent(event: MarkListEvent) = when (event) {
        is MarkListEvent.LoadMarkListByZone -> loadMarkList()
        is MarkListEvent.SetZoneId -> zoneId = event.zoneId
        is MarkListEvent.ChangeAttachment -> sendFileRequest(event.markId, event.bitmap)
        is MarkListEvent.ChangeMark -> updateMark(
            event.markId,
            event.comment,
            event.answer,
            event.pkd
        )
    }

    private fun updateMark(markId: Long, comment: String, answer: Float, pkd: String) {
        viewModelScope.launch(exceptionHandler) {
            updateMarkUseCase.get().run(markId, comment, answer, pkd)
        }
    }

    private fun sendFileRequest(markId: Long, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "photo[content]", "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )
        Timber.i("Размер изображения: ${byteArray.size}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                addPhotoToMarkUseCase.get().run(markId, byteArray)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun loadMarkList() {
        viewModelScope.launch(exceptionHandler) {
            getMarkListByZoneLazy.get().runWithCount(zoneId)
                .collectLatest { data ->
                    _listChannel.send(data)
                }
        }
    }
}