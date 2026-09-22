package com.project.binar.okariru.core.security

import android.content.Context
import android.content.pm.PackageManager
import java.io.File

/**
 * Heuristic root detector. No single check is bulletproof against a determined
 * attacker (Magisk hide, etc.), so results are OR-combined across several
 * independent signals to raise overall confidence.
 */
class RootChecker(private val context: Context) {

    fun isDeviceRooted(): Boolean {
        return checkKnownRootAppsInstalled() ||
            checkSuBinaryExists() ||
            checkSuCommandExecutable() ||
            checkSystemPartitionWritable()
    }

    private fun checkSuBinaryExists(): Boolean {
        return KNOWN_SU_PATHS.any { path -> File(path).exists() }
    }

    private fun checkSuCommandExecutable(): Boolean {
        return try {
            val process = ProcessBuilder("which", "su").redirectErrorStream(true).start()
            val output = process.inputStream.bufferedReader().readLine()
            val exitCode = process.waitFor()
            exitCode == 0 && !output.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun checkSystemPartitionWritable(): Boolean {
        return WRITABLE_PATHS_TO_CHECK.any { path ->
            val dir = File(path)
            dir.exists() && dir.canWrite()
        }
    }

    private fun checkKnownRootAppsInstalled(): Boolean {
        val packageManager = context.packageManager
        return KNOWN_ROOT_APP_PACKAGES.any { packageName ->
            isPackageInstalled(packageManager, packageName)
        }
    }

    private fun isPackageInstalled(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    private companion object {
        val KNOWN_SU_PATHS = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su",
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
            "/su/bin/su",
            "/system/app/Superuser.apk",
            "/cache/su",
        )

        val WRITABLE_PATHS_TO_CHECK = arrayOf(
            "/system",
            "/system/bin",
            "/system/sbin",
            "/vendor/bin",
        )

        val KNOWN_ROOT_APP_PACKAGES = arrayOf(
            "com.topjohnwu.magisk",
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.kingroot.kinguser",
            "com.kingouser.com",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot",
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "com.amphoras.hidemyroot",
            "com.formyhm.hiderootPremium",
            "com.saurik.substrate",
        )
    }
}
