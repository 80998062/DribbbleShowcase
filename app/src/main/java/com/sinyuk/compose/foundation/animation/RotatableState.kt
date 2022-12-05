package com.sinyuk.compose.foundation.animation

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.debugInspectorInfo
import com.sinyuk.compose.dribbbleshowcase.playbutton.rotateBy
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Created by Sinyuk on 2022/11/21.
 */

interface RotatableState {
    suspend fun rotate(
        rotatePriority: MutatePriority = MutatePriority.Default,
        block: suspend RotateScope.() -> Unit
    )

    val isRotateInProgress: Boolean
}

interface RotateScope {

    fun rotateBy(pixels: Float): Float
}

private class DefaultRotatableState(val onDelta: (Float) -> Float) : RotatableState {

    private val isRotatingState = mutableStateOf(false)
    private val rotateMutex = MutatorMutex()

    private val rotateScope: RotateScope = object : RotateScope {
        override fun rotateBy(pixels: Float): Float = onDelta(pixels)

    }

    override suspend fun rotate(
        rotatePriority: MutatePriority,
        block: suspend RotateScope.() -> Unit
    ): Unit = coroutineScope {
        rotateMutex.mutateWith(rotateScope, rotatePriority) {
            try {
                isRotatingState.value = true
                block()
            } finally {
                isRotatingState.value = false
            }
        }
    }

    override val isRotateInProgress = isRotatingState.value

}

fun Modifier.rotateBy(degrees:Float) = composed(debugInspectorInfo { }) {

    val state = rememberRotateState()
    LaunchedEffect(key1 = 1, ){
        state.rotateBy(degrees)
    }

    Modifier.rotate(state.value.toFloat())
}


suspend fun RotatableState.rotateBy(value: Float): Float {
    var consumed = 0f
    rotate {
        consumed = rotateBy(value)
    }
    return consumed
}

@Composable
fun rememberRotateState(initial: Int = 0): RotateState {
    return rememberSaveable(saver = RotateState.Saver) {
        RotateState(initial = initial)
    }
}

@Stable
class RotateState(initial: Int) : RotatableState {

    var value: Int by mutableStateOf(initial, structuralEqualityPolicy())
        private set

    override suspend fun rotate(
        rotatePriority: MutatePriority,
        block: suspend RotateScope.() -> Unit
    ) = rotatableState.rotate(rotatePriority, block)

    override val isRotateInProgress: Boolean
        get() = rotatableState.isRotateInProgress


    private var accumulator: Float = 0f

    private val rotatableState = DefaultRotatableState {
        val absolute = (value + it + accumulator)
        val newValue = absolute.coerceIn(0f, Int.MAX_VALUE.toFloat())
        val changed = absolute != newValue
        val consumed = newValue - value
        val consumedInt = consumed.roundToInt()
        value += consumedInt
        accumulator = consumed - consumedInt

        // Avoid floating-point rounding error
        if (changed) consumed else it
    }

    companion object {
        /**
         * The default [Saver] implementation for [RotateState].
         */
        val Saver: Saver<RotateState, *> = Saver(
            save = { it.value },
            restore = { RotateState(it) }
        )
    }
}