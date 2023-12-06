package com.example.presentation.navigation

enum class Screen(val title: String, val isRoot: Boolean = false) {
    COURSES("Курси", true),
    STUDENTS("Студенти", true),
    STUDENT_PERFORMANCE("Успішність студента"),
}