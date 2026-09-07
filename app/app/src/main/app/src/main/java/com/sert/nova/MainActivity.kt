package com.sert.nova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(player)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

@Composable
fun MainScreen(player: ExoPlayer) {
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var isLooping by remember { mutableStateOf(false) }
    var loopStart by remember { mutableStateOf(0L) }
    var loopEnd by remember { mutableStateOf(10000L) }

    LaunchedEffect(isLooping) {
        while (isLooping) {
            if (player.currentPosition >= loopEnd) {
                player.seekTo(loopStart)
            }
            delay(100)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Nova Player", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Hız: ${playbackSpeed}x")
        Slider(
            value = playbackSpeed,
            onValueChange = { 
                playbackSpeed = it
                player.playbackParameters = PlaybackParameters(it)
            },
            valueRange = 0.5f..3.0f
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { player.seekTo(player.currentPosition - 10000L) }) { Text("-10 Sn") }
            Button(onClick = { player.seekTo(player.currentPosition + 10000L) }) { Text("+10 Sn") }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("A-B Bölge Tekrarı")
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = isLooping, onCheckedChange = { isLooping = it })
                    Text("Döngüyü Aktif Et")
                }
            }
        }
    }
}
