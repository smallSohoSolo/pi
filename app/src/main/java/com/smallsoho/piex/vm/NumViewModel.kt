package com.smallsoho.piex.vm

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class NumViewModel : ViewModel() {

    companion object {
        const val STOP = 0
        const val START = 1
        const val PAUSE = 2
    }

    private var runningState = AtomicInteger(STOP)

    private val _calculateTimes = MutableLiveData<Long>(0)

    private val _piNumber = MutableLiveData<Long>(0)

    val calculateTimes: LiveData<String> = Transformations.map(_calculateTimes) {
        it.toString()
    }

    val piNumber: LiveData<String> = Transformations.map(_piNumber) {
        it.toString()
    }

    fun startCalculate() {
        if (runningState.get() == START) {
            return
        }
        runningState.set(START)
        GlobalScope.launch {
            resetNumState()
            while (runningState.get() == START) {
                getNextPi()
            }
            when (runningState.get()) {
                STOP -> {
                    resetNumState()
                }
                START, PAUSE -> {
                    return@launch
                }
            }
        }
    }

    fun stopCalculate() {
        if (runningState.get() != START) {
            resetNumStateMain()
        } else {
            runningState.set(STOP)
        }
    }

    fun pauseCalculate() {
        runningState.set(PAUSE)
    }

    @WorkerThread
    private fun resetNumState() {
        _piNumber.postValue(0L)
        _calculateTimes.postValue(0)
    }

    @MainThread
    private fun resetNumStateMain() {
        _piNumber.value = 0L
        _calculateTimes.value = 0
    }

    @WorkerThread
    private fun getNextPi() {
        _piNumber.postValue(_piNumber.value?.plus(1))
        _calculateTimes.postValue(_calculateTimes.value?.plus(1))
    }

}