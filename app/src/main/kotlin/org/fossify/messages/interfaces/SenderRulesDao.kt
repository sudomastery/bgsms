package org.fossify.messages.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.fossify.messages.models.ArchiveException
import org.fossify.messages.models.SenderRule

@Dao
interface SenderRulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(rule: SenderRule)

    @Query("SELECT * FROM sender_rules WHERE address_key = :addressKey")
    fun get(addressKey: String): SenderRule?

    @Query("SELECT * FROM sender_rules WHERE muted = 1 OR auto_archive = 1")
    fun getAll(): List<SenderRule>

    @Query("DELETE FROM sender_rules WHERE address_key = :addressKey")
    fun delete(addressKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertException(exception: ArchiveException): Long

    @Query("SELECT * FROM archive_exceptions ORDER BY id DESC")
    fun getExceptions(): List<ArchiveException>

    @Query("DELETE FROM archive_exceptions WHERE id = :id")
    fun deleteException(id: Long)
}
