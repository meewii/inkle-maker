package com.gdt.inklemaker.nav.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdt.inklemaker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber

class GalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Find better way to observe the fab click events
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.main_fab)
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener { _ ->
            Timber.d("Clicky clicky in Gallery")
        }
    }

}
