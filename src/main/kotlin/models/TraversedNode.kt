package models

import com.google.gson.annotations.SerializedName

class Relationship(
  relname: String,
  rel: ArrayList<NodeRel>
  )
data class TraversedNode (

  @SerializedName("destroyed" ) var destroyed : Boolean? = null,
  @SerializedName("kind"      ) var kind      : String?  = null,
  @SerializedName("modified"  ) var modified  : String?  = null,
  @SerializedName("relationships" ) var relationships : Map<String, ArrayList<NodeRel>>,
  @SerializedName("state"     ) var state     : State?   = State()

)