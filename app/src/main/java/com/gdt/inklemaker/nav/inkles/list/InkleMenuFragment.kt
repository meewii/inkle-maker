package com.gdt.inklemaker.nav.inkles.list

import android.app.Activity
import android.content.Intent
import com.google.gson.Gson
import meewii.core.pickers.Option
import meewii.core.pickers.SingleOptionPickerFragment

class InkleMenuFragment : SingleOptionPickerFragment() {

  private val _inkleId: String? by lazy { arguments?.getString(INKLE_ID_KEY) }

  companion object {
    const val TAG = "InkleMenuFragment_TAG"
    const val INKLE_MENU_RESULT_CODE = 0xBED
    const val SELECTED_OPTION_KEY = "SELECTED_OPTION_KEY"
    const val INKLE_ID_KEY = "INKLE_ID_KEY"

    fun create(optionList: List<Option>, inkleId: String): SingleOptionPickerFragment {
      return InkleMenuFragment().apply {
        arguments = createBundle(optionList).apply {
          putString(INKLE_ID_KEY, inkleId)
        }
      }
    }
  }

  override fun onOptionSelected(option: Option) {
    val intent = Intent().apply {
      val optionJson = Gson().toJson(option)
      putExtra(SELECTED_OPTION_KEY, optionJson)
      putExtra(INKLE_ID_KEY, _inkleId)
    }
    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    this@InkleMenuFragment.dismiss()
  }

}