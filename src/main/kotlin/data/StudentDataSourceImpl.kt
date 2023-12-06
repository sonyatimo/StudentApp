package data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.database.StudentEntity
import com.example.database.AppDatabase
import com.example.domain.repository.StudentDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudentDataSourceImpl(
    db: AppDatabase
): StudentDataSource {

    private val queries = db.studentEntityQueries

    override fun getStudentById(id: Long): StudentEntity? {
        return queries.getStudentById(id).executeAsOneOrNull()
    }

    override fun getAllStudents(): Flow<List<StudentEntity>> {
        return queries.getAllStudents().asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun insertStudent(firstName: String, lastName: String, groupId: Long) {
        withContext(Dispatchers.IO) {
            queries.insertStudent(firstName, lastName, groupId)
        }
    }

    override suspend fun updateStudentById(id: Long, firstName: String, lastName: String, groupId: Long) {
        withContext(Dispatchers.IO) {
            queries.updateStudentById(firstName, lastName, groupId, id)
        }
    }

    override suspend fun deleteStudentById(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteStudentById(id)
        }
    }
}