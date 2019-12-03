package com.gdt.inklemaker.nav.inkles.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Inkle
import com.gdt.inklemaker.databinding.FragmentBaseListBinding
import com.gdt.inklemaker.nav.inkles.list.InkleMenuFragment.Companion.INKLE_ID_KEY
import com.gdt.inklemaker.nav.inkles.list.InkleMenuFragment.Companion.INKLE_MENU_RESULT_CODE
import com.gdt.inklemaker.nav.inkles.list.InkleMenuFragment.Companion.SELECTED_OPTION_KEY
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import dagger.android.support.AndroidSupportInjection
import meewii.core.pickers.Option
import timber.log.Timber
import javax.inject.Inject

class InkleListFragment : Fragment(R.layout.fragment_base_list) {

  @Inject lateinit var viewModel: InkleListViewModel

  private lateinit var binding: FragmentBaseListBinding

  private val viewAdapter: InkleListAdapter = InkleListAdapter { onOpenMenu(it) }

  private val menuOptions = listOf(
    Option(title = R.string.inkle_menu_edit, icon = R.drawable.ic_edit_24px),
    Option(title = R.string.inkle_menu_duplicate, icon = R.drawable.ic_file_copy_24px),
    Option(title = R.string.inkle_menu_delete, icon = R.drawable.ic_delete_24px))

  private val inkleObserver = Observer<List<Inkle>> { list ->
    list?.let { inkles ->
      Timber.i("${inkles.size}")
      viewAdapter.setData(inkles.sortedBy { inkle -> inkle.createdAt })
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding = FragmentBaseListBinding.bind(view)
    binding.apply {
      layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
      adapter = viewAdapter
    }

    val fab = requireActivity().findViewById<FloatingActionButton>(R.id.main_fab)
    fab?.setOnClickListener { _ ->
      findNavController().navigate(R.id.action_add_inkle)
    }
    viewModel.inkles.observe(viewLifecycleOwner, inkleObserver)
    viewModel.subscribeToInkles()
  }

  private fun onOpenMenu(inkle: Inkle) {
    if (parentFragmentManager.findFragmentByTag(InkleMenuFragment.TAG) == null) {
      val targetFragment = InkleMenuFragment.create(menuOptions, inkle.id)
      targetFragment.setTargetFragment(this, INKLE_MENU_RESULT_CODE)
      parentFragmentManager.let { targetFragment.show(it, InkleMenuFragment.TAG) }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
      if (requestCode == INKLE_MENU_RESULT_CODE) {
        data?.extras?.apply {
          val option = getString(SELECTED_OPTION_KEY)?.let { Gson().fromJson<Option>(it, Option::class.java) }
          val inkleId = getString(INKLE_ID_KEY)
          if (inkleId != null && option != null) startActionForInkle(option, inkleId)
        }
      }
    }
  }

  private fun startActionForInkle(option: Option, inkleId: String) {
    when (option.title) {
      R.string.inkle_menu_delete -> {
        AlertDialog.Builder(requireContext())
          .setTitle(R.string.inkle_list_delete_confirm_title)
          .setPositiveButton(R.string.confirm_delete_yes) { _, _ -> viewModel.delete(inkleId) }
          .setNegativeButton(R.string.confirm_delete_cancel) { dialog, _ ->
            dialog.dismiss()
          }
          .show()
      }
      R.string.inkle_menu_edit -> {
        // TODO
      }
      R.string.inkle_menu_duplicate -> {
        // TODO
      }
    }

  }
}
