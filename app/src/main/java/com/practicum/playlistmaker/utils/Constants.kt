package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

const val HISTORY_PREFERENCES = "history_log"
const val TRACK = "track"
const val HISTORY_LEN = 10


class Constants {
    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }


    }
}