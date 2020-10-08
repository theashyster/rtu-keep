package lv.theashyster.rtu_keep.screens.element.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_movie.*
import kotlinx.android.synthetic.main.fragment_text.*
import lv.theashyster.rtu_keep.fragments.DeleteDialogFragment
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.IMAGE_NOTE
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.TEXT_NOTE
import lv.theashyster.rtu_keep.screens.element.ElementViewModel
import lv.theashyster.rtu_keep.screens.main.MainActivity.Companion.EXTRA_ITEM_ID
import kotlin.random.Random.Default.nextInt

abstract class BaseFragment : Fragment(), ActivityClickListener {

    protected lateinit var fragmentActivity: FragmentActivity

    protected lateinit var fragmentContext: Context

    protected val viewModel: ElementViewModel by activityViewModels()

    protected val randomColor: Int = Color.rgb(nextInt(256), nextInt(256), nextInt(256))

    protected var item: NoteItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentActivity = requireActivity()
        fragmentContext = requireContext()

        val itemId = fragmentActivity.intent.getLongExtra(EXTRA_ITEM_ID, 0)
        item = viewModel.getItemById(itemId)
    }

    override fun shareClicked() {
        shareNote()
    }

    override fun deleteClicked() {
        item?.let {
            val dialogFragment = DeleteDialogFragment()
            dialogFragment.action = {
                viewModel.deleteItem(it)
                finishActivity(DELETE)
            }
            dialogFragment.show(fragmentActivity.supportFragmentManager, null)
        }
    }

    protected fun save(noteItem: NoteItem) {
        item?.let {
            noteItem.id = it.id
        }

        if (item == null) {
            viewModel.insertItem(noteItem)
            finishActivity(INSERT)
        } else {
            viewModel.updateItem(noteItem)
            finishActivity(UPDATE)
        }
    }

    private fun shareNote() {
        val intent = Intent(ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(EXTRA_SUBJECT, "${getString(R.string.email_subject)} ${getString(R.string.app_name)}")
            putExtra(EXTRA_TEXT, getExtraText())
        }

        startActivity(intent)
    }

    private fun getExtraText(): String {
        item?.let {
            return when (it.type) {
                TEXT_NOTE -> {
                    val title = if (it.title == textTitleEditText.text.toString()) {
                        it.title
                    } else {
                        textTitleEditText.text.toString()
                    }

                    val note = if (it.note == textNoteEditText.text.toString()) {
                        it.note
                    } else {
                        textNoteEditText.text.toString()
                    }

                    "${getString(R.string.email_text_text)}\n${getString(R.string.email_title_text)} - $title\n${
                        getString(
                            R.string.email_note_text
                        )
                    } - $note"
                }
                IMAGE_NOTE -> getString(R.string.email_image_text)
                else -> {
                    val title = if (it.title == movieTitleTextView.text.toString()) {
                        it.title
                    } else {
                        movieTitleTextView.text.toString()
                    }

                    val rating = if (it.rating == movieRatingBar.rating) {
                        it.rating
                    } else {
                        movieRatingBar.rating
                    }


                    "${getString(R.string.email_movie_text)}\n${getString(R.string.email_title_text)} - $title\n${
                        getString(
                            R.string.email_rating_text
                        )
                    } - $rating"
                }
            }
        }

        return ""
    }

    private fun finishActivity(action: Int) {
        val intent = Intent().putExtra(EXTRA_ACTION, action)
        fragmentActivity.setResult(RESULT_OK, intent)
        fragmentActivity.finish()
    }

    companion object {

        const val EXTRA_ACTION = "lv.theashyster.rtu_keep.extra.action"
        const val INSERT = 1
        const val UPDATE = 2
        const val DELETE = 3
    }
}

interface ActivityClickListener {

    fun shareClicked()

    fun deleteClicked()
}
