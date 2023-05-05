package ru.dl.checklist.data.source.cache

import kotlinx.coroutines.CoroutineDispatcher
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.data.source.cache.ProtocolDao
import ru.dl.checklist.data.source.cache.ProtocolsRepository
import javax.inject.Inject

class ProtocolsRepositoryImpl @Inject constructor(
    private val protocolDao: ProtocolDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ProtocolsRepository {
    fun test() {
        val data = protocolDao.selectAll()
    }
}