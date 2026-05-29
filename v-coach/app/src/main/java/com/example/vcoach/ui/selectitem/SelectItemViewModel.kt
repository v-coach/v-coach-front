package com.example.vcoach.ui.selectitem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vcoach.data.local.AppDatabase
import com.example.vcoach.data.local.FoodEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SelectItemViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val foodDao = AppDatabase.getInstance(application).foodDao()

    val foodItems: StateFlow<List<FoodEntity>> = foodDao.observeFoods()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )
}
