package com.gdt.inklemaker.nav.inkles.add.setup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.databinding.FragmentYarnPickerBinding
import com.gdt.inklemaker.nav.yarns.adapters.BaseYarnPickerAdapter
import com.gdt.inklemaker.nav.yarns.adapters.WeftPickerAdapter
import com.gdt.inklemaker.nav.yarns.adapters.YarnPickerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_yarn_picker.ui_yarn_picker_add
import kotlinx.android.synthetic.main.fragment_yarn_picker.ui_yarn_picker_add_more
import kotlinx.android.synthetic.main.fragment_yarn_picker.ui_yarn_picker_done
import kotlinx.android.synthetic.main.fragment_yarn_picker.ui_yarn_picker_recycler
import javax.inject.Inject

const val YARN_PICKER_LIST_RESULT_CODE = 0xBEE
const val YARN_PICKER_LIST_EXTRA = "YARN_PICKER_LIST_EXTRA"
const val WEFT_PICKER_RESULT_CODE = 0xB00
const val WEFT_PICKER_EXTRA = "WEFT_PICKER_EXTRA"

class YarnPickerFragment : BottomSheetDialogFragment() {

  private var cachedYarns: ArrayList<Yarn> = arrayListOf()

  private var viewAdapter: BaseYarnPickerAdapter? = null

  private val _resultCode: Int by lazy { arguments!!.getInt(YARN_PICKER_RESULT_CODE_ARG, -1) }
  private val _selectedItems: List<String> by lazy {
    arguments!!.getStringArrayList(YARN_PICKER_SELECTED_ITEMS_ARG) ?: arrayListOf()
  }
  private val _isMultipleChoice: Boolean by lazy { _resultCode == YARN_PICKER_LIST_RESULT_CODE }

  companion object {
    const val TAG = "YarnPickerFragment_TAG"
    const val YARN_PICKER_RESULT_CODE_ARG = "YARN_PICKER_RESULT_CODE_ARG"
    const val YARN_PICKER_SELECTED_ITEMS_ARG = "YARN_PICKER_SELECTED_ITEMS_ARG"

    fun create(resultCode: Int, selectedItems: ArrayList<String>): YarnPickerFragment {
      val targetFragment = YarnPickerFragment()
      targetFragment.arguments = Bundle().apply {
        putInt(YARN_PICKER_RESULT_CODE_ARG, resultCode)
        putStringArrayList(YARN_PICKER_SELECTED_ITEMS_ARG, selectedItems)
      }
      return targetFragment
    }
  }

  @Inject lateinit var viewModel: YarnPickerViewModel

  private lateinit var binding: FragmentYarnPickerBinding

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  private val yarnObserver = Observer<List<Yarn>> { yarns ->
    val updatedYarns = if (_selectedItems.isNotEmpty()) {
      yarns.map {
        it.isSelected = _selectedItems.contains(it.id)
        it
      }
    } else yarns
    cachedYarns.clear()
    cachedYarns.addAll(updatedYarns)
    viewAdapter?.setData(updatedYarns)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_yarn_picker, container, false)
    binding.lifecycleOwner = this.viewLifecycleOwner
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.isMultipleChoice = _isMultipleChoice
    if (_isMultipleChoice) {
      viewAdapter = YarnPickerAdapter()
      ui_yarn_picker_done.setOnClickListener { onDonePickingYarns() }
    } else {
      viewAdapter = WeftPickerAdapter(::onDonePickingWeft)
    }

    ui_yarn_picker_recycler.apply {
      layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
      adapter = viewAdapter
    }

    ui_yarn_picker_add.setOnClickListener { addNewYarn() }
    ui_yarn_picker_add_more.setOnClickListener { addNewYarn() }

    binding.viewModel = viewModel
    viewModel.yarns.observe(viewLifecycleOwner, yarnObserver)
  }

  private fun addNewYarn() {
    findNavController().navigate(R.id.nav_add_yarn)
    dismiss()
  }

  private fun onDonePickingYarns() {
    val selectedYarns = viewAdapter!!.yarnData
      .filterIsInstance<Yarn>()
      .filter { it.isSelected }
      .map { Gson().toJson(it) }

    val intent = Intent().apply {
      val list = arrayListOf<String>().apply { addAll(selectedYarns) }
      putStringArrayListExtra(YARN_PICKER_LIST_EXTRA, list)
    }
    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    this@YarnPickerFragment.dismiss()
  }

  private fun onDonePickingWeft(weft: Yarn) {
    val intent = Intent().apply {
      val weftJson = Gson().toJson(weft)
      putExtra(WEFT_PICKER_EXTRA, weftJson)
    }
    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    this@YarnPickerFragment.dismiss()
  }
}