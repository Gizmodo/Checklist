package ru.dl.checklist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.ZoneDomain

interface CheckListRepository {
    fun getChecklists(): Flow<ApiResult<List<ChecklistDomain>>>
    fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain>>
    fun getMarksByZone(zoneId: Long): Flow<List<MarkDomain>>
    suspend fun changeAnswer(markId: Long, answer: Answer)
    suspend fun changeComment(markId: Long, comment: String)
}