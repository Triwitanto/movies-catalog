package id.namikaze.moviescatalog.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ProductionCompanies (

  @SerializedName("id")
  val id : Int?,

  @SerializedName("logo_path")
  val logoPath : String?,

  @SerializedName("name")
  val name : String?,

  @SerializedName("origin_country")
  val originCountry : String?

)