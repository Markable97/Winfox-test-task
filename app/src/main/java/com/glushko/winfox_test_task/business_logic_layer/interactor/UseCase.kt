package com.glushko.winfox_test_task.business_logic_layer.interactor

import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import com.glushko.winfox_test_task.data_layer.datasource.NetworkService
import com.glushko.winfox_test_task.data_layer.datasource.response.ResponseMenu
import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody

class UseCase {

    fun checkUser(data: LoginData): Observable<LoginData>{
        return NetworkService.makeNetworkServiceRxJava().checkUser(data)
    }

    fun getPlaces(): Observable<List<Place>>{
        return NetworkService.makeNetworkServiceRxJava().getPlaces()
    }

    fun getMenu(idPlace: String): Observable<List<Menu>>{
        return NetworkService.makeNetworkServiceRxJava().getMenu(ResponseMenu.createMap(idPlace))
    }
}