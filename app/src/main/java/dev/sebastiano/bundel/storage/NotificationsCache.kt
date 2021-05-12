package dev.sebastiano.bundel.storage

import android.app.Application
import android.os.Parcelable
import java.io.File
import android.os.Parcel
import android.os.Parcelable.Creator
import android.service.notification.StatusBarNotification
import timber.log.Timber
import javax.inject.Inject

// Some code thanks to Omar Miatello <3
// Licensed under Apache 2.0 license
// https://gist.github.com/omarmiatello/6711967
class NotificationsCache @Inject constructor(
    private val application: Application
) {

    // TODO memcache shit cause I/O is slow

    fun getStatusBarNotification(notificationId: String): StatusBarNotification {
        val file = cachedFileFor(notificationId)
        require(file.exists()) { "No entry found for notification ID $notificationId" }

        return file.readAsStatusBarNotification()
    }

    fun storeStatusBarNotification(notification: StatusBarNotification, notificationId: String) {
        val file = cachedFileFor(notificationId)
        if (file.exists()) {
            Timber.v("No need to save notification with ID '$notificationId' as it exists already")
            return
        }

        notification.writeToFile(file)
    }

    private fun cachedFileFor(notificationId: String) = File(application.cacheDir, notificationId)

    private fun Parcelable.writeToFile(file: File) {
        file.writeBytes(toByteArray())
    }

    private fun File.readAsStatusBarNotification() =
        readBytes().toParcelable(StatusBarNotification.CREATOR)

    private fun Parcelable.toByteArray(): ByteArray {
        val parcel = Parcel.obtain()
        writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle() // not sure if needed or a good idea
        return bytes
    }

    private fun <T : Parcelable?> ByteArray.toParcelable(creator: Creator<T>): T {
        val parcel = toParcelable()
        return creator.createFromParcel(parcel)
    }

    private fun ByteArray.toParcelable(): Parcel {
        val parcel = Parcel.obtain()
        parcel.unmarshall(this, 0, size)
        parcel.setDataPosition(0) // this is extremely important!
        return parcel
    }

    fun deleteNotification(notificationId: String) {
        cachedFileFor(notificationId).delete()
    }
}
