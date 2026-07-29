package com.example.zzzpizzza.presentation.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

private const val TOLERANCE = 1e-6

class MathUtilsTest {

    @Test
    fun `distributePoints returns no points when count is zero`() {
        val pointList = distributePoints(0)

        assertThat(pointList).isEmpty()
    }

    @Test
    fun `distributePoints returns center point when count is one`() {
        val pointList = distributePoints(1)

        assertThat(pointList).containsExactly(0.5f)
    }

    @Test
    fun `distributePoints spreads points around center with default spread when count is three`() {
        val pointList = distributePoints(3)

        assertThat(pointList.toFloatArray())
            .usingTolerance(TOLERANCE)
            .containsExactly(0.237f, 0.5f, 0.763f)
            .inOrder()
    }
}
