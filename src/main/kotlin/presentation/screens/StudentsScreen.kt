package presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.database.GroupEntity
import com.example.domain.repository.GroupDataSource
import com.example.domain.repository.StudentDataSource
import com.example.presentation.components.*
import com.example.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun StudentsScreen(
    scope: CoroutineScope,
    studentRepository: StudentDataSource,
    groupRepository: GroupDataSource,
    onStudentClick: (studentId: Long, screen: Screen) -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var dialogTitle by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }

    var firstNameToEdit by remember { mutableStateOf("") }
    var lastNameToEdit by remember { mutableStateOf("") }
    var groupToEdit by remember { mutableStateOf("") }

    var studentId by remember { mutableStateOf<Long?>(null) }

    CommonScreen(
        screen = Screen.STUDENTS,
        onFloatingActionButtonClick = {
            firstNameToEdit = ""
            lastNameToEdit = ""
            groupToEdit = ""

            studentId = null

            dialogTitle = "Додати студента"
            isDialogVisible = true
        },
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        val students = studentRepository.getAllStudents()
            .collectAsState(initial = emptyList())
            .value

        val groups = groupRepository.getAllGroups()
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
                    TableCell(text = "Імʼя", weight = .3f, isHeader = true)
                    TableCell(text = "Прізвище", weight = .3f, isHeader = true)
                    TableCell(text = "Група", weight = .2f, isHeader = true)
                    TableCell(text = "", weight = .05f, isHeader = true)
                    TableCell(text = "", weight = .05f, isHeader = true)
                }
                Divider(color = Color.LightGray, thickness = 2.dp)
            }

            if (students.isEmpty()) {
                item { EmptyTableLabel() }
            }

            itemsIndexed(students) { index, student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(intrinsicSize = IntrinsicSize.Max)
                        .clickable { onStudentClick(student.id, Screen.STUDENT_PERFORMANCE) }
                ) {
                    val group = groupRepository.getGroupById(student.groupId)

                    TableCell(text = student.id.toString(), weight = .1f)
                    TableCell(text = student.firstName, weight = .3f)
                    TableCell(text = student.lastName, weight = .3f)
                    TableCell(text = group?.code ?: "-", weight = .2f)
                    TableCell(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редагувати",
                        tint = Color.DarkGray,
                        weight = .05f
                    ) {
                        firstNameToEdit = student.firstName
                        lastNameToEdit = student.lastName
                        groupToEdit = group?.code ?: ""

                        studentId = student.id

                        dialogTitle = "Редагувати студента"
                        isDialogVisible = true
                    }
                    TableCell(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Видалити",
                        tint = Color.Red,
                        weight = .05f
                    ) {
                        scope.launch {
                            studentRepository.deleteStudentById(student.id)
                        }
                    }
                }
                if (index != students.lastIndex) {
                    Divider(color = Color.LightGray, thickness = 2.dp)
                }
            }
        }

        if (isDialogVisible) {
            StudentDialog(
                title = dialogTitle,
                initialFirstName = firstNameToEdit,
                initialLastName = lastNameToEdit,
                initialGroup = groupToEdit,
                groups = groups,
                onDismissRequest = {
                    dialogTitle = ""
                    isDialogVisible = false
                },
                onSaveClick = { firstName, lastName, groupId ->
                    val id = studentId
                    if (id == null) {
                        scope.launch {
                            studentRepository.insertStudent(
                                firstName = firstName,
                                lastName = lastName,
                                groupId = groupId
                            )
                        }
                    } else {
                        scope.launch {
                            studentRepository.updateStudentById(
                                id = id,
                                firstName = firstName,
                                lastName = lastName,
                                groupId = groupId
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun StudentDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (firstName: String, lastName: String, groupId: Long) -> Unit,
    groups: List<GroupEntity>,
    initialFirstName: String = "",
    initialLastName: String = "",
    initialGroup: String = ""
) = CommonDialog(
    title = title,
    onDismissRequest = onDismissRequest
) {
    var firstName by remember { mutableStateOf(initialFirstName) }
    var lastName by remember { mutableStateOf(initialLastName) }
    var group by remember { mutableStateOf(initialGroup) }

    var isFirstNameError by remember { mutableStateOf(false) }
    var isLastNameError by remember { mutableStateOf(false) }
    var isGroupError by remember { mutableStateOf(false) }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    TextField(
        value = firstName,
        onValueChange = {
            firstName = it
            isFirstNameError = false
        },
        singleLine = true,
        label = {
            Text(text = "Імʼя")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isFirstNameError,
        modifier = Modifier.fillMaxWidth()
    )

    TextField(
        value = lastName,
        onValueChange = {
            lastName = it
            isLastNameError = false
        },
        singleLine = true,
        label = {
            Text(text = "Прізвище")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isLastNameError,
        modifier = Modifier.fillMaxWidth()
    )

    DropDownMenu(
        value = group,
        items = groups.map { it.code },
        onItemClick = {
            group = it
            isDropdownExpanded = false
            isGroupError = false
        },
        label = "Група",
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        onDismissRequest = { isDropdownExpanded = false },
        isError = isGroupError,
        modifier = Modifier
    )

    Row {
        CommonTextButton(title = "Скасувати", color = Color.Black) { onDismissRequest() }
        CommonTextButton(title = "Зберегти", color = Color.Black) {
            val groupId = groups.find { it.code == group }?.id

            if (firstName.isBlank()) isFirstNameError = true
            if (lastName.isBlank()) isLastNameError = true
            if (group.isBlank()) isGroupError = true
            if (firstName.isBlank() || lastName.isBlank() || groupId == null) return@CommonTextButton

            onSaveClick(firstName, lastName, groupId)
            onDismissRequest()
        }
    }
}
