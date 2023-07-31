package com.fone.user.domain.repository

import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

interface MessageRepository {
    fun generateRandomCode(): String {
        return String.format("%05d", rng.nextInt(0, 1000000))
    }
}

private val rng = Random(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
