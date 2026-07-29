package com.example.zzzpizzza.presentation.screen.splash

import com.example.zzzpizzza.presentation.base.mvvm.BaseInternalState

data class SplashInternalState(
    val isAnimationFinished: Boolean = false,
    val isDataLoaded: Boolean = false,
) : BaseInternalState
