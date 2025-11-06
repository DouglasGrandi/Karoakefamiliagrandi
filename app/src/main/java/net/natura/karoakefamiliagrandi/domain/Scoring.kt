package net.natura.karoakefamiliagrandi.domain

import java.util.ArrayDeque
import kotlin.math.abs

class Scoring(
    private val minRms: Double = 300.0,
    private val maxRms: Double = 4000.0
) {
    private val lastPitches: ArrayDeque<Double> = ArrayDeque()

    fun update(rms: Double, pitchHz: Double?): Pair<Int, Int> {
        val energyScore = ((rms - minRms) / (maxRms - minRms)).coerceIn(0.0, 1.0) * 50.0
        if (pitchHz != null) {
            lastPitches.addLast(pitchHz)
            if (lastPitches.size > 12) lastPitches.removeFirst()
        }
        val stabilityScore = if (lastPitches.size >= 4) {
            val avg = lastPitches.average()
            val dev = lastPitches.map { abs(it - avg) }.average()
            (1.0 - (dev / 50.0).coerceIn(0.0, 1.0)) * 50.0
        } else 0.0
        return energyScore.toInt() to stabilityScore.toInt()
    }
}
