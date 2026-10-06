package org.fossify.messages.dialogs

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.setupDialogStuff
import org.fossify.commons.extensions.toast
import org.fossify.commons.extensions.value
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.messages.R
import org.fossify.messages.databinding.DialogAddArchiveExceptionBinding
import org.fossify.messages.extensions.senderRulesDB
import org.fossify.messages.helpers.IncomingRules
import org.fossify.messages.models.ArchiveException

/** Lists archive exceptions and lets the user add one. [defaultSenderKey] pre-fills the sender field. */
class ArchiveExceptionsDialog(
    private val activity: BaseSimpleActivity,
    private val defaultSenderKey: String = "",
) {
    private val binding = DialogAddArchiveExceptionBinding.inflate(activity.layoutInflater)

    init {
        binding.exceptionSenderEdittext.setText(defaultSenderKey)

        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.add_archive_exception, null)
            .setNegativeButton(org.fossify.commons.R.string.close, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.archive_exceptions) { dialog ->
                    loadList()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { add() }
                }
            }
    }

    private fun add() {
        val sender = binding.exceptionSenderEdittext.value
        val keyword = binding.exceptionKeywordEdittext.value.trim()
        if (sender.isBlank() && keyword.isEmpty()) {
            activity.toast(R.string.exception_needs_one)
            return
        }
        ensureBackgroundThread {
            activity.senderRulesDB.insertException(
                ArchiveException(
                    addressKey = if (sender.isBlank()) "" else IncomingRules.keyFor(sender),
                    keyword = keyword
                )
            )
            activity.runOnUiThread {
                binding.exceptionKeywordEdittext.setText("")
                loadList()
            }
        }
    }

    private fun loadList() {
        ensureBackgroundThread {
            val items = activity.senderRulesDB.getExceptions()
            activity.runOnUiThread {
                val list = binding.exceptionsList
                list.removeAllViews()
                if (items.isEmpty()) {
                    list.addView(row(activity.getString(R.string.no_archive_exceptions), null))
                }
                items.forEach { ex ->
                    val label = listOf(ex.addressKey, ex.keyword)
                        .mapIndexed { i, v -> if (v.isEmpty()) null else if (i == 0) v else "\"$v\"" }
                        .filterNotNull()
                        .joinToString("  +  ")
                    list.addView(row(label) {
                        ensureBackgroundThread {
                            activity.senderRulesDB.deleteException(ex.id)
                            activity.runOnUiThread { loadList() }
                        }
                    })
                }
            }
        }
    }

    private fun row(text: String, onDelete: (() -> Unit)?): View {
        val pad = activity.resources.getDimensionPixelSize(org.fossify.commons.R.dimen.activity_margin)
        return LinearLayout(activity).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(pad, 0, pad, 0)
            addView(TextView(activity).apply {
                this.text = text
                setTextColor(activity.getProperTextColor())
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            if (onDelete != null) {
                addView(TextView(activity).apply {
                    this.text = "✕"
                    setTextColor(activity.getProperTextColor())
                    setPadding(pad, pad / 2, pad, pad / 2)
                    setOnClickListener { onDelete() }
                })
            }
        }
    }
}
