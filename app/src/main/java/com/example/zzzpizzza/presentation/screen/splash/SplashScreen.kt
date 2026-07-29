package com.example.zzzpizzza.presentation.screen.splash

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zzzpizzza.R
import com.example.zzzpizzza.presentation.components.PizzaCircleAnimation
import com.example.zzzpizzza.presentation.screen.splash.SplashEvent.OnAnimationEnd
import com.example.zzzpizzza.presentation.screen.splash.SplashLabel.NetworkExceptionMessage
import com.example.zzzpizzza.presentation.utils.asString

private const val PIZZA_ASPECT_RATIO = 1f

private val PIZZA_PADDING = 52.dp

@Composable
fun SplashScreen() {
    val viewModel = hiltViewModel<SplashViewModel>()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val image = ImageBitmap.imageResource(R.drawable.img_pizza)

    LaunchedEffect(Unit) {
        viewModel.labelFlow.collect { label ->
            when (label) {
                is NetworkExceptionMessage -> {
                    Toast.makeText(context, label.message.asString(context), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    SplashContent(
        state = state,
        image = image,
        onAnimationEnd = { viewModel.onEvent(OnAnimationEnd) },
    )
}

@Composable
fun SplashContent(state: SplashState, image: ImageBitmap, onAnimationEnd: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(PIZZA_PADDING),
    ) {
        PizzaCircleAnimation(
            image = image,
            startAnimation = state.isAnimatePizza,
            onAnimationEnd = onAnimationEnd,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PIZZA_ASPECT_RATIO),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SplashContent(
        state = SplashState(isAnimatePizza = true),
        image = ImageBitmap.imageResource(R.drawable.img_pizza),
        onAnimationEnd = { },
    )
}