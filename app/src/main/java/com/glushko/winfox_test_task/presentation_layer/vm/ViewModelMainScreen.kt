package com.glushko.winfox_test_task.presentation_layer.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import com.glushko.winfox_test_task.business_logic_layer.interactor.UseCase
import com.glushko.winfox_test_task.data_layer.datasource.response.ResponseMenu
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
    val liveDataPlace: LiveData<ResponsePlaces> = _liveDataPlaces
    private val _liveDataMenu: MutableLiveData<ResponseMenu> = MutableLiveData()
    val liveDataMenu: LiveData<ResponseMenu> = _liveDataMenu
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
        _liveDataPlaces.value = ResponsePlaces(true)
    }

    fun getMenu(idPlace: String){
        myCompositeDisposable?.addAll(
            useCase.getMenu(idPlace)
                .delay(5L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerResponseMenu, this::handleErrorMenu)
        )
    }

    private fun handlerResponseMenu(response: List<Menu>) {
        println(response)
        _liveDataMenu.value = ResponseMenu(true, response)
    }

    private fun handleErrorMenu(err: Throwable) {
        println(err.message)
        _liveDataMenu.value = ResponseMenu(false)
    }

    override fun onCleared() {
        super.onCleared()
        myCompositeDisposable?.let {
            it.clear()
        }
    }


}