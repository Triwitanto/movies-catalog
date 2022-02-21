package id.namikaze.moviescatalog.data.source.remote.response

import com.google.gson.annotations.SerializedName


data class ProductionCountries (

  @SerializedName("iso_3166_1")
  val iso31661 : String?,

  @SerializedName("name")
  val name : String?

)