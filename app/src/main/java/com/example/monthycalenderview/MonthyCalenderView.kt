package com.example.monthycalenderview

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monthycalenderview.components.CheckInOutCompose
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

private val primaryColor = Color(0xFF313131)
private val selectionColor = Color(0xFF333333)
private val continuousSelectionColor = Color(0xFFF5F5F5)

@Composable
fun MonthlyCalenderView() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val today = remember { LocalDate.now() }
    var checkInDate = remember { LocalDate.now() }
    var checkOutDate = remember<LocalDate?> { null }
    var checkInDisplayDate = remember { mutableStateOf(LocalDate.now()) }
    var checkOutDisplayDate = remember { mutableStateOf<LocalDate?>(null) }
    val daysOfWeek = remember { daysOfWeek() }
    var selection by remember { mutableStateOf(DateSelection(startDate = today)) }
//    val checkOutView = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(), color = Color.White
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            elevation = CardDefaults.cardElevation(24.dp),
            colors = CardDefaults.cardColors(Color(0xFFFFFFFF)),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                val state = rememberCalendarState(
                    startMonth = startMonth,
                    endMonth = endMonth,
                    firstVisibleMonth = currentMonth,
                    firstDayOfWeek = daysOfWeek.first(),
                )
                val coroutineScope = rememberCoroutineScope()
                val visibleMonth = rememberFirstVisibleMonthAfterScroll(state)
                var checkOutView by remember { mutableStateOf(false) }
                CheckInOutCompose(
                    onClick = { checkOutView = !checkOutView },
                    click = checkOutView,
                )
                CalendarTitle(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                    currentMonth = visibleMonth.yearMonth,
                    goToPrevious = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                        }
                    },
                    goToNext = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                        }
                    },
                )
                HorizontalCalendar(state = state,
                    contentPadding = PaddingValues(start = 16.dp),
                    dayContent = { value ->
                        Day(
                            value, today = today, selection = selection
                        ) { day ->
                            if (day.position == DayPosition.MonthDate && (day.date == today || day.date.isAfter(
                                    today
                                ))
                            ) {
                                /* Log.e(
                                     "selection",
                                     "${selection.startDate}>>>>${selection.endDate}>>>>>date>>>${day.date}"
                                 )*/
                                if (day.date.isBefore(selection.startDate) && checkOutView) {
                                    Toast.makeText(
                                        context,
                                        "Check-out date can\\'t be before Check-in date.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (day.date.isBefore(selection.startDate) && !checkOutView) {
                                    checkOutDate = selection.endDate
                                    checkInDate = day.date
                                    checkInDisplayDate.value = day.date
                                    checkOutDisplayDate.value = selection.endDate
                                    selection = DateSelection(day.date, endDate = selection.endDate)
                                } else if (!checkOutView && selection.endDate != null && day.date.isBefore(
                                        selection.endDate
                                    )
                                ) {
                                    checkOutDate = selection.endDate
                                    checkInDate = day.date
                                    checkInDisplayDate.value = day.date
                                    checkOutDisplayDate.value = selection.endDate
                                    selection = DateSelection(day.date, endDate = selection.endDate)
                                } else if (checkOutDate == null) {
                                    if (checkOutView) {
                                        checkInDisplayDate.value = selection.startDate
                                        checkOutDisplayDate.value = day.date
                                        selection = DateSelection(
                                            startDate = selection.startDate, endDate = day.date
                                        )
                                    } else {
                                        checkInDisplayDate.value = day.date
                                        checkOutDisplayDate.value = null
                                        selection =
                                            DateSelection(startDate = day.date, endDate = null)
                                    }
                                } else if (!checkOutView && checkOutDate != null && day.date.isAfter(
                                        checkOutDate
                                    )
                                ) {
                                    checkOutDate = null
                                    checkInDate = day.date
                                    checkInDisplayDate.value = day.date
                                    checkOutDisplayDate.value = null
                                    DateSelection(startDate = day.date, endDate = null)
                                } /*else {
                                    if (checkOutView) {
                                        checkInDisplayDate.value = selection.startDate
                                        checkOutDisplayDate.value = day.date
                                        selection = DateSelection(
                                            startDate = selection.startDate,
                                            endDate = day.date
                                        )
                                    } else {
                                        checkInDisplayDate.value = day.date
                                        checkOutDisplayDate.value = null
                                        selection =
                                            DateSelection(startDate = day.date, endDate = null)
                                    }
                                }*/
                            }
                        }
                    },
                    monthHeader = { MonthHeader(daysOfWeek = daysOfWeek) })
            }
        }
    }

}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
    ) {

        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                color = primaryColor,
                text = dayOfWeek.displayText(),
            )

        }
    }
}

fun getSelection(
    clickedDate: LocalDate,
    dateSelection: DateSelection,
    checkOutView: Boolean,
): DateSelection {
    val (selectionStartDate, selectionEndDate) = dateSelection
    return if (selectionStartDate != null) {
        if (checkOutView) {
            DateSelection(startDate = selectionStartDate, endDate = clickedDate)
        } else {
            DateSelection(startDate = clickedDate, endDate = null)
        }

    } else {
        DateSelection(startDate = clickedDate, endDate = null)
    }
}

@Composable
fun rememberFirstVisibleMonthAfterScroll(state: CalendarState): CalendarMonth {
    val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }.filter { scrolling -> !scrolling }
            .collect { visibleMonth.value = state.firstVisibleMonth }
    }
    return visibleMonth.value
}

@Composable
fun Day(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit,
) {
    var textColor = primaryColor
//    Log.e("", "${selection.startDate}>>>${selection.endDate}")
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(
                enabled = day.position == DayPosition.MonthDate && day.date >= today,
                showRipple = false,
                onClick = { onClick(day) },
            )
            .backgroundHighlight(
                day = day,
                today = today,
                selection = selection,
                selectionColor = selectionColor,
                continuousSelectionColor = continuousSelectionColor,
            ) { textColor = it }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(), textAlign = TextAlign.Center,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview(heightDp = 800)
@Composable
private fun MonthlyCalenderViewPreview() {
    MonthlyCalenderView()
}

fun Modifier.backgroundHighlight(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    selectionColor: Color,
    continuousSelectionColor: Color,
    textColor: (Color) -> Unit,
): Modifier = composed {
    val (startDate, endDate) = selection
    val padding = 4.dp
    when (day.position) {
        DayPosition.MonthDate -> {
            when {
                day.date.isBefore(today) -> {
                    textColor(colorResource(R.color.inactive_text_color))
                    this
                }

                startDate == day.date && endDate == null -> {
                    textColor(Color.White)
                    padding(padding).background(color = selectionColor, shape = CircleShape)
                }

                day.date == startDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = true),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = CircleShape)
                }

                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(colorResource(R.color.example_4_grey))
                    padding(vertical = padding).background(color = continuousSelectionColor)
                }

                day.date == endDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = false),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = CircleShape)
                }

                else -> {
                    textColor(colorResource(R.color.example_4_grey))
                    this
                }
            }
        }

        DayPosition.InDate -> {
            textColor(Color.White)
            this
        }

        DayPosition.OutDate -> {
            textColor(Color.White)
            this
        }
    }
}

@Composable
fun CalendarTitle(
    modifier: Modifier,
    currentMonth: YearMonth,
    goToPrevious: () -> Unit,
    goToNext: () -> Unit,
) {
    Row(
        modifier = modifier.height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_previous),
            contentDescription = "Previous",
            onClick = goToPrevious,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .testTag("MonthTitle"),
            text = currentMonth.displayText(),
            color = primaryColor,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        CalendarNavigationIcon(
            icon = painterResource(id = R.drawable.ic_next),
            contentDescription = "Next",
            onClick = goToNext,
        )
    }
}

@Composable
private fun CalendarNavigationIcon(icon: Painter, contentDescription: String, onClick: () -> Unit) =
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .align(Alignment.Center),
            painter = icon,
            tint = primaryColor,
            contentDescription = contentDescription
        )
    }

private class HalfSizeShape(private val clipStart: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val half = size.width / 2f
        val offset = if (layoutDirection == LayoutDirection.Ltr) {
            if (clipStart) Offset(half, 0f) else Offset.Zero
        } else {
            if (clipStart) Offset.Zero else Offset(half, 0f)
        }
        return Outline.Rectangle(Rect(offset, Size(half, size.height)))
    }

}

data class DateSelection(val startDate: LocalDate? = null, val endDate: LocalDate? = null) {
    val daysBetween by lazy(LazyThreadSafetyMode.NONE) {
        if (startDate == null || endDate == null) null else {
            ChronoUnit.DAYS.between(startDate, endDate)
        }
    }
}
