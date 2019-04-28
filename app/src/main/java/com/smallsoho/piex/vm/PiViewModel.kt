package com.smallsoho.piex.vm

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicBoolean

class PiViewModel : ViewModel() {

    private var _calculateTimes = MutableLiveData<BigDecimal>()
    private var isRunning = AtomicBoolean(false)

    private var _piNumber = MutableLiveData<BigDecimal>()

    var piNumber: LiveData<String> = Transformations.map(_piNumber) {
        it.toPlainString()
    }

    var calculateTimes: LiveData<String> = Transformations.map(_calculateTimes) {
        it.toPlainString()
    }

    init {
        _piNumber.value = BigDecimal.ZERO
        _calculateTimes.value = BigDecimal.ZERO
    }

    fun startCalculate() {
        if (isRunning.get()) {
            return
        }
        GlobalScope.launch {
            isRunning.set(true)
            while (isRunning.get()) {
                getNextPi()
            }
        }
    }

    fun stopCalculate() {
        isRunning.set(false)
        _piNumber.value = BigDecimal.ZERO
        _calculateTimes.value = BigDecimal.ZERO
    }

    fun pauseCalculate() {
        isRunning.set(false)
    }

    override fun onCleared() {
        isRunning.set(false)
    }

    @WorkerThread
    private fun getNextPi() {
        if (_calculateTimes.value == BigDecimal.ZERO) {
            _calculateTimes.postValue(_calculateTimes.value?.add(BigDecimal.ONE))
            _piNumber.postValue(BigDecimal(3))
            return
        }
        var start = BigDecimal.ONE
        var number = BigDecimal(3)
        while (start <= _calculateTimes.value) {
            val flag = if (start % BigDecimal(2) == BigDecimal.ONE) {
                BigDecimal(1)
            } else {
                BigDecimal(-1)
            }
            number += flag * BigDecimal(4).divide(
                (start * BigDecimal(2) * (start * BigDecimal(2) + BigDecimal(
                    1
                )) * (start * BigDecimal(2) + BigDecimal(
                    2
                ))), _calculateTimes.value!!.toInt(), RoundingMode.HALF_UP
            )
            start++
        }
        _piNumber.postValue(number)
        _calculateTimes.postValue(_calculateTimes.value?.add(BigDecimal.ONE))
    }

}