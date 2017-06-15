package com.eje_c.simple360videoplayer.waiting

import com.eje_c.simple360videoplayer.R
import org.meganekkovr.FrameInput
import org.meganekkovr.Scene
import org.meganekkovr.SurfaceRendererComponent

/**
 * ピントを合わせるシーン。一定時間経ったら次のシーンへ。
 */
class WaitingScene : Scene() {

    var onTimeoutListener: Runnable? = null
    private var timeout = 0.0
    private var started: Boolean = false
    private lateinit var countDownRendererRenderer: CountDownRenderer

    override fun init() {
        super.init()

        // CountDownRendererを取得
        val countdown = findById(R.id.countdown)
        val surfaceRenderer = countdown!!.getComponent(SurfaceRendererComponent::class.java)
        this.countDownRendererRenderer = surfaceRenderer.canvasRenderer as CountDownRenderer
    }

    /**
     * カウントダウンを開始する。
     */
    fun startTimer() {
        started = true
        // コールバックを呼ぶしきい値の時間を記憶する
        timeout = System.currentTimeMillis() * 0.001 + app.context.resources.getInteger(R.integer.focus_timer)
    }

    /**
     * カウントダウンを停止する。
     */
    fun stopTimer() {
        started = false
    }

    override fun update(frame: FrameInput) {

        if (timeout > 0.0 && started) {
            val now = System.currentTimeMillis() * 0.001

            // カウントダウンを更新
            val countTime = timeout - now
            countDownRendererRenderer.count = countTime.toFloat()

            // 一定時間経過したらコールバックを呼ぶ
            if (timeout < now) {
                if (onTimeoutListener != null) {
                    app.runOnGlThread(onTimeoutListener!!)
                    timeout = 0.0
                    onTimeoutListener = null
                }
            }
        }

        super.update(frame)
    }
}
