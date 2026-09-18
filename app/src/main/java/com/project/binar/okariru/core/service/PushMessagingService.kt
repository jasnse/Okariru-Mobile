package com.project.binar.okariru.core.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.binar.okariru.core.notification.AppNotification
import com.project.binar.okariru.core.notification.AppNotifier
import com.project.binar.okariru.core.notification.FcmLocalStore
import com.project.binar.okariru.core.notification.NotificationChannelType
import com.project.binar.okariru.data.auth.repository.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TOPIC_PREFIX = "/topics/"

@AndroidEntryPoint
class PushMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notifier: AppNotifier

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var fcmLocalStore: FcmLocalStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)

        fcmLocalStore.save(installationId)

        serviceScope.launch {
            authRepository.updateFcmToken(installationId)
        }

        Log.d("OkariruLog", "Registration ID: $installationId")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("PushMessagingService", message.toString())
        val data = message.data
        val title = message.notification?.title ?: data[KEY_TITLE] ?: return
        val body = message.notification?.body ?: data[KEY_BODY].orEmpty()

        notifier.show(
            AppNotification(
                title = title,
                body = body,
                channel = NotificationChannelType.fromId(data[KEY_CHANNEL] ?: message.topic()),
                deepLink = data[KEY_DEEP_LINK],
            )
        )
    }

    private fun RemoteMessage.topic(): String? =
        from?.takeIf { it.startsWith(TOPIC_PREFIX) }?.removePrefix(TOPIC_PREFIX)

    private companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_CHANNEL = "channel"
        const val KEY_DEEP_LINK = "deeplink"
    }
}