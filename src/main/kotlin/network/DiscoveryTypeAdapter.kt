package network

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import models.ApiKindsResponse

import models.Kind
import models.KindType
import java.lang.reflect.Type

class DiscoveryTypeAdapterKinds : JsonDeserializer<ApiKindsResponse> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ApiKindsResponse {
//        println("Type Adaptor -  $json , $typeOfT , $context")

        val data = json.asJsonObject
        val count = data.get("count").asInt
        val kind = data.get("kind").asString
        val next = data.get("next").asString
        val next_offset = data.get("next_offset").asInt
        val offset = data.get("offset").asInt
        val results = data.getAsJsonArray("results").asJsonArray
        val results_id = data.get("results_id").asString

        val resultsInObject = arrayListOf<Kind>()

        when (kind) {
            KindType.HOST.type -> {
                results.forEach { result ->
                    val r = result.asJsonObject
//                    println(r)
                    val endpoint = if (r.get("#InferredElement:Inference:Associate:DiscoveryAccess.endpoint").isJsonNull)  "" else r.get("#InferredElement:Inference:Associate:DiscoveryAccess.endpoint").asString
                    val id = if (r.get("#id").isJsonNull)  "" else  r.get("#id").asString
                    val cloud = if (r.get("cloud").isJsonNull)  false else  r.get("cloud").asBoolean
                    val name = if (r.get("name").isJsonNull)  "" else  r.get("name").asString
                    val os = if (r.get("os").isJsonNull)  "" else  r.get("os").asString
                    val partition = if (r.get("partition").isJsonNull)  false else  r.get("partition").asBoolean
                    val vendor = if (r.get("vendor").isJsonNull)  "" else  r.get("vendor").asString
                    val virtual = if (r.get("virtual").isJsonNull)  false else  r.get("virtual").asBoolean
                    val node = Kind(endpoint, id, cloud, name, os, partition,vendor,  virtual)
                    resultsInObject.add(node)
                }
            }
            else -> {
                println("Type Adaptor - unknown Kind $kind , please add converter in the adaptor .")
            }
        }



        return ApiKindsResponse(count, kind, next, next_offset, offset, resultsInObject, results_id)
    }
}