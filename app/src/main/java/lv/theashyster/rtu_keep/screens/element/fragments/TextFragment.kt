package lv.theashyster.rtu_keep.screens.element.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat.getColor
import kotlinx.android.synthetic.main.fragment_text.*
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.TEXT_NOTE

class TextFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorNamesList = COLORS.map { getString(it.key) }
        val colorsList = COLORS.map { getColor(fragmentContext, it.value) }

        textColorSpinner.adapter =
            ArrayAdapter(fragmentContext, android.R.layout.simple_spinner_item, colorNamesList).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        item?.let {
            textTitleEditText.setText(it.title)
            textNoteEditText.setText(it.note)
            textColorSpinner.setSelection(colorsList.indexOf(it.color))
        }

        textSaveButton.setOnClickListener { saveText() }
    }

    private fun saveText() {
        val textTitle = textTitleEditText.text.toString()
        val textNote = textNoteEditText.text.toString()
        val textColor = textColorSpinner.selectedItem.toString()

        if (textTitle.isEmpty()) {
            textTitleEditText.error = getString(R.string.text_title_empty_text)
            Toast.makeText(fragmentContext, getString(R.string.text_title_empty_text), LENGTH_SHORT).show()
            return
        }

        if (textNote.isEmpty()) {
            textNoteEditText.error = getString(R.string.text_note_empty_text)
            Toast.makeText(fragmentContext, getString(R.string.text_note_empty_text), LENGTH_SHORT).show()
            return
        }

        val colorsMap = COLORS.mapKeys { getString(it.key) }
        val color = colorsMap[textColor]?.let { getColor(fragmentContext, it) }
        val noteItem = NoteItem(
            type = TEXT_NOTE,
            title = textTitle,
            note = textNote,
            color = color ?: randomColor
        )

        save(noteItem)
    }

    companion object {

        private val COLORS = mapOf(
            Pair(R.string.color_red, R.color.colorRed),
            Pair(R.string.color_green, R.color.colorGreen),
            Pair(R.string.color_blue, R.color.colorBlue)
        )
    }
}
