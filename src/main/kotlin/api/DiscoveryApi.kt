package api

import models.*
import retrofit2.Call
import retrofit2.http.*

interface DiscoveryApi {

//    @POST("/token")
//    @JvmSuppressWildcards
//    suspend fun postAuthRequest(@Body body: RequestBody): Call<AuthentificationResponse>

    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    @POST("/api/token")
    fun authenticateUser(
        @Field("grant_type") grant_type: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<AuthentificationResponse>


    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/search")
    fun apiGetDataByQuery(
        @Query("format") format: String = "object",
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("results_id") results_id: String,
//        @Query("delete") delete: Boolean
    ): Call<List<ApiSearchResponse>>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/kinds/{kind}")
    fun apiGetKindsData(
        @Path("kind") kind: String,
        @Query("format") format: String = "object",
//        @Query("attributes") attributes: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("results_id") results_id: String,
//        @Query("delete") delete: Boolean
    ): Call<List<ApiKindsResponse>>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/nodes/{node_id}")
    fun apiGetTraversedNodeData(
        @Path("node_id") nodeId: String,
        @Query("traverse") traverse: String,
//        @Query("delete") delete: Boolean
    ): Call<TraversedNode>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/nodes/{node_id}")
    fun apiGetNodeData(
        @Path("node_id") nodeId: String,
        @Query("relationships") relationships: Boolean = true,
//        @Query("delete") delete: Boolean
    ): Call<Node>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("data/nodes/{node_id}/graph")
    fun apiGetGraph(
        @Path("node_id") nodeId: String,
        @Query("focus") focus: String = "software-connected",
        @Query("apply_rules") applyRules : Boolean = true,
        @Query("complete") complete : Boolean = false
//        @Query("delete") delete: Boolean
    ): Call<Graph>

    @Headers(
        "Content-Type: application/json"
    )
    @GET("taxonomy/nodekinds/{kind}")
    fun apiGetNodeKindDetails(
        @Path("kind") kind: String,
        @Query("locale") locale: String = "en-US",
    ): Call<NodeKindDetails>

}