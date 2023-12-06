package com.example.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false
) = Text(
    text = text,
    fontWeight = if (isHeader) FontWeight.SemiBold else FontWeight.Normal,
    modifier = Modifier
        .fillMaxHeight()
        .weight(weight)
        .padding(8.dp)
)

@Composable
fun RowScope.TableCell(
    imageVector: ImageVector,
    contentDescription: String,
    tint: Color,
    weight: Float,
    onClick: () -> Unit
) = Box(
    modifier = Modifier
        .fillMaxHeight()
        .weight(weight)
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
            .padding(8.dp)
            .align(Alignment.Center)
    )
}