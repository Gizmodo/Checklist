package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class GetChecklistByAddressUseCase @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run(address: String) = repository.getChecklistsByAddress(address)
}