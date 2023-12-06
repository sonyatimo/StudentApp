package data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.database.GroupEntity
import com.example.database.AppDatabase
import com.example.domain.repository.GroupDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GroupDataSourceImpl(
    db: AppDatabase
): GroupDataSource {

    private val queries = db.groupEntityQueries

    override fun getGroupById(id: Long): GroupEntity? {
        return queries.getGroupById(id).executeAsOneOrNull()
    }

    override fun getAllGroups(): Flow<List<GroupEntity>> {
        return queries.getAllGroups().asFlow().mapToList(Dispatchers.Default)
    }
}