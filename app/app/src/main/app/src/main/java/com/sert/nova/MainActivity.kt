package com.sert.nova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        var sistemHatasi: String? = null
        
        // Uygulama çökmek yerine hatayı yakalayıp ekrana yansıtacak
        try {
            player = ExoPlayer.Builder(this).build()
        } catch (e: Exception) {
            sistemHatasi = e.stackTraceToString()
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (sistemHatasi != null) {
                        // Eğer hata varsa çökmek yerine kırmızı uyarı ekranı açılacak
                        HataEkrani(sistemHatasi!!)
                    } else {
                        // Hata yoksa uygulamamız normal çalışacak
                        AnaEkran(player!!)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}

@Composable
fun HataEkrani(hataDetayi: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text("Uygulama Başlatılamadı!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Lütfen aşağıdaki hatayı kopyalayıp veya ekran görüntüsü alıp paylaşın:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Text(hataDetayi, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun AnaEkran(player: ExoPlayer) {
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

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Nova Player (Beta Test)", style = MaterialTheme.typography.headlineMedium)
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
                Text("A-B Bölge Tekrarı", color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = isLooping, onCheckedChange = { isLooping = it })
                    Text("Döngüyü Aktif Et")
                }
            }
        }
    }
}
