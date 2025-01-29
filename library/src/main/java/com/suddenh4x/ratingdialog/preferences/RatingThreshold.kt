package com.suddenh4x.ratingdialog.preferences

import java.io.Serializable

enum class RatingThreshold : Serializable {
    NONE,
    HALF,
    ONE,
    ONE_AND_A_HALF,
    TWO,
    TWO_AND_A_HALF,
    THREE,
    THREE_AND_A_HALF,
    FOUR,
    FOUR_AND_A_HALF,
    FIVE,
}

fun RatingThreshold.toFloat() = this.ordinal / 2f
