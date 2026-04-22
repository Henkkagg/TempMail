package model

import kotlin.time.Instant

data class Email(
    val id: Int,
    val sender: String,
    val recipient: String,
    val subject: String,
    val body: String
)
