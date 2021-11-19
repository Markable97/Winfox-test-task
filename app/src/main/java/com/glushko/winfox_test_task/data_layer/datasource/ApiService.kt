package com.glushko.winfox_test_task.data_layer.datasource



import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.POST

interface ApiService {
    companion object{
        const val CHECK_USER = "checkUser"
        const val GET_PLACES = "getPlaces"
    }

    @POST(CHECK_USER)
    fun checkUser(@Body body: LoginData): Observable<LoginData>

    @GET(GET_PLACES)
    fun getPlaces(): Observable<List<Place>>

}