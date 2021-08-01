package com.suddenh4x.ratingdialog.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.dialog.DialogOptions
import com.suddenh4x.ratingdialog.logging.RatingLogger
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.mockk.verify
import java.util.Calendar
import java.util.Date
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ConditionsCheckerTest {
    @MockK
    lateinit var context: Context
    @MockK
    lateinit var activity: AppCompatActivity
    internal lateinit var dialogOptions: DialogOptions

    @BeforeEach
    fun setup() {
        RatingLogger.isLoggingEnabled = false
        mockkObject(PreferenceUtil)
        every { PreferenceUtil.isDialogAgreed(context) } returns false
        every { PreferenceUtil.isDoNotShowAgain(context) } returns false
        every { PreferenceUtil.getRemindTimestamp(context) } returns 0
        dialogOptions = DialogOptions()
    }

    @Nested
    inner class WithLaterButtonClicked {
        @BeforeEach
        fun setup() {
            every { PreferenceUtil.wasLaterButtonClicked(context) } returns true
        }

        @AfterEach
        fun cleanup() {
            dialogOptions.customConditionToShowAgain = null
        }

        @Test
        fun `and dialog agreed should return false`() {
            every { PreferenceUtil.isDialogAgreed(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
        }

        @Test
        fun `and do not show again clicked should return false`() {
            every { PreferenceUtil.isDoNotShowAgain(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
        }

        @Test
        fun `and false custom condition to show again should immediately return false`() {
            dialogOptions.customConditionToShowAgain = { false }
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
            verify(exactly = 0) { PreferenceUtil.getMinimumDaysToShowAgain(context) }
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
                    }

                    @Test
                    fun `and with true custom condition to show again and launch times set to 0 returns true`() {
                        dialogOptions.customConditionToShowAgain = { true }
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
                    }

                    @Test
                    fun `and with false custom condition to show again and launch times set to 0 returns false`() {
                        dialogOptions.customConditionToShowAgain = { false }
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                    assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                    assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
                }
            }
        }
    }

    @Nested
    inner class WithLaterButtonNotClicked {
        @BeforeEach
        fun setup() {
            every { PreferenceUtil.wasLaterButtonClicked(context) } returns false
        }

        @AfterEach
        fun cleanup() {
            dialogOptions.customCondition = null
        }

        @Test
        fun `and dialog agreed should return false`() {
            every { PreferenceUtil.isDialogAgreed(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
        }

        @Test
        fun `and do not show again clicked should return false`() {
            every { PreferenceUtil.isDoNotShowAgain(context) } returns true
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
        }

        @Test
        fun `and false custom condition should immediately return false`() {
            dialogOptions.customCondition = { false }
            // fixme: Getting strange errors if using property access
            assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
            verify(exactly = 0) { PreferenceUtil.getMinimumDays(context) }
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                    assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
                    }

                    @Test
                    fun `and with true custom condition and launch times set to 0 returns true`() {
                        dialogOptions.customCondition = { true }
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
                    }

                    @Test
                    fun `and with false custom condition and launch times set to 0 returns false`() {
                        dialogOptions.customCondition = { false }
                        every { PreferenceUtil.getLaunchTimes(context) } returns 0
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 1 returns false`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 1
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isFalse()
                    }

                    @Test
                    fun `and with launch times set to 2 returns true`() {
                        every { PreferenceUtil.getLaunchTimes(context) } returns 2
                        // fixme: Getting strange errors if using property access
                        assertThat(
                            ConditionsChecker.shouldShowDialog(
                                context,
                                dialogOptions
                            )
                        ).isTrue()
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
                    assertThat(ConditionsChecker.shouldShowDialog(context, dialogOptions)).isFalse()
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

    private fun getBuilder() = AppRating.Builder(activity, dialogOptions)
}
