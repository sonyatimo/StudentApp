package com.example.presentation.components

import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationButton(
    label: String,
    colors: ButtonColors,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) = Button(
    colors = colors,
    shape = MaterialTheme.shapes.medium,
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
) {
    Text(text = label)
}