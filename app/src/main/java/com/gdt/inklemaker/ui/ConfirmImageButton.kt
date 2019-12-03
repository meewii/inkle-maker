package com.gdt.inklemaker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.gdt.inklemaker.R

/**
 * Image button that overrides OnClickListener by showing confirm AlertDialog
 */
class ConfirmImageButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ImageButton(context, attrs, defStyleAttr) {

  var title: String? = null
  var message: String? = null

  init {
    val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ConfirmImageButton, 0, 0)
    attributes.apply {
      val titleString = getString(R.styleable.ConfirmImageButton_cib_dialogTitle)
      val titleResId = getResourceId(R.styleable.ConfirmImageButton_cib_dialogTitle, 0)
      title = titleString ?: titleResId.takeUnless { it == 0 }?.let { context.getString(it) }

      val messageString = getString(R.styleable.ConfirmImageButton_cib_dialogMessage)
      val messageResId = getResourceId(R.styleable.ConfirmImageButton_cib_dialogMessage, 0)
      message = messageString ?: messageResId.takeUnless { it == 0 }?.let { context.getString(it) }
    }
    attributes.recycle()
  }

  override fun setOnClickListener(desiredListener: OnClickListener?) {
    val newListener = OnClickListener { showDialog(desiredListener) }
    super.setOnClickListener(newListener)
  }

  private fun showDialog(desiredListener: OnClickListener?) {
    AlertDialog.Builder(context)
      .setTitle(title)
      .setMessage(message)
      .setPositiveButton(R.string.confirm_delete_yes) { _, _ -> desiredListener?.onClick(this) }
      .setNegativeButton(R.string.confirm_delete_cancel) { dialog, _ ->
        dialog.dismiss()
      }
      .show()
  }
}

