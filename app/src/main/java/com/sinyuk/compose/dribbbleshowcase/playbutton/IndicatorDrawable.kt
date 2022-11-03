package com.sinyuk.compose.dribbbleshowcase.playbutton


import android.graphics.RectF
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinyuk.compose.animatePathAsState

/**
 * Created by Sinyuk on 2022/10/25.
 */

@ExperimentalTransitionApi
@Preview(widthDp = 100, heightDp = 100)
@Composable
fun Preview() {
    MaterialTheme {
        Surface {
            PlayIndicatorStateful(false)
        }
    }
}

val TAG = "Sinyuk"

@ExperimentalTransitionApi
@Composable
fun PlayIndicatorStateful(isPlaying: Boolean, modifier: Modifier = Modifier) {
    var currentState by remember { mutableStateOf(isPlaying) }

    val transition = updateTransition(currentState, label = "PlayIndicator")

    val viewPort = 100f
    // altitude of a right-angled triangle: h = sqrt(a*a - b*b)
    val altitude = 86.6f
    val pauseIconWidth = 30f
    val barSpacing = 22f

    val pauseIconLeftBar = remember {
        listOf(
            PathNode.MoveTo(0f, 0f),
            PathNode.LineTo(0f, viewPort),
            PathNode.LineTo(pauseIconWidth, viewPort),
            PathNode.LineTo(pauseIconWidth, viewPort / 2),
            PathNode.LineTo(pauseIconWidth, 0f),
            PathNode.Close
        )
    }

    val playIcon = remember {
        listOf(
            PathNode.MoveTo(0f, 0f), // (0,0)
            PathNode.LineTo(0f, viewPort), // (0,100)
            PathNode.LineTo(0.5f * altitude, 0.75f * viewPort), // (0.5h,75)
            PathNode.LineTo(altitude, 0.5f * viewPort),// (h,50)
            PathNode.LineTo(0.5f * altitude, 0.25f * viewPort), // (0.5h,25)
            PathNode.Close
        )
    }


    val pauseIconRightBar = remember {
        val startX = barSpacing + pauseIconWidth
        val endX = barSpacing + pauseIconWidth * 2
        listOf(
            PathNode.MoveTo(startX, 0f),
            PathNode.LineTo(startX, viewPort),
            PathNode.LineTo(endX, viewPort),
            PathNode.LineTo(endX, 0f),
            PathNode.Close
        )
    }

    val rotatedCube = RectF(
        barSpacing + pauseIconWidth,
        viewPort - barSpacing,
        barSpacing + pauseIconWidth * 2,
        viewPort
    )

    val rotatedCubePath = remember {
        listOf(
            PathNode.MoveTo(rotatedCube.left, rotatedCube.top),
            PathNode.LineTo(rotatedCube.left, rotatedCube.bottom),
            PathNode.LineTo(rotatedCube.right, rotatedCube.bottom),
            PathNode.LineTo(rotatedCube.right, rotatedCube.top),
            PathNode.Close
        )
    }

    val groupParams by pauseCubeState(
        rotatedCube,
        transition.createChildTransition { currentState })

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxSize()
    ) {
        val pathDataAtLeft by animatePathAsState(
            if (currentState) playIcon
            else pauseIconLeftBar
        )

        val pathDataAtRight by animatePathAsState(
            if (currentState) rotatedCubePath
            else pauseIconRightBar
        )

        val imageVector by remember {
            derivedStateOf {
                ImageVector.Builder(
                    defaultWidth = 100.dp,
                    defaultHeight = 100.dp,
                    viewportWidth = viewPort,
                    viewportHeight = viewPort
                )
                    .addGroup(
                        name = "right",
//                        rotate = groupParams.rotate,
//                        pivotX = groupParams.pivotX,
//                        pivotY = groupParams.pivotY,
                    )
                    .addPath(pathData = pathDataAtRight, fill = SolidColor(Color.LightGray))
                    .clearGroup()
                    .addGroup(name = "left")
                    .addPath(pathData = pathDataAtLeft, fill = SolidColor(Color.LightGray))
                    .clearGroup()
                    .build()
            }
        }

        IconButton(onClick = { currentState = !currentState }, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = imageVector, contentDescription = null)

        }
    }
}


@Immutable
private data class GroupParams(
    val rotate: Float = DefaultRotation,
    val pivotX: Float = DefaultPivotX,
    val pivotY: Float = DefaultPivotY
)


@Composable
private fun pauseCubeState(
    rectF: RectF,
    transition: Transition<Boolean>,
): State<GroupParams> {
    val fraction by transition.animateFloat(
        label = "pauseCube",
        transitionSpec = { tween() }
    ) { isPlaying -> if (isPlaying) 1f else 0f }

    return remember(rectF) {
        derivedStateOf {
//            val rotate = if (fraction <= 0.5f) {
//                lerp(0f, -180f, fraction)
//            } else {
//                lerp(0f, -180f, fraction - 0.5f)
//            }
            val rotate = lerp(0f, -180f, fraction)
            //val pivotX = if (fraction <= 0.5f) rectF.left else (rectF.left - rectF.height())
            // val pivotX = if (fraction <= 0.5f) 52f else 30f
            GroupParams(
                pivotX = 52f,
                pivotY = 100f,
                rotate = rotate,
            )
        }
    }
}


private fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}




