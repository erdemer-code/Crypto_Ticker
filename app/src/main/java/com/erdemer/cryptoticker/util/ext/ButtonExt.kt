package com.erdemer.cryptoticker.util.ext

import android.widget.Button

fun Button.enable(){
    isEnabled = true
    alpha = 1f
}

fun Button.disable(){
    isEnabled = false
    alpha = 0.5f
}