package com.sinyuk.compose.foundation.animation

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.*
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

/**
 * Created by Sinyuk on 2022/11/21.
 */

interface RotatableState {
    suspend fun rotate(
        rotatePriority: MutatePriority = MutatePriority.Default,
        block: suspend RotateScope.() -> Unit
    )

    fun dispatchRawDelta(delta: Float): Float


    val isRotateInProgress: Boolean
}
interface RotateScope {

    fun rotateBy(pixels: Float): Float
}

private class DefaultRotatableState(val onDelta: (Float) -> Float) : RotatableState {

    private val isRotatingState = mutableStateOf(false)


    override suspend fun rotate(
        rotatePriority: MutatePriority,
        block: suspend RotateScope.() -> Unit
    ) : Unit = coroutineScope {

    }
    override fun dispatchRawDelta(delta: Float): Float {
        TODO("Not yet implemented")
    }

    override val isRotateInProgress = isRotatingState.value

}

//@Stable
//class RotateState(initial: Float, override val isRotateInProgress: Boolean) : RotatableState {

//    var value: Float by mutableStateOf(initial, structuralEqualityPolicy())
//        private set
//
//
//    override suspend fun rotate(
//        scrollPriority: MutatePriority,
//        block: suspend ScrollScope.() -> Unit
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun dispatchRawDelta(delta: Float): Float {
//        TODO("Not yet implemented")
//    }
//
//    private var accumulator: Float = 0f
//
//    private val rotatableState = RotatableState {
//
//    }

//}