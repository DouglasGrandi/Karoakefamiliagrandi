package net.natura.karoakefamiliagrandi.playback

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class YouTubeIntentPlayer(private val ctx: Context) {
    fun play(videoId: String) {
        val uri = Uri.parse("vnd.youtube:$videoId")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            ctx.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(web)
        }
    }
}
