package com.example.zzzpizzza.presentation.screen.detail

import com.example.zzzpizzza.domain.entity.Pizza
import com.example.zzzpizzza.presentation.base.mvvm.BaseInternalState

data class DetailInternalState(
    val pizzaList: List<Pizza> = emptyList(),
) : BaseInternalState
