package org.fossify.messages.helpers

import android.content.Context
import org.fossify.messages.extensions.senderRulesDB

data class IncomingDecision(val muted: Boolean, val archive: Boolean)

object IncomingRules {
    private const val NUMBER_KEY_LENGTH = 9

    /** Normalizes a sender so "+254712345678" and "0712345678" match. Names like "MPESA" are upper-cased. */
    fun keyFor(address: String): String {
        val trimmed = address.trim()
        if (trimmed.any { it.isLetter() }) return trimmed.uppercase()
        val digits = trimmed.filter { it.isDigit() }
        return digits.takeLast(NUMBER_KEY_LENGTH)
    }

    fun decide(context: Context, address: String, body: String, isFlash: Boolean = false): IncomingDecision {
        val dao = context.senderRulesDB
        val key = keyFor(address)
        val rule = dao.get(key)
        val muted = rule?.muted == true

        var archive = isFlash || rule?.autoArchive == true
        if (archive && !isFlash) {
            val kept = dao.getExceptions().any { ex ->
                val senderOk = ex.addressKey.isEmpty() || ex.addressKey == key
                val keywordOk = ex.keyword.isEmpty() || body.contains(ex.keyword, ignoreCase = true)
                (ex.addressKey.isNotEmpty() || ex.keyword.isNotEmpty()) && senderOk && keywordOk
            }
            if (kept) archive = false
        }
        return IncomingDecision(muted = muted, archive = archive)
    }
}
