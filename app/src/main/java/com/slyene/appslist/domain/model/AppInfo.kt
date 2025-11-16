package com.slyene.appslist.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val versionName: String,
    val sourceDir: String,
    val icon: Drawable
)
