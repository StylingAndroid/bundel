package dev.sebastiano.bundel.storage.model

import android.os.Parcelable
import android.service.notification.StatusBarNotification
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
internal data class DbNotification(
    @PrimaryKey val notificationId: String,
    val timestamp: Long,
    @ColumnInfo(name = "app_package") val appPackageName: String
)
