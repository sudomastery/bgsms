package org.fossify.messages.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sender_rules")
data class SenderRule(
    @PrimaryKey @ColumnInfo(name = "address_key") var addressKey: String,
    @ColumnInfo(name = "muted") var muted: Boolean = false,
    @ColumnInfo(name = "auto_archive") var autoArchive: Boolean = false,
)
