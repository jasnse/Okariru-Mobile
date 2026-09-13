package com.project.binar.okariru.data.plafond.repository

import com.project.binar.okariru.core.database.dao.PlafondDao
import com.project.binar.okariru.core.database.entity.toDTO
import com.project.binar.okariru.core.database.entity.toEntity
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.plafond.dto.PlafondDto
import com.project.binar.okariru.data.plafond.remote.PlafondApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PlafondRepository internal constructor(
    private val api: PlafondApi,
    private val json: Json,
    private val dao: PlafondDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun getPlafond(userId: Int): AppResult<PlafondDto> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.getPlafond(userId).asAppResult()
        }
    }

    fun observePlafond(userId: Int): Flow<PlafondDto?> =
        dao.observeList(userId).map { it.firstOrNull()?.toDTO() }

    suspend fun refreshPlafond(userId: Int): AppResult<Unit> = withContext(ioDispatcher) {
        runApiCatching(json) {
            val dto = api.getPlafond(userId)
            dao.replaceAll(listOf(dto.toEntity()))
            Unit.asAppResult()
        }
    }
}
