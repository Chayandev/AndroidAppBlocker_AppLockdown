package com.example.applockdown.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val appName:String,
    val appIcon:Drawable,
    var isSelected:Boolean=false
)
