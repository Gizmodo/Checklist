package ru.dl.checklist.core.injection

import kotlinx.coroutines.CoroutineDispatcher
import ru.dl.checklist.di.module.IoDispatcher
import javax.inject.Inject

class ProtocolsRepositoryImpl @Inject constructor(
    private val protocolDao: ProtocolDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ProtocolsRepository {
    fun test() {
        val data = protocolDao.selectAll()
    }
}