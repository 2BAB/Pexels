package com.linroid.pexels

import com.linroid.pexels.screen.ai.LLMOperator
import com.linroid.pexels.screen.ai.LLMOperatorFactory
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

object Startup {
	fun run(platformModule: Module) {
		Napier.base(DebugAntilog())
		startKoin {
			modules(apiModule, sharedModule, platformModule)
		}
	}
}

val sharedModule = module {
	single<LLMOperator> { get<LLMOperatorFactory>().create() }
}