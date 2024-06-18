package com.linroid.pexels.screen.ai

expect class LLMOperatorFactory() {
    fun create(): LLMOperator
}

interface LLMOperator {

    fun sizeInTokens(text: String): Int
    fun generateResponse(inputText: String): String
    fun generateResponseAsync(inputText: String)

}