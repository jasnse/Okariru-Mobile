package com.project.binar.okariru.data.status_pinjaman.repository

import com.project.binar.okariru.core.database.dao.StatusPinjamanDao
import com.project.binar.okariru.core.database.entity.toDTO
import com.project.binar.okariru.core.database.entity.toEntity
import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.data.status_pinjaman.remote.StatusPinjamanApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class StatusPinjamanRepository internal constructor(
    private val api: StatusPinjamanApi,
    private val json: Json,
    private val dao: StatusPinjamanDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun getListPinjaman(customerId: Int, status: String? = null, keyword: String? = null): AppResult<List<ListPinjamanDto>> = withContext(ioDispatcher) {
        runApiCatching(json) {
            api.getListPinjaman(customerId, status, keyword).asAppResult()
        }
    }

    fun observeStatusPinjaman(customerId: Int): Flow<List<ListPinjamanDto>> =
        dao.observeList(customerId).map { list -> list.map { it.toDTO() } }

    suspend fun refreshStatusPinjaman(customerId: Int, status: String? = null, keyword: String? = null): AppResult<Unit> = withContext(ioDispatcher) {
        runApiCatching(json) {
            val items = api.getListPinjaman(customerId, status, keyword)
            dao.replaceAll(customerId, items.map { it.toEntity() })
            Unit.asAppResult()
        }
    }
}