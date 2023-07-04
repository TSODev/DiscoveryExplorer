package processing

import api.ServiceApi
import api.ServiceApi.Companion.apiGetTraversalNodes
import api.ServiceApi.Companion.apiTraversedNodesData
import graphviz.Edge
import graphviz.Node
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import models.Graph
import models.Kind

private val logger = KotlinLogging.logger {  }

class Host (
    private val server: String,
    private val unsafe: Boolean,
    private val verbose: Boolean,
    private var nodeInfo: models.Node,
    private var source: Kind,
    private var location: ArrayList<Pair<String,Map<String, Any>>>,
    private var osDetails: ArrayList<Pair<String,Map<String, Any>>>,
    private var communicatingWith: ArrayList<Pair<String,Map<String, Any>>>,
    private var hwRefData: ArrayList<Pair<String,Map<String, Any>>>,
    private var runningSoftware: ArrayList<Pair<String,Map<String, Any>>>,
    private var businessOwner: ArrayList<Pair<String,Map<String, Any>>>,
    private var itOwner: ArrayList<Pair<String,Map<String, Any>>>,
    private var supportOwner: ArrayList<Pair<String,Map<String, Any>>>,
    private var networkInterfaces: ArrayList<Pair<String,Map<String, Any>>>,
) {

    constructor(server: String, unsafe: Boolean, verbose: Boolean) :
            this(
                server,
                unsafe,
                verbose,
                models.Node(),
                Kind(),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf())),
                arrayListOf(Pair("",mapOf()))
            )



    fun getSource() = this.source
    fun getNodeInfo(): models.Node = this.nodeInfo
    fun getLocation() = this.location.first().second
    fun getOsDetails(): Map<String, Any> {
        var result = mapOf<String, Any>()
        if (this.osDetails.isNotEmpty()) result = this.osDetails.first().second
        return result
    }
    fun getHwRefData(): Map<String, Any> {
        var result = mapOf<String, Any>()
        if (this.hwRefData.isNotEmpty()) result = this.hwRefData.first().second
        return result
    }
    fun getRunningSoftware() = this.runningSoftware
    fun getBusinessOwner() = this.businessOwner.first().second
    fun getITOwner() = this.itOwner.first().second
    fun getSupportOwner() = this.supportOwner.first().second
    fun getNetworkInterfaces() = this.networkInterfaces
    fun getCommunicatingWith() = this.communicatingWith

    private fun setNodeInfos(id: String)  {
        nodeInfo = ServiceApi.apiNodesData(server,id, unsafe, verbose)!!
    }

    private fun setSource(source: Kind){
        this.source = source
    }
    private fun setLocation(source: Kind) {
        location =
            apiGetTraversalNodes(
                server,
                source,
                "ElementInLocation:Location:Location:Location",
                unsafe,
                verbose
            )
    }

    private fun  setOsDetails(source: Kind) {
        osDetails =
            apiGetTraversalNodes(
                server,
                source,
                "ElementWithDetail:SupportDetail:OSDetail:SupportDetail",
                unsafe,
                verbose
            )
    }

    private fun  setCommunicatingWith(source: Kind) {
        communicatingWith =
            apiGetTraversalNodes(
                server,
                source,
                "Host:ObservedCommunication:Host:Host",
                unsafe,
                verbose
            )
    }

    private fun setHwRefData(source: Kind) {
        hwRefData =
            apiGetTraversalNodes(
                server,
                source,
                "Hardware:ReferenceData:ReferenceData:HardwareReferenceData",
                unsafe,
                verbose
            )
    }

    private fun setRunningSoftware(source: Kind)  {
        runningSoftware =
            apiGetTraversalNodes(
                server,
                source,
                "Host:HostedSoftware:RunningSoftware:SoftwareInstance",
                unsafe,
                verbose
            )
    }

    private fun setBusinessOwner(source: Kind)  {
        businessOwner =
            apiGetTraversalNodes(
                server,
                source,
                "OwnedItem:Ownership:BusinessOwner:Person",
                unsafe,
                verbose
            )
    }
    private fun setITOwner(source: Kind){
        itOwner =
            apiGetTraversalNodes(
                server,
                source,
                "OwnedItem:Ownership:ITOwner:Person",
                unsafe,
                verbose
            )
    }
    private fun setSupportOwner(source: Kind)  {
        supportOwner =
            apiGetTraversalNodes(
                server,
                source,
                "OwnedItem:Ownership:SupportOwner:Person",
                unsafe,
                verbose
            )

    }

    private fun setNetworkInterfaces(source: Kind) {
        networkInterfaces =
            apiGetTraversalNodes(
                server,
                source,
                "DeviceWithAddress:DeviceAddress:IPv4Address:IPAddress",
                unsafe,
                verbose
            )
    }

    fun setHost(source: Kind) = runBlocking {
        setSource(source)
        setNodeInfos(source.id)
        setHwRefData(source)
        setLocation(source)
        setOsDetails(source)
        setRunningSoftware(source)
        setBusinessOwner(source)
        setITOwner(source)
        setSupportOwner(source)
        setNetworkInterfaces(source)
        setCommunicatingWith(source)
    }

    fun logHost(){
        if (verbose) {
            logger.info(" Infos         | ${this.getNodeInfo()}")
            logger.info(" ID / Name     | ${this.source.id} ${this.source.name}")
            logger.info(" Location      | ${this.getLocation()}")
            logger.info(" Business      | ${this.getBusinessOwner()}")
            logger.info(" IT            | ${this.getITOwner()}")
            logger.info(" Support       | ${this.getSupportOwner()}")
            logger.info(" OS            | ${this.getOsDetails()}")
            logger.info(" HW            | ${this.getHwRefData()}")
            logger.info(" Software      | ${this.getRunningSoftware()}")
            logger.info(" Network       | ${this.getNetworkInterfaces()}")
            logger.info(" Communication | ${this.getCommunicatingWith()}")
        }
    }

    fun setGraph(graph: Graph, graphviz: graphviz.Graph) {
        var nodeList = mutableListOf<Node>()
        var edgeList = mutableListOf<Edge>()

        if (verbose) graph.nodes.forEach { logger.debug(" [GRAPH-NODES] :  ${it.toString()}") }
        graph.links.forEach { link ->
            val srcNode = graph.nodes.filter { it.id == link.srcId }[0]
            val tgtNode = graph.nodes.filter { it.id == link.tgtId }[0]
            if (verbose) logger.debug(" [LINK] (${link.kind}) from ${link.srcRole} : ${srcNode.shortName} to ${link.tgtRole} : ${tgtNode.shortName} ")
        }
//        if (verbose) logger.debug("[NODE] ${graph.nodes.toString()}")

        graph.nodes.forEach { node ->
            nodeList.add(Node("circle", node.id!!, node.name!!, node.shortName!!, node.kind!!))
        }
        graphviz.addNodes(nodeList)
        graph.links.forEach { link ->
            val srcNode = graph.nodes.filter { it.id == link.srcId }[0]
            val tgtNode = graph.nodes.filter { it.id == link.tgtId }[0]
            edgeList.add(
                Edge(
                    link.kind!!,
                    srcNode.shortName!!,
                    link.srcRole!!,
                    tgtNode.shortName!!,
                    link.tgtRole!!
                )
            )
        }
        graphviz.addEdges((edgeList))
        graphviz.close()
    }

    fun getAllHostByKind() {

    }
}