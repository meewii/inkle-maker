package meewii.core.extensions

import android.content.res.ColorStateList
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:errorMessage")
fun setErrorMessage(view: TextInputLayout, errorMessage: String? = null) {
  view.error = errorMessage
}

@BindingAdapter("app:errorMessage")
fun setErrorMessage(view: TextInputLayout, @StringRes errorMessage: Int? = null) {
  view.error = errorMessage?.let { view.context.getString(it) }
}

@BindingAdapter("app:setSelected")
fun setSelected(view: Button, isSelected: Boolean? = false) {
  isSelected?.let { view.isSelected = it }
}

@BindingAdapter("app:visibleIf")
fun View.visibleIf(isVisible: Boolean? = false) {
  visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("app:textColor")
fun TextView.setTextColor(@ColorInt color: Int) {
  if (color != 0) {
    setTextColor(color)
  }
}

@BindingAdapter("app:iconRes")
fun ImageView.setIconRes(@DrawableRes iconRes: Int) {
  setImageResource(iconRes)
}

@BindingAdapter("app:tintColor")
fun ImageView.setTintColor(@ColorInt color: Int) {
  if (color != 0) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
  }
}