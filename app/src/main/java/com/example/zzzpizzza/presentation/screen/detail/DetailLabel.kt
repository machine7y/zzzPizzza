package com.example.zzzpizzza.presentation.screen.detail

import com.example.zzzpizzza.presentation.base.mvvm.BaseLabel

sealed interface DetailLabel : BaseLabel {

    data object InternalLoadingException : DetailLabel
}
