package processing

import api.ServiceApi.Companion.getValueFromTraverseResult
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import models.KindType
import models.Node
import utils.Dateformat
import utils.PDFformat
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//enum class Section (val type : KindType) {
//    HOSTIDENTITY(KindType.HOST),
//    ORGANISATION(KindType.HOST),
//    OPERATINGSYSTEM(KindType.HOST),
//    HARDWARE(KindType.HOST),
//    NETWORKINTERFACE(KindType.HOST),
//    RUNNINGSOFTWARE((KindType.HOST))
//}

class ExplorerDocument(
    val name: String,
    val doc: Document = Document(),
    val writer: PdfWriter = PdfWriter.getInstance(doc, FileOutputStream("$name.pdf"))
    ) {
    init {
        writer.setSpaceCharRatio(PdfWriter.NO_SPACE_CHAR_RATIO)
    }

    fun setHeader(content: String) {
        val header = HeaderFooter(true, Phrase(content))
        header.setAlignment(Element.ALIGN_LEFT)
        header.borderWidth = 0f
        doc.setHeader(header)
    }

    fun setFooter(content: String) {
        val footer = HeaderFooter(true, Phrase(content))
        footer.setAlignment(Element.ALIGN_LEFT)
        footer.borderWidth = 0f
        doc.setFooter(footer)
    }

    fun setMetaData(Title: String, Subject: String, Keywords: String, Creator: String, Author: String) {
        doc.apply {
            open()
            /* Here we add some metadata to the generated pdf */
            addTitle(Title)
            addSubject(Subject)
            addKeywords(Keywords)
            addCreator(Creator)
            addAuthor(Author)
            /* End of the adding metadata section */
        }
    }

    fun setTitle(NodeName: String) {
        val dateActuelle = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        doc.apply {
            add(PDFformat.MainTitle(NodeName))
            add(PDFformat.Note("Généré le $dateActuelle"))
        }
    }

    fun setBook(type: KindType, host: Host) {
        when (type) {
            KindType.HOST -> {
                ChapterHostIdentity(host.getNodeInfo())
                ChapterHostOrganisation(host)
                ChapterHostOperatingSystem(host)
                ChapterHardware(host)
                ChapterNetworkInterface(host)
                ChapterRunningSoftware(host)
            }
            else -> {}
        }

    }

    fun ChapterHostIdentity(node: Node) {
        doc.apply {
            add(PDFformat.Title("[ IDENTITE ]"))
            val identite = arrayListOf<Pair<String, String>>()
            identite.add(Pair("FQDN : ", node.state["local_fqdn"].toString()))
            identite.add(
                Pair(
                    "Dernière modification  : ",
                    Dateformat.DateFromString(node.state["last_update_success"].toString())
                )
            )
            add(PDFformat.multiplePairValue(identite))
            add(PDFformat.newLine(2f))
        }
    }

    private fun ChapterHostOrganisation(host: Host) {
        doc.apply {
            add(PDFformat.Title("[ ORGANISATION ]"))
            val organisation = arrayListOf<Pair<String, String>>()
            organisation.add(
                Pair(
                    "Business Owner : ",
                    host.getBusinessOwner()["name"].toString())

                )
            organisation.add(Pair("IT Owner : ", host.getITOwner()["name"].toString()))
            organisation.add(
                Pair(
                    "Support Owner :",
                    host.getSupportOwner()["name"].toString()
                )
            )
            organisation.add(
                Pair(
                    "Location :",
                    host.getLocation()["address"].toString()
                )
            )
            add(PDFformat.multiplePairValue(organisation))
            add(PDFformat.newLine(2f))
        }
    }

    private fun ChapterHostOperatingSystem(host: Host) {
        doc.apply {
            add(PDFformat.Title("[ OPERATING_SYSTEM ]"))
            val os = arrayListOf<Pair<String, String>>()
            val node = host.getNodeInfo()
            os.add(Pair("Operating System :", node.state["os"].toString()))
            os.add(Pair("Kernel :",  node.state["kernel"].toString()))
            val endOfSupport = host.getOsDetails()["end_support_date"]
            if (endOfSupport != null)
            //                         os.add(Pair("Package :", node.await()?.state?.get("packageCount").toString()))
            os.add(
                Pair(
                    "Non supporté depuis :",
                    Dateformat.DateFromString(endOfSupport.toString())
                )
            )
            else
                os.add(
                    Pair(
                        "Non supporté depuis :",
                        "non défini"
                    )
                )
            add(PDFformat.multiplePairValue(os))
            add(PDFformat.newLine(2f))

        }
    }

    fun ChapterHardware(host: Host) {
        doc.apply {
            add(PDFformat.Title("[ HARDWARE ]"))
            val hw = arrayListOf<Pair<String, String>>()
            val node = host.getNodeInfo()
            hw.add(Pair("Vendor :", node.state["vendor"].toString()))
            hw.add(Pair("Model :", node.state["model"].toString()))
            hw.add(Pair("Size :", host.getHwRefData()["u_size"].toString()))
            hw.add(Pair("Power :", host.getHwRefData()["power_watts"].toString()))
            hw.add(Pair("Serial :", node.state["serial"].toString()))
            hw.add(Pair("Memory :", node.state["ram"].toString()))
            hw.add(Pair("CPU :", node.state["processor_type"].toString()))
            add(PDFformat.multiplePairValue(hw))
            add(PDFformat.newLine(2f))
        }
    }

    fun ChapterNetworkInterface(host: Host) {
        doc.apply {
            val node = host.getNodeInfo()
            add(PDFformat.Title("[ NETWORK_INTERFACE ]",))
            add(Chunk(""))
            val ni = arrayListOf<ArrayList<Pair<String, String>>>()
            host.getNetworkInterfaces().forEach { nif ->
                val ni_info = arrayListOf<Pair<String, String>>()
                ni_info.add(Pair("Bound", nif.second["__interface_ids"].toString()))
                ni_info.add(Pair("IP Address", nif.second["ip_addr"].toString()))
                ni_info.add(Pair("FQDNS", nif.second["fqdns"].toString()))
                ni.add(ni_info)
            }
            add(PDFformat.createPdfPTable(ni))
            newPage()
        }
    }

    fun ChapterRunningSoftware(host: Host) {
        doc.apply {
            val node = host.getNodeInfo()
            val run = arrayListOf<ArrayList<Pair<String, String>>>()
            val listOfRunningSoftware = host.getRunningSoftware()
            if (listOfRunningSoftware.isNotEmpty()) {
                add(PDFformat.Title("[ RUNNING_SOFTWARE ]"))
                add(Chunk(""))
                listOfRunningSoftware.forEach { soft ->
                    //                          val softId = softwareInstances.await().filter { it.name == soft.get("name") }.first()
                    val softInfo = arrayListOf<Pair<String, String>>()
                    softInfo.add(Pair("Software", soft.second["short_name"].toString()))
                    softInfo.add(Pair("Version", soft.second["version"].toString()))
                    run.add(softInfo)
                }
                add(PDFformat.createPdfPTable(run))
            }
            newPage()
        }
    }

    fun PageGraph(image: Image, title: String) {
        doc.apply {
            setPageSize(PageSize.A3.rotate())
            newPage()
            add(PDFformat.Title(title))
            add(image)
            newPage()
            }

    }

    fun close() {
        doc.close()
    }

    }