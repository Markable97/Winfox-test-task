package com.glushko.winfox_test_task.business_logic_layer.interactor

import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.data_layer.datasource.NetworkService
import io.reactivex.Observable
import okhttp3.Response
import okhttp3.ResponseBody

class UseCase {

    fun checkUser(data: LoginData): Observable<LoginData>{
        return NetworkService.makeNetworkServiceRxJava().checkUser(data)
    }
}