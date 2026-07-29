package com.example.zzzpizzza.presentation.utils

import android.content.Context
import androidx.annotation.StringRes

sealed interface StringResourceWrapper {

    data class ResourceWrapper(
        @field:StringRes val id: Int,
    ): StringResourceWrapper
}

fun StringResourceWrapper.asString(context: Context): String = when (this) {
    is StringResourceWrapper.ResourceWrapper -> context.getString(id)
}
