package data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.database.StudentCourseEntity
import com.example.database.AppDatabase
import com.example.domain.repository.StudentCourseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudentCourseDataSourceImpl(
    db: AppDatabase
) : StudentCourseDataSource {

    private val queries = db.studentCourseEntityQueries

    override fun getStudentCourseById(id: Long): StudentCourseEntity? {
        return queries.getStudentCourseById(id).executeAsOneOrNull()
    }

    override fun getAllCoursesByStudentId(studentId: Long): Flow<List<StudentCourseEntity>> {
        return queries.getAllCoursesByStudentId(studentId).asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun insertStudentCourse(
        studentId: Long,
        courseId: Long,
        currentMark: Long?,
        examMark: Long?,
        totalMark: Long?
    ) {
        withContext(Dispatchers.IO) {
            queries.insertStudentCourse(studentId, courseId, currentMark, examMark, totalMark)
        }
    }

    override suspend fun updateStudentCourse(id: Long, currentMark: Long?, examMark: Long?, totalMark: Long?) {
        withContext(Dispatchers.IO) {
            queries.updateStudentCourse(currentMark, examMark, totalMark, id)
        }
    }

    override suspend fun deleteStudentCourseById(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteStudentCourseById(id)
        }
    }
}