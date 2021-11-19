package com.glushko.winfox_test_task.data_layer.datasource



import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body

import retrofit2.http.POST

interface ApiService {
    companion object{
        const val CHECK_USER = "checkUser"
    }

    @POST(CHECK_USER)
    fun checkUser(@Body body: LoginData): Observable<LoginData>

}