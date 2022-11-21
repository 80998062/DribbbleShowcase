package com.sinyuk.compose.dribbbleshowcase.volume

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlin.math.max
import kotlin.math.roundToInt

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        Surface {
        }
    }
}

object MySliderTokens {
    val HandleWidth: Dp = 20.dp
    val HandleHeight: Dp = 20.dp
}


internal val ThumbWidth = MySliderTokens.HandleWidth
private val ThumbHeight = MySliderTokens.HandleHeight
private val ThumbSize = DpSize(ThumbWidth, ThumbHeight)
private val ThumbDefaultElevation = 1.dp
private val ThumbPressedElevation = 6.dp

@Composable
fun MySlider(
    modifier: Modifier,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
) {
    val onValueChangeState = rememberUpdatedState<(Float) -> Unit> {
        if (it != value) {
            onValueChange(it)
        }
    }

    val thumbWidth = remember { mutableStateOf(ThumbWidth.value) }
    val totalWidth = remember { mutableStateOf(0) }

    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)

    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    val draggableState = remember {
        SliderDraggableState { delta->

        }
    }

    val drag = Modifier.draggable(
        state = draggableState,
        orientation = Orientation.Horizontal,
        enabled = enabled,
        interactionSource = interactionSource,
        startDragImmediately = draggableState.isDragging,
        onDragStarted = {},
        onDragStopped = {},
        reverseDirection = false
    )

    Layout(
        modifier = modifier
            .requiredSizeIn(
                minWidth = MySliderTokens.HandleWidth,
                minHeight = MySliderTokens.HandleHeight
            ),
        content = {
            Box(
                Modifier
                    .layoutId("THUMB")
                    .background(Color.Blue)
                    .size(ThumbSize)
            )
            Box(modifier = Modifier.layoutId("TRACK"))

        }, measurePolicy = object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                val thumbPlaceable = measurables.first {
                    it.layoutId == "THUMB"
                }.measure(constraints)
                val maxTrackWidth = constraints.maxWidth - thumbPlaceable.width
                val trackPlaceable = measurables.first {
                    it.layoutId == "TRACK"
                }.measure(
                    constraints.copy(
                        minWidth = 0,
                        maxWidth = maxTrackWidth,
                        minHeight = 0
                    )
                )

                val sliderWidth = thumbPlaceable.width + trackPlaceable.width
                val sliderHeight = max(trackPlaceable.height, thumbPlaceable.height)

                val trackOffsetX = thumbPlaceable.width / 2
                val thumbOffsetX = ((trackPlaceable.width) * positionFraction).roundToInt()
                val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
                val thumbOffsetY = (sliderHeight - thumbPlaceable.height) / 2

                return layout(sliderWidth, sliderHeight) {
                    trackPlaceable.placeRelative(
                        trackOffsetX,
                        trackOffsetY
                    )

                    thumbPlaceable.placeRelative(
                        thumbOffsetX,
                        thumbOffsetY
                    )
                }
            }
        })
}

private class SliderDraggableState(
    val onDelta: (Float) -> Unit
) : DraggableState {

    var isDragging by mutableStateOf(false)
        private set

    private val dragScope: DragScope = object : DragScope {
        override fun dragBy(pixels: Float): Unit = onDelta(pixels)
    }

    private val scrollMutex = MutatorMutex()

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit
    ): Unit = coroutineScope {
        isDragging = true
        scrollMutex.mutateWith(dragScope, dragPriority, block)
        isDragging = false
    }

    override fun dispatchRawDelta(delta: Float) {
        return onDelta(delta)
    }
}

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)