package com.gdt.inklemaker.nav.yarns.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Yarn
import kotlinx.android.synthetic.main.li_yarn.view.ui_yarn_color
import kotlinx.android.synthetic.main.li_yarn.view.ui_yarn_delete
import kotlinx.android.synthetic.main.li_yarn.view.ui_yarn_name
import kotlinx.android.synthetic.main.li_yarn.view.ui_yarn_size
import kotlinx.android.synthetic.main.li_yarn_cat.view.ui_yarn_category_name

class YarnListAdapter(private val onDelete: (Yarn) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  companion object {
    const val TYPE_YARN = 0
    const val TYPE_YARN_CATEGORY = 1
  }

  class YarnViewHolder(view: View) : RecyclerView.ViewHolder(view)
  class YarnCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

  private var yarnData: List<Any> = emptyList()

  override fun getItemViewType(position: Int): Int {
    return if (yarnData[position] is Yarn) {
      TYPE_YARN
    } else TYPE_YARN_CATEGORY
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == TYPE_YARN) {
      val view = inflater.inflate(R.layout.li_yarn, parent, false)
      YarnViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.li_yarn_cat, parent, false)
      YarnCategoryViewHolder(view)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = yarnData[position]
    if (holder.itemViewType == TYPE_YARN) {
      holder.itemView.apply {
        item as Yarn
        ui_yarn_name.text = item.name
        val color = Color.parseColor(item.color)
        ui_yarn_color.setImageResource(item.size.icon)
        ui_yarn_color.setColorFilter(color)
        ui_yarn_size.text = context.getString(item.size.label)
        ui_yarn_delete.setOnClickListener { onDelete(item) }
      }
    } else {
      holder.itemView.ui_yarn_category_name.text = item as String
    }
  }

  override fun getItemCount() = yarnData.size

  fun setData(yarns: List<Any>) {
    yarnData = yarns
    notifyDataSetChanged()
  }
}