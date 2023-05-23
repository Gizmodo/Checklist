package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class UpdateMarkUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(markId: Long, comment: String, answer: Float, pkd: String) =
        repository.updateMark(markId, comment, answer, pkd)
}