package com.smallsoho.piex.vm

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.atomic.AtomicBoolean

class PiViewModel : ViewModel() {

    private var _calculateTimes = MutableLiveData<BigDecimal>()
    private var isRunning = AtomicBoolean(false)

    private var _piNumber = MutableLiveData<Array<BigDecimal>>()

    var piNumber: LiveData<String> = Transformations.map(_piNumber) {
        it[0].divide(it[1], _calculateTimes.value!!.toInt(), RoundingMode.HALF_EVEN).toPlainString()
    }

    var calculateTimes: LiveData<String> = Transformations.map(_calculateTimes) {
        it.toPlainString()
    }

    init {
        _piNumber.value = arrayOf(BigDecimal.ZERO, BigDecimal.ONE)
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
        _piNumber.value = arrayOf(BigDecimal.ZERO, BigDecimal.ONE)
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
            _piNumber.postValue(arrayOf(BigDecimal(3), BigDecimal.ONE))
            return
        }
        val flag = if (_calculateTimes.value!! % BigDecimal(2) == BigDecimal.ONE) {
            BigDecimal(1)
        } else {
            BigDecimal(-1)
        }

        val temp = _calculateTimes.value!! * BigDecimal(2)

        _piNumber.postValue(
            fractionAdd(
                _piNumber.value!![0], _piNumber.value!![1],
                BigDecimal(4) * flag , temp * (temp + BigDecimal.ONE) * (temp + BigDecimal(2))
            )
        )
        _calculateTimes.postValue(_calculateTimes.value?.add(BigDecimal.ONE))
    }

    private fun fractionAdd(
        oneMolecule: BigDecimal, oneDenominator: BigDecimal,
        twoMolecule: BigDecimal, twoDenominator: BigDecimal
    ): Array<BigDecimal> {
        return arrayOf(
            oneMolecule * twoDenominator + twoMolecule * oneDenominator,
            oneDenominator * twoDenominator
        )
    }

    @TestOnly
    fun testPi() {
        getNextPi()
    }

}