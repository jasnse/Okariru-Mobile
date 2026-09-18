package com.project.binar.okariru.core.notification

import android.content.Context

private const val FCM_PREFS = "fcm_prefs"
private const val KEY_INSTALLATION_ID = "fcm_installation_id"

// diisi PushMessagingService.onRegistered(),
// dibaca AuthRepository.login() supaya tiap kali ada user login, FID ini ke-assign ulang
class FcmLocalStore(private val context: Context) {

    fun save(installationId: String) {
        context.getSharedPreferences(FCM_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_INSTALLATION_ID, installationId)
            .apply()
    }

    fun get(): String? =
        context.getSharedPreferences(FCM_PREFS, Context.MODE_PRIVATE)
            .getString(KEY_INSTALLATION_ID, null)
}