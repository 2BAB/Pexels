package com.linroid.pexels.screen.ai

import cocoapods.MediaPipeTasksGenAI.MPPLLMInference
import cocoapods.MediaPipeTasksGenAI.MPPLLMInferenceOptions
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle

actual class LLMOperatorFactory actual constructor() {
    actual fun create(): LLMOperator = LLMOperatorIOSImpl()
}

@OptIn(ExperimentalForeignApi::class)
class LLMOperatorIOSImpl: LLMOperator {

    private val inference: MPPLLMInference
    init {
        val modelPath = NSBundle.mainBundle.pathForResource("gemma-2b-it-gpu-int4",  "bin")

        val options = MPPLLMInferenceOptions("")
//        options.setModelPath(modelPath!!)
        options.setMaxTokens(2048)
        options.setTopk(40)
        options.setTemperature(0.8f)
        options.setRandomSeed(102)
        inference = MPPLLMInference(options, null)
    }
    override fun sizeInTokens(text: String): Int {
        return 0
    }

    override fun generateResponse(inputText: String): String {
        return "dummy" // inference.generateResponseWithInputText(inputText, null)!!
    }

    override fun generateResponseAsync(inputText: String) {

    }

}