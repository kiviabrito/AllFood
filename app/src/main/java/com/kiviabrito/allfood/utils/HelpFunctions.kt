package com.kiviabrito.allfood.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.kiviabrito.allfood.R
import com.kiviabrito.allfood.data.model.PlaceDTO
import java.text.Normalizer

class HelpFunctions {

    companion object {

        private val REGEX_UNACCENTED = "\\p{InCombiningDiacriticalMarks}+".toRegex()

        inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
            return if (p1 != null && p2 != null) block(p1, p2) else null
        }

        fun Double?.orZero(): Double = this ?: 0.0
        fun Float?.orZero(): Float = this ?: 0.0f
        fun Int?.orZero(): Int = this ?: 0

        fun String.removeAccent(): String {
            val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
            return REGEX_UNACCENTED.replace(temp, "")
        }

        fun List<PlaceDTO>.copy() = this.map { it.copy() }

        fun ImageView.loadImage(imageURL: String?) {
            imageURL?.let { url ->
                GlideApp.with(this.context)
                    .load(url)
                    .placeholder(R.mipmap.ic_restaurant_placeholder)
                    .into(this)
            }
        }

        fun ImageView.setDrawable(drawableResId: Int) {
            val drawable = ContextCompat.getDrawable(context, drawableResId)
            setImageDrawable(drawable)
        }

        fun View.setIsVisible(isVisible: Boolean){
            visibility = if (isVisible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        fun Activity.closeKeyboard() {
            val focusedView = currentFocus
            if (focusedView != null) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
            }
        }

    }

}