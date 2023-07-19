package com.example.monthycalenderview

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    /* size: Dp = 80.dp*/
    padding: Dp = 6.dp,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onUpdate: (Boolean) -> Unit,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 500)
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    val isCheckInSelected = remember { true }
    val isCheckOutSelected = remember { false }

    var offSetSize by remember { mutableStateOf(0.dp) }

    val offset by animateDpAsState(
        targetValue = if (!selected) 0.dp else offSetSize, animationSpec = animationSpec
    )
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clip(shape = CircleShape)
            .onSizeChanged {
                size = it
            }
            .background(color = Color.Red),
    ) {
        Box(
            modifier = Modifier
                .then(with(LocalDensity.current) {
                    offSetSize = size.width.toDp() / 2
                    println(size.width.toDp())
                    println(size.width.toDp())
                    Modifier.size(width = size.width.toDp() / 2, height = size.height.toDp())
                })
                .padding(all = 6.dp)
                .offset(x = offset)
                .shadow(12.dp, shape = CircleShape)
                .clickable {
                    if (isCheckInSelected) {
                        onUpdate(!selected)
                    }
                }
                .background(color = Color(0xFFFFFFFF))
        ) {

        }

        /*Row {
            Column(
                modifier = Modifier
                    .then(with(LocalDensity.current) {
                        Modifier.size(width = size1.width.toDp() / 2, height = size1.height.toDp())
                    })
                    .padding(start = 17.dp)
                    .clickable {

                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,

                ) {
                Text(text = "Check-in", color = Color(0xFF818181))
                Text(
                    text = "May 03 12:45 PM",
                    color = Color(0xFF333333)
                )
            }
            Column(
                modifier = Modifier
                    .then(with(LocalDensity.current) {
                        Modifier.size(width = size1.width.toDp() / 2, height = size1.height.toDp())
                    })
                    .padding(start = 17.dp, end = 10.dp)
                    .clickable {

                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,

                ) {
                Text(text = "Checkout", color = Color(0xFF818181))
                Text(
                    text = "May 09 12:45 PM",
                    color = Color(0xFF333333)
                )
            }
        }*/
    }
}