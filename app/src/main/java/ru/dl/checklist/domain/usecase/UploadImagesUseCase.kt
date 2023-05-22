package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class UploadImagesUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run(uuid: String) = repository.uploadImages(uuid)
}