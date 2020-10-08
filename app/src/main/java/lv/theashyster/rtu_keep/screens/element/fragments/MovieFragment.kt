package lv.theashyster.rtu_keep.screens.element.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat.getDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import kotlinx.android.synthetic.main.fragment_movie.*
import lv.theashyster.rtu_keep.BuildConfig.REEL_GOOD_IMG
import lv.theashyster.rtu_keep.repositories.NoteItem
import lv.theashyster.rtu_keep.R
import lv.theashyster.rtu_keep.repositories.Resource.*
import lv.theashyster.rtu_keep.utils.Util
import lv.theashyster.rtu_keep.screens.element.ElementActivity.Companion.MOVIE_NOTE

class MovieFragment : BaseFragment() {

    private var movieUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item?.let {
            movieUrl = it.uri

            movieTitleTextView.text = it.title
            movieRatingBar.rating = it.rating!!
            movieRatingBar.visibility = VISIBLE
            Glide.with(this)
                .load(movieUrl)
                .error(R.drawable.ic_broken_image)
                .placeholder(
                    Util.getDefaultProgressBar(fragmentContext) ?: getDrawable(
                        fragmentContext,
                        R.drawable.ic_refresh
                    )
                )
                .override(SIZE_ORIGINAL)
                .into(movieImageView)
        }

        movieGenerateButton.setOnClickListener { generateMovie() }
        movieSaveButton.setOnClickListener { saveMovie() }
    }

    private fun generateMovie() {
        viewModel.getMovie().observe(viewLifecycleOwner, {
            when (it) {
                is Loading -> showProgressBar()
                is Loaded -> hideProgressBar()
                is Error -> showError(it.message.orEmpty())
                is Success -> {
                    movieUrl = "$REEL_GOOD_IMG${it.data.id}/poster-780.webp"

                    movieTitleTextView.text = it.data.title
                    movieRatingBar.rating = it.data.imdbRating
                    movieRatingBar.visibility = VISIBLE
                    Glide.with(this)
                        .load(movieUrl)
                        .error(R.drawable.ic_broken_image)
                        .placeholder(
                            Util.getDefaultProgressBar(fragmentContext) ?: getDrawable(
                                fragmentContext,
                                R.drawable.ic_refresh
                            )
                        )
                        .override(SIZE_ORIGINAL)
                        .into(movieImageView)
                }
            }
        })
    }

    private fun showProgressBar() {
        movieProgressBar.visibility = VISIBLE
    }

    private fun hideProgressBar() {
        movieProgressBar.visibility = GONE
    }

    private fun showError(message: String) {
        Toast.makeText(fragmentContext, message, LENGTH_SHORT).show()
    }

    private fun saveMovie() {
        val movieTitle = movieTitleTextView.text.toString()

        if (movieTitle.isEmpty()) {
            Toast.makeText(fragmentContext, getString(R.string.movie_not_generated_text), LENGTH_SHORT).show()
            return
        }

        val noteItem = NoteItem(
            type = MOVIE_NOTE,
            title = movieTitle,
            rating = movieRatingBar.rating,
            uri = movieUrl,
            color = randomColor
        )

        save(noteItem)
    }
}
