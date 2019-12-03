package com.gdt.inklemaker.nav.inkles.add.draw

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.nav.inkles.add.InkleRepository
import com.gdt.inklemaker.nav.yarns.adapters.SelectableIconYarnAdapter
import com.gdt.inklemaker.ui.patterns.HexPatternView
import com.gdt.inklemaker.ui.patterns.HexPatternViewResHolder
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_draw_inkle.dif_drawing_view_container
import kotlinx.android.synthetic.main.fragment_draw_inkle.dif_yarn_list
import meewii.core.ViewStatus
import timber.log.Timber
import javax.inject.Inject

class DrawInkleFragment : Fragment(R.layout.fragment_draw_inkle) {

  companion object {
    const val WIP_INKLE_ID_ARG = "WIP_INKLE_ID_ARG"
  }

  @Inject lateinit var viewModel: DrawInkleViewModel
  @Inject lateinit var inkleRepository: InkleRepository

  private val viewAdapter: SelectableIconYarnAdapter = SelectableIconYarnAdapter(::onYarnSelected)
  private var hexViewPattern: HexPatternView? = null
  private var wipInkleId: String? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    setHasOptionsMenu(true)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel.wipInkle.observe(viewLifecycleOwner, Observer {
      val colorMap: Map<String, Int> = it.generateColorMap()
      hexViewPattern = HexPatternView(
        colorMap, it.upperWarpYarnIds, it.lowerWarpYarnIds, it.weftYarnId,
        HexPatternViewResHolder(requireContext()), requireContext()
      )
      dif_drawing_view_container.addView(hexViewPattern)
      viewAdapter.setData(it.yarns)
    })
    viewModel.viewStatus.observe(viewLifecycleOwner, Observer { processViewStatus(it) })

    wipInkleId = arguments?.getString(WIP_INKLE_ID_ARG)
    wipInkleId?.let { viewModel.getInkle(it) }

    dif_yarn_list.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = viewAdapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_draw_inkle, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_done -> {
        hexViewPattern?.let {
          wipInkleId?.let { inkleId ->
            viewModel.saveDrawing(inkleId, it.upperYarnIds, it.lowerYarnIds, it.weftYarnId)
          }
        }
        super.onOptionsItemSelected(item)
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun onYarnSelected(yarn: Yarn) {
    hexViewPattern?.apply {
      selectedColumns.forEach {
        if (it % 2 == 0) {
          upperYarnIds[it.toUpperIndex()] = yarn.id
        } else {
          lowerYarnIds[it.toLowerIndex()] = yarn.id
        }
      }
      if (isWeftSelected) {
        weftYarnId = yarn.id
      }
      invalidate()
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  private fun Int.toUpperIndex(): Int {
    return this / 2
  }

  private fun Int.toLowerIndex(): Int {
    return (this - 1) / 2
  }

  private fun processViewStatus(status: ViewStatus) {
    when (status) {
      is ViewStatus.Error -> {
      }
      is ViewStatus.Idle -> {
      }
      is ViewStatus.Loading -> {
        // TODO Start a loading wheel
      }
      is ViewStatus.Success -> {
        status.message?.let { msg ->
          val args = Bundle().apply {
            putString(WIP_INKLE_ID_ARG, msg)
          }

          hexViewPattern?.apply {
            redrawForImage()
            val disposable = inkleRepository
              .saveToStorage(this, msg, totalWidth, totalHeight)
              .subscribe({ status ->
                findNavController().navigate(R.id.action_inkle_draw_done, args)
                // TODO Stop the loading wheel
                // TODO Display error if storing img failed
              }, {
                Timber.e("Error while saving image to storage", it)
                // TODO Display error that storing img failed
                findNavController().navigate(R.id.action_inkle_draw_done, args)
              })
          }

        }
      }
    }
  }

}