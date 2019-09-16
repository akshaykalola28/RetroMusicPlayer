/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.appthemehelper.util

import android.content.Context
import android.content.res.ColorStateList

import com.afollestad.materialdialogs.internal.ThemeSingleton

import code.name.monkey.appthemehelper.ThemeStore


object MaterialDialogsUtil {

    fun updateMaterialDialogsThemeSingleton(context: Context) {
        val md = ThemeSingleton.get()
        md.titleColor = ThemeStore.textColorPrimary(context)
        md.contentColor = ThemeStore.textColorSecondary(context)
        md.itemColor = md.titleColor
        md.widgetColor = ThemeStore.accentColor(context)
        md.linkColor = ColorStateList.valueOf(md.widgetColor)
        md.positiveColor = ColorStateList.valueOf(md.widgetColor)
        md.neutralColor = ColorStateList.valueOf(md.widgetColor)
        md.negativeColor = ColorStateList.valueOf(md.widgetColor)
        md.darkTheme = ATHUtil.isWindowBackgroundDark(context)
    }
}