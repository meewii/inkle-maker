package com.gdt.inklemaker.nav.inkles.add.recap

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gdt.inklemaker.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_recap_inkle.btn_done

class RecapInkleFragment : Fragment(R.layout.fragment_recap_inkle) {

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    btn_done.setOnClickListener {
      findNavController().navigate(R.id.action_inkle_recap_done)
    }
  }

}