package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import com.suddenh4x.ratingdialog.logging.RatingLogger
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Calendar
import java.util.Date

@ExtendWith(MockKExtension::class)
class ConditionsCheckerTest {
    @MockK
    lateinit var context: Context

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        mockkObject(PreferenceUtil)
        every { PreferenceUtil.isDialogAgreed(context) } returns false
        every { PreferenceUtil.isDoNotShowAgain(context) } returns false
        every { PreferenceUtil.getRemindTimestamp(context) } returns 0
    }

    @Nested
    inner class WithLaterButtonClicked {
        @BeforeEach
        fun setup() {
            every { PreferenceUtil.shouldShowDialogLater(context) } returns true
        }

        @Test
        fun `and dialog agreed should return false`() {
            every { PreferenceUtil.isDialogAgreed(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
        }

        @Test
        fun `and do not show again clicked should return false`() {
            every { PreferenceUtil.isDoNotShowAgain(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
        }

        @Nested
        inner class AndWithRemindTimestampIsSetToNow {
            @BeforeEach
            fun setup() {
                every { PreferenceUtil.getRemindTimestamp(context) } returns System.currentTimeMillis()
            }

            @Nested
            inner class AndWithMinimumDaysToShowAgainIsSetTo0 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDaysToShowAgain(context) } returns 0
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysToShowAgainIsSetTo3 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDaysToShowAgain(context) } returns 3
                }

                @Test
                fun `and with launch times set to 10 returns false`() {
                    every { PreferenceUtil.getLaunchTimes(context) } returns 10
                    // fixme: Getting strange errors if using property access
                    assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                }
            }
        }

        @Nested
        inner class AndWithRemindTimestampIsSetToThreeDaysAgo {
            @BeforeEach
            fun setup() {
                every { PreferenceUtil.getRemindTimestamp(context) } returns getDateThreeDaysAgo().time
            }

            @Nested
            inner class AndWithMinimumDaysToShowAgainIsSetTo0 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDaysToShowAgain(context) } returns 0
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysToShowAgainIsSetTo3 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDaysToShowAgain(context) } returns 3
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesToShowAgainIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimesToShowAgain(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysToShowAgainIsSetTo7 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDaysToShowAgain(context) } returns 7
                }

                @Test
                fun `and with launch times set to 10 returns false`() {
                    every { PreferenceUtil.getLaunchTimes(context) } returns 10
                    // fixme: Getting strange errors if using property access
                    assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                }
            }
        }
    }

    @Nested
    inner class WithLaterButtonNotClicked {
        @BeforeEach
        fun setup() {
            every { PreferenceUtil.shouldShowDialogLater(context) } returns false
        }

        @Test
        fun `and dialog agreed should return false`() {
            every { PreferenceUtil.isDialogAgreed(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
        }

        @Test
        fun `and do not show again clicked should return false`() {
            every { PreferenceUtil.isDoNotShowAgain(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
        }

        @Nested
        inner class AndWithRemindTimestampIsSetToNow {
            @BeforeEach
            fun setup() {
                every { PreferenceUtil.getRemindTimestamp(context) } returns System.currentTimeMillis()
            }

            @Nested
            inner class AndWithMinimumDaysIsSetTo0 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDays(context) } returns 0
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysIsSetTo3 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDays(context) } returns 3
                }

                @Test
                fun `and with launch times set to 10 returns false`() {
                    every { PreferenceUtil.getLaunchTimes(context) } returns 10
                    // fixme: Getting strange errors if using property access
                    assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                }
            }
        }

        @Nested
        inner class AndWithRemindTimestampIsSetToThreeDaysAgo {
            @BeforeEach
            fun setup() {
                every { PreferenceUtil.getRemindTimestamp(context) } returns getDateThreeDaysAgo().time
            }

            @Nested
            inner class AndWithMinimumDaysIsSetTo0 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDays(context) } returns 0
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysIsSetTo3 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDays(context) } returns 3
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo0 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 0
                    }

                    @Test
                    fun `and with launch times set to 0 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }

                @Nested
                inner class AndWithMinimumLaunchTimesIsSetTo2 {
                    @BeforeEach
                    fun setup() {
                        every { PreferenceUtil.getMinimumLaunchTimes(context) } returns 2
                    }

                    @Test
                    fun `and with launch times set to 0 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(ConditionsChecker.shouldShowDialog(context)).isTrue()
                    }
                }
            }

            @Nested
            inner class AndWithMinimumDaysIsSetTo7 {
                @BeforeEach
                fun setup() {
                    every { PreferenceUtil.getMinimumDays(context) } returns 7
                }

                @Test
                fun `and with launch times set to 10 returns false`() {
                    every { PreferenceUtil.getLaunchTimes(context) } returns 10
                    // fixme: Getting strange errors if using property access
                    assertThat(ConditionsChecker.shouldShowDialog(context)).isFalse()
                }
            }
        }
    }

    @Test
    fun `day calculation is correct`() {
        val threeDaysAgo = getDateThreeDaysAgo()
        val currentTime = Date(System.currentTimeMillis())
        val days = ConditionsChecker.calculateDaysBetween(threeDaysAgo, currentTime)
        assertEquals(days, 3)
    }

    private fun getDateThreeDaysAgo(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -3)
        return calendar.time
    }
}
