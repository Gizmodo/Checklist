package ru.dl.checklist.domain.usecase

import ru.dl.checklist.data.source.cache.CheckListRepository
import javax.inject.Inject

class GetChecklistUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run() = repository.getCheckList()
}