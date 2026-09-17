package com.project.binar.okariru.data.angsuran.repository

import com.project.binar.okariru.core.network.AppResult
import com.project.binar.okariru.core.network.asAppResult
import com.project.binar.okariru.core.network.runApiCatching
import com.project.binar.okariru.data.angsuran.dto.AngsuranBayarRequest
import com.project.binar.okariru.data.angsuran.dto.AngsuranBayarResponse
import com.project.binar.okariru.data.angsuran.dto.AngsuranDto
import com.project.binar.okariru.data.angsuran.remote.AngsuranApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AngsuranRepository internal constructor(
    private val api: AngsuranApi,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun getAngsuranForCustomer(transPinjamanId: Int): AppResult<List<AngsuranDto>> =
        withContext(ioDispatcher) {
        runApiCatching(json) {
            api.getByTransPinjamanId(transPinjamanId).asAppResult()
        }
    }

    suspend fun bayarAngsuran(transPinjamanId: Int, nominalBayar: Int): AppResult<AngsuranBayarResponse> =
        withContext(ioDispatcher) {
            runApiCatching(json) {
                api.bayarAngsuran(AngsuranBayarRequest(transPinjamanId, nominalBayar)).asAppResult()
            }
        }
}