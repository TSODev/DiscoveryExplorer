package models

import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("kind"     ) var kind    : String? = null,
    @SerializedName("rel_id"   ) var relId   : String? = null,
    @SerializedName("src_id"   ) var srcId   : String? = null,
    @SerializedName("src_role" ) var srcRole : String? = null,
    @SerializedName("tgt_id"   ) var tgtId   : String? = null,
    @SerializedName("tgt_role" ) var tgtRole : String? = null
)
