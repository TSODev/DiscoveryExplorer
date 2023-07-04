package models

import com.google.gson.annotations.SerializedName


data class Attrs (

    @SerializedName("description"  ) var description : String? = null,
    @SerializedName("display_name" ) var displayName : String? = null,
    @SerializedName("name"         ) var name        : String? = null,
    @SerializedName("type"         ) var type        : String? = null

)
