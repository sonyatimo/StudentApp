package com.example.domain.repository

import com.example.database.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupDataSource {

    fun getGroupById(id: Long): GroupEntity?

    fun getAllGroups(): Flow<List<GroupEntity>>
}