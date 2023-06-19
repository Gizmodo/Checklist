package ru.dl.checklist.data.repository

import com.skydoves.sandwich.getOrElse
import com.skydoves.sandwich.mapSuccess
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.app.ext.RetrofitHandler.errorHandler
import ru.dl.checklist.app.ext.RetrofitHandler.exceptionHandler
import ru.dl.checklist.app.ext.whenNotNullNorEmpty
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.Constants.currentUser
import ru.dl.checklist.app.utils.hasOnlyOnePath
import ru.dl.checklist.data.mapper.DtoToDomainMapper.toDomain
import ru.dl.checklist.data.mapper.DtoToEntityMapper.toEntity
import ru.dl.checklist.data.mapper.EntityToDomainMapper.toDomain
import ru.dl.checklist.data.model.entity.HouseMediaEntity
import ru.dl.checklist.data.model.entity.MediaEntity
import ru.dl.checklist.data.model.remote.ReadyChecklist
import ru.dl.checklist.data.source.cache.ChecklistDao
import ru.dl.checklist.data.source.cache.HouseCheckDao
import ru.dl.checklist.data.source.cache.HouseChecklistDao
import ru.dl.checklist.data.source.cache.HouseMediaDao
import ru.dl.checklist.data.source.cache.MarkDao
import ru.dl.checklist.data.source.cache.MediaDao
import ru.dl.checklist.data.source.cache.ZoneDao
import ru.dl.checklist.data.source.remote.RemoteApi
import ru.dl.checklist.domain.model.AuthPayload
import ru.dl.checklist.domain.model.BackendResponseDomain
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.HouseCheckDomain
import ru.dl.checklist.domain.model.HouseChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.MarkDomainWithCount
import ru.dl.checklist.domain.model.ObjectDomain
import ru.dl.checklist.domain.model.TemplateDomain
import ru.dl.checklist.domain.model.UserDomain
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.repository.CheckListRepository
import timber.log.Timber
import javax.inject.Inject

class CheckListRepositoryImpl @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val zoneDao: ZoneDao,
    private val markDao: MarkDao,
    private val mediaDao: MediaDao,
    private val houseChecklistDao: HouseChecklistDao,
    private val houseCheckDao: HouseCheckDao,
    private val houseMediaDao: HouseMediaDao,
    private val remoteDataSource: RemoteApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CheckListRepository {

    override fun getChecklists(): Flow<ApiResult<List<ChecklistDomain>>> = flow {
        val myScope = CoroutineScope(Dispatchers.IO)
        val response = remoteDataSource.getChecklist(currentUser)
        response.suspendOnSuccess {
            var scopeResult: ApiResult<List<ChecklistDomain>> = ApiResult.Loading
            val job = myScope.launch {
                try {
                    data.checklists.whenNotNullNorEmpty { list ->
                        list.forEach { checklist ->
                            checklistDao.getByUUID(checklist.uuid.toString()).whatIfNotNull(
                                whatIf = { },
                                whatIfNot = {
                                    val newChecklistId =
                                        checklistDao.insert(checklist.toEntity())
                                    checklist.zones.whenNotNullNorEmpty { zoneList ->
                                        zoneList.forEach { zone ->
                                            val newZoneId =
                                                zoneDao.insert(zone.toEntity(newChecklistId))
                                            zone.marks.whenNotNullNorEmpty { markList ->
                                                markList.map { it.toEntity(newZoneId) }
                                                    .onEach { markDao.insert(it) }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    val checklist = checklistDao.getAll()
                    scopeResult = ApiResult.Success(checklist)
                } catch (e: Exception) {
                    Timber.e("An error occurred: " + e.message)
                    scopeResult = ApiResult.Error(e.message.toString())
                }
            }
            job.join()
            emit(scopeResult)
        }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getHouseChecklists(): Flow<ApiResult<List<HouseChecklistDomain>>> = flow {
        val myScope = CoroutineScope(dispatcher)
        val response = remoteDataSource.getHouseChecklists(currentUser)
        response.suspendOnSuccess {
            var scopeResult: ApiResult<List<HouseChecklistDomain>> = ApiResult.Loading
            val job = myScope.launch {
                try {
                    data.checklistsHouse.whenNotNullNorEmpty { list ->
                        list.forEach { houseChecklistItem ->
                            houseChecklistDao.getByUUID(houseChecklistItem.uuid.toString())
                                .whatIfNotNull(
                                    whatIf = {},
                                    whatIfNot = {
                                        houseChecklistItem.checks.whenNotNullNorEmpty { checkList ->
                                            val checkListDFS = checkList.map { it.toDomain() }
                                            val checkResult = hasOnlyOnePath(checkListDFS)
                                            if (checkResult) {
                                                val newHouseChecklistId =
                                                    houseChecklistDao.insert(houseChecklistItem.toEntity())
                                                houseChecklistItem.checks.whenNotNullNorEmpty { checksList ->
                                                    checksList.map {
                                                        it.toEntity(newHouseChecklistId)
                                                    }.onEach { houseCheckDao.insert(it) }
                                                }
                                            }
                                        }
                                    }
                                )
                        }
                    }
                    val houseCheckList = houseChecklistDao.getAll().map { it.toDomain() }
                    scopeResult = ApiResult.Success(houseCheckList)
                } catch (e: Exception) {
                    Timber.e("An error occurred: " + e.message)
                    scopeResult = ApiResult.Error(e.message.toString())
                }
            }
            job.join()
            emit(scopeResult)
        }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }
        .onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getHouseChecks(uuid: String): Flow<List<HouseCheckDomain>> {
        val inter = houseCheckDao.getHouseChecksByUUID(uuid)
        return inter.flowOn(dispatcher)
    }

    override fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain>> {
        val inter = zoneDao.getZoneListByChecklist(uuid)
        return inter.flowOn(dispatcher)
    }

    override fun getMarksByZone(zoneId: Long): Flow<List<MarkDomain>> {
        val inter = markDao.getMarkListByZone(zoneId)
        val interMap = inter.map { list ->
            list.map { it.toDomain() }
        }
        return interMap.flowOn(dispatcher)
    }

    override fun getMarksByZoneWithCount(zoneId: Long): Flow<List<MarkDomainWithCount>> =
        markDao.getMarkListByZoneWithCount(zoneId).flowOn(dispatcher)

    override suspend fun updateMark(markId: Long, comment: String, answer: Float, pkd: String) {
        withContext(dispatcher) { markDao.updateMark(markId, comment, answer, pkd) }
    }

    override suspend fun updateHouseAnswer(houseCheckId: Long, answer: Boolean) {
        withContext(dispatcher) { houseCheckDao.updateCheck(houseCheckId, answer) }
    }

    override suspend fun addPhoto(markId: Long, byteArray: ByteArray) {
        withContext(dispatcher) {
            val mediaEntity = MediaEntity(markId = markId, media = byteArray)
            val res = mediaDao.insert(mediaEntity)
            Timber.i("mediaId: $res")
        }
    }

    override suspend fun addHousePhoto(houseCheckId: Long, byteArray: ByteArray) {
        withContext(dispatcher) {
            val houseMediaEntity = HouseMediaEntity(houseCheckId = houseCheckId, media = byteArray)
            val res = houseMediaDao.insert(houseMediaEntity)
        }
    }

    override fun uploadImages(uuid: String) = flow {
        val mediaList = markDao.getMediaListByChecklist(uuid)
        when {
            mediaList.isEmpty() -> {
                emit(ApiResult.Error("Фотографии отсутствуют"))
            }

            else -> {
                val parts = mutableListOf<MultipartBody.Part>()
                mediaList.forEach { media ->
                    val mediaRequestBody =
                        media.media.toRequestBody(
                            "image/jpeg".toMediaTypeOrNull(),
                            0,
                            media.media.size
                        )
                    val mediaPart =
                        MultipartBody.Part.createFormData("media", "image.jpg", mediaRequestBody)
                    parts.add(mediaPart)

                    val uuidRequestBody =
                        media.uuid.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val uuidPart =
                        MultipartBody.Part.createFormData("uuid", uuidRequestBody.toString())
                    parts.add(uuidPart)
                }

                val response = remoteDataSource.uploadImages(parts)
                response.suspendOnSuccess {
                    val res = response.mapSuccess {
                        BackendResponseDomain(
                            this.message ?: "Пустое поле message",
                            this.result ?: false
                        )
                    }.getOrElse(BackendResponseDomain("Ошибка маппинга ответа", false))
                    when {
                        res.result -> {
                            emit(ApiResult.Success(res))
                        }

                        else -> {
                            emit(ApiResult.Error(res.message))
                        }
                    }
                }
                    .suspendOnError { emit(errorHandler(this)) }
                    .suspendOnException { emit(exceptionHandler(exception)) }
            }
        }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getChecklistTemplates(): Flow<ApiResult<List<TemplateDomain>>> = flow {
        val response = remoteDataSource.getTemplates()
        response
            .suspendOnSuccess {
                val mapped = this.data.templates?.map { it.toDomain() }
                emit(ApiResult.Success(mapped ?: emptyList()))
            }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getObjectsList(): Flow<ApiResult<List<ObjectDomain>>> = flow {
        val response = remoteDataSource.getCheckedObjects()
        response
            .suspendOnSuccess {
                val mapped = this.data.objects?.map { it.toDomain() }
                emit(ApiResult.Success(mapped ?: emptyList()))
            }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getUsersList(): Flow<ApiResult<List<UserDomain>>> = flow {
        val response = remoteDataSource.getUsersList()
        response.suspendOnSuccess {
            val mapped = this.data.users?.map { it.toDomain() }
            emit(ApiResult.Success(mapped ?: emptyList()))
        }.suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun sendAuth(auth: AuthPayload): Flow<ApiResult<BackendResponseDomain>> = flow {
        val response = remoteDataSource.postAuth(auth)
        response.suspendOnSuccess {
            val res = response.mapSuccess {
                BackendResponseDomain(
                    this.message ?: "Пустое поле message",
                    this.result ?: false
                )
            }.getOrElse(BackendResponseDomain("Ошибка маппинга ответа", false))
            if (res.result) emit(ApiResult.Success(res))
            else emit(ApiResult.Error(res.message))
        }.suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun uploadMarks(uuid: String) = flow {
        val marks = markDao.getMarkListByChecklist(uuid)
        val response =
            remoteDataSource.uploadMarks(ReadyChecklist(uuid, marks))
        response.suspendOnSuccess {
            val res = response.mapSuccess {
                BackendResponseDomain(
                    this.message ?: "Пустое поле message",
                    this.result ?: false
                )
            }.getOrElse(BackendResponseDomain("Ошибка маппинга ответа", false))
            when {
                res.result -> {
                    emit(ApiResult.Success(res))
                }

                else -> {
                    emit(ApiResult.Error(res.message))
                }
            }
        }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)
}