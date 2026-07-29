package com.example.zzzpizzza.presentation.screen.splash

import com.example.zzzpizzza.presentation.base.mvvm.BaseLabel
import com.example.zzzpizzza.presentation.utils.StringResourceWrapper

sealed interface SplashLabel : BaseLabel {

    data class NetworkExceptionMessage(
        val message: StringResourceWrapper,
    ): SplashLabel
}
