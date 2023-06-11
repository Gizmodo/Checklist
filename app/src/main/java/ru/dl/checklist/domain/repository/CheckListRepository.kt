package ru.dl.checklist.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.AuthPayload
import ru.dl.checklist.domain.model.BackendResponseDomain
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.HouseChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.MarkDomainWithCount
import ru.dl.checklist.domain.model.ObjectDomain
import ru.dl.checklist.domain.model.TemplateDomain
import ru.dl.checklist.domain.model.UserDomain
import ru.dl.checklist.domain.model.ZoneDomain

interface CheckListRepository {
    fun getChecklists(): Flow<ApiResult<List<ChecklistDomain>>>
    fun getHouseChecklists(): Flow<ApiResult<List<HouseChecklistDomain>>>
    fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain>>
    fun getMarksByZone(zoneId: Long): Flow<List<MarkDomain>>
    fun getMarksByZoneWithCount(zoneId: Long): Flow<List<MarkDomainWithCount>>
    suspend fun updateMark(markId: Long, comment: String, answer: Float, pkd: String)
    suspend fun addPhoto(markId: Long, byteArray: ByteArray)
    fun uploadImages(uuid: String): Flow<ApiResult<BackendResponseDomain>>
    fun uploadMarks(uuid: String): Flow<ApiResult<BackendResponseDomain>>
    fun getChecklistTemplates(): Flow<ApiResult<List<TemplateDomain>>>
    fun getObjectsList(): Flow<ApiResult<List<ObjectDomain>>>
    fun getUsersList(): Flow<ApiResult<List<UserDomain>>>
    fun sendAuth(auth: AuthPayload): Flow<ApiResult<BackendResponseDomain>>
}