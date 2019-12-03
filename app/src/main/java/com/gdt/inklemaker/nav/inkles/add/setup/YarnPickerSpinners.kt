package com.gdt.inklemaker.nav.inkles.add.setup

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import com.gdt.inklemaker.nav.yarns.adapters.IconYarnAdapter
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_background
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_error
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_label_warp
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_label_weft_icon
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_warp_button
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_warp_recycler
import kotlinx.android.synthetic.main.layout_yarn_picker_spinners.view.yps_weft_container

class YarnPickerSpinners @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs,
    defStyleAttr) {

  private val yarnViewAdapter: IconYarnAdapter = IconYarnAdapter()

  var warpClickListener: OnClickListener? = null
    set(value) {
      field = value
      yps_warp_button.setOnClickListener(value)
    }

  var weftClickListener: OnClickListener? = null
    set(value) {
      field = value
      yps_weft_container.setOnClickListener(value)
    }

  var selectedWeft: Yarn? = null
    set(value) {
      field = value
      value?.let {
        val color = Color.parseColor(value.color)
        yps_label_weft_icon.setColorFilter(color)
      }
    }

  var selectedWarpList: List<Yarn>? = null
    set(value) {
      field = value
      value?.let {
        yarnViewAdapter.setData(value)
      }
    }

  var error: String? = null
    set(value) {
      field = value
      if (value == null) {
        yps_background.error = value
        yps_error.text = ""
        yps_error.visibility = View.GONE
        yps_label_warp.setTextColor(ContextCompat.getColor(context, R.color.primary))
      } else {
        yps_background.error = value
        yps_error.text = value
        yps_error.visibility = View.VISIBLE
        yps_label_warp.setTextColor(ContextCompat.getColor(context, R.color.error))
      }
    }

  init {
    val view = LayoutInflater.from(context).inflate(R.layout.layout_yarn_picker_spinners, this)
    view.yps_warp_recycler.apply {
      layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
      adapter = yarnViewAdapter
    }
  }

}

@BindingAdapter("app:errorMessage", requireAll = false)
internal fun YarnPickerSpinners.bind(errorMessage: String?) {
  this.error = errorMessage
}

@BindingAdapter("app:errorMessage", requireAll = false)
internal fun YarnPickerSpinners.bindRes(@StringRes errorMessage: Int?) {
  this.error = errorMessage?.let { context.getString(it) }
}