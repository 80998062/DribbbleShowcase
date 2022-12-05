package com.sinyuk.compose.dribbbleshowcase.playbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.unit.dp

/**
 * Created by Sinyuk on 2022/10/25.
 */


@Composable
fun RotateShape() {
    Box(modifier = Modifier.rotate(100f).size(40.dp).background(color = Color.DarkGray)){

    }
}

