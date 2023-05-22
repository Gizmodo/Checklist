package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class AddPhotoToMarkUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(markId: Long, byteArray: ByteArray) =
        repository.addPhoto(markId, byteArray)
}