import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.MediaPipeTasksGenAI.MPPLLMInference
import cocoapods.MediaPipeTasksGenAI.MPPLLMInferenceOptions
import com.linroid.pexels.App
import com.linroid.pexels.Startup
import com.linroid.pexels.screen.ai.LLMOperatorFactory
import io.github.aakira.napier.Napier
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.value
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

@OptIn(ExperimentalForeignApi::class)
fun onStartup() {
    Startup.run(iOSModule)
    try {
        val modelPath = NSBundle.mainBundle.pathForResource("gemma-2b-it-gpu-int4", "bin")
        Napier.i("modelPath: $modelPath") // it's correct and print the file path on iOS
        // MPPLLMInferenceOptions()
        val options = MPPLLMInferenceOptions(modelPath!!)
        options.setMaxTokens(1000)
        // options.setTopk(40)
        // options.setTemperature(0.8f)
        // options.setRandomSeed(102)

        // Both get crashed
        memScoped {
            val pp: CPointerVar<ObjCObjectVar<NSError?>> = allocPointerTo()
            val inference = MPPLLMInference(options, pp.value)
            Napier.i(pp.value.toString())
        }
//        memScoped {
//            val pp: CPointerVar<ObjCObjectVar<NSError?>> = allocPointerTo()
//            val inference = MPPLLMInference(modelPath, pp.value)
//        }

    } catch (e: Exception) {
        Napier.i("llm error occurred")
        Napier.e(e.message ?: "unknown llm error")
        e.printStackTrace()
    }


}

val iOSModule = module {
    factory { LLMOperatorFactory() }
}