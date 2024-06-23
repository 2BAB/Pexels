package com.linroid.pexels.screen.ai

import com.linroid.pexels.randomUUID

/**
 * Used to represent a ChatMessage
 */
data class ChatMessage(
    val id: String = randomUUID(),
    val message: String = "",
    val author: String,
    val isLoading: Boolean = false
) {
    val isFromUser: Boolean
        get() = author == USER_PREFIX
}