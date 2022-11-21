package com.sinyuk.compose.dribbbleshowcase.playbutton

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import com.sinyuk.compose.animatePathAsState
import com.sinyuk.compose.dribbbleshowcase.playbutton.PlayPauseAVDTokens.MorphAnimationSpec
import com.sinyuk.compose.dribbbleshowcase.playbutton.PlayPauseAVDTokens.RotateAnimationSpec
import com.sinyuk.compose.dribbbleshowcase.playbutton.PlayPauseAVDTokens.pathGroup1
import com.sinyuk.compose.dribbbleshowcase.playbutton.PlayPauseAVDTokens.pathGroup2

@Preview(widthDp = 100, heightDp = 100)
@Composable
fun PlayPauseAVDPreview() {
    MaterialTheme {
        Surface {
            var isPlaying by remember { mutableStateOf(false) }
            PlayPauseAVD(isPlaying = isPlaying) {
                isPlaying = !isPlaying
            }
        }
    }
}

@Composable
fun PlayPauseAVD(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val pathData1 =
        animatePathAsState(
            path = if (!isPlaying) pathGroup1.first else pathGroup1.second,
            spec = MorphAnimationSpec
        )

    val pathData2 =
        animatePathAsState(
            path = if (!isPlaying) pathGroup2.first else pathGroup2.second,
            spec = MorphAnimationSpec
        )


    val degrees: Float by animateFloatAsState(
        targetValue = if (!isPlaying) 0f else 90f,
        animationSpec = RotateAnimationSpec
    )

//    LaunchedEffect(key1 = isPlaying) {
//        count++
//    }

//    val degrees: Float by remember {
//        derivedStateOf {
//            rotate * 90
//        }
//    }

    PlayPauseAVDStateLess(
        pathData1 = pathData1.value,
        pathData2 = pathData2.value,
        rotation = degrees,
        modifier = modifier
    ) {
        onClick()
    }
}


@Composable
private fun PlayPauseAVDStateLess(
    pathData1: List<PathNode>,
    pathData2: List<PathNode>,
    rotation: Float,
    modifier: Modifier = Modifier,
    contentDescription: String = "PlayOrPause",
    onClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {

        val imageVector = ImageVector.Builder(
            defaultWidth = maxWidth,
            defaultHeight = maxHeight,
            viewportWidth = PlayPauseAVDTokens.ViewPort,
            viewportHeight = PlayPauseAVDTokens.ViewPort
        )
            .addPath(
                pathData = pathData1,
                fill = PlayPauseAVDTokens.FillColor
            )
            .addPath(
                pathData = pathData2,
                fill = PlayPauseAVDTokens.FillColor
            )
            .build()

        rememberScrollState()
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            Icon(imageVector = imageVector, contentDescription = contentDescription)
        }
    }
}

fun Modifier.rotateBy(degrees: Float) = composed(
    inspectorInfo = debugInspectorInfo {
    }, factory = {
        var currentRotation by remember {
            mutableStateOf(0f)
        }

        LaunchedEffect(degrees) {
            currentRotation += degrees
        }
        Modifier.rotate(currentRotation)
    })


private object PlayPauseAVDTokens {
    // default canvas size
    const val ViewPort = 100f

    // default stroke color, not working on Surface
    val FillColor = SolidColor(Color.LightGray)

    val MorphAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val RotateAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // altitude of a right-angled triangle: h = sqrt(a*a - b*b)
    private const val altitude = 86.6f
    private const val inset = (ViewPort - altitude) * .5f
    private const val pauseWidth = 24f
    private const val pauseSpacing = 24f

    val pathGroup1 = Pair(Vector1.pathData, VectorAlt1.pathData)
    val pathGroup2 = Pair(Vector2.pathData, VectorAlt2.pathData)

    private object Vector1 {
        private val rect = Rect(inset, 0f, ViewPort - inset, ViewPort * .5f)
        val pathData = listOf(
            PathNode.MoveTo(rect.left, rect.top),
            PathNode.LineTo(rect.left, rect.bottom),
            PathNode.LineTo(rect.right, rect.bottom),
            PathNode.LineTo(rect.right, rect.bottom),
            PathNode.Close
        )
    }


    private object VectorAlt1 {
        private const val top = (ViewPort - pauseSpacing) * .5f - pauseWidth
        private val rect = Rect(inset, top, ViewPort - inset, top + pauseWidth)
        val pathData = listOf(
            PathNode.MoveTo(rect.left, rect.top),
            PathNode.LineTo(rect.left, rect.bottom),
            PathNode.LineTo(rect.right, rect.bottom),
            PathNode.LineTo(rect.right, rect.top),
            PathNode.Close
        )
    }

    private object Vector2 {
        private val rect = Rect(inset, ViewPort * .5f, ViewPort - inset, ViewPort)
        val pathData = listOf(
            PathNode.MoveTo(rect.left, rect.top),
            PathNode.LineTo(rect.left, rect.bottom),
            PathNode.LineTo(rect.right, rect.top),
            PathNode.LineTo(rect.right, rect.top),
            PathNode.Close
        )
    }

    private object VectorAlt2 {
        private const val top = ViewPort * .5f + pauseSpacing * .5f
        private val rect = Rect(inset, top, ViewPort - inset, top + pauseWidth)
        val pathData = listOf(
            PathNode.MoveTo(rect.left, rect.top),
            PathNode.LineTo(rect.left, rect.bottom),
            PathNode.LineTo(rect.right, rect.bottom),
            PathNode.LineTo(rect.right, rect.top),
            PathNode.Close
        )
    }
}