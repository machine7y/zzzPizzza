package com.example.zzzpizzza.presentation.screen.splash

import com.example.zzzpizzza.presentation.base.mvvm.BaseEvent

sealed class SplashEvent : BaseEvent {

    data object OnAnimationEnd: SplashEvent()
}
