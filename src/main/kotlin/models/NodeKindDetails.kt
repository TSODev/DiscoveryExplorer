package models


import com.google.gson.annotations.SerializedName


data class NodeKindDetails (

    @SerializedName("attrs"                  ) var attrs                : ArrayList<Attrs>          = arrayListOf(),
    @SerializedName("category"               ) var category             : String?                   = null,
    @SerializedName("child_classes"          ) var childClasses         : ArrayList<String>         = arrayListOf(),
    @SerializedName("description"            ) var description          : String?                   = null,
    @SerializedName("display_name"           ) var displayName          : String?                   = null,
    @SerializedName("display_plural"         ) var displayPlural        : String?                   = null,
    @SerializedName("display_rels"           ) var displayRels          : ArrayList<DisplayRels>    = arrayListOf(),
    @SerializedName("exprs"                  ) var exprs                : ArrayList<String>         = arrayListOf(),
    @SerializedName("extension_attrs"        ) var extensionAttrs       : ArrayList<String>         = arrayListOf(),
    @SerializedName("inherited_attrs"        ) var inheritedAttrs       : ArrayList<InheritedAttrs> = arrayListOf(),
    @SerializedName("inherited_display_rels" ) var inheritedDisplayRels : ArrayList<String>         = arrayListOf(),
    @SerializedName("inherited_exprs"        ) var inheritedExprs       : ArrayList<String>         = arrayListOf(),
    @SerializedName("inherited_rels"         ) var inheritedRels        : ArrayList<InheritedRels>  = arrayListOf(),
    @SerializedName("label_attr"             ) var labelAttr            : String?                   = null,
    @SerializedName("long_description"       ) var longDescription      : String?                   = null,
    @SerializedName("name"                   ) var name                 : String?                   = null,
    @SerializedName("parent_class"           ) var parentClass          : String?                   = null,
    @SerializedName("rels"                   ) var rels                 : ArrayList<Rels>           = arrayListOf(),
    @SerializedName("section"                ) var section              : String?                   = null

)