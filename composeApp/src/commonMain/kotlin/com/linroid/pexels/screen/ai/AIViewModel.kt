package com.linroid.pexels.screen.ai

import cafe.adriel.voyager.core.model.ScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AIViewModel: ScreenModel, KoinComponent {

    private val coroutineScope = MainScope()
    private val llmOperator: LLMOperator by inject()

    override fun onDispose() {
        super.onDispose()
        coroutineScope.cancel()
    }

    fun generateResponse(message: String) {
        coroutineScope.launch {
            val res = llmOperator.generateResponse(message)
            Napier.i(tag ="AIViewModel", message = res)
        }
    }

}