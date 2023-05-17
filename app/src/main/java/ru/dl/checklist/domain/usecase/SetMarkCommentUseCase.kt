package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class SetMarkCommentUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    suspend fun run(mark: MarkDomain) =
        repository.changeComment(mark)
}