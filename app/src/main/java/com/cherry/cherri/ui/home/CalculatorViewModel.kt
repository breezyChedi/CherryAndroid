package com.cherry.cherri.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CalculatorViewModel : ViewModel() {

    private val _marks = MutableLiveData<List<Int>>(List(6) { 0 }) // Default marks
    val marks: LiveData<List<Int>> get() = _marks

    fun updateMark(index: Int, mark: Int) {
        val currentMarks = _marks.value?.toMutableList() ?: return
        currentMarks[index] = mark
        _marks.value = currentMarks
    }
}