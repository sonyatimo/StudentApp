package presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.repository.CourseDataSource
import com.example.presentation.components.*
import com.example.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CoursesScreen(
    scope: CoroutineScope,
    courseRepository: CourseDataSource,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }

    var nameToEdit by remember { mutableStateOf("") }
    var isExamToEdit by remember { mutableStateOf("") }

    var courseId by remember { mutableStateOf<Long?>(null) }

    CommonScreen(
        screen = Screen.COURSES,
        onFloatingActionButtonClick = {
            nameToEdit = ""
            isExamToEdit = ""

            courseId = null

            dialogTitle = "Додати курс"
            isDialogVisible = true
        },
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        val courses = courseRepository.getAllCourses()
            .collectAsState(initial = emptyList())
            .value

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 18.dp, horizontal = 24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
                ) {
                    TableCell(text = "ID", weight = .1f, isHeader = true)
                    TableCell(text = "Назва курсу", weight = .6f, isHeader = true)
                    TableCell(text = "Іспит", weight = .2f, isHeader = true)
                    TableCell(text = "", weight = .05f, isHeader = true)
                    TableCell(text = "", weight = .05f, isHeader = true)
                }
                Divider(color = Color.LightGray, thickness = 2.dp)
            }

            if (courses.isEmpty()) {
                item { EmptyTableLabel() }
            }

            itemsIndexed(courses) { index, course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Max)
                ) {
                    TableCell(text = course.id.toString(), weight = .1f)
                    TableCell(text = course.name, weight = .6f)
                    TableCell(text = course.isExam?.toText() ?: "-", weight = .2f)
                    TableCell(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редагувати",
                        tint = Color.DarkGray,
                        weight = .05f
                    ) {
                        nameToEdit = course.name
                        isExamToEdit = course.isExam?.toText() ?: ""

                        courseId = course.id

                        dialogTitle = "Редагувати курс"
                        isDialogVisible = true
                    }
                    TableCell(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Видалити",
                        tint = Color.Red,
                        weight = .05f
                    ) {
                        scope.launch {
                            courseRepository.deleteCourseById(course.id)
                        }
                    }
                }
                if (index != courses.lastIndex) {
                    Divider(color = Color.LightGray, thickness = 2.dp)
                }
            }
        }

        if (isDialogVisible) {
            CourseDialog(
                title = dialogTitle,
                initialName = nameToEdit,
                initialIsExam = isExamToEdit,
                onDismissRequest = {
                    dialogTitle = ""
                    isDialogVisible = false
                },
                onSaveClick = { name, isExam ->
                    val id = courseId
                    if (id == null) {
                        scope.launch {
                            courseRepository.insertCourse(
                                name = name,
                                isExam = isExam.toLong(),
                            )
                        }
                    } else {
                        scope.launch {
                            courseRepository.updateCourseById(
                                id = id,
                                name = name,
                                isExam = isExam.toLong()
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CourseDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (name: String, isExam: Boolean) -> Unit,
    initialName: String = "",
    initialIsExam: String = "",
) = CommonDialog(
    title = title,
    onDismissRequest = onDismissRequest
) {
    var name by remember { mutableStateOf(initialName) }
    var isExamText by remember { mutableStateOf(initialIsExam) }

    var isNameError by remember { mutableStateOf(false) }
    var isExamError by remember { mutableStateOf(false) }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    TextField(
        value = name,
        onValueChange = {
            name = it
            isNameError = false
        },
        singleLine = true,
        label = {
            Text(text = "Назва курсу")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isNameError,
        modifier = Modifier.fillMaxWidth()
    )

    DropDownMenu(
        value = isExamText,
        items = listOf("Так", "Ні"),
        onItemClick = {
            isExamText = it
            isDropdownExpanded = false
            isExamError = false
        },
        label = "Чи передбачено іспит?",
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        onDismissRequest = { isDropdownExpanded = false },
        isError = isExamError,
        modifier = Modifier
    )

    Row {
        CommonTextButton(title = "Скасувати", color = Color.Black) { onDismissRequest() }
        CommonTextButton(title = "Зберегти", color = Color.Black) {
            if (name.isBlank()) isNameError = true
            if (isExamText.isBlank()) isExamError = true
            if (name.isBlank() || isExamText.isBlank()) return@CommonTextButton

            val isExam = isExamText.toBoolean()
            onSaveClick(name, isExam)
            onDismissRequest()
        }
    }
}

private fun String.toBoolean() = this.lowercase() == "так"

private fun Long.toText() = if (this == 1L) "Так" else "Ні"

private fun Boolean.toLong() = if (this) 1L else 0L