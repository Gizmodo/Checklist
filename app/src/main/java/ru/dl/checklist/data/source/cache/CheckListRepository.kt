package ru.dl.checklist.data.source.cache

import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.ChecklistsDomain

interface CheckListRepository {
    fun getCheckList(): Flow<ApiResult<ChecklistsDomain>>
}