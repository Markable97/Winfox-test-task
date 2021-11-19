package com.glushko.winfox_test_task.presentation_layer.vm

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glushko.winfox_test_task.business_logic_layer.domain.LoginData
import com.glushko.winfox_test_task.business_logic_layer.interactor.UseCase
import com.glushko.winfox_test_task.data_layer.datasource.response.ResponseServer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.concurrent.schedule
import java.util.concurrent.TimeUnit

class ViewModelLogin: ViewModel() {

    companion object {
        const val TIMEOUT = 120L
        private const val ONE_SECOND = 1000L
        private const val DONE = 0L
    }

    private val useCase by lazy { UseCase() }
    private var myCompositeDisposable: CompositeDisposable? = null
    private val _liveDataFirebaseTimeOut: MutableLiveData<Pair<Boolean,Long>> = MutableLiveData()
    val liveDataFirebaseTimeOut: LiveData<Pair<Boolean,Long>> = _liveDataFirebaseTimeOut
    private val _liveDataCheckUser: MutableLiveData<ResponseServer> = MutableLiveData()
    val liveDataCheckUser: LiveData<ResponseServer> = _liveDataCheckUser
    private lateinit var timer: CountDownTimer

    init {
        myCompositeDisposable = CompositeDisposable()
    }

    fun startTimer(timeout: Long){

        timer = object : CountDownTimer(timeout * ONE_SECOND, ONE_SECOND + 1000){
            override fun onTick(tick: Long) {
                _liveDataFirebaseTimeOut.value = false to tick / ONE_SECOND
            }

            override fun onFinish() {
                _liveDataFirebaseTimeOut.value = true to DONE
            }

        }.start()
    }

    fun stopTimer(){
        timer.cancel()
    }

    fun sendToServer(id: String, phoneNumber: String) {
        myCompositeDisposable?.addAll(
            useCase.checkUser(LoginData(phoneNumber,id))
                .delay(2L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handlerResponse, this::handleError)
        )
    }

    private fun handlerResponse(response: LoginData) {
        println("handlerResponse $response")
        _liveDataCheckUser.value = ResponseServer(true, response)
    }

    private fun handleError(err: Throwable) {
        println("handleError ${err.message}")
        _liveDataCheckUser.value = ResponseServer(false, LoginData("", ""))
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }




}