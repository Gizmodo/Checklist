package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class SetMarkAnswerUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(markId: Long, answer: Answer) =
        repository.changeAnswer(markId, answer)
}