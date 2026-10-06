package org.fossify.messages.helpers

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.View

/** Finds the account balance inside M-PESA messages so only that figure can be blurred. */
object MpesaBlur {
    private val BALANCE = Regex(
        """(?i)\bbalance\s+(?:is\s+|:\s*)?(ksh\.?\s?-?[\d,]+(?:\.\d+)?)"""
    )

    fun isMpesaSender(vararg senders: String?): Boolean {
        return senders.any { s ->
            val clean = s.orEmpty().replace("-", "").replace(" ", "")
            clean.contains("mpesa", ignoreCase = true)
        }
    }

    fun balanceRanges(body: String): List<IntRange> {
        return BALANCE.findAll(body).mapNotNull { it.groups[1]?.range }.toList()
    }

    /** Replaces the balance with dots for places where a blur cannot be drawn, such as notifications. */
    fun mask(body: String): String {
        var result = body
        balanceRanges(body).sortedByDescending { it.first }.forEach { range ->
            result = result.replaceRange(range, "Ksh ****")
        }
        return result
    }

    /** [onReveal] null means the blur is permanent, as in the conversation list. */
    fun blur(body: String, textColor: Int, onReveal: (() -> Unit)?): SpannableString? {
        val ranges = balanceRanges(body)
        if (ranges.isEmpty()) return null
        val spannable = SpannableString(body)
        ranges.forEach { range ->
            val end = range.last + 1
            spannable.setSpan(BlurSpan(textColor), range.first, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (onReveal != null) {
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) = onReveal()
                    override fun updateDrawState(ds: TextPaint) {}
                }, range.first, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }

    private class BlurSpan(private val color: Int) : CharacterStyle() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.TRANSPARENT
            ds.setShadowLayer(ds.textSize / 2f, 0f, 0f, color)
        }
    }
}
