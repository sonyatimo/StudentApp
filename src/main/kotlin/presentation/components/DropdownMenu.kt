package com.example.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownMenu(
    value: String,
    items: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    onItemClick: (String) -> Unit,
    placeholder: String = "",
    label: String = "",
    isError: Boolean = false,
    modifier: Modifier = Modifier
) = ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = onExpandedChange,
    modifier = modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = {
            Text(text = label)
        },
        textStyle = TextStyle(
            fontSize = 14.sp
        ),
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
        },
        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        modifier = Modifier.fillMaxWidth()
    )

    ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        val menuItemModifier = Modifier.height(32.dp)

        if (items.isEmpty()) {
            DropdownMenuItem(
                onClick = onDismissRequest,
                modifier = menuItemModifier
            ) {
                Text(
                    text = "No elements",
                    fontSize = 14.sp
                )
            }
            return@ExposedDropdownMenu
        }

        items.forEach { item ->
            DropdownMenuItem(
                onClick = { onItemClick(item) },
                modifier = menuItemModifier
            ) {
                Text(
                    text = item,
                    fontSize = 14.sp
                )
            }
        }
    }
}