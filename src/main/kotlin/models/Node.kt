package models

import com.google.gson.annotations.SerializedName

data class Node (

  @SerializedName("destroyed" ) var destroyed : Boolean? = null,
  @SerializedName("kind"      ) var kind      : String?  = null,
  @SerializedName("modified"  ) var modified  : String?  = null,
  @SerializedName("relationships" ) var relationships : ArrayList<String> = arrayListOf(),
//  @SerializedName("state"     ) var state     : State?   = State()
  @SerializedName("state"     ) var state     : Map<String,Any> = mapOf()

)