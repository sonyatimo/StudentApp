package com.example.presentation.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp


@Composable
fun CommonTextButton(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = { onClick() }
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = color
        )
    }
}