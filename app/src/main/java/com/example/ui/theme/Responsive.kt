package com.example.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ScreenWidthClass {
    Compact,
    Medium,
    Expanded
}

data class AdaptiveLayoutInfo(
    val widthClass: ScreenWidthClass,
    val maxWidth: Dp,
    val maxHeight: Dp
) {
    val isCompact: Boolean get() = widthClass == ScreenWidthClass.Compact
    val isMediumOrExpanded: Boolean get() = widthClass != ScreenWidthClass.Compact
    val isExpanded: Boolean get() = widthClass == ScreenWidthClass.Expanded
    val isLandscape: Boolean get() = maxWidth > maxHeight
    val isCompactHeight: Boolean get() = maxHeight < 500.dp

    val horizontalPadding: Dp
        get() = when (widthClass) {
            ScreenWidthClass.Compact -> 16.dp
            ScreenWidthClass.Medium -> 24.dp
            ScreenWidthClass.Expanded -> 32.dp
        }

    val contentMaxWidth: Dp
        get() = when (widthClass) {
            ScreenWidthClass.Compact -> maxWidth
            ScreenWidthClass.Medium -> 760.dp
            ScreenWidthClass.Expanded -> 1100.dp
        }

    val formMaxWidth: Dp
        get() = if (widthClass == ScreenWidthClass.Compact) maxWidth else 560.dp

    val twoOrOneColumns: Int
        get() = if (widthClass == ScreenWidthClass.Compact) 1 else 2

    val cardGridColumns: Int
        get() = when {
            maxWidth >= 960.dp -> 3
            maxWidth >= 600.dp -> 2
            else -> 1
        }
}

@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable (AdaptiveLayoutInfo) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val widthClass = when {
            maxWidth >= 840.dp -> ScreenWidthClass.Expanded
            maxWidth >= 600.dp -> ScreenWidthClass.Medium
            else -> ScreenWidthClass.Compact
        }
        val info = AdaptiveLayoutInfo(
            widthClass = widthClass,
            maxWidth = maxWidth,
            maxHeight = maxHeight
        )
        content(info)
    }
}

@Composable
fun <T> ResponsiveGridRow(
    items: List<T>,
    columns: Int,
    horizontalSpacing: Dp = 12.dp,
    itemContent: @Composable (T) -> Unit
) {
    if (columns <= 1) {
        items.forEach { item ->
            Box(modifier = Modifier.fillMaxWidth()) {
                itemContent(item)
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            verticalAlignment = Alignment.Top
        ) {
            for (index in 0 until columns) {
                if (index < items.size) {
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(items[index])
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
