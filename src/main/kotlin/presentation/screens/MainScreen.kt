package presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.database.AppDatabase
import com.example.presentation.components.CommonTextButton
import com.example.presentation.navigation.Screen
import data.CourseDataSourceImpl
import data.GroupDataSourceImpl
import data.StudentCourseDataSourceImpl
import data.StudentDataSourceImpl

@Composable
fun MainScreen(
    db: AppDatabase,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier.fillMaxSize(),
) {
    var currentScreen by remember { mutableStateOf(Screen.STUDENTS) }

    SidePanel(
        modifier = Modifier.fillMaxWidth(0.2f)
    ) {
        currentScreen = it
    }

    Content(
        screen = currentScreen,
        onScreenChange = {
            currentScreen = it
        },
        db = db
    )
}

@Composable
private fun SidePanel(
    modifier: Modifier = Modifier,
    onTabClick: (Screen) -> Unit
) = Column(
    modifier = modifier
        .fillMaxHeight()
        .background(color = MaterialTheme.colors.primary)
        .padding(vertical = 18.dp, horizontal = 12.dp)
) {
    CommonTextButton(title = "Студенти", color = Color.White) { onTabClick(Screen.STUDENTS) }
    CommonTextButton(title = "Курси", color = Color.White) { onTabClick(Screen.COURSES) }
}

@Composable
private fun Content(
    screen: Screen,
    onScreenChange: (Screen) -> Unit,
    db: AppDatabase,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val studentRepository = StudentDataSourceImpl(db)
    val studentCourseRepository = StudentCourseDataSourceImpl(db)
    val courseRepository = CourseDataSourceImpl(db)
    val groupRepository = GroupDataSourceImpl(db)

    var studentId by remember { mutableStateOf(0L) }

    when (screen) {
        Screen.STUDENTS -> StudentsScreen(
            scope = scope,
            studentRepository = studentRepository,
            groupRepository = groupRepository,
            onStudentClick = { id, scr ->
                studentId = id
                onScreenChange(scr)
            },
            modifier = modifier
        )
        Screen.COURSES -> CoursesScreen(
            scope = scope,
            courseRepository = courseRepository,
            modifier = modifier
        )
        Screen.STUDENT_PERFORMANCE -> StudentPerformanceScreen(
            scope = scope,
            studentId = studentId,
            studentRepository = studentRepository,
            studentCourseRepository = studentCourseRepository,
            groupRepository = groupRepository,
            courseRepository = courseRepository,
            onBackClick = { onScreenChange(Screen.STUDENTS) }, //Bad practise
            modifier = modifier
        )
    }
}