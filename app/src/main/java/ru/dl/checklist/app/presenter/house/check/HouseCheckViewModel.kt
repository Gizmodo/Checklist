package ru.dl.checklist.app.presenter.house.check

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.dl.checklist.app.app.App
import ru.dl.checklist.domain.model.HouseCheckDomain
import ru.dl.checklist.domain.usecase.AddPhotoHouseCheckUseCase
import ru.dl.checklist.domain.usecase.GetHouseChecksUseCase
import ru.dl.checklist.domain.usecase.UpdateHouseAnswerUseCase
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class HouseCheckViewModel : ViewModel(), HouseChecksContract {
    var uuidArgs: String = ""
    private val mutableState = MutableStateFlow(HouseChecksContract.State())
    override val state: StateFlow<HouseChecksContract.State>
        get() = mutableState.asStateFlow()
    private val effectFlow = MutableSharedFlow<HouseChecksContract.Effect>()
    override val effect: SharedFlow<HouseChecksContract.Effect>
        get() = effectFlow.asSharedFlow()

    private fun isNextEnabled(
        isPhotoRequired: Boolean,
        mediaCount: Int,
        isHasAnswer: Boolean?
    ) =
        if (isPhotoRequired) mediaCount > 0 && isHasAnswer != null else isHasAnswer != null

    override fun event(event: HouseChecksContract.Event) {
        when (event) {
            is HouseChecksContract.Event.OnItemClick -> TODO()
            HouseChecksContract.Event.OnRefresh -> getData(uuidArgs)
            HouseChecksContract.Event.OnSend -> sendHouseChecklist(uuidArgs)
            is HouseChecksContract.Event.OnChangeSlide -> {
                when (event.position) {
                    0 -> {
                        mutableState.update {
                            it.copy(
                                isNextVisible = true,
                                isPrevEnabled = false,
                                isSendVisible = false,
                                isNextEnabled = isNextEnabled(
                                    event.item.isPhotoRequired,
                                    mutableState.value.list[event.position].mediacount,
                                    mutableState.value.list[event.position].answer
                                )
                            )
                        }
                    }

                    mutableState.value.list.size - 1 -> {
                        mutableState.update {
                            it.copy(
                                isNextVisible = false,
                                isPrevEnabled = true,
                                isSendVisible = true,
                                isNextEnabled = isNextEnabled(
                                    event.item.isPhotoRequired,
                                    mutableState.value.list[event.position].mediacount,
                                    mutableState.value.list[event.position].answer
                                ),
                            )
                        }
                    }

                    else -> {
                        mutableState.update {
                            it.copy(
                                isNextVisible = true,
                                isPrevEnabled = true,
                                isSendVisible = false,
                                isNextEnabled = isNextEnabled(
                                    event.item.isPhotoRequired,
                                    mutableState.value.list[event.position].mediacount,
                                    mutableState.value.list[event.position].answer
                                )
                            )
                        }
                    }
                }
            }

            is HouseChecksContract.Event.OnNoClick -> {
                updateAnswer(event.item, false)
            }

            is HouseChecksContract.Event.OnYesClick -> {
                updateAnswer(event.item, true)
            }

            is HouseChecksContract.Event.OnChangeAttachment -> {
                sendFileRequest(
                    event.houseCheckId,
                    event.bitmap
                )
            }

            is HouseChecksContract.Event.OnCheckPhoto -> {
                val res = isNextEnabled(
                    event.item.isPhotoRequired,
                    event.item.mediacount,
                    event.item.answer
                )
                mutableState.update { it.copy(isNextEnabled = res) }
            }
        }
    }

    private fun sendFileRequest(houseCheckId: Long, image: Bitmap) {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val body = MultipartBody.Part.createFormData(
            "photo[content]", "photo",
            byteArray.toRequestBody("image/*".toMediaTypeOrNull(), 0, byteArray.size)
        )
//        Timber.i("Размер изображения: ${byteArray.size}")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Timber.i("Попытка вставить изображение в родительский Check с ID=${houseCheckId}")
                addPhotoHouseCheckUseCase.get().run(houseCheckId, byteArray)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun updateAnswer(item: HouseCheckDomain, answer: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            Timber.i("Обновляем check по id ${item.id}")
            updateHouseAnswerUseCase.get().run(item.id, answer)
        }
    }

    private fun sendHouseChecklist(uuidArgs: String) {
        Timber.i("Отправка чеклиста")
    }

    init {
        App.appComponent.inject(this)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

    @Inject
    lateinit var getHouseChecksUseCase: dagger.Lazy<GetHouseChecksUseCase>

    @Inject
    lateinit var updateHouseAnswerUseCase: dagger.Lazy<UpdateHouseAnswerUseCase>

    @Inject
    lateinit var addPhotoHouseCheckUseCase: dagger.Lazy<AddPhotoHouseCheckUseCase>
    private fun getData(uuid: String) {
        viewModelScope.launch { getHouseChecksList(uuid) }
    }

    private suspend fun getHouseChecksList(uuid: String) = getHouseChecksUseCase.get().run(uuid)
        .catch { exception ->
            effectFlow.emit(
                HouseChecksContract.Effect.ShowMessage(
                    message = exception.localizedMessage ?: "An unexpected error occurred."
                )
            )
        }
        .onEach { result ->
            mutableState.update { it.copy(list = result) }
        }
        .launchIn(viewModelScope)
}