package com.eje_c.simple360videoplayer.waiting

import android.content.Context
import android.graphics.*
import com.eje_c.simple360videoplayer.R
import org.meganekkovr.SurfaceRendererComponent

/**
 * 動画が始まるまでのカウントダウンを表示する。
 */
class CountDownRenderer(context: Context) : SurfaceRendererComponent.CanvasRenderer(64, 64) {

    // 数字
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 数字の周りの丸
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 数字の周りの丸の描画領域
    private val oval = RectF(4f, 4f, 60f, 60f)

    // 描画時に数字のフォントサイズを取得する
    private val textBounds = Rect()

    // 初期時間
    private val focusTimer: Float

    // 残り時間
    var count: Float = 0.0f
        set(value) {
            field = value
            invalidate()
        }

    init {

        // 数字
        textPaint.color = Color.WHITE
        textPaint.textSize = 20f

        // 数字の周りの丸
        circlePaint.color = 0xFFEEEEEE.toInt()
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = 2f;

        // 初期時間
        focusTimer = context.resources.getInteger(R.integer.focus_timer).toFloat()
    }

    public override fun render(canvas: Canvas): Boolean {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)

        // テキストの大きさを取得
        val text = (count + 1).toInt().toString()
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        // 数字を描画
        canvas.drawText(text, oval.centerX() - textBounds.width() * 0.5f, oval.centerY() + textBounds.height() * 0.5f, textPaint)

        // 数字の周りの丸を描画
        val sweepAngle = 360.0f - count / focusTimer * 360.0f
        canvas.drawArc(oval, -90f, sweepAngle, false, circlePaint)

        // trueを返すと次にinvalidate()が呼ばれるまで再描画しない
        return true
    }
}
