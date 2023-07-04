package api


import io.github.oshai.kotlinlogging.KotlinLogging
import models.*
import network.RetrofitClient
import java.net.URL

private val logger = KotlinLogging.logger {}

interface ServiceApi {

    companion object {

        fun apiGetToken(serverUrl: String, username: String, password: String, unsafe: Boolean): String? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)

            return apiService.authenticateUser("password", username, password).execute().body()?.token

        }

        fun apiQueryData(serverUrl: String, query: String, unsafe: Boolean, verbose: Boolean): List<Any>? {
            val discoveredData: MutableList<ApiSearchResponse> = mutableListOf()
//            if (verbose) logger.debug("Requetage pour  ($query)")
            getAllDataFragment(serverUrl, unsafe, query, 0, 100, "", discoveredData, verbose)
            return discoveredData
        }

        fun apiKindsData(serverUrl: String, kind: String, unsafe: Boolean, verbose: Boolean): List<Kind> {
            val kindsData: MutableList<Kind> = mutableListOf()
//            if (verbose) logger.debug("Requetage Kind pour  ($kind)")
            getAllKindsFragment(serverUrl, unsafe, kind,  0, 100, "", kindsData, verbose)
            return kindsData
        }

        fun apiNodesData(serverUrl: String, id: String, unsafe: Boolean, verbose: Boolean): Node? {
//            if (verbose) logger.debug("Requetage Noeud pour  ($id)")
            return getNode(serverUrl, unsafe, id, verbose)
        }

        fun apiTraversedNodesData(serverUrl: String, id: String, traverse: String, unsafe: Boolean, verbose: Boolean): TraversedNode? {
//            if (verbose) logger.debug("Requetage Traverse : $traverse")
            return getTraversedNode(serverUrl, traverse, unsafe, id, verbose)
        }

        fun apiGetSoftwareConnectedGraph(serverUrl: String, id: String, unsafe: Boolean, verbose: Boolean): Graph? {
//            if (verbose) logger.debug("Requetage Graphe pour le noeud  ($id)")
            return getGraph(serverUrl, "software-connected", false, unsafe, id, verbose)
        }

        fun apiGetSoftwareGraph(serverUrl: String, id: String, unsafe: Boolean, verbose: Boolean): Graph? {
            if (verbose) logger.debug("Requetage Graphe pour le noeud  ($id)")
            return getGraph(serverUrl, "software", false, unsafe, id, verbose)
        }

        fun apiGetInfrastructureGraph(serverUrl: String, id: String, unsafe: Boolean, verbose: Boolean): Graph? {
            if (verbose) logger.debug("Requetage Graphe pour le noeud  ($id)")
            return getGraph(serverUrl, "infrastructure", false, unsafe, id, verbose)
        }
        fun apiGetNodeKindDetails(serverUrl: String, kind: String, unsafe: Boolean, verbose: Boolean): NodeKindDetails? {
            if (verbose) logger.debug("Requetage Details pour le kind  ($kind)")
            return getNodeKindDetails(serverUrl, unsafe, kind, verbose)
        }
        fun apiGetTraversalNodes(server: String, source: Kind, traverse: String, unsafe: Boolean, verbose: Boolean) : ArrayList<Pair<String,Map<String, Any>>> {
            val traversed = arrayListOf<Pair<String, Map<String,Any>>>()
            try {
                logger.debug("$traverse pour Source: ${source}")
                apiTraversedNodesData(server, source.id, traverse, unsafe, verbose).let { node ->
                    val relation = node?.relationships?.get(traverse)
                    if (!relation.isNullOrEmpty()) {
                        relation.forEach { rel ->
                            rel.node?.let {
                                try {
//                                    logger.debug("$traverse pour Node: ${it}")
                                    apiNodesData(server, it, unsafe, verbose).let { dest ->
                                        logger.debug("$traverse Node: ${dest}")
                                        traversed.add(Pair( traverse, dest?.state!!))//********** !!!!
                                    }
                                } catch (e: Exception) {
                                    logger.error(e){"Erreur Traverse : $e)"}
                                }

                            }
                        }
                    }
                    else {
                        if (verbose) logger.debug {"no Node attached on traverse $traverse"}
//                        traversed.add(mapOf())
                    }
                }
            } catch (e: Exception)
            {
                logger.error(e){"Erreur : $e"}
            }
            return traversed
        }

        fun getValueFromTraverseResult(
            traverseResult: ArrayList<Map<String, Any>>,
            key: String
        ): String {
            var value = ""
            if (traverseResult.isNotEmpty()) value = traverseResult[0][key].toString()
            return value
        }

        private fun getAllDataFragment(
            serverUrl: String,
            unsafe: Boolean,
            query: String,
            offset: Int,
            limit: Int,
            results_id: String,
            discoveredData: MutableList<ApiSearchResponse>,
            verbose: Boolean
        ): List<Any> {

            val searchData = mutableListOf<ApiSearchResponse>()
            val result = mutableListOf<Any>()
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiService = retrofit.create(DiscoveryApi::class.java)

            //           val discoveredData: MutableList<ApiSearchData> = mutableListOf()
            val response = apiService.apiGetDataByQuery(
                query = query,
                offset = offset,
                limit = limit,
                results_id = results_id,
//                delete = false
            ).execute()

            val data = response.body()?.first()
            if (data != null) {
                discoveredData.add(data)
                data.results.forEach { result.add(it) }

                if (!data.next.isNullOrEmpty()) {
                    val next = URL(data.next)
                    val requestedNext = next.query
                        .removePrefix("query=")
                        .replace('+', ' ')
                        .split('&')
                    val nextQuery = requestedNext.first()
                    val nextOffset = data.next_offset
                    if (verbose) logger.info("Requetage Node complémentaire avec offset ${data.next_offset} $next")
                    getAllDataFragment(
                        serverUrl,
                        unsafe,
                        nextQuery,
                        data.next_offset,
                        limit,
                        data.results_id!!,
                        discoveredData,
                        verbose
                    )
                    discoveredData.forEach {
                        it.results.forEach { it ->
                            result.add(it)
                        }
                    }
                }
            }
            return result
        }


        private fun getAllKindsFragment(
            serverUrl: String,
            unsafe: Boolean,
            kind: String,
//            attributes_query: String?,
            offset: Int,
            limit: Int,
            results_id: String,
            kindsData: MutableList<Kind>,
            verbose: Boolean
        ): List<Kind> {

            val searchData = mutableListOf<Kind>()
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)

            //           val discoveredData: MutableList<ApiSearchData> = mutableListOf()
            val response = apiDiscovery.apiGetKindsData(
                kind = kind,
//                attributes = attributes_query,
                offset = offset,
                limit = limit,
                results_id = results_id,
//                delete = false
            ).execute()

            val data = response.body()?.first()
            if (data != null) {
                data.results.forEach { kindsData.add(it) }
                var nbOfKindAlreadyProcessed = 100
                if (!data.next.isNullOrEmpty()) {
                    val next = URL(data.next)
                    val requestedNext = next.query
                        .removePrefix("query=")
                        .replace('+', ' ')
                        .split('&')
                    val nextQuery = requestedNext.first()
                    val nextOffset = data.next_offset
 //                   if (verbose) logger.debug("Requetage Kind complémentaire avec offset ${data.next_offset} $next")
                    val nextData = getAllKindsFragment(
                        serverUrl,
                        unsafe,
                        nextQuery,
                        nextOffset,
                        limit,
                        data.results_id!!,
                        searchData,
                        verbose
                    )
                    nextData.forEach { data ->
                        kindsData.add(data)
                    }
                }
            }

            return kindsData
        }


        private fun getNode(
            serverUrl: String,
            unsafe: Boolean,
            id: String,
            verbose: Boolean
        ): Node? {

            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            val response = apiDiscovery.apiGetNodeData(
                nodeId = id
            ).execute()
            return response.body()
        }

        private fun getTraversedNode(
            serverUrl: String,
            traverse: String,
            unsafe: Boolean,
            id: String,
            verbose: Boolean
        ): TraversedNode? {

            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            val response = apiDiscovery.apiGetTraversedNodeData(
                nodeId = id,
                traverse = traverse
            ).execute()
//            if (verbose) logger.debug {"Traverse : $traverse - ${response.body()}"}
            return response.body()
        }

        private fun getGraph(
            serverUrl: String,
            focus: String,
            complete: Boolean,
            unsafe: Boolean,
            id: String,
            verbose: Boolean): Graph? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            val response = apiDiscovery.apiGetGraph(
                nodeId = id , focus = focus, complete = complete
            ).execute()
            return response.body()
        }

        private fun getNodeKindDetails(
            serverUrl: String,
            unsafe: Boolean,
            kind: String,
            verbose: Boolean): NodeKindDetails? {
            val retrofit =
                if (!unsafe) RetrofitClient.getClient(serverUrl) else RetrofitClient.getUnsafeClient(serverUrl)
            val apiDiscovery = retrofit.create(DiscoveryApi::class.java)
            val response = apiDiscovery.apiGetNodeKindDetails(
                kind = kind
            ).execute()
     //       println(response.body())
            return response.body()
        }




    }
}




