package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class SetMarkCommentUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(markId: Long, comment: String) =
        repository.changeComment(markId, comment)
}