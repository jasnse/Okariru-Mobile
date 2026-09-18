package com.project.binar.okariru.core.notification

interface AppNotifier {
    fun show(notification: AppNotification): Int

    fun cancel(id: Int)
}