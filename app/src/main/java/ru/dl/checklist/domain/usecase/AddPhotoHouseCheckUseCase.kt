package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class AddPhotoHouseCheckUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(houseCheckId: Long, byteArray: ByteArray) =
        repository.addHousePhoto(houseCheckId, byteArray)
}