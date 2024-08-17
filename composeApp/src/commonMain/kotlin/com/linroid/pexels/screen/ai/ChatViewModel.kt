package com.linroid.pexels.screen.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.math.max

// 9.
class ChatViewModel(
    private val aiViewModel: AIViewModel
) : ScreenModel {
    private val coroutineScope = MainScope()
    var forceJSON by mutableStateOf(false)
        private set


    // `GemmaUiState()` is optimized for the Gemma model.
    // Replace `GemmaUiState` with `ChatUiState()` if you're using a different model
    private val _uiState: MutableStateFlow<GemmaUiState> = MutableStateFlow(GemmaUiState())
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val _textInputEnabled: MutableStateFlow<Boolean> =
        MutableStateFlow(true)
    val isTextInputEnabled: StateFlow<Boolean> =
        _textInputEnabled.asStateFlow()

    fun sendMessage(userMessage: String) {
        coroutineScope.launch(Dispatchers.IO) {
            _uiState.value.addMessage(userMessage, USER_PREFIX)
            var currentMessageId: String? = _uiState.value.createLoadingMessage()
            setInputEnabled(false)
            try {
                val fullPrompt = _uiState.value.fullPrompt
                if (forceJSON) {
                    var matched = false
                    val maxAttempts = 5
                    var attemptsCount = maxAttempts
                    while (matched.not() && attemptsCount > 0) {
                        attemptsCount--
                        val res = extractJsonObject(aiViewModel.generateResponse(fullPrompt))
                        if (res != null) {
                            matched = true
                            _uiState.value.appendMessage(currentMessageId!!, res, true)
                        }
                    }
                    if (matched.not()) {
                        _uiState.value.appendMessage(currentMessageId!!,
                            "Can not answer in JSON format within $maxAttempts times.", true)
                    }

                    setInputEnabled(true)
                } else {
                    aiViewModel.generateResponseInflow(fullPrompt)
                        .collectIndexed { index, (partialResult, done) ->
                            currentMessageId?.let {
                                if (index == 0) {
                                    _uiState.value.appendFirstMessage(it, partialResult)
                                } else {
                                    _uiState.value.appendMessage(it, partialResult, done)
                                }
                                if (done) {
                                    currentMessageId = null
                                    // Re-enable text input
                                    setInputEnabled(true)
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                _uiState.value.addMessage(e.message ?: "Unknown Error", MODEL_PREFIX)
                setInputEnabled(true)
            }
        }
    }

    private fun setInputEnabled(isEnabled: Boolean) {
        _textInputEnabled.value = isEnabled
    }

    fun switchForceJson(enabled: Boolean) {
        forceJSON = enabled
    }

    override fun onDispose() {
        super.onDispose()
        coroutineScope.cancel()
    }

    private fun extractJsonObject(input: String): String? {
        val jsonRegex = """\{.*?\}""".toRegex()
        val matchResult = jsonRegex.find(input)
        return if (matchResult != null) {
            val jsonString = matchResult.value
            try {
                Json.parseToJsonElement(jsonString).toString()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}
