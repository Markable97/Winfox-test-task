package com.glushko.winfox_test_task.presentation_layer.vm

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelLogin: ViewModel() {

    companion object {
        private const val ONE_SECOND = 1000L
        private const val DONE = 0L
    }

    private val _liveDataFirebaseTimeOut: MutableLiveData<Pair<Boolean,Long>> = MutableLiveData()
    val liveDataFirebaseTimeOut: LiveData<Pair<Boolean,Long>> = _liveDataFirebaseTimeOut
    private lateinit var timer: CountDownTimer

    fun startTimer(timeout: Long){

        timer = object : CountDownTimer(timeout, ONE_SECOND){
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

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

}