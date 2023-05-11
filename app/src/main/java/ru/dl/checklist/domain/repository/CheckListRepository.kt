package ru.dl.checklist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.ChecklistsDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.model.ZoneDomain2

interface CheckListRepository {
    fun getCheckList(): Flow<ApiResult<ChecklistsDomain>>
    fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain2>>
}