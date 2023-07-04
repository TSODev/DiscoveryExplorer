package models

import com.google.gson.annotations.SerializedName


data class DisplayRels (

    @SerializedName("description"  ) var description : String?           = null,
    @SerializedName("display_name" ) var displayName : String?           = null,
    @SerializedName("name"         ) var name        : String?           = null,
    @SerializedName("specs"        ) var specs       : ArrayList<String> = arrayListOf()

)