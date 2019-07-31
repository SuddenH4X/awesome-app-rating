package com.suddenh4x.ratingdialog.preferences

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RatingThresholdTest {

    @Test
    fun `calculation of toFloat() is correct`() {
        val ratingThresholdNone = RatingThreshold.NONE.toFloat()
        val ratingThresholdThreeAndAHalf = RatingThreshold.THREE_AND_A_HALF.toFloat()
        val ratingThresholdFive = RatingThreshold.FIVE.toFloat()

        assertEquals(ratingThresholdNone, 0f)
        assertEquals(ratingThresholdThreeAndAHalf, 3.5f)
        assertEquals(ratingThresholdFive, 5f)
    }
}
