package com.linroid.pexels.screen.ai

expect class LLMOperatorFactory {
    fun create(): LLMOperator
}

interface LLMOperator {

    fun sizeInTokens(text: String): Int
    suspend fun generateResponse(inputText: String): String
    suspend fun generateResponseAsync(inputText: String,
                              progress: (partialResponse: String) -> Unit,
                              completion: (completeResponse: String) -> Unit)

}
