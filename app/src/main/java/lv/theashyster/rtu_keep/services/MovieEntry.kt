package lv.theashyster.rtu_keep.services

import com.google.gson.annotations.SerializedName

data class MovieEntry(
    val id: String,
    val title: String,
    @SerializedName("imdb_rating")
    val imdbRating: Float
)
