package com.gdt.inklemaker.nav.templates

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdt.inklemaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TemplateListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_template_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.main_fab)
        fab?.visibility = View.GONE
    }

}
