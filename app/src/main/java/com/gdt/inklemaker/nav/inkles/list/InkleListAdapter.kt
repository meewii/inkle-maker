package com.gdt.inklemaker.nav.inkles.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gdt.inklemaker.R
import com.gdt.inklemaker.core.database.Inkle
import kotlinx.android.synthetic.main.li_inkle.view.ui_inkle_info
import kotlinx.android.synthetic.main.li_inkle.view.ui_inkle_menu
import kotlinx.android.synthetic.main.li_inkle.view.ui_inkle_name
import kotlinx.android.synthetic.main.li_inkle.view.ui_inkle_preview
import kotlinx.android.synthetic.main.li_yarn_cat.view.ui_yarn_category_name
import meewii.core.formatDateTo

class InkleListAdapter(private val onSelect: (Inkle) -> Unit) :
  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  companion object {
    const val TYPE_INKLE = 0
    const val TYPE_INKLE_CATEGORY = 1
  }

  class InkleViewHolder(view: View) : RecyclerView.ViewHolder(view)
  class InkleCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

  private var data: List<Any> = emptyList()

  override fun getItemViewType(position: Int): Int {
    return if (data[position] is Inkle) {
      TYPE_INKLE
    } else TYPE_INKLE_CATEGORY
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return if (viewType == TYPE_INKLE) {
      val view = inflater.inflate(R.layout.li_inkle, parent, false)
      InkleViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.li_yarn_cat, parent, false)
      InkleCategoryViewHolder(view)
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val item = data[position]
    if (holder.itemViewType == TYPE_INKLE) {
      holder.itemView.apply {
        item as Inkle
        ui_inkle_name.text = item.name
        ui_inkle_info.text = item.createdAt.formatDateTo("EEE, MMM dd yyyy")
        ui_inkle_menu.setOnClickListener { onSelect(item) }
        item.savedImage?.let { ui_inkle_preview.setImageBitmap(it) }
      }
    } else {
      holder.itemView.ui_yarn_category_name.text = item as String
    }
  }

  override fun getItemCount() = data.size

  fun setData(inkles: List<Any>) {
    data = inkles
    notifyDataSetChanged()
  }
}