package com.suddenh4x.ratingdialog

import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import com.suddenh4x.ratingdialog.preferences.toFloat
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RatingThresholdTest {

    @Test
    fun ratingThresholdCalculationIsCorrect() {
        val ratingThresholdNone = RatingThreshold.NONE.toFloat()
        val ratingThresholdThreeAndAHalf = RatingThreshold.THREE_AND_A_HALF.toFloat()
        val ratingThresholdFive = RatingThreshold.FIVE.toFloat()

        assertEquals(ratingThresholdNone, 0f)
        assertEquals(ratingThresholdThreeAndAHalf, 3.5f)
        assertEquals(ratingThresholdFive, 5f)
    }
}
