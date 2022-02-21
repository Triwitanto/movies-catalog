package id.namikaze.moviescatalog.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GenresResponse (

  @SerializedName("genres")
  val genres: List<GenreResponse>

)