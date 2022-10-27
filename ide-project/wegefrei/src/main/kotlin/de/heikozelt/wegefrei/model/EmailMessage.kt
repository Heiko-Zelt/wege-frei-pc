package de.heikozelt.wegefrei.model

data class EmailMessage(
    val fromName: String,
    val fromAddress: String,
    val toName: String,
    val toAddress: String,
    val subject: String,
    val content: String
)