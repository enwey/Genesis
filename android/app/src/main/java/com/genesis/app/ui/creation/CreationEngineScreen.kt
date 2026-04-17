package com.genesis.app.ui.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.genesis.app.util.Resource
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode

@Composable
fun CreationEngineScreen(
    viewModel: CreationViewModel = hiltViewModel()
) {
    var prompt by remember { mutableStateOf("") }
    val creationState by viewModel.creationState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Creation Engine", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter prompt (e.g., 'a low poly tree')") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.generate(prompt) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate 3D Asset")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (creationState) {
                is Resource.Success -> {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                            ModelViewerPlaceholder(url = creationState.data?.modelUrl ?: "")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        AttributeSection(attributes = creationState.data?.attributes)
                    }
                }
                is Resource.Error -> {
                    Text(text = creationState.message ?: "Error", color = MaterialTheme.colorScheme.error)
                }
                is Resource.Loading -> {
                    if (prompt.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "AI is thinking...")
                        }
                    } else {
                        Text(text = "Your creation will appear here")
                    }
                }
            }
        }
    }
}

@Composable
fun ModelViewerPlaceholder(url: String) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    
    val cameraNode = rememberCameraNode(engine).apply {
        position = Position(z = 4.0f)
    }
    
    val centerNode = rememberNode(engine)
        .addChildNode(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = url
                ),
                scaleToUnits = 2.0f
            )
        )

    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        modelLoader = modelLoader,
        cameraNode = cameraNode,
        childNodes = listOf(centerNode),
        environment = environmentLoader.createHDREnvironment(
            assetFileLocation = "https://sceneview.github.io/assets/environments/sybarm_1k.hdr"
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttributeSection(attributes: Map<String, String>?) {
    if (attributes == null) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Appraisal Result", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            attributes.forEach { (key, value) ->
                AttributeChip(label = key.uppercase(), value = value)
            }
        }
    }
}

@Composable
fun AttributeChip(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = "$label: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
        }
    }
}
