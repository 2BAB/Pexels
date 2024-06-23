import androidx.compose.ui.window.ComposeUIViewController
import com.linroid.pexels.App
import com.linroid.pexels.Startup
import com.linroid.pexels.screen.ai.LLMOperator
import com.linroid.pexels.screen.ai.LLMOperatorFactory
import com.linroid.pexels.screen.ai.LLMOperatorSwift
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }

fun onStartup(llmInferenceDelegate: LLMOperatorSwift) {
    Startup.run(module {
        single { LLMOperatorFactory(llmInferenceDelegate) }
    })
}
