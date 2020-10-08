package lv.theashyster.rtu_keep.utils

import android.content.Context
import android.graphics.drawable.Drawable

object Util {

    fun getDefaultProgressBar(context: Context): Drawable? {
        val attributes = arrayOf(android.R.attr.indeterminateDrawable).toIntArray()
        val styledAttributes = context.obtainStyledAttributes(android.R.style.Widget_ProgressBar, attributes)
        try {
            return styledAttributes.getDrawable(0)
        } finally {
            styledAttributes.recycle()
        }
    }
}
