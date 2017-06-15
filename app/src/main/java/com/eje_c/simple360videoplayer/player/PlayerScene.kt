package com.eje_c.simple360videoplayer.player

import android.net.Uri
import com.eje_c.simple360videoplayer.PlayerService
import com.eje_c.simple360videoplayer.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import org.meganekkovr.Entity
import org.meganekkovr.Scene
import org.meganekkovr.SurfaceRendererComponent
import java.io.File

class PlayerScene : Scene(), ExoPlayer.EventListener {

    private val mediaPlayer: SimpleExoPlayer = PlayerService.get()!!.exoPlayer
    private var onPlayEndedListener: Runnable? = null
    private lateinit var playerObject: Entity

    override fun init() {
        super.init()

        mediaPlayer.audioStreamType = C.STREAM_TYPE_MUSIC
        mediaPlayer.addListener(this)

        // 動画をシーンオブジェクトにマッピング
        playerObject = findById(R.id.player)!!

        val surfaceRenderer = SurfaceRendererComponent()
        surfaceRenderer.setContinuousUpdate(true)
        mediaPlayer.setVideoSurface(surfaceRenderer.surface)

        playerObject.add(surfaceRenderer)
    }

    override fun onStopRendering() {
        super.onStopRendering()

        // 他のシーンに切り替わった後で再度シーンがアクティブになることはないので、リソースを解放する
        release()
    }

    fun loadVideo(video: File) {
        PlayerService.get()!!.prepareFor(Uri.fromFile(video))
    }

    fun start() {
        mediaPlayer.playWhenReady = true
    }

    fun pause() {
        mediaPlayer.playWhenReady = false
    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun seekTo(pos: Long) {
        mediaPlayer.seekTo(pos)
    }

    fun release() {
        mediaPlayer.setVideoSurface(null)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        when (playbackState) {
            ExoPlayer.STATE_READY -> app.runOnGlThread {
                val component = playerObject.getComponent(SurfaceRendererComponent::class.java)

                // 立体視設定
                if (mediaPlayer.videoFormat.width == mediaPlayer.videoFormat.height) {
                    component.stereoMode = SurfaceRendererComponent.StereoMode.TOP_BOTTOM
                } else {
                    component.stereoMode = SurfaceRendererComponent.StereoMode.NORMAL
                }
            }
            ExoPlayer.STATE_ENDED -> {
                mediaPlayer.removeListener(this)
                mediaPlayer.stop()
                if (onPlayEndedListener != null) {
                    app.runOnGlThread(onPlayEndedListener!!)
                }
            }
        }
    }

    /**
     * 再生終了時のコールバックを設定する

     * @param listener 再生終了時のコールバック
     */
    fun onPlayEnded(listener: Runnable) {
        this.onPlayEndedListener = listener
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerError(error: ExoPlaybackException) {}

    override fun onPositionDiscontinuity() {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
}
