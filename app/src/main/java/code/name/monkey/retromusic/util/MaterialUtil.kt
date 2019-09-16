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

import android.content.res.ColorStateList
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.kabouzeid.appthemehelper.ThemeStore
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.kabouzeid.appthemehelper.util.MaterialValueHelper


object MaterialUtil {

    @JvmOverloads
    fun setTint(button: MaterialButton, background: Boolean = true,
                color: Int = ThemeStore.accentColor(button.context)) {

        button.isAllCaps = false
        val context = button.context
        val colorState = ColorStateList.valueOf(color)
        val textColor = ColorStateList.valueOf(MaterialValueHelper.getPrimaryTextColor(context, ColorUtil.isColorLight(color)))


        if (background) {
            button.backgroundTintList = colorState
            button.setTextColor(textColor)
            button.iconTint = textColor
        } else {
            button.setTextColor(colorState)
            button.iconTint = colorState
        }

    }

    fun setTint(textInputLayout: TextInputLayout, background: Boolean = true) {
        val context = textInputLayout.context
        val accentColor = ThemeStore.accentColor(context)
        val colorState = ColorStateList.valueOf(accentColor)

        if (background) {
            textInputLayout.backgroundTintList = colorState
            textInputLayout.defaultHintTextColor = colorState
        } else {
            textInputLayout.boxStrokeColor = accentColor
            textInputLayout.defaultHintTextColor = colorState
            textInputLayout.isHintAnimationEnabled = true
        }

    }
}
