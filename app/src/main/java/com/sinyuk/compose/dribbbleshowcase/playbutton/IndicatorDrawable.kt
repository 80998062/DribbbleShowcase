package com.sinyuk.compose.dribbbleshowcase.playbutton

import android.graphics.Matrix
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Sinyuk on 2022/10/25.
 */

@Preview(widthDp = 200, heightDp = 200)
@Composable
fun Preview() {


    MaterialTheme {
        Surface {
            draw()
        }
    }
}

val TAG = "Sinyuk"

@Composable
fun draw(modifier: Modifier = Modifier) {
    val viewPortWidth = 68f

    // altitude of a right-angled triangle
    // h = sqrt(a*a - b*b)
    val altitude = sqrt(viewPortWidth.pow(2) - viewPortWidth.div(2).pow(2))
    Log.d(TAG, "altitude: $altitude")
    // percent value
    val horizontalPadding = viewPortWidth.minus(altitude).div(2).div(viewPortWidth)
    Log.d(TAG, "horizontalBaseline: $horizontalPadding")

    // percent value
    val rectWidth = 20.div(viewPortWidth)


    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        val path = remember {
            Path()
        }

        val path2 = remember {
            Path()
        }
        Canvas(modifier = Modifier) {

            val baseLineStart = maxWidth.toPx().times(horizontalPadding)
            // 绘制背景
            // drawCircle(Color.LightGray, maxWidth.div(2).toPx())
            path.moveTo(baseLineStart, 0f)
            path.lineTo(baseLineStart, maxHeight.toPx())
            path.lineTo(maxWidth.toPx().times(1 - horizontalPadding), maxHeight.div(2).toPx())
            path.close()


            val rect = path.getBounds()

            val matrix = Matrix()
            matrix.postRotate(0f, rect.center.x, rect.center.y)
            path.asAndroidPath().transform(matrix)
            drawPath(path, Color.LightGray)
//            path2.moveTo(baseLineStart, 0f)
//            path2.lineTo(baseLineStart + rectWidth.times(maxWidth.toPx()), 0f)
//            path2.lineTo(baseLineStart + rectWidth.times(maxWidth.toPx()), maxHeight.toPx())
//            path2.lineTo(baseLineStart, maxHeight.toPx())
//            path2.close()
//            drawPath(path2, Color.Blue)
        }
    }

}

@Composable
fun xxxxx() {
    TODO("Not yet implemented")
}



