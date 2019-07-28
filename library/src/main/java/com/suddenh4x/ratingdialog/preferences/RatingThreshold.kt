package com.suddenh4x.ratingdialog.preferences

enum class RatingThreshold {
    NONE, HALF, ONE, ONE_AND_A_HALF, TWO, TWO_AND_A_HALF, THREE, THREE_AND_A_HALF, FOUR, FOUR_AND_A_HALF, FIVE;
}

fun RatingThreshold.toFloat() = this.ordinal / 2f
