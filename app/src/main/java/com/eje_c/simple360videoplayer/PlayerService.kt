package com.eje_c.simple360videoplayer

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.lang.ref.WeakReference

/**
 * This service holds [SimpleExoPlayer] instance for playing media.
 */
class PlayerService : Service() {

    companion object {
        // Have a weak reference statically
        private lateinit var ref: WeakReference<PlayerService>

        fun get(): PlayerService? {
            return ref.get()
        }
    }

    lateinit var exoPlayer: SimpleExoPlayer
        private set
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var extractorsFactory: ExtractorsFactory

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        super.onCreate()

        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)))
        extractorsFactory = DefaultExtractorsFactory()

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())

        // Keep reference static
        ref = WeakReference(this)
    }

    override fun onDestroy() {

        // Release player
        exoPlayer.release()

        // Clear Service reference
        ref.clear()

        super.onDestroy()
    }

    /**
     * 動画を再生するためのSimpleExoPlayerを取得する。
     * @param uri 動画URI
     * @return 動画を再生するために準備されたSimpleExoPlayer
     */
    fun prepareFor(uri: Uri): SimpleExoPlayer {
        val videoSource = ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null)
        exoPlayer.prepare(videoSource)
        return exoPlayer
    }
}
