package ru.tanexc.roomservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.tanexc.roomservice.data.entity.Question

interface QuestionRepository: JpaRepository<Question, Long>