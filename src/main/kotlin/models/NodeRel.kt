package models

import com.google.gson.annotations.SerializedName


data class NodeRel (

    @SerializedName("node" ) var node : String? = null,
    @SerializedName("rel"  ) var rel  : String? = null

)