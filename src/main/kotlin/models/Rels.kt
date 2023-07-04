package models
import com.google.gson.annotations.SerializedName


data class Rels (

    @SerializedName("description"  ) var description : String? = null,
    @SerializedName("display_name" ) var displayName : String? = null,
    @SerializedName("spec"         ) var spec        : String? = null

)