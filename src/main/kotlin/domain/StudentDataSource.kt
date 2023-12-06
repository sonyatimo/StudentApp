package com.example.domain.repository

import com.example.database.StudentEntity
import kotlinx.coroutines.flow.Flow

interface StudentDataSource {

    fun getStudentById(id: Long): StudentEntity?

    fun getAllStudents(): Flow<List<StudentEntity>>

    suspend fun insertStudent(firstName: String, lastName: String, groupId: Long)

    suspend fun updateStudentById(id: Long, firstName: String, lastName: String, groupId: Long)

    suspend fun deleteStudentById(id: Long)
}