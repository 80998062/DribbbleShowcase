package com.sinyuk.compose.dribbbleshowcase.playbutton


import android.graphics.RectF
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinyuk.compose.animatePathAsState

/**
 * Created by Sinyuk on 2022/10/25.
 */

@ExperimentalTransitionApi
@Preview(widthDp = 200, heightDp = 200)
@Composable
fun Preview() {
    MaterialTheme {
        Surface {
            PlayIndicatorStateful()
        }
    }
}

val TAG = "Sinyuk"

private enum class AnimationSection {
    Morphing,
    Rolling,
    Shrinking
}

@Composable
private fun Transition<AnimationSection>.animatePathNodes(targetValueByState: @Composable (state: AnimationSection) -> List<PathNode>): State<List<PathNode>> {

    val initialValue = targetValueByState(currentState)
    val targetValue = targetValueByState(targetState)


    return TODO()
}

@ExperimentalTransitionApi
@Composable
fun PlayIndicatorStateful(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }

    val viewPort = 100f
    // altitude of a right-angled triangle: h = sqrt(a*a - b*b)
    val altitude = 86.6f
    val pauseBarWidth = 30f
    val pauseBarSpacing = 22f

    val pauseBarLeftPath = remember {
        listOf(
            PathNode.MoveTo(0f, 0f), // (0,0)
            PathNode.LineTo(0f, viewPort), // (0,100)
            PathNode.LineTo(pauseBarWidth, viewPort), // (30,100)
            PathNode.LineTo(pauseBarWidth, viewPort / 2), // (30,50)
            PathNode.LineTo(pauseBarWidth, 0f), // (30,0)
            PathNode.Close
        )
    }

    val playTrianglePath = remember {
        listOf(
            PathNode.MoveTo(0f, 0f), // (0,0)
            PathNode.LineTo(0f, viewPort), // (0,100)
            PathNode.LineTo(0.5f * altitude, 0.75f * viewPort), // (0.5h,75)
            PathNode.LineTo(altitude, 0.5f * viewPort),// (h,50)
            PathNode.LineTo(0.5f * altitude, 0.25f * viewPort), // (0.5h,25)
            PathNode.Close
        )
    }


    val pauseBarRightPath = remember {
        val startX = pauseBarSpacing + pauseBarWidth
        val endX = pauseBarSpacing + pauseBarWidth * 2
        listOf(
            PathNode.MoveTo(startX, 0f),
            PathNode.LineTo(startX, viewPort),
            PathNode.LineTo(endX, viewPort),
            PathNode.LineTo(endX, 0f),
            PathNode.Close
        )
    }

    val pauseBarShortenRectF = RectF(
        pauseBarSpacing + pauseBarWidth,
        viewPort - pauseBarSpacing,
        pauseBarSpacing + pauseBarWidth * 2,
        viewPort
    )

    val pauseBarRightPathShorten = remember {
        listOf(
            PathNode.MoveTo(pauseBarShortenRectF.left, pauseBarShortenRectF.top),
            PathNode.LineTo(pauseBarShortenRectF.left, pauseBarShortenRectF.bottom),
            PathNode.LineTo(pauseBarShortenRectF.right, pauseBarShortenRectF.bottom),
            PathNode.LineTo(pauseBarShortenRectF.right, pauseBarShortenRectF.top),
            PathNode.Close
        )
    }

    var currentState = remember { MutableTransitionState(isPlaying) }
    // currentState.targetState = true

    val transition = updateTransition(currentState, label = "")

    val pauseBarRightParams by pauseBarRight(transition.createChildTransition { isPlaying })

//    transition.animateDp() {
//
//    }


    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxSize()
    ) {
        val pathData by animatePathAsState(
            if (isPlaying) pauseBarRightPathShorten
            else pauseBarRightPath
        )

//        val pathData = mutableListOf<PathNode>()

//        pathData.addAll(pauseBarLeftPath)
//        pathData.addAll(pauseBarRightPathShorten)

        val fillColor = SolidColor(MaterialTheme.colorScheme.onSurface)

        val imageVector by remember(fillColor) {
            derivedStateOf {
                ImageVector.Builder(
                    defaultWidth = 100.dp,
                    defaultHeight = 100.dp,
                    viewportWidth = viewPort,
                    viewportHeight = viewPort
                )
                    .addGroup(
                        name = "pathBarRight",
//                        rotate = pauseBarRightParams.rotate,
//                        pivotX = pauseBarRightParams.pivotX,
//                        pivotY = pauseBarRightParams.pivotY,
                        translationX = pauseBarRightParams.translationX
                    )
                    .addPath(pathData = pauseBarRightPathShorten, fill = fillColor)
                    .build()
            }
        }

        IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = imageVector, contentDescription = null)

        }
    }
}


@Immutable
private data class Params(
    val rotate: Float = DefaultRotation,
    val pivotX: Float = DefaultPivotX,
    val pivotY: Float = DefaultPivotY,
    val translationX: Float = DefaultTranslationX,
    val translationY: Float = DefaultTranslationY,
)


@Composable
private fun pauseBarRight(
    transition: Transition<Boolean>,
): State<Params> {
    val fraction by transition.animateFloat(
        label = "pauseBarRight",
        transitionSpec = { tween(durationMillis = 2000) }
    ) { isPlaying ->
        if (isPlaying) 1f else 0f
    }
    return remember {
        derivedStateOf {
            val rotate = if (fraction < 0.5) {
                lerp(0f, 90f, fraction)
            } else {
                lerp(90f, 180f, fraction)
            }
            val pivot = if (fraction < 0.5) {
                Pair(0f, 1f)
            } else {
                Pair(0f, 0f)
            }
            val offsetX = lerp(0f, -22f, fraction)

            Params(
//                pivotX = pivot.first,
//                pivotY = pivot.second,
//                rotate = rotate,
                translationX = offsetX
            )
        }
    }
}


private fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}




