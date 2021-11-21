package com.glushko.winfox_test_task.data_layer.datasource



import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    companion object{
        //Methods
        const val CHECK_USER = "checkUser"
        const val GET_PLACES = "getPlaces"
        const val GET_MENU = "getMenu"
        //Params
        const val PARAM_PLACE_ID = "place_id"
    }

    @POST(CHECK_USER)
    fun checkUser(@Body body: LoginData): Observable<LoginData>

    @GET(GET_PLACES)
    fun getPlaces(): Observable<List<Place>>

    @FormUrlEncoded
    @POST(GET_MENU)
    fun getMenu(@FieldMap params: Map<String, String>): Observable<List<Menu>>



}