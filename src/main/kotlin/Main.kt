
import api.ServiceApi.Companion.apiGetInfrastructureGraph
import api.ServiceApi.Companion.apiGetSoftwareConnectedGraph
import api.ServiceApi.Companion.apiGetToken
import api.ServiceApi.Companion.apiKindsData
import api.ServiceApi.Companion.apiNodesData
import api.ServiceApi.Companion.apiTraversedNodesData
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import graphviz.Graph
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import models.Kind
import models.KindType
import network.TokenHolder
import processing.ExplorerDocument
import processing.Host
import java.io.FileOutputStream
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {  }

//enum class LoggingLevel(){
//    TRACE,
//    DEBUG,
//    INFO,
//    WARN,
//    ERROR
//}

//Host:ObservedCommunication:Host:Host [Communicating With]
//Other groups that this group is communicating with

//ElementWithDetail:SupportDetail:OSDetail:SupportDetail [OS Support Details]
//OS Support Details for this Host

class ParsedArgs(parser: ArgParser) {

    val validURL = "^(http(s):\\/\\/.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)(/)\$"
    val verbose by parser.flagging(
        "-v", "--verbose",
        help = "valide le mode verbeux"
    )

    val server by parser.storing(
        "-s", "--server",
        help = "URL API du serveur Discovery , (https et termine avec '/') \n " +
        "généralement https://server/api/v1.4/"
    )
//        .addValidator {
//        if (!value.matches(Regex(validURL)))
//            throw InvalidArgumentException("URL du serveur invalide : $value \n" +
//                "lancer le programme avec l'option -h pour de l'aide")
//    }

    val unsafe by parser.flagging(
        "-x", "--unsecure",
        help = "do not verify SSL certificate checking process (useful with self signed certificate)"
    ).default(false)

    val username by parser.storing(
        "-u", "--username",
        help = "Login - Nom de l'utilisateur"
    )

    val password by parser.storing(
        "-p", "--password",
        help = "Login - Mot de passe"
    )

//    val query by parser.storing(
//        "-q", "--query",
//        help = "requete sur le serveur (par défaut : search Host)"
//    ).default("search Host")

//    val kind by parser.storing(
//        "-k", "--kind",
//        help = "Type de noeud (kind) (par défaut : Host)"
//    ).default("Host")

    val kindtype by parser.mapping(
        "--host" to KindType.HOST,
        "--softwareinstance" to KindType.SOFTWAREINSTANCE,
        help = "Type de noeud (kind) (par défaut : --host)"
    ).default(KindType.HOST)
//
//    val logginglevel by parser.mapping(
//        "--info" to LoggingLevel.INFO,
//        "--debug" to LoggingLevel.DEBUG,
//        "--error" to LoggingLevel.ERROR,
//        help = "niveau de logging"
//    ).default(LoggingLevel.INFO)
//


    val name by parser.storing(
        "-n", "--name",
        help = "nom du noeud"
    )

    val clean by parser.flagging(
        "-c", "--clean",
        help = "efface les fichiers intermédiaires (.gv ; .png)"
    ).default(false)
}


    fun main(args: Array<String>) = mainBody {

        runBlocking {

 //           logger.debug {" Logger class name : ${logger.underlyingLogger.javaClass.name}"}
            val prologue = "Discovery Explorer : "
            val epilogue = "TSODev pour Orange Business"

            ArgParser(args, ArgParser.Mode.GNU, DefaultHelpFormatter(prologue, epilogue)).parseInto(::ParsedArgs).run {


                logger.info("===========================================================================")
                logger.info(" Discovery Data Explorer - TSO pour Orange Business - 07/23 - version 1.0.1 ")
                logger.info("===========================================================================")

//                var pdfFilePath = name
//
//                val pdfOutputFile = FileOutputStream("$pdfFilePath.pdf")
//                val pdfDoc = Document()
//
//                val pdfWriter = PdfWriter.getInstance(pdfDoc, pdfOutputFile)
//                pdfWriter.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO)

                val token = getToken(server, username, password, unsafe)
                if (token != null) {
                    TokenHolder.saveToken(token)

                    when (kindtype) {
                        KindType.HOST -> {
                            val host = Host(server = server, unsafe = unsafe, verbose = verbose)
//                            val pdfDocument = ExplorerDocument("${name}")
//                            pdfDocument.setMetaData("Discovery Extractor PDF result","provide discovered information on node", "Kotlin, OpenPDF, graphviz", "Orange Business", "Thierry Soulie")
                            val hosts = async {
                                apiKindsData(server, KindType.HOST.type, unsafe, verbose)
                            }
                            val hostList = hosts.await()
                            try {
                                val source = hostList.first { it.name.contains(name) }
                                host.setHost(source)
                            } catch (e: Exception) {
                                logger.error() {"Erreur HOST : $name non trouvé dans le liste des noeuds de type Host "}
                                logger.error() {"$e"}
                                exitProcess(-1)
                            }
                            host.logHost()

                            val graphvizSC = Graph(filename = name + "_Software", id = name, label = "Software Connected Graph from $name")

                            var graph = apiGetSoftwareConnectedGraph(server, host.getSource().id, unsafe, verbose)
                            if (graph != null) {
                                host.setGraph(graph,graphvizSC)
                            }

                            graphvizSC.producePNG()

                            val imageSC = Image.getInstance(name + "_Software.png")
                            imageSC.scaleToFit(900f, 680f)

                            val graphvizINFRA = Graph(filename = name + "_Infrastructure", id = name, label = "Infrastructure Graph from $name")

                            graph = apiGetInfrastructureGraph(server, host.getSource().id, unsafe, verbose)
                            if (graph != null) {
                                host.setGraph(graph,graphvizINFRA)
                            }
                            graphvizINFRA.producePNG()

                            val imageINFRA = Image.getInstance(name + "_Infrastructure.png")
                            imageINFRA.scaleToFit(900f, 680f)

                            createDocumentForHost(host, imageSC, imageINFRA)

                            if (clean) graphvizSC.clean()
                            if (clean) graphvizINFRA.clean()

                            logger.info("Et voilà , le fichier $name a été généré")
        //                    pdfWriter.close()

                        }
                        KindType.SOFTWAREINSTANCE -> {}
                        else -> {}
                    }

                }
            }
            }
        }

private fun createDocumentForHost(
    host: Host,
    imageSC: Image,
    imageINFRA: Image
) {
    val pdfDocument = ExplorerDocument("${host.getNodeInfo().state["name"]}")
    pdfDocument.setMetaData(
        "Discovery Extractor PDF result",
        "provide discovered information on node",
        "Kotlin, OpenPDF, graphviz",
        "Orange Business",
        "Thierry Soulie"
    )
    pdfDocument.setTitle(host.getNodeInfo().state["name"].toString())
    pdfDocument.setBook(KindType.HOST, host)
    pdfDocument.PageGraph(imageSC, "[ SOFTWARE CONTEXT ]")
    pdfDocument.PageGraph(imageINFRA, "[ INFRASTRUCTURE CONTEXT ]")
    pdfDocument.close()

}

private fun getToken(server: String,username: String, password: String, unsafe: Boolean) : String? {
        var token: String? = null
        try {
            if (username.isNotEmpty() && password.isNotEmpty())
                apiGetToken(server, username, password, unsafe)?.let {
                    TokenHolder.saveToken(it)
                    token = it
                }
        } catch (e: Exception) {
            logger.error(e){"Authentification : $e"}
        }
    return token
}

private fun apiGetTraversalNodes(
    server: String,
    source: Kind,
    traverse: String,
    unsafe: Boolean,
    verbose: Boolean
) : ArrayList<Map<String, Any>> {
    val traversed = arrayListOf<Map<String, Any>>()
    try {
        apiTraversedNodesData(
            server,
            source.id,
            traverse,
            unsafe,
            verbose
        ).let { node ->
            val relation = node?.relationships?.get(traverse)
            if (!relation.isNullOrEmpty()) {
                relation!!.forEach { rel ->
                    rel.node?.let {
                        try {
                            apiNodesData(server, it, unsafe, verbose).let { dest ->
                                traversed.add(dest?.state!!)
                            }
                        } catch (e: Exception) {
                            logger.error(e){"Erreur Traverse : $e)"}
                        }

                    }
                }
            }
     }
    } catch (e: Exception)
    {
        logger.error(e){"Erreur : $e"}
    }
    return traversed
}

private fun getValueFromTraverseResult(
    traverseResult: ArrayList<Map<String, Any>>,
    key: String
): String {
    var value = ""
    if (traverseResult.isNotEmpty()) value = traverseResult[0].get(key).toString()
    return value
}
