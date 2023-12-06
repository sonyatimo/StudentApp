package com.example.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.repository.*
import com.example.presentation.components.TableCell
import com.example.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CommonScreen(
    screen: Screen,
    onFloatingActionButtonClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) = Scaffold(
    topBar = {
        TopAppBar(
            title = {
                Text(
                    text = screen.title,
                    fontSize = 24.sp
                )
            },
            backgroundColor = Color.White,
            elevation = 0.dp,
            navigationIcon = if (screen.isRoot) null else {
                {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.Black,
                        modifier = Modifier.clickable {
                            onBackClick?.invoke()
                        }
                    )
                }
            }
        )
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { onFloatingActionButtonClick() }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Додати запис в таблицю")
        }
    },
    modifier = modifier
) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        content()
    }
}