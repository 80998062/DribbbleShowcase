package com.sinyuk.compose

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes


private val defaultAnimation = tween<Float>()


@OptIn(ExperimentalTransitionApi::class)
@Composable
fun Parent() {
    val isPlaying by remember {
        mutableStateOf(false)
    }
    var currentState = remember { MutableTransitionState(isPlaying) }
    // currentState.targetState = true

    val transition = updateTransition(currentState, label = "")

    val rightPauseBarPathData by rightPauseBar(transition.createChildTransition { isPlaying })

    // 我可以用一个 ImageVector 画出 2 个长方形不 一个画完 然后接着 MoveTo
    // 就是把两个楞的 PathNode 加起来?

    // 那么 在这个 Image 的 pathData 就等于 2 个 三角形/长方形 + 长方形

    // 那么旋转如何处理 ??

    // 从 一个 pathData 到 另一个 pathData 才是 morphing 把
    // 对于一个 pathData 那么就用 path 自己的 rotate 和  translate 方法?
    // 卧槽好像可以用 ImageVector??? 里面自己就有很多东东
    ImageVector.Builder()
        .addGroup(name = "g_rightBar")
        .addPath(name = "p_rightBar", pathData = rightPauseBarPathData)
}

@Composable
fun rightPauseBar(
    transition: Transition<Boolean>,
): State<List<PathNode>> {
    val fraction by transition.animateFloat(
        label = "RightPauseBar",
        transitionSpec = { tween() }
    ) { isPlaying ->
        if (isPlaying) 1f else 0f
    }
    val path: List<PathNode> = listOf()
    var from by remember { mutableStateOf(path) }
    var to by remember { mutableStateOf(path) }
    return remember {
        derivedStateOf {
            if (canMorph(from, to)) {
                lerp(from, to, fraction)
            } else {
                to
            }
        }
    }
}

@Composable
fun xxx(path: List<PathNode>): State<List<PathNode>> {
    var currentState by remember { mutableStateOf(false) }
    val transition = updateTransition(currentState, label = "PathNodeTransition")

    val progress by transition.animateFloat(label = "PathNodeTransition") {
        if (it) {
            0f
        } else {
            1f
        }
    }
    var from by remember { mutableStateOf(path) }
    var to by remember { mutableStateOf(path) }

    return remember {
        derivedStateOf {
            if (canMorph(from, to)) {
                lerp(from, to, progress)
            } else {
                to
            }
        }
    }
}

@Composable
fun animatePathAsState(path: String): State<List<PathNode>> {
    return animatePathAsState(remember(path) { addPathNodes(path) })
}

@Composable
fun animatePathAsState(path: List<PathNode>): State<List<PathNode>> {

    var from by remember { mutableStateOf(path) }
    var to by remember { mutableStateOf(path) }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(path) {
        if (to != path) {
            from = to
            to = path
            progress.snapTo(0f)
            progress.animateTo(targetValue = 1f)
        }
    }

    return remember {
        derivedStateOf {
            if (canMorph(from, to)) {
                lerp(from, to, progress.value)
            } else {
                to
            }
        }
    }

}

// Paths can morph if same size and same node types at same positions.
private fun canMorph(from: List<PathNode>, to: List<PathNode>): Boolean {
    if (from.size != to.size) {
        return false
    }

    for (i in from.indices) {
        if (from[i].javaClass != to[i].javaClass) {
            return false
        }
    }

    return true
}

// Assume paths can morph (see [canMorph]). If not, will throw.
private fun lerp(
    fromPath: List<PathNode>,
    toPath: List<PathNode>,
    fraction: Float
): List<PathNode> {
    return fromPath.mapIndexed { i, from ->
        val to = toPath[i]
        lerp(from, to, fraction)
    }
}

private fun lerp(from: PathNode, to: PathNode, fraction: Float): PathNode {
    return when (from) {
        PathNode.Close -> {
            to as PathNode.Close
            from
        }
        is PathNode.RelativeMoveTo -> {
            to as PathNode.RelativeMoveTo
            PathNode.RelativeMoveTo(
                lerp(from.dx, to.dx, fraction),
                lerp(from.dy, to.dy, fraction),
            )
        }
        is PathNode.MoveTo -> {
            to as PathNode.MoveTo
            PathNode.MoveTo(
                lerp(from.x, to.x, fraction),
                lerp(from.y, to.y, fraction),
            )
        }
        is PathNode.RelativeLineTo -> {
            to as PathNode.RelativeLineTo
            PathNode.RelativeLineTo(
                lerp(from.dx, to.dx, fraction),
                lerp(from.dy, to.dy, fraction),
            )
        }
        is PathNode.LineTo -> {
            to as PathNode.LineTo
            PathNode.LineTo(
                lerp(from.x, to.x, fraction),
                lerp(from.y, to.y, fraction),
            )
        }
        is PathNode.RelativeHorizontalTo -> {
            to as PathNode.RelativeHorizontalTo
            PathNode.RelativeHorizontalTo(
                lerp(from.dx, to.dx, fraction)
            )
        }
        is PathNode.HorizontalTo -> {
            to as PathNode.HorizontalTo
            PathNode.HorizontalTo(
                lerp(from.x, to.x, fraction)
            )
        }
        is PathNode.RelativeVerticalTo -> {
            to as PathNode.RelativeVerticalTo
            PathNode.RelativeVerticalTo(
                lerp(from.dy, to.dy, fraction)
            )
        }
        is PathNode.VerticalTo -> {
            to as PathNode.VerticalTo
            PathNode.VerticalTo(
                lerp(from.y, to.y, fraction)
            )
        }
        is PathNode.RelativeCurveTo -> {
            to as PathNode.RelativeCurveTo
            PathNode.RelativeCurveTo(
                lerp(from.dx1, to.dx1, fraction),
                lerp(from.dy1, to.dy1, fraction),
                lerp(from.dx2, to.dx2, fraction),
                lerp(from.dy2, to.dy2, fraction),
                lerp(from.dx3, to.dx3, fraction),
                lerp(from.dy3, to.dy3, fraction),
            )
        }
        is PathNode.CurveTo -> {
            to as PathNode.CurveTo
            PathNode.CurveTo(
                lerp(from.x1, to.x1, fraction),
                lerp(from.y1, to.y1, fraction),
                lerp(from.x2, to.x2, fraction),
                lerp(from.y2, to.y2, fraction),
                lerp(from.x3, to.x3, fraction),
                lerp(from.y3, to.y3, fraction),
            )
        }
        is PathNode.RelativeReflectiveCurveTo -> {
            to as PathNode.RelativeReflectiveCurveTo
            PathNode.RelativeReflectiveCurveTo(
                lerp(from.dx1, to.dx1, fraction),
                lerp(from.dy1, to.dy1, fraction),
                lerp(from.dx2, to.dx2, fraction),
                lerp(from.dy2, to.dy2, fraction),
            )
        }
        is PathNode.ReflectiveCurveTo -> {
            to as PathNode.ReflectiveCurveTo
            PathNode.ReflectiveCurveTo(
                lerp(from.x1, to.x1, fraction),
                lerp(from.y1, to.y1, fraction),
                lerp(from.x2, to.x2, fraction),
                lerp(from.y2, to.y2, fraction),
            )
        }
        is PathNode.RelativeQuadTo -> {
            to as PathNode.RelativeQuadTo
            PathNode.RelativeQuadTo(
                lerp(from.dx1, to.dx1, fraction),
                lerp(from.dy1, to.dy1, fraction),
                lerp(from.dx2, to.dx2, fraction),
                lerp(from.dy2, to.dy2, fraction),
            )
        }
        is PathNode.QuadTo -> {
            to as PathNode.QuadTo
            PathNode.QuadTo(
                lerp(from.x1, to.x1, fraction),
                lerp(from.y1, to.y1, fraction),
                lerp(from.x2, to.x2, fraction),
                lerp(from.y2, to.y2, fraction),
            )
        }
        is PathNode.RelativeReflectiveQuadTo -> {
            to as PathNode.RelativeReflectiveQuadTo
            PathNode.RelativeReflectiveQuadTo(
                lerp(from.dx, to.dx, fraction),
                lerp(from.dy, to.dy, fraction),
            )
        }
        is PathNode.ReflectiveQuadTo -> {
            to as PathNode.ReflectiveQuadTo
            PathNode.ReflectiveQuadTo(
                lerp(from.x, to.x, fraction),
                lerp(from.y, to.y, fraction),
            )
        }
        is PathNode.RelativeArcTo -> TODO("Support for RelativeArcTo not implemented yet")
        is PathNode.ArcTo -> TODO("Support for ArcTo not implemented yet")
    }
}

/**
 * Calculates a number between two numbers at a specific increment.
 */
private fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}
