package ru.dl.checklist.domain.usecase

import ru.dl.checklist.domain.repository.CheckListRepository
import javax.inject.Inject

class GetMarkListByZone @Inject constructor(
    private val repository: CheckListRepository
) {
    fun run(zoneId: Long) =
        repository.getMarksByZone(zoneId)

    fun runWithCount(zoneId: Long) =
        repository.getMarksByZoneWithCount(zoneId)
}