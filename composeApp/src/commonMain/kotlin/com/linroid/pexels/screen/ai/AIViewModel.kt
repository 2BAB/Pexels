package com.linroid.pexels.screen.ai

import cafe.adriel.voyager.core.model.ScreenModel
import io.github.aakira.napier.Napier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AIViewModel: ScreenModel, KoinComponent {

    private val llmOperator: LLMOperator by inject()

    fun generateResponse(message: String): String {
        val res = llmOperator.generateResponse(message)
        Napier.i(tag ="AIViewModel", message = "generateResponse: $res")
        return res
    }

}