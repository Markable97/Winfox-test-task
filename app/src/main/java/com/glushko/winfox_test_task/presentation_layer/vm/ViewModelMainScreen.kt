package com.glushko.winfox_test_task.presentation_layer.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import com.glushko.winfox_test_task.business_logic_layer.interactor.UseCase
import com.glushko.winfox_test_task.data_layer.datasource.response.ResponsePlaces
import com.glushko.winfox_test_task.data_layer.datasource.response.ResponseServer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ViewModelMainScreen: ViewModel() {
    private val useCase by lazy { UseCase() }
    private var myCompositeDisposable: CompositeDisposable? = null
    private val _liveDataPlaces: MutableLiveData<ResponsePlaces> = MutableLiveData()
    val liveDataPlace = _liveDataPlaces
    init {
        myCompositeDisposable = CompositeDisposable()
    }

    fun getPlaces(){
        myCompositeDisposable?.addAll(
            useCase.getPlaces()
                .delay(5L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerResponsePlaces, this::handleErrorPlaces)
        )
    }

    private fun handlerResponsePlaces(response: List<Place>) {
        _liveDataPlaces.value = ResponsePlaces(true, response)
    }

    private fun handleErrorPlaces(err: Throwable) {
        println("handleError ${err.message}")
        _liveDataPlaces.value = ResponsePlaces(true)
    }

}