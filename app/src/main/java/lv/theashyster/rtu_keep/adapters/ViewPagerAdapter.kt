package lv.theashyster.rtu_keep.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import lv.theashyster.rtu_keep.screens.element.ElementActivity
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.IMAGE_NOTE
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.TABS
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.TEXT_NOTE
import lv.theashyster.rtu_keep.screens.element.fragments.ImageFragment
import lv.theashyster.rtu_keep.screens.element.fragments.MovieFragment
import lv.theashyster.rtu_keep.screens.element.fragments.TextFragment

class ViewPagerAdapter(private val activity: ElementActivity) : FragmentStateAdapter(activity) {

    private var enabled: Boolean = true

    override fun getItemCount(): Int {
        return TABS.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            TEXT_NOTE -> TextFragment().also { activity.setListener(it) }
            IMAGE_NOTE -> ImageFragment().also { activity.setListener(it) }
            else -> MovieFragment().also { activity.setListener(it) }
        }
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}
