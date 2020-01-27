package com.mateoj.pokesearch.extensions

import android.content.Context
import android.util.TypedValue




fun Int.dpToSp(context: Context) : Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics
    )