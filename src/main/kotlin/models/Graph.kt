package models

import com.google.gson.annotations.SerializedName

data class Graph(
    @SerializedName("links" ) var links : ArrayList<Links> = arrayListOf(),
    @SerializedName("nodes" ) var nodes : ArrayList<Nodes> = arrayListOf()
)
