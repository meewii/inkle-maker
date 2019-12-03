package com.gdt.inklemaker.nav.inkles.add.setup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.databinding.FragmentSetUpInkleBinding
import com.gdt.inklemaker.nav.inkles.add.draw.DrawInkleFragment
import com.gdt.inklemaker.nav.inkles.add.setup.YarnPickerFragment.Companion.create
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_set_up_inkle.field_set_up_threads_split_count
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_form_container
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_length_input
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_length_layout
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_name_input
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_name_layout
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_threads_count_input
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_threads_count_layout
import kotlinx.android.synthetic.main.fragment_set_up_inkle.set_up_yarn_picker_spinners
import meewii.core.ViewStatus
import meewii.core.toIntOrValue
import javax.inject.Inject

class SetUpInkleFragment : Fragment() {

  @Inject lateinit var setUpInkleViewModel: SetUpInkleViewModel

  private lateinit var binding: FragmentSetUpInkleBinding

  private var _selectedYarnIds: ArrayList<String> = arrayListOf()
  private var _selectedWeftId: String = ""
  private var cachedYarnCount: Int? = null
  private var cachedPickerResultCode: Int? = null

  private val yarnObserver = Observer<List<Yarn>> { yarns ->
    yarns?.let {
      _selectedYarnIds = ArrayList<String>().apply { addAll(yarns.map { yarn -> yarn.id }) }
      set_up_yarn_picker_spinners.selectedWarpList = yarns
    }
  }

  private val weftObserver = Observer<Yarn> { weft ->
    weft?.let {
      _selectedWeftId = weft.id
      set_up_yarn_picker_spinners.selectedWeft = weft
    }
  }

  private val splitThreadCountObserver = Observer<Pair<Int, Int>> {
    val showText = it.first > 0 && it.second > 0
    if (showText) {
      field_set_up_threads_split_count.text =
        getString(R.string.set_up_split_threads, it.first, it.second)
      field_set_up_threads_split_count.visibility = View.VISIBLE
    } else {
      field_set_up_threads_split_count.visibility = View.GONE
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_up_inkle, container, false)
    binding.lifecycleOwner = this.viewLifecycleOwner
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.viewModel = setUpInkleViewModel

    setUpInkleViewModel.apply {
      splitThreadCount.observe(viewLifecycleOwner, splitThreadCountObserver)
      yarnList.observe(viewLifecycleOwner, yarnObserver)
      weft.observe(viewLifecycleOwner, weftObserver)
      viewStatus.observe(viewLifecycleOwner, Observer { processViewStatus(it) })

      yarnCount.observe(viewLifecycleOwner, Observer { count ->
        // Show bottom sheet when returning from AddYarnFragment
        if (cachedYarnCount != null && cachedYarnCount != count) {
          when (cachedPickerResultCode) {
            YARN_PICKER_LIST_RESULT_CODE -> showYarnPicker(YARN_PICKER_LIST_RESULT_CODE, _selectedYarnIds)
            WEFT_PICKER_RESULT_CODE -> showYarnPicker(WEFT_PICKER_RESULT_CODE, arrayListOf(_selectedWeftId))
            else -> {}
          }
        }
        cachedYarnCount = count
      })
    }

    set_up_yarn_picker_spinners.warpClickListener = View.OnClickListener {
      showYarnPicker(YARN_PICKER_LIST_RESULT_CODE, _selectedYarnIds)
    }
    set_up_yarn_picker_spinners.weftClickListener = View.OnClickListener {
      showYarnPicker(WEFT_PICKER_RESULT_CODE, arrayListOf(_selectedWeftId))
    }

    subscribeFieldsToError()
  }

  private fun showYarnPicker(resultCode: Int, selectedIds: ArrayList<String>) {
    if (parentFragmentManager.findFragmentByTag(YarnPickerFragment.TAG) == null) {
      cachedPickerResultCode = resultCode
      cachedYarnCount = null
      val targetFragment = create(resultCode, selectedIds)
      targetFragment.setTargetFragment(this, resultCode)
      parentFragmentManager.let { targetFragment.show(it, YarnPickerFragment.TAG) }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == YARN_PICKER_LIST_RESULT_CODE) {
        data?.extras?.getStringArrayList(YARN_PICKER_LIST_EXTRA)?.let { list ->
          val yarns = list.map { Gson().fromJson<Yarn>(it, Yarn::class.java) }
          setUpInkleViewModel.updateYarnList(yarns)
        }
      }
      if (requestCode == WEFT_PICKER_RESULT_CODE) {
        data?.extras?.getString(WEFT_PICKER_EXTRA)?.let {
          val yarn = Gson().fromJson<Yarn>(it, Yarn::class.java)
          setUpInkleViewModel.updateWeft(yarn)
        }
      }
    }
  }

  private fun processViewStatus(status: ViewStatus) {
    when (status) {
      is ViewStatus.Error -> {
        Snackbar.make(set_up_form_container, R.string.form_error_general, Snackbar.LENGTH_SHORT).show()
      }
      is ViewStatus.Idle -> {
      }
      is ViewStatus.Loading -> {
      }
      is ViewStatus.Success -> {
        val args = Bundle().apply {
          putString(DrawInkleFragment.WIP_INKLE_ID_ARG, status.message)
        }
        findNavController().navigate(R.id.action_inkle_set_up_done, args)
        setUpInkleViewModel.resetViewStatus()
      }
    }
  }

  //TODO Improve field validation, helper class?
  private fun subscribeFieldsToError() {

    //TODO: replace fields and layouts by better solution
    val fields = mutableMapOf(
      set_up_name_input to setUpInkleViewModel.nameError,
      set_up_threads_count_input to setUpInkleViewModel.threadCountError,
      set_up_length_input to setUpInkleViewModel.lengthError
    )

    val layouts = mutableMapOf(
      set_up_name_input to set_up_name_layout,
      set_up_threads_count_input to set_up_threads_count_layout,
      set_up_length_input to set_up_length_layout
    )

    fields.forEach { field ->
      field.key.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
          val error: Int? = when (field.key) {
            set_up_threads_count_input -> {
              if (s.toString().toIntOrValue(0) > 100) {
                R.string.set_up_thread_count_error
              } else null
            }
            set_up_length_input -> {
              if (s.toString().toIntOrValue(0) > 5000) {
                R.string.set_up_length_error
              } else null
            }
            else -> null
          }

          if (error != null) layouts[field.key]?.isErrorEnabled = true
          fields[field.key]?.value = error
          if (error == null) layouts[field.key]?.isErrorEnabled = false
        }

        override fun afterTextChanged(s: Editable) {}
      })

      field.key.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
          if ((v as EditText).text.isNullOrBlank()) {
            layouts[field.key]?.isErrorEnabled = true
            fields[field.key]?.value = R.string.form_error_empty
          } else {
            if (v.text.toString().toIntOrValue(0) < 100) {
              fields[field.key]?.value = null
              layouts[field.key]?.isErrorEnabled = false
            }
          }
        }
      }
    }
  }
}