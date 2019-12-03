package com.gdt.inklemaker.nav.yarns.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gdt.inklemaker.R
import com.gdt.inklemaker.databinding.FragmentAddYarnBinding
import com.google.android.material.snackbar.Snackbar
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_yarn.add_yarn_form_container
import meewii.core.ViewStatus
import javax.inject.Inject

class AddYarnFragment : Fragment() {

  @Inject lateinit var addYarnViewModel: AddYarnViewModel

  private lateinit var binding: FragmentAddYarnBinding

  private var dialog: AlertDialog? = null

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_yarn, container, false)
    binding.lifecycleOwner = this.viewLifecycleOwner
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      viewModel = addYarnViewModel
      openColorPicker = {
        dismissKeyboard()
        if (dialog == null) dialog = createPickerDialog(view.context)
        dialog!!.show()
      }
      openSizePicker = {
        val targetFragment = YarnSizePickerFragment()
        targetFragment.setTargetFragment(this@AddYarnFragment, YARN_SIZE_PICKER_RESULT_CODE)
        parentFragmentManager.let { targetFragment.show(it, YarnSizePickerFragment::class.java.simpleName) }
      }
    }

    addYarnViewModel.viewStatus.observe(viewLifecycleOwner, Observer { processViewStatus(it) })
  }

  private fun dismissKeyboard() {
    activity?.let {
      val imm: InputMethodManager? = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm?.hideSoftInputFromWindow(it.currentFocus?.windowToken, 0)
    }
  }

  private fun createPickerDialog(context: Context): AlertDialog {
    return ColorPickerDialog.Builder(context)
      .setTitle(getString(R.string.add_yarn_color_picker_title))
      .setPositiveButton(getString(R.string.add_yarn_color_picker_confirm),
        ColorEnvelopeListener { envelope, fromUser ->
          if (fromUser) addYarnViewModel.setColor(envelope.color, envelope.hexCode)
        })
      .setNegativeButton(getString(R.string.add_yarn_color_picker_cancel)) { dialogInterface, _ ->
        dialogInterface.dismiss()
      }
      .attachAlphaSlideBar(false)
      .create()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == YARN_SIZE_PICKER_RESULT_CODE) {
        data?.extras?.getString(YARN_SIZE_PICKER_EXTRA)?.let {
          addYarnViewModel.setSize(it)
        }
      }
    }
  }

  private fun processViewStatus(status: ViewStatus) {
    when (status) {
      is ViewStatus.Error -> {
        Snackbar.make(add_yarn_form_container, R.string.form_error_general, Snackbar.LENGTH_SHORT).show()
      }
      is ViewStatus.Idle -> {
      }
      is ViewStatus.Loading -> {
      }
      is ViewStatus.Success -> {
        findNavController().navigateUp()
        addYarnViewModel.resetViewStatus()
      }
    }
  }

}