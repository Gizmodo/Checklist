package ru.dl.checklist.data.source.cache

import kotlinx.coroutines.flow.Flow
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.domain.model.ChecklistDomain

interface CheckListRepository {
    fun getCheckList(): Flow<ApiResult<ChecklistDomain>>
}