package dev.sebastiano.bundel.notifications

import android.app.Notification
import android.app.Notification.EXTRA_SHOW_WHEN
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.service.notification.StatusBarNotification

internal fun StatusBarNotification.toNotificationOrNull(context: Context) =
    toNotificationEntry(context).takeIf { it.isNotEmpty }

internal fun StatusBarNotification.toNotificationEntry(context: Context) = NotificationEntry(
    timestamp = notification.`when`,
    showTimestamp = notification.run { `when` != 0L && extras.getBoolean(EXTRA_SHOW_WHEN) },
    isGroup = notification.run { groupKey != null && flags and Notification.FLAG_GROUP_SUMMARY != 0 },
    text = text,
    title = title,
    subText = subText,
    titleBig = titleBig,
    icons = extractIcons(),
    appInfo = extractAppInfo(context.packageManager),
    interactions = extractInteractions(),
    originalNotification = this
)

private fun StatusBarNotification.extractIcons() = NotificationEntry.Icons(
    small = notification.smallIcon,
    large = notification.getLargeIcon(),
    extraLarge = notification.extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG) as Icon?
)

private fun StatusBarNotification.extractAppInfo(packageManager: PackageManager): NotificationEntry.SenderAppInfo {
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    return NotificationEntry.SenderAppInfo(
        packageName = packageName,
        name = packageManager.getResourcesForApplication(applicationInfo).getString(applicationInfo.labelRes),
        icon = Icon.createWithResource(packageName, applicationInfo.icon)
    )
}

private fun StatusBarNotification.extractInteractions() = NotificationEntry.Interactions(
    main = notification.contentIntent,
    dismiss = notification.deleteIntent,
    actions = notification.actions?.map { NotificationEntry.Interactions.ActionItem(it.title, it.getIcon(), it.actionIntent) }
        ?: emptyList()
)

internal val StatusBarNotification.text: String?
    get() = notification.extras.get(Notification.EXTRA_TEXT)?.toString()

internal val StatusBarNotification.title: String?
    get() = notification.extras.get(Notification.EXTRA_TITLE)?.toString()

internal val StatusBarNotification.titleBig: String?
    get() = notification.extras.get(Notification.EXTRA_TITLE_BIG)?.toString()

internal val StatusBarNotification.subText: String?
    get() = notification.extras.get(Notification.EXTRA_SUB_TEXT)?.toString()
