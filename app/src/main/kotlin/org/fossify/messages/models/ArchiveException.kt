package org.fossify.messages.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Keeps a message out of auto-archive. Empty [addressKey] matches any sender,
 * empty [keyword] matches any text. A rule needs at least one of them.
 */
@Entity(tableName = "archive_exceptions")
data class ArchiveException(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "address_key") var addressKey: String = "",
    @ColumnInfo(name = "keyword") var keyword: String = "",
)
