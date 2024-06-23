package com.linroid.pexels.screen.ai

import kotlinx.coroutines.flow.Flow

expect class LLMOperatorFactory {
    fun create(): LLMOperator
}

interface LLMOperator {

    /**
     * To load the model into current context.
     * @return 1. null if it went well 2. an error message in string
     */
    suspend fun initModel(): String?

    /**
     * To calculate the token size of a string.
     */
    fun sizeInTokens(text: String): Int

    /**
     * To generate response for give inputText in synchronous way.
     */
    suspend fun generateResponse(inputText: String): String

    /**
     * To generate response for give inputText in asynchronous way.
     * @return A flow with partial response in String and completion flag in Boolean.
     */
    suspend fun generateResponseAsync(inputText: String): Flow<Pair<String, Boolean>>


}
