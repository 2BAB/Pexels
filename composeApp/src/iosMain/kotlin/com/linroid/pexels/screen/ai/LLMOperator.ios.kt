package com.linroid.pexels.screen.ai

actual class LLMOperatorFactory(private val llmInferenceDelegate: LLMOperator) {
    actual fun create(): LLMOperator = llmInferenceDelegate // LLMOperatorIOSImpl()
}

/*
// FIXME: MPPLLMInference throws NPE during initialization stage without detailed stacktrace
@OptIn(ExperimentalForeignApi::class)
class LLMOperatorIOSImpl: LLMOperator {

    private val inference: MPPLLMInference

    init {
        val modelPath = NSBundle.mainBundle.pathForResource("gemma-2b-it-gpu-int4", "bin")

        val options = MPPLLMInferenceOptions(modelPath!!)
        options.setModelPath(modelPath!!)
        options.setMaxTokens(2048)
        options.setTopk(40)
        options.setTemperature(0.8f)
        options.setRandomSeed(102)

        inference = MPPLLMInference(options, null) // NPE throws here!!
    }

    override fun sizeInTokens(text: String): Int {
        return 0
    }

    override fun generateResponse(inputText: String): String {
        return inference.generateResponseWithInputText(inputText, null)!!
    }

    override fun generateResponseAsync(inputText: String, ...) {
        // inference.generateResponseAsyncWithInputText(...)
    }

}
*/
