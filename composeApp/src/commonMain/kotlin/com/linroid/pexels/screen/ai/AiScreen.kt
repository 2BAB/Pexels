package com.linroid.pexels.screen.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pexels.composeapp.generated.resources.Res
import pexels.composeapp.generated.resources.force_json

// 6.
class AiScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val aiViewModel = rememberScreenModel { AIViewModel() }
        val chatViewModel = rememberScreenModel { ChatViewModel(aiViewModel) }
        var showLoading by rememberSaveable { mutableStateOf(true) }
        Column {
            TopAppBar(
                title = {
                    Text("AI Samples")
                },
                navigationIcon = {
                    val navigator = LocalNavigator.currentOrThrow
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource( Res.string.force_json))
                        Checkbox(
                            checked = chatViewModel.forceJSON,
                            onCheckedChange = { chatViewModel.switchForceJson(it) }
                        )
                    }
                }

            )
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White)
            ) {
                if (showLoading) {
                    LoadingRoute(aiViewModel, onModelLoaded = {
                        showLoading = false
                    })
                } else {
                    ChatRoute(chatViewModel)
                }
            }
        }
    }

}

@Preview
@Composable
fun SimpleComposablePreview() {
    AiScreen()
}