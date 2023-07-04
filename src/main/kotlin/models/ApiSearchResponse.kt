package models

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class ApiSearchResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("headings") var headings: List<String>,
    @SerializedName("kind") var kind: String?,
    @SerializedName("next") var next: String?,
    @SerializedName("next_offset") var next_offset: Int,
    @SerializedName("offset") var offset: Int,
    @SerializedName("results") var results: ArrayList<Any>,
    @SerializedName("results_id") var results_id: String?,
) {
    constructor() : this(0, arrayListOf(), null, "", 0, 0, arrayListOf(), "id")

    companion object {
        fun fromMapToSearchResponse(data: Any): ApiSearchResponse {
            val gson = GsonBuilder().create()
            return gson.fromJson(gson.toJson(data), ApiSearchResponse::class.java)
        }
    }
}

