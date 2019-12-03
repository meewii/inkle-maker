package com.gdt.inklemaker.nav.yarns

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.databinding.FragmentBaseListBinding
import com.gdt.inklemaker.nav.yarns.adapters.YarnListAdapter
import com.gdt.inklemaker.nav.yarns.business.YarnListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject

class YarnListFragment : Fragment(R.layout.fragment_base_list) {

  @Inject
  lateinit var viewModel: YarnListViewModel

  private val viewAdapter: YarnListAdapter = YarnListAdapter(::onDeleteYarn)
  private lateinit var binding: FragmentBaseListBinding

  private val yarnObserver = Observer<List<Yarn>> { yarns ->
    yarns?.let {
      Timber.v("$it")
      viewAdapter.setData(it.sortedBy { yarn -> yarn.name })
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
      layoutManager = LinearLayoutManager(context)
      adapter = viewAdapter
    }

    // TODO: Find better way to observe the fab click events
    val fab = requireActivity().findViewById<FloatingActionButton>(R.id.main_fab)
    fab?.visibility = View.VISIBLE
    fab?.setOnClickListener { _ ->
      findNavController().navigate(R.id.action_add_yarn)
    }

    viewModel.yarns.observe(viewLifecycleOwner, yarnObserver)
  }

  private fun onDeleteYarn(yarn: Yarn) {
    viewModel.deleteYarn(yarn)
  }
}
