package data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.database.CourseEntity
import com.example.database.AppDatabase
import com.example.domain.repository.CourseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CourseDataSourceImpl(
    db: AppDatabase
): CourseDataSource {

    private val queries = db.courseEntityQueries

    override fun getCourseById(id: Long): CourseEntity? {
        return queries.getCourseById(id).executeAsOneOrNull()
    }

    override fun getAllCourses(): Flow<List<CourseEntity>> {
        return queries.getAllCourses().asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun insertCourse(name: String, isExam: Long) {
        withContext(Dispatchers.IO) {
            queries.insertCourse(name, isExam)
        }
    }

    override suspend fun updateCourseById(id: Long, name: String, isExam: Long) {
        withContext(Dispatchers.IO) {
            queries.updateCourseById(name, isExam, id)
        }
    }

    override suspend fun deleteCourseById(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteCourseById(id)
        }
    }
}