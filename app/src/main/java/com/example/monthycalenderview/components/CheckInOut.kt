package com.example.monthycalenderview.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.monthycalenderview.SelectDateTimeViewModel


@Composable
fun CheckInOutCompose(
    height: Dp = 80.dp,
    padding: Dp = 6.dp,
    toggleShape: Shape = CircleShape,
    click: Boolean = false,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 500),
    onClick: () -> Unit,
    mViewModel: SelectDateTimeViewModel = SelectDateTimeViewModel(),
) {

    var size by remember { mutableStateOf(IntSize.Zero) }
    var widthOfBox by remember { mutableStateOf(0.dp) }
    var heightOfBox by remember { mutableStateOf(0.dp) }
    val checkData = mViewModel.checkState.value
    var isCheckOutSelected by remember { mutableStateOf(false) }
    var isCheckInSelected by remember { mutableStateOf(true) }

    val offset by animateDpAsState(
        targetValue = if (!click) 0.dp else widthOfBox, animationSpec = animationSpec
    )
    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(height)
            .onSizeChanged {
                size = it
            }
            .clip(shape = toggleShape)
            .background(color = Color(0xFFDADADA)),
    ) {

        Box(
            modifier = Modifier
                .then(with(LocalDensity.current) {
                    widthOfBox = size.width.toDp() / 2
                    heightOfBox = size.height.toDp()
                    Modifier.size(width = widthOfBox, height = heightOfBox)

                })
                .offset(x = offset)
                .padding(all = padding)
                .shadow(12.dp, shape = toggleShape)
                .background(color = Color(0xFFFFFFFF))
        ) {

        }
        Row {
            Column(
                modifier = Modifier
                    .width(widthOfBox)
                    .height(heightOfBox)
                    .padding(start = 15.dp)
                    .clickable {
                        if (isCheckOutSelected) {
                            onClick()
                            isCheckInSelected = true
                            isCheckOutSelected = false
                        }
                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,

                ) {
                Text(text = "Check-in")
                Text(text = checkData.checkInDate)
            }


            Column(
                modifier = Modifier
                    .width(widthOfBox)
                    .height(heightOfBox)
                    .padding(start = 15.dp)
                    .clickable {
                        if (isCheckInSelected) {
                            onClick()
                            isCheckInSelected = false
                            isCheckOutSelected = true
                        }
                    },
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,

                ) {
                Text(text = "Check-out")
                Text(text = checkData.checkOutDate)

            }
        }
    }
}

data class CheckInOutData(
    val checkInDate: String = "",
    val checkOutDate: String = "",
    var selection: CheckInOutSelection = CheckInOutSelection.In,
)

enum class CheckInOutSelection {
    In, Out
}

sealed class UIEvent {
    data class CheckInChanged(val date: String) : UIEvent()
    data class CheckOutChanged(val date: String) : UIEvent()
    object Reset : UIEvent()
}

