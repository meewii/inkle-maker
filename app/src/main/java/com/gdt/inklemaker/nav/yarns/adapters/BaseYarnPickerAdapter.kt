package com.gdt.inklemaker.nav.yarns.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import kotlinx.android.synthetic.main.li_yarn_cat.view.ui_yarn_category_name
import kotlinx.android.synthetic.main.li_yarn_picker.view.ui_yarn_color
import kotlinx.android.synthetic.main.li_yarn_picker.view.ui_yarn_container
import kotlinx.android.synthetic.main.li_yarn_picker.view.ui_yarn_name

abstract class BaseYarnPickerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  companion object {
    const val TYPE_YARN = 0
    const val TYPE_YARN_CATEGORY = 1
  }

  class YarnViewHolder(view: View) : RecyclerView.ViewHolder(view)
  class YarnCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

  var yarnData: List<Any> = emptyList()

  override fun getItemViewType(position: Int): Int {
    return if (yarnData[position] is Yarn) {
      TYPE_YARN
    } else TYPE_YARN_CATEGORY
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == TYPE_YARN) {
      val view = inflater.inflate(R.layout.li_yarn_picker, parent, false)
      YarnViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.li_yarn_cat, parent, false)
      YarnCategoryViewHolder(view)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = yarnData[position]
    if (holder.itemViewType == YarnListAdapter.TYPE_YARN) {
      holder.itemView.apply {
        item as Yarn
        ui_yarn_container.setBackgroundColor(
          ContextCompat.getColor(
            context,
            if (item.isSelected) {
              R.color.primary_light
            } else {
              R.color.surface
            }
          )
        )
        ui_yarn_container.setOnClickListener { selectYarn(item) }

        ui_yarn_name.text = item.name

        val color = Color.parseColor(item.color)
        ui_yarn_color.setImageResource(item.size.icon)
        ui_yarn_color.setColorFilter(color)
      }
    } else {
      holder.itemView.ui_yarn_category_name.text = item as String
    }
  }

  protected abstract fun selectYarn(yarn: Yarn)

  override fun getItemCount() = yarnData.size

  fun setData(yarns: List<Any>) {
    yarnData = yarns
    notifyDataSetChanged()
  }

}

class YarnPickerAdapter : BaseYarnPickerAdapter() {
  override fun selectYarn(yarn: Yarn) {
    yarnData.find { it is Yarn && it.id == yarn.id }?.let {
      it as Yarn
      it.isSelected = yarn.isSelected.not()
    }
    setData(yarnData)
  }
}

class WeftPickerAdapter(private val onDonePickingWeft: (Yarn) -> Unit) : BaseYarnPickerAdapter() {
  override fun selectYarn(yarn: Yarn) {
    yarn.isSelected = true
    onDonePickingWeft(yarn)
  }
}
