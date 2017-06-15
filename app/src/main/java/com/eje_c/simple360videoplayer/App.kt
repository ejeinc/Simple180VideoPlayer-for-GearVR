package com.eje_c.simple360videoplayer

import android.os.Environment
import com.eje_c.simple360videoplayer.end.EndScene
import com.eje_c.simple360videoplayer.player.PlayerScene
import com.eje_c.simple360videoplayer.waiting.WaitingScene
import org.meganekkovr.KeyCode
import org.meganekkovr.MeganekkoApp
import org.meganekkovr.ovrjni.OVRApp
import java.io.File

/**
 * Application main class.
 */
class App : MeganekkoApp() {

    /**
     * Called from Framework at initialization time.
     */
    override fun init() {
        super.init()

        // CPU・GPUクロックを下げる
        OVRApp.getInstance().cpuLevel = 0
        OVRApp.getInstance().gpuLevel = 0

        setSceneFromXml(R.xml.waiting)
    }

    /**
     * Called from [MainActivity.onResume]. This is in main thread not GL thread.
     */
    override fun onResume() {
        super.onResume()
        runOnGlThread { onHmdMounted() }
    }

    /**
     * Called from Framework when Gear VR is mounted.
     */
    override fun onHmdMounted() {

        // ピント合わせシーンにいる場合はタイマーを開始する
        val scene = scene
        if (scene is WaitingScene) {
            scene.startTimer()

            // タイマー後に動画再生を開始
            scene.onTimeoutListener = Runnable { startPlaying() }
        }
    }

    /**
     * 動画の再生を開始する。
     */
    private fun startPlaying() {

        // 現在のシーンをフェードアウト
        scene!!.animate().opacity(0f)
                .duration(2000)
                .onEnd {
                    // プレイヤーシーンに切り替えて動画を読み込み
                    val playerScene = setSceneFromXml(R.xml.player) as PlayerScene
                    val videoPath = context.getString(R.string.video_path)
                    val videoFile = getExternalFile(videoPath)

                    if (videoFile.exists()) {
                        // 動画を読み込む
                        playerScene.loadVideo(videoFile)
                    }

                    // 再生開始
                    playerScene.start()

                    // 再生完了したら終了シーンに遷移
                    playerScene.onPlayEnded(Runnable {
                        setSceneFromXml(R.xml.end)
                        OVRApp.getInstance().recenterYaw(false)
                    })
                }.start()
    }

    /**
     * Convert path string to External [File] reference.
     */
    private fun getExternalFile(path: String): File {
        return File(Environment.getExternalStorageDirectory(), path)
    }

    /**
     * Called from Framework when Gear VR is unmounted.
     */
    override fun onHmdUnmounted() {

        // 完了シーンにいる場合はピント合わせシーンに戻る
        if (scene is EndScene) {
            setSceneFromXml(R.xml.waiting)
        } else if (scene is WaitingScene) {
            (scene as WaitingScene).stopTimer()
        }
    }

    /*
     * ボタン操作制限
     */

    override fun onKeyDown(keyCode: Int, repeatCount: Int): Boolean {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, repeatCount)
    }

    override fun onKeyPressed(keyCode: Int, repeatCount: Int): Boolean {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true
        }
        return super.onKeyPressed(keyCode, repeatCount)
    }

    override fun onKeyLongPressed(keyCode: Int, repeatCount: Int): Boolean {
        if (keyCode == KeyCode.OVR_KEY_BACK) {
            return true
        }
        return super.onKeyLongPressed(keyCode, repeatCount)
    }
}
