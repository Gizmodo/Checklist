package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class UpdateHouseAnswerUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(houseCheckId: Long, answer: Boolean) =
        repository.updateHouseAnswer(houseCheckId, answer)
}