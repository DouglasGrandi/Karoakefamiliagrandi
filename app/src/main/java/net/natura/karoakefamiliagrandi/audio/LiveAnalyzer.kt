package net.natura.karoakefamiliagrandi.audio

import android.media.*
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.sqrt

class LiveAnalyzer(
    private val sampleRate: Int = 44100,
    private val bufferSize: Int = 2048,
    private val onMetrics: (rms: Double, pitchHz: Double?) -> Unit
) {
    private var isRunning = false
    private var recorder: AudioRecord? = null

    fun start() {
        val min = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            max(min, bufferSize * 2)
        )
        isRunning = true
        recorder?.startRecording()
        thread(start = true) {
            val buf = ShortArray(bufferSize)
            while (isRunning) {
                val read = recorder?.read(buf, 0, bufferSize) ?: 0
                if (read > 0) {
                    val rms = computeRms(buf, read)
                    val pitch = estimatePitch(buf, read, sampleRate)
                    onMetrics(rms, pitch)
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    private fun computeRms(data: ShortArray, n: Int): Double {
        var sum = 0.0
        for (i in 0 until n) sum += (data[i] * data[i]).toDouble()
        return sqrt(sum / n)
    }

    private fun estimatePitch(data: ShortArray, n: Int, sr: Int): Double? {
        val maxLag = sr / 80
        val minLag = sr / 1000
        var bestLag = 0
        var bestCorr = 0.0
        for (lag in minLag..maxLag) {
            var corr = 0.0
            for (i in 0 until n - lag) {
                corr += data[i] * data[i + lag]
            }
            if (corr > bestCorr) { bestCorr = corr; bestLag = lag }
        }
        return if (bestLag > 0) sr.toDouble() / bestLag else null
    }
}
