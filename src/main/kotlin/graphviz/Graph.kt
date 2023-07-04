package graphviz

/*
The style attribute of Graphviz is used to control the appearance of nodes, edges, clusters and subgraphs. It can take one or more values separated by commas, and each value can have optional arguments in parentheses. For example:

node [style="filled,rounded"]
edge [style="dashed,tapered(10)"]

The recognized style values depend on the type of the component (node, edge, cluster or graph). Some common values are:

•  solid: Draws a solid outline or line (default)

•  dashed: Draws a dashed outline or line

•  dotted: Draws a dotted outline or line

•  bold: Draws a thicker outline or line

•  invis: Makes the component invisible

•  filled: Fills the background of the component with a color

•  striped: Fills the background of the component with vertical stripes of colors

•  wedged: Fills the background of the component with wedges of colors

•  rounded: Makes the corners of the component rounded

•  diagonals: Draws diagonal lines across the component

•  tapered: Makes the edge taper from a given width to 1 point

You can learn more about the style attribute from these sources:

•  style | Graphviz (Official Graphviz website)

•  style | Graphviz (Official Graphviz website)

•  Graphviz - style attribute (René Nyffenegger's website)

 */

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import models.KindType
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

private val logger = KotlinLogging.logger {  }

class Graph (
    var filename: String,
    val id: String,
    val label: String,

) {

    val gvFileName = "$filename.gv"
    val streamFile = FileOutputStream(File(gvFileName),false)
    val printWriter = PrintWriter(streamFile)
    init  {
        printWriter.println(
            """
            digraph "$id" {
             layout = neato
             mode = ipsep
             sep = "4"
             overlap=false
             center = true
             dim = 2
             label=""
             fontsize=12;
             margin=0.5
             bgcolor="gray96"
            """.trimIndent()
        )
    }

    fun addNodes(
        nodes: MutableList<Node>,
    ) {
        addMainNode(nodes)

        addNodeByKindType(nodes,KindType.HOST.type, color="black", style="solid", fontcolor = "black")
        addNodeByKindType(nodes,KindType.VIRTUALMACHINE.type,color="webpurple",style="diagonals", fontcolor = "webpurple")
        addNodeByKindType(nodes,KindType.SOFTWAREINSTANCE.type,color = "navy", style="solid", fontcolor = "navy")
        addNodeByKindType(nodes,KindType.DATABASE.type,color = "white", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.SOFTWARECOMPONENT.type,color="tomato",style="solid", fontcolor = "tomato")
        addNodeByKindType(nodes,KindType.LOADBALANCERMEMBER.type,color = "webgreen",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.LOADBALANCERINSTANCE.type, color ="darkolivegreen1",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.LOADBALANCERSERVICE.type, color="aquamarine",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.LOADBALANCERPOOL.type, color="aquamarine",style="solid", fontcolor = "gray10")

        addNodeByKindType(nodes,KindType.HARDWARECONTAINER.type, color="goldenrod",style="solid", fontcolor = "gray10")

        addNodeByKindType(nodes,KindType.FILESYSTEM.type, color ="orange",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.SUBNET.type, color = "hotpink",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.NETWORKDEVICE.type, color ="plum",style="solid", fontcolor = "gray10")
        addNodeByKindType(nodes,KindType.DISKDRIVE.type,color= "gray51",style="solid", fontcolor = "gray10")



//Default
        printWriter.print("node [shape=Mrecord, fontsize=10,height=0.2,width=0.4,fontname=\"Helvetica\",color=\"green\",style=\"filled\" fontcolor=\"black\"];")
        nodes.forEach { node ->
            printWriter.print("\"${node.shortName}\" [label=\"{<f0> ${node.shortName}|<f1> ${node.kind}}\"];")
        }
        printWriter.println()
    }

    private fun addMainNode(
        nodes: MutableList<Node>,
        type: String = KindType.HOST.type,
        shape: String = "Mrecord",
        fontsize: Int = 14,
        fontname: String = "Helvetica",
        fontcolor: String = "red",
        height: Double = 1.0,
        width: Double = 2.0,
        color: String = "red",
        style: String = "bold",
    ): List<Node> {
        printWriter.print("node [shape=$shape,fontsize=$fontsize,height=$height,width=$width,fontname=$fontname,color=$color,style=$style, fontcolor=$fontcolor];")
        val part = nodes.filter { it.shortName == id}
        part.forEach { node ->
            printWriter.print("\"${node.shortName}\" [label=\"{<f0> ${node.shortName}|<f1> ${node.kind}}\"];")
        }
        nodes.removeAll(part)           // !
        printWriter.println()
        return part
    }
    private fun addNodeByKindType(
        nodes: MutableList<Node>,
        type: String,
        shape: String = "Mrecord",
        fontsize: Int = 10,
        fontname: String = "Helvetica",
        fontcolor: String = "white",
        height: Double = 0.2,
        width: Double = 0.4,
        color: String,
        style: String = "filled",
    ): List<Node> {
        printWriter.print("node [shape=$shape,fontsize=$fontsize,height=$height,width=$width,fontname=$fontname,color=$color,style=$style, fontcolor=$fontcolor];")
        val part = nodes.filter { it.kind == type }
        when (type) {
//            KindType.VIRTUALMACHINE.type -> {
//                logger.info {"Traitement Machines Virtuelles"}
//                printWriter.print("\"$type\" [label=\"{$type|")
//                part.forEach { node ->
//                    printWriter.print("<${node.id}>${node.shortName}|")
//                }
//                printWriter.print("}\"];")
//            }
            else -> {
                part.forEach { node ->
                    printWriter.print("\"${node.shortName}\" [label=\"{${node.shortName}|${node.kind}}\"];")
                }
            }
        }

        nodes.removeAll(part)           // !
        printWriter.println()
        return part
    }

    fun addEdges(
        edges: MutableList<Edge>,
    ) {

        edges.forEach { edge ->
            printWriter.println("\"${edge.sourceId}\"->\"${edge.destinationId}\" [label = ${edge.destinationRole},fontsize=8,fontname=\"Helvetica\",color=\"grey25\"]")
            }
    }
    fun close() {
        printWriter.println("}")
        printWriter.close()
    }

    fun producePNG() {
        val gvFileName = "$filename.gv"
        val pngFileName = "$filename.png"
        val streamFile = FileOutputStream(File(pngFileName),false)
        try {
            val result = ProcessBuilder("dot", "-Tpng", gvFileName)
                .redirectOutput(File(pngFileName))
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
                .waitFor()
 //           println("dot has been processed")
        }
        catch (e: Exception) {
            println("Erreur :  ${e.message}")
        }


    }

    fun clean() {
        File("$filename.gv").delete()
        File("$filename.png").delete()
    }
}