package ru.dl.checklist.app.utils

import ru.dl.checklist.R

object ColorCategory {
    fun getColorCategory(percent: Int): Int {
        val color = when (percent) {
            in 0 until 75 -> R.color.category_2
            in 75 until 85 -> R.color.category_3
            in 85 until 94 -> R.color.category_4
            else -> R.color.category_5
        }
        return color
    }
}