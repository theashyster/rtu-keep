package lv.theashyster.rtu_keep.screens.element

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import lv.theashyster.rtu_keep.screens.element.fragments.ActivityClickListener
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.adapters.ViewPagerAdapter
import lv.theashyster.rtu_keep.databinding.ActivityElementBinding
import lv.theashyster.rtu_keep.screens.main.MainActivity.Companion.EXTRA_ITEM_ID

class ElementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElementBinding

    private lateinit var listener: ActivityClickListener

    private val viewModel: ElementViewModel by viewModels()

    private var item: NoteItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.add_note)

        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.elementViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.elementTabLayout, binding.elementViewPager) { tab, position ->
            tab.text = TABS[position]?.let { getString(it) }
        }.attach()

        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0)
        item = viewModel.getItemById(itemId)

        item?.let {
            supportActionBar?.setTitle(R.string.edit_note)
            binding.elementViewPager.setCurrentItem(it.type, false)
            binding.elementViewPager.isUserInputEnabled = false

            TABS.forEach { tab ->
                binding.elementTabLayout.getTabAt(tab.key)?.view?.isEnabled = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_element, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val shareElementButton = menu?.findItem(R.id.shareElementButton)
        val deleteElementButton = menu?.findItem(R.id.deleteElementButton)

        item?.let {
            shareElementButton?.isVisible = true
            deleteElementButton?.isVisible = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.shareElementButton -> {
                listener.shareClicked()
                true
            }
            R.id.deleteElementButton -> {
                listener.deleteClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setListener(listener: ActivityClickListener) {
        this.listener = listener
    }

    companion object {

        const val TEXT_NOTE = 0
        const val IMAGE_NOTE = 1
        const val MOVIE_NOTE = 2

        val TABS = mapOf(
            Pair(TEXT_NOTE, R.string.text_tab),
            Pair(IMAGE_NOTE, R.string.image_tab),
            Pair(MOVIE_NOTE, R.string.movie_tab)
        )
    }
}
