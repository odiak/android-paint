package net.odiak.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint = Paint()
    private val paint2 = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
    }
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private var lastX = Float.NaN
    private var lastY = Float.NaN

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        refreshBitmap()
    }

    private fun refreshBitmap() {
        val mw = measuredWidth
        val mh = measuredHeight
        val b = bitmap
        if (b == null || b.width != mw || b.height != mh) {
            bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                paintLine(event.x, event.y)
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                paintLine(event.x, event.y)
                lastX = Float.NaN
                lastY = Float.NaN
            }
        }

        return true
    }

    private fun paintLine(x: Float, y: Float) {
        if (!lastX.isFinite() || !lastY.isFinite()) return

        println("@ $x, $y")

        canvas?.let {
            it.drawLine(lastX, lastY, x, y, paint2)
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)
    }
}