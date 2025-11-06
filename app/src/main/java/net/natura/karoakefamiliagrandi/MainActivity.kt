package net.natura.karoakefamiliagrandi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.natura.karoakefamiliagrandi.domain.Scoring
import net.natura.karoakefamiliagrandi.domain.model.Song
import net.natura.karoakefamiliagrandi.domain.util.YoutubeIdExtractor
import net.natura.karoakefamiliagrandi.playback.YouTubeIntentPlayer
import net.natura.karoakefamiliagrandi.audio.LiveAnalyzer

class MainActivity : ComponentActivity() {
    private lateinit var yt: YouTubeIntentPlayer
    private var analyzer: LiveAnalyzer? = null
    private val scoring = Scoring()
    private val queue = mutableStateListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        yt = YouTubeIntentPlayer(this)
        setContent { AppUI() }
    }

    @Composable
    private fun AppUI() {
        var rms by remember { mutableStateOf(0.0) }
        var energy by remember { mutableStateOf(0) }
        var stability by remember { mutableStateOf(0) }
        var countdown by remember { mutableStateOf<Int?>(null) }

        fun ensureMicPermission(onGranted: () -> Unit) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                onGranted()
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
            }
        }

        fun startMic() {
            analyzer = LiveAnalyzer { r, p ->
                rms = r
                val (e, s) = scoring.update(r, p)
                energy = e; stability = s
            }.also { it.start() }
        }

        MaterialTheme {
            Box(Modifier.fillMaxSize()) {
                HomeScreen(
                    queue = queue,
                    onAdd = { input ->
                        YoutubeIdExtractor.extractId(input)?.let { id ->
                            queue.add(Song(id = id, title = "Música $id"))
                        }
                    },
                    onPlay = { song ->
                        countdown = 3
                        lifecycleScope.launch {
                            for (i in 3 downTo 1) { countdown = i; delay(1000) }
                            countdown = null
                            ensureMicPermission { startMic() }
                            yt.play(song.id)
                        }
                    },
                    onRemove = { song -> queue.remove(song) }
                )

                val partial = (energy + stability).coerceIn(0, 100)
                ShowOverlay(rms = rms, partialScore = partial, countdown = countdown)
            }
        }
    }
}

@Composable
fun HomeScreen(
    queue: List<Song>,
    onAdd: (String) -> Unit,
    onPlay: (Song) -> Unit,
    onRemove: (Song) -> Unit
) {
    var url by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Karoakefamiliagrandi", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Cole o link ou ID do YouTube") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Button(onClick = { onAdd(url); url = "" }) { Text("Adicionar") }
        }
        Spacer(Modifier.height(24.dp))
        Text("Fila", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        queue.forEach { song ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(song.title.ifBlank { song.id }, Modifier.weight(1f))
                TextButton(onClick = { onPlay(song) }) { Text("Reproduzir") }
                TextButton(onClick = { onRemove(song) }) { Text("Remover") }
            }
            Divider()
        }
    }
}

@Composable
fun ShowOverlay(rms: Double, partialScore: Int, countdown: Int?) {
    Box(Modifier.fillMaxSize()) {
        if (countdown != null && countdown > 0) {
            Text(
                countdown.toString(),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(Modifier.align(Alignment.BottomCenter).padding(24.dp)) {
            LinearProgressIndicator(progress = ((rms / 4000.0).coerceIn(0.0, 1.0)).toFloat())
            Spacer(Modifier.height(8.dp))
            Text("Pontuação parcial: " + partialScore)
        }
    }
}
