package lv.theashyster.rtu_keep.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import kotlinx.android.synthetic.main.button_delete.view.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_text.view.*
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.utils.Util
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.IMAGE_NOTE
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.TEXT_NOTE
import lv.theashyster.rtu_keep.screens.main.AdapterClickListener

class NoteItemRecyclerAdapter(
    private val listener: AdapterClickListener,
    private val items: MutableList<NoteItem>
) : RecyclerView.Adapter<NoteItemRecyclerAdapter.NoteViewHolder>() {

    abstract class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(item: NoteItem)
    }

    inner class TextViewHolder(view: View) : NoteViewHolder(view) {

        override fun bind(item: NoteItem) {
            itemView.textItemTitleTextView.text = item.title
            itemView.textItemNoteTextView.text = item.note
        }
    }

    inner class ImageViewHolder(view: View) : NoteViewHolder(view) {

        override fun bind(item: NoteItem) {
            Glide.with(itemView)
                .load(item.uri)
                .error(R.drawable.ic_broken_image)
                .placeholder(
                    Util.getDefaultProgressBar(itemView.context) ?: ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_refresh
                    )
                )
                .override(SIZE_ORIGINAL)
                .into(itemView.imageItemImageView)
        }
    }

    inner class MovieViewHolder(view: View) : NoteViewHolder(view) {

        override fun bind(item: NoteItem) {
            Glide.with(itemView)
                .load(item.uri)
                .error(R.drawable.ic_broken_image)
                .placeholder(
                    Util.getDefaultProgressBar(itemView.context) ?: ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.ic_refresh
                    )
                )
                .override(SIZE_ORIGINAL)
                .into(itemView.movieItemImageView)
            itemView.movieItemTitleTextView.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TEXT_NOTE -> TextViewHolder(inflater.inflate(R.layout.item_text, parent, false))
            IMAGE_NOTE -> ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false))
            else -> MovieViewHolder(inflater.inflate(R.layout.item_movie, parent, false))
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = items[position]

        holder.bind(item)

        val itemView = holder.itemView as CardView

        itemView.setCardBackgroundColor(item.color)

        itemView.setOnClickListener {
            listener.itemClicked(item)
        }

        itemView.itemDeleteButton.setOnClickListener {
            listener.deleteClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }
}
