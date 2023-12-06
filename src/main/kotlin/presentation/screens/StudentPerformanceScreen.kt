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
import androidx.compose.ui.unit.sp
import com.example.database.CourseEntity
import com.example.database.GroupEntity
import com.example.database.StudentEntity
import com.example.domain.repository.CourseDataSource
import com.example.domain.repository.GroupDataSource
import com.example.domain.repository.StudentCourseDataSource
import com.example.domain.repository.StudentDataSource
import com.example.presentation.components.*
import com.example.presentation.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun StudentPerformanceScreen(
    scope: CoroutineScope,
    studentCourseRepository: StudentCourseDataSource,
    studentRepository: StudentDataSource,
    courseRepository: CourseDataSource,
    groupRepository: GroupDataSource,
    studentId: Long,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var dialogTitle by remember { mutableStateOf("") }
    var isDialogVisible by remember { mutableStateOf(false) }

    var courseToEdit by remember { mutableStateOf("") }
    var currentMarkToEdit by remember { mutableStateOf("") }
    var examMarkToEdit by remember { mutableStateOf("") }

    var studentCourseId by remember { mutableStateOf<Long?>(null) }

    val student = studentRepository.getStudentById(studentId) ?: return
    val group = groupRepository.getGroupById(student.groupId) ?: return

    CommonScreen(
        screen = Screen.STUDENT_PERFORMANCE,
        onFloatingActionButtonClick = {
            courseToEdit = ""
            currentMarkToEdit = ""
            examMarkToEdit = ""

            studentCourseId = null

            dialogTitle = "Додати дисципліну"
            isDialogVisible = true
        },
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        val studentCourses = studentCourseRepository.getAllCoursesByStudentId(studentId = student.id)
            .collectAsState(initial = emptyList())
            .value
        val allCourses = courseRepository.getAllCourses()
            .collectAsState(initial = emptyList())
            .value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 18.dp, horizontal = 24.dp)
        ) {
            Header(student = student, group = group)

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Row(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
                        TableCell(text = "ID", weight = .1f, isHeader = true)
                        TableCell(text = "Назва дисципліни", weight = .2f, isHeader = true)
                        TableCell(text = "Поточна оцінка", weight = .2f, isHeader = true)
                        TableCell(text = "Оцінка за іспит", weight = .2f, isHeader = true)
                        TableCell(text = "Загальна оцінка", weight = .2f, isHeader = true)
                        TableCell(text = "", weight = .05f, isHeader = true)
                        TableCell(text = "", weight = .05f, isHeader = true)
                    }
                    Divider(color = Color.LightGray, thickness = 2.dp)
                }

                if (studentCourses.isEmpty()) {
                    item { EmptyTableLabel() }
                }

                itemsIndexed(studentCourses) { index, studentCourse ->
                    val course = courseRepository.getCourseById(id = studentCourse.courseId) ?: return@itemsIndexed

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(intrinsicSize = IntrinsicSize.Max)
                    ) {
                        TableCell(text = studentCourse.id.toString(), weight = .1f)
                        TableCell(text = course.name, weight = .2f)
                        TableCell(text = studentCourse.currentMark.toString(), weight = .2f)
                        TableCell(text = studentCourse.examMark.toString(), weight = .2f)
                        TableCell(text = studentCourse.totalMark.toString(), weight = .2f)
                        TableCell(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редагувати",
                            tint = Color.DarkGray,
                            weight = .05f
                        ) {
                            courseToEdit = course.name
                            currentMarkToEdit = studentCourse.currentMark.toString()
                            examMarkToEdit = studentCourse.examMark.toString()

                            studentCourseId = studentCourse.id

                            dialogTitle = "Редагувати оцінки з дисципліни"
                            isDialogVisible = true
                        }
                        TableCell(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Видалити",
                            tint = Color.Red,
                            weight = .05f
                        ) {
                            scope.launch {
                                studentCourseRepository.deleteStudentCourseById(studentCourse.id)
                            }
                        }
                    }
                    if (index != studentCourses.lastIndex) {
                        Divider(color = Color.LightGray, thickness = 2.dp)
                    }
                }
            }
        }

        if (isDialogVisible) {
            val id = studentCourseId
            if (id == null) {
                StudentPerformanceDialog(
                    title = dialogTitle,
                    initialCourse = courseToEdit,
                    initialCurrentMark = currentMarkToEdit,
                    initialExamMark = examMarkToEdit,
                    courses = allCourses,
                    onDismissRequest = {
                        dialogTitle = ""
                        isDialogVisible = false
                    },
                    onSaveClick = { courseId, currentMark, examMark, totalMark ->
                        scope.launch {
                            studentCourseRepository.insertStudentCourse(
                                studentId = studentId,
                                courseId = courseId,
                                currentMark = currentMark,
                                examMark = examMark,
                                totalMark = totalMark
                            )
                        }
                    }
                )
            } else {
                val courseId = studentCourseRepository.getStudentCourseById(id = id)?.courseId
                val isExam = courseId?.let { courseRepository.getCourseById(id = it)?.isExam ?: 0 }
                StudentPerformanceDialog(
                    title = dialogTitle,
                    isExam = isExam ?: 0,
                    initialCurrentMark = currentMarkToEdit,
                    initialExamMark = examMarkToEdit,
                    onDismissRequest = {
                        dialogTitle = ""
                        isDialogVisible = false
                    },
                    onSaveClick = { currentMark, examMark, totalMark ->
                        scope.launch {
                            studentCourseRepository.updateStudentCourse(
                                id = id,
                                currentMark = currentMark,
                                examMark = examMark,
                                totalMark = totalMark
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Header(
    student: StudentEntity,
    group: GroupEntity
) = Column(modifier = Modifier.fillMaxWidth()) {
    Text(
        text = student.firstName + " " + student.lastName,
        fontSize = 24.sp
    )

    Text(text = "Група: ${group.code}")
}

@Composable
private fun StudentPerformanceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (courseId: Long, currentMark: Long, examMark: Long?, totalMark: Long?) -> Unit,
    courses: List<CourseEntity>,
    initialCourse: String = "",
    initialCurrentMark: String = "",
    initialExamMark: String = "",
) = CommonDialog(
    title = title,
    onDismissRequest = onDismissRequest
) {
    var course by remember { mutableStateOf(initialCourse) }
    var isExam by remember { mutableStateOf(0L) }
    var currentMark by remember { mutableStateOf(initialCurrentMark) }
    var examMark by remember { mutableStateOf(initialExamMark) }
    if (isExam == 0L) examMark = ""

    var isCourseError by remember { mutableStateOf(false) }
    var isCurrentMarkError by remember { mutableStateOf(false) }
    var isExamMarkError by remember { mutableStateOf(false) }

    var isDropdownExpanded by remember { mutableStateOf(false) }


    DropDownMenu(
        value = course,
        items = courses.map { it.name },
        onItemClick = { courseName ->
            course = courseName
            isExam = courses.find { it.name == courseName }?.isExam ?: 1
            isDropdownExpanded = false
            isCourseError = false
        },
        label = "Дисципліна",
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        onDismissRequest = { isDropdownExpanded = false },
        isError = isCourseError,
        modifier = Modifier
    )

    val pattern = remember { Regex("^\\d+\$") }
    TextField(
        value = currentMark,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                currentMark = it
            }
            isCurrentMarkError = false
        },
        singleLine = true,
        label = {
            Text(text = "Поточна оцінка")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isCurrentMarkError,
        modifier = Modifier.fillMaxWidth()
    )

    TextField(
        value = examMark,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                examMark = it
            }
            isExamMarkError = false
        },
        singleLine = true,
        label = {
            Text(text = "Оцінка за іспит")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isExamMarkError,
        enabled = isExam == 1L,
        modifier = Modifier.fillMaxWidth()
    )

    Row {
        CommonTextButton(title = "Скасувати", color = Color.Black) { onDismissRequest() }
        CommonTextButton(title = "Зберегти", color = Color.Black) {
            val courseId = courses.find { it.name == course }?.id
            if (course.isBlank()) isCourseError = true
            if (course.isBlank() || courseId == null) return@CommonTextButton

            if (isExam == 1L) {
                val parsedCurrentMark = currentMark.toLongOrNull()
                val parsedExamMark = examMark.toLongOrNull()

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..60) isCurrentMarkError = true
                if (parsedExamMark == null || parsedExamMark !in 0..40) isExamMarkError = true

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..60) return@CommonTextButton
                if (parsedExamMark == null || parsedExamMark !in 0..40) return@CommonTextButton

                val totalMark = parsedCurrentMark + parsedExamMark

                onSaveClick(courseId, parsedCurrentMark, parsedExamMark, totalMark)
            } else {
                val parsedCurrentMark = currentMark.toLongOrNull()

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..100) isCurrentMarkError = true
                isExamMarkError = false

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..100) return@CommonTextButton

                onSaveClick(courseId, parsedCurrentMark, null, parsedCurrentMark)
            }

            onDismissRequest()
        }
    }
}

@Composable
private fun StudentPerformanceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (currentMark: Long?, examMark: Long?, totalMark: Long?) -> Unit,
    isExam: Long,
    initialCurrentMark: String = "",
    initialExamMark: String = ""
) = CommonDialog(
    title = title,
    onDismissRequest = onDismissRequest
) {
    var currentMark by remember { mutableStateOf(initialCurrentMark) }
    var examMark by remember { mutableStateOf(initialExamMark) }

    var isCurrentMarkError by remember { mutableStateOf(false) }
    var isExamMarkError by remember { mutableStateOf(false) }

    val pattern = remember { Regex("^\\d+\$") }
    TextField(
        value = currentMark,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                currentMark = it
            }
            isCurrentMarkError = false
        },
        singleLine = true,
        label = {
            Text(text = "Поточна оцінка")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        isError = isCurrentMarkError,
        modifier = Modifier.fillMaxWidth()
    )

    TextField(
        value = examMark,
        onValueChange = {
            if (it.isEmpty() || it.matches(pattern)) {
                examMark = it
            }
            isExamMarkError = false
        },
        singleLine = true,
        label = {
            Text(text = "Оцінка за іспит")
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        enabled = isExam == 1L,
        isError = isExamMarkError,
        modifier = Modifier.fillMaxWidth()
    )

    Row {
        CommonTextButton(title = "Скасувати", color = Color.Black) { onDismissRequest() }
        CommonTextButton(title = "Зберегти", color = Color.Black) {
            if (isExam == 1L) {
                val parsedCurrentMark = currentMark.toLongOrNull()
                val parsedExamMark = examMark.toLongOrNull()

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..60) isCurrentMarkError = true
                if (parsedExamMark == null || parsedExamMark !in 0..40) isExamMarkError = true

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..60) return@CommonTextButton
                if (parsedExamMark == null || parsedExamMark !in 0..40) return@CommonTextButton

                val totalMark = parsedCurrentMark + parsedExamMark

                onSaveClick(parsedCurrentMark, parsedExamMark, totalMark)
            } else {
                val parsedCurrentMark = currentMark.toLongOrNull()

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..100) isCurrentMarkError = true
                isExamMarkError = false

                if (parsedCurrentMark == null || parsedCurrentMark !in 0..100) return@CommonTextButton

                onSaveClick(parsedCurrentMark, null, parsedCurrentMark)
            }

            onDismissRequest()
        }
    }
}