package com.project.binar.okariru.core.security

import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RootCheckerTest {

    private val rootChecker = RootChecker(ApplicationProvider.getApplicationContext())

    @Test
    fun `isDeviceRooted returns false on a stock non-rooted test environment`() {
        assertFalse(rootChecker.isDeviceRooted())
    }
}
