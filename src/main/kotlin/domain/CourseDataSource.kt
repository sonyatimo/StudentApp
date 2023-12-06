package com.example.domain.repository

import com.example.database.CourseEntity
import kotlinx.coroutines.flow.Flow

interface CourseDataSource {

    fun getCourseById(id: Long): CourseEntity?

    fun getAllCourses(): Flow<List<CourseEntity>>

    suspend fun insertCourse(name: String, isExam: Long)

    suspend fun updateCourseById(id: Long, name: String, isExam: Long)

    suspend fun deleteCourseById(id: Long)
}