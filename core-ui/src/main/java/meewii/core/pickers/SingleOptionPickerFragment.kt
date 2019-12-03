package meewii.core.pickers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_simple_recycler_view.recycler_view
import kotlinx.android.synthetic.main.li_option_picker.view.ui_option_icon
import kotlinx.android.synthetic.main.li_option_picker.view.ui_option_subtitle
import kotlinx.android.synthetic.main.li_option_picker.view.ui_option_title
import meewii.core.R
import meewii.core.extensions.visibleIf

/**
 * Base BottomSheetDialogFragment for Single Option menus.
 */
abstract class SingleOptionPickerFragment : BottomSheetDialogFragment() {

  private var viewAdapter: SingleOptionAdapter = SingleOptionAdapter(::onOptionSelected)

  private val _optionList: ArrayList<String>? by lazy { arguments?.getStringArrayList(PICKER_OPTION_LIST_KEY) }
  private lateinit var optionList: List<Option>

  companion object {
    private const val PICKER_OPTION_LIST_KEY = "PICKER_OPTION_LIST_KEY"
  }

  protected fun createBundle(optionList: List<Option>): Bundle {
    val list = ArrayList<String>().apply { addAll(optionList.map { Gson().toJson(it) }) }
    return Bundle().apply {
      putStringArrayList(PICKER_OPTION_LIST_KEY, list)
    }
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_simple_recycler_view, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    recycler_view.apply {
      layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
      adapter = viewAdapter
    }

    optionList = _optionList?.let { list ->
      list.map { Gson().fromJson<Option>(it, Option::class.java) }
    } ?: listOf()

    viewAdapter.setData(optionList)
  }

  abstract fun onOptionSelected(option: Option)
}

/**
 * Model use as list in SingleOptionPickerFragment
 */
data class Option(@StringRes val title: Int? = null, @StringRes val subtitle: Int? = null, @DrawableRes val icon: Int? = null)

/**
 * ViewHolder for Option model
 */
class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view)

/**
 * List adaption for SingleOptionPickerFragment
 */
class SingleOptionAdapter(private val onSelected: (Option) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var optionData: List<Option> = emptyList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.li_option_picker, parent, false)
    return OptionViewHolder(view)
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val option = optionData[position]
    holder.itemView.apply {
      option.icon?.let { ui_option_icon.setImageResource(it) }
      ui_option_icon.visibleIf(option.icon != null)

      option.title?.let { ui_option_title.text = context.getString(it) }
      ui_option_title.visibleIf(option.title != null)

      option.subtitle?.let { ui_option_subtitle.text = context.getString(it) }
      ui_option_subtitle.visibleIf(option.subtitle != null)

      setOnClickListener { onSelected(option) }
    }
  }

  fun setData(options: List<Option>) {
    optionData = options
    notifyDataSetChanged()
  }

  override fun getItemCount() = optionData.size

}
