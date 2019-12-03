package com.gdt.inklemaker.nav.yarns.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.YarnSize
import com.gdt.inklemaker.databinding.FragmentYarnSizePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

const val YARN_SIZE_PICKER_RESULT_CODE = 0xB11
const val YARN_SIZE_PICKER_EXTRA = "YARN_SIZE_PICKER_EXTRA"

class YarnSizePickerFragment : BottomSheetDialogFragment() {

  private lateinit var binding: FragmentYarnSizePickerBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_yarn_size_picker, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      onThinThreadClick = { onSizeSelected(YarnSize.S) }
      onRegularThreadClick = { onSizeSelected(YarnSize.M) }
      onThickThreadClick = { onSizeSelected(YarnSize.L) }
    }
  }

  private fun onSizeSelected(size: YarnSize) {
    val intent = Intent().apply { putExtra(YARN_SIZE_PICKER_EXTRA, size.toString()) }
    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    this@YarnSizePickerFragment.dismiss()
  }
}