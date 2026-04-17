package com.genesis.app.data.websocket

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreationWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private var webSocket: WebSocket? = null

    // Emit progress updates as a Pair of (message, progress_float)
    private val _progressUpdates = MutableSharedFlow<Pair<String, Float>>(extraBufferCapacity = 10)
    val progressUpdates: SharedFlow<Pair<String, Float>> = _progressUpdates

    fun connect(token: String) {
        val request = Request.Builder()
            // Assume backend endpoint for creation progress
            .url("ws://10.0.2.2:8000/ws/social?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("CreationWS", "Connected to creation progress stream")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("CreationWS", "Progress message received: $text")
                try {
                    val json = JSONObject(text)
                    if (json.has("progress") && json.has("message")) {
                        val progress = json.getDouble("progress").toFloat()
                        val message = json.getString("message")
                        _progressUpdates.tryEmit(Pair(message, progress))
                    }
                } catch (e: Exception) {
                    Log.e("CreationWS", "Error parsing progress JSON", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("CreationWS", "Error", t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("CreationWS", "Closed: $reason")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Creation finished or cancelled")
        webSocket = null
    }
}
