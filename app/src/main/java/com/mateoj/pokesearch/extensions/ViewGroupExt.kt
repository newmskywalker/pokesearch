package com.mateoj.pokesearch.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.inflateChild(@LayoutRes layoutId: Int) =
    LayoutInflater.from(context).inflate(layoutId, this, false)