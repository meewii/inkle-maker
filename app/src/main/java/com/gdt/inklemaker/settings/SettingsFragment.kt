package com.gdt.inklemaker.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gdt.inklemaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_settings, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val fab = requireActivity().findViewById<FloatingActionButton>(R.id.main_fab)
    fab?.visibility = View.GONE
  }

}
