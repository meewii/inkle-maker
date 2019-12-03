package com.gdt.inklemaker.nav.yarns.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import kotlinx.android.synthetic.main.li_yarn_icon.view.ui_yarn_color
import kotlinx.android.synthetic.main.li_yarn_icon.view.ui_yarn_item

abstract class BaseIconYarnAdapter : RecyclerView.Adapter<BaseIconYarnAdapter.YarnViewHolder>() {

  class YarnViewHolder(view: View) : RecyclerView.ViewHolder(view)

  protected var yarnData: List<Yarn> = emptyList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YarnViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.li_yarn_icon, parent, false)
    return YarnViewHolder(view)
  }

  override fun onBindViewHolder(holder: YarnViewHolder, position: Int) {
    val item = yarnData[position]
    holder.itemView.apply {
      ui_yarn_color.contentDescription = item.name
      val color = Color.parseColor(item.color)
      ui_yarn_color.setImageResource(item.size.icon)
      ui_yarn_color.setColorFilter(color)
    }
  }

  override fun getItemCount() = yarnData.size

  fun setData(yarns: List<Yarn>) {
    yarnData = yarns
    notifyDataSetChanged()
  }
}

class SelectableIconYarnAdapter(private val onYarnSelected: ((Yarn) -> Unit)) : BaseIconYarnAdapter() {

  override fun onBindViewHolder(holder: YarnViewHolder, position: Int) {
    super.onBindViewHolder(holder, position)
    val item = yarnData[position]

    holder.itemView.apply {
      setOnClickListener { onYarnSelected(item) }
      val bg = if (item.isSelected) R.color.primary_light else R.color.surface
      ui_yarn_color.backgroundTintList = ContextCompat.getColorStateList(context, bg)
    }
  }

}

class IconYarnAdapter : BaseIconYarnAdapter() {

  override fun onBindViewHolder(holder: YarnViewHolder, position: Int) {
    super.onBindViewHolder(holder, position)
    holder.itemView.ui_yarn_item.background = null
  }

}
