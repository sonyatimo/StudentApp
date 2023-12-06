package com.example.domain.repository

import com.example.database.StudentCourseEntity
import kotlinx.coroutines.flow.Flow

interface StudentCourseDataSource {

    fun getStudentCourseById(id: Long): StudentCourseEntity?

    fun getAllCoursesByStudentId(studentId: Long): Flow<List<StudentCourseEntity>>

    suspend fun insertStudentCourse(
        studentId: Long,
        courseId: Long,
        currentMark: Long?,
        examMark: Long?,
        totalMark: Long?
    )

    suspend fun updateStudentCourse(id: Long, currentMark: Long?, examMark: Long?, totalMark: Long?)

    suspend fun deleteStudentCourseById(id: Long)
}