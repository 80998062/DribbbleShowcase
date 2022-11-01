package com.sinyuk.compose.dribbbleshowcase.playbutton


import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinyuk.compose.animatePathAsState

/**
 * Created by Sinyuk on 2022/10/25.
 */

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

@Composable
fun PlayIndicatorStateful(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }

    val viewPort = 100f
    // altitude of a right-angled triangle: h = sqrt(a*a - b*b)
    val altitude = 86.6f

    val pauseRectLeft = remember {
        listOf(
            PathNode.MoveTo(0f, 0f), // (0,0)
            PathNode.LineTo(0f, 100f), // (0,100)
            PathNode.LineTo(30f, 100f), // (30,100)
            PathNode.LineTo(30f, 50f), // (30,50)
            PathNode.LineTo(30f, 0f), // (30,0)
            PathNode.Close
        )
    }

    val playTriangle = remember {
        listOf(
            PathNode.MoveTo(0f, 0f), // (0,0)
            PathNode.LineTo(0f, viewPort), // (0,100)
            PathNode.LineTo(0.5f * altitude, 0.75f * viewPort), // (0.5h,75)
            PathNode.LineTo(altitude, 0.5f * viewPort),// (h,50)
            PathNode.LineTo(0.5f * altitude, 0.25f * viewPort), // (0.5h,25)
            PathNode.Close
        )
    }

    var currentSection by remember { mutableStateOf(AnimationSection.Morphing) }
    val transition = updateTransition(currentSection)
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
            if (isPlaying) playTriangle
            else pauseRectLeft
        )

        val imageVector by remember {
            derivedStateOf {
                ImageVector.Builder(
                    defaultWidth = 100.dp,
                    defaultHeight = 100.dp,
                    viewportWidth = viewPort,
                    viewportHeight = viewPort
                )
                    .addPath(pathData = pathData, fill = SolidColor(Color.LightGray))
                    .build()
            }
        }

        IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = imageVector, contentDescription = null)

        }
    }
}




