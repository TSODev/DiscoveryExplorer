package models

import com.google.gson.annotations.SerializedName

data class Nodes(
    @SerializedName("id"         ) var id        : String? = null,
    @SerializedName("kind"       ) var kind      : String? = null,
    @SerializedName("name"       ) var name      : String? = null,
    @SerializedName("short_name" ) var shortName : String? = null
)
