package com.mateoj.pokesearch.ui.detail

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.mateoj.pokesearch.extensions.dpToSp
import kotlin.math.max

class MyBarChart @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int = 0) : View(context, attributeSet, defStyle) {
    private val paint : HashMap<Int, Paint> = hashMapOf()
    private var animStarted: Boolean = false
    private val oddAnimator : ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = LinearOutSlowInInterpolator()
        duration = 1500
        startDelay = 500
        addUpdateListener {
            animStarted = true
            invalidate()
        }
    }

    private val evenAnimator : ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        interpolator = LinearOutSlowInInterpolator()
        duration = 2000
        startDelay = 500
        addUpdateListener { invalidate() }
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 12.dpToSp(context)
    }
    var min : Float = 0f
    var max : Float = 100f
    private val textHorizontalMargin = 5.dpToSp(context)
    private val textTopMargin = 12.dpToSp(context)

    var data : List<Entry>? = null
        set(value) {
            field = value
            min = max((value?.minBy { it.y }?.y ?: 0f) - 5, 0f)
            max = value?.maxBy { it.y }?.y ?: 100f
            data?.let {
                it.forEachIndexed { index, entry ->
                    paint[index] = Paint().apply {
                        color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                    }
                }
            }
            if (oddAnimator.isRunning) oddAnimator.cancel()
            if (evenAnimator.isRunning) evenAnimator.cancel()

            oddAnimator.start()
            evenAnimator.start()
        }

    var itemWidth: Float = 0f

    private val rectangles : MutableList<Pair<RectF, Path>> = mutableListOf()

    private val rnd = java.util.Random()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val margin = 16
        data?.let {
            val usableWidth = w - (margin * (it.size - 1))
            if(it.isNotEmpty())
                itemWidth = usableWidth / it.size.toFloat()
            it.forEachIndexed { index, entry ->
                val normalized = (entry.y - min)/(max - min)

                val left = index * (itemWidth + if(index > 0 ) margin else 0)
                val top = 0f
                val right = left + itemWidth
                val bottom = height * normalized
                rectangles.add(Pair(RectF(left, top, right, bottom), Path().apply {
                    moveTo((left + right) / 2f, bottom)
                    lineTo((left + right) / 2f, top)
                }))
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(!animStarted) return

        canvas?.let { c ->
            rectangles.forEachIndexed { index, pair ->
                val animator = if(index %2 == 0) evenAnimator else oddAnimator
                c.drawRoundRect(pair.first.left, pair.first.top, pair.first.right, pair.first.bottom * (animator.animatedValue as Float), 10f, 10f, paint[index]!!)

                data?.get(index)?.let {
                    c.drawText(it.label, pair.first.left + textHorizontalMargin, pair.first.top + textTopMargin, textPaint)
//                    c.drawTextOnPath(it.label, pair.second, textHorizontalMargin,0f, textPaint)
                    c.drawText(it.y.toString(), pair.first.left + textHorizontalMargin, pair.first.bottom - textTopMargin, textPaint)
                }
            }
        }
    }
}

data class Entry(val y : Float, val label: String)