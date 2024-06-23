package com.linroid.pexels

import com.linroid.pexels.screen.ai.LLMOperator
import com.linroid.pexels.screen.ai.LLMOperatorFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

object Startup {
	fun run(platformSpecifiedKoinInitBlock: (koin: KoinApplication) -> Unit) {
		Napier.base(DebugAntilog())
		startKoin {
			modules(apiModule, sharedModule)
			platformSpecifiedKoinInitBlock(this)
		}
	}
}

val sharedModule = module {
	single<LLMOperator> { get<LLMOperatorFactory>().create() }
}