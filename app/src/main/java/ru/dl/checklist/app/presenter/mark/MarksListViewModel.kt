package ru.dl.checklist.app.presenter.mark

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.dl.checklist.app.app.App
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.usecase.GetMarkListByZone
import ru.dl.checklist.domain.usecase.SetMarkAnswerUseCase
import ru.dl.checklist.domain.usecase.SetMarkCommentUseCase
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class MarksListViewModel : ViewModel() {
    init {
        App.appComponent.inject(this)
    }

    private var zoneId: Long = 0
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }
    private val _listChannel = Channel<List<MarkDomain>>()
    val markListEvent = _listChannel.receiveAsFlow()

    @Inject
    lateinit var getMarkListByZoneLazy: dagger.Lazy<GetMarkListByZone>

    @Inject
    lateinit var setMarkAnswerUseCase: dagger.Lazy<SetMarkAnswerUseCase>

    @Inject
    lateinit var setMarkCommentUseCase: dagger.Lazy<SetMarkCommentUseCase>
    fun onEvent(event: MarkListEvent) = when (event) {
        is MarkListEvent.LoadMarkListByZone -> loadMarkList()
        is MarkListEvent.ChangeAnswer -> updateMark(event.mark)
        is MarkListEvent.SetZoneId -> zoneId = event.zoneId
        is MarkListEvent.ChangeComment -> updateComment(event.mark)
    }

    private fun updateComment(mark: MarkDomain) {
        viewModelScope.launch(exceptionHandler) {
            setMarkCommentUseCase.get().run(mark)
            loadMarkList()
        }
    }

    private fun updateMark(mark: MarkDomain) {
        viewModelScope.launch(exceptionHandler) {
            setMarkAnswerUseCase.get().run(mark)
            //loadMarkList()
        }
    }

    fun sendFileRequest(image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "photo[content]", "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                //questGoApi.sendFile(body)
            } catch (e: java.lang.Exception) {

            }
        }
    }

    private fun loadMarkList() {
        viewModelScope.launch(exceptionHandler) {
            getMarkListByZoneLazy.get().run(zoneId).collectLatest { data ->
                _listChannel.send(data)
            }
        }
    }
}