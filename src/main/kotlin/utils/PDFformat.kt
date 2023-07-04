package utils

import com.lowagie.text.*
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import java.awt.Color

class PDFformat() {

    companion object {
        fun Title(title: String): Paragraph {
            var chunk: Chunk = Chunk(
                title,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, Font.BOLD, Color.BLACK)
            ).apply {
//                setBackground(Color.LIGHT_GRAY)
            }

            val p= Paragraph(chunk).apply {
                    alignment = Element.ALIGN_LEFT
                    spacingAfter = 2f
                    spacingBefore = 2f
            }

            return p
        }

        fun MainTitle(title: String): Paragraph {
            var chunk: Chunk = Chunk(
                title,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28f, Font.BOLD, Color.BLACK)
            ).apply {
//                setBackground(Color.LIGHT_GRAY)
            }

            val p= Paragraph(chunk).apply {
                alignment = Element.ALIGN_RIGHT
                spacingAfter = 10f
                spacingBefore = 2f
            }

            return p
        }

        fun Note(note: String): Paragraph {
            var chunk: Chunk = Chunk(
                note,
                FontFactory.getFont(FontFactory.HELVETICA, 8f, Font.ITALIC, Color.BLACK)
            ).apply {
//                setBackground(Color.LIGHT_GRAY)
            }

            val p= Paragraph(chunk).apply {
                alignment = Element.ALIGN_RIGHT
                spacingAfter = 2f
                spacingBefore = 2f
            }

            return p
        }

        fun Info(note: String): Paragraph {
            var chunk: Chunk = Chunk(
                note,
                FontFactory.getFont(FontFactory.HELVETICA, 8f, Font.ITALIC, Color.BLACK)
            ).apply {
//                setBackground(Color.LIGHT_GRAY)
            }

            val p= Paragraph(chunk).apply {
                alignment = Element.ALIGN_LEFT
                spacingAfter = 2f
                spacingBefore = 2f
            }

            return p
        }

        fun SingleLeftAndSingleRight(left: String, right: String): Table {


            val table = Table(2).apply {
                padding = 2f
                spacing = 0f
                width = 100f
                borderWidth = 1f
            }

            var leftChunk: Chunk = Chunk(
                left,
                FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.BOLD, Color.GRAY)
            )
            var rightChunk: Chunk = Chunk(
                right,
                FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.NORMAL, Color.BLACK)
            )

            // Create two cells with text
            val leftcell = Cell(Phrase(leftChunk)).apply {
                //isHeader = true
                //              setBackgroundColor(Color.LIGHT_GRAY)
                border = Rectangle.NO_BORDER
                setHorizontalAlignment(HorizontalAlignment.RIGHT)
            }
            val rightcell = Cell(Phrase(rightChunk)).apply {
                border = Rectangle.NO_BORDER
            }

// Add the cells to the table
            table.addCell(leftcell)
            table.addCell(rightcell)

            return table
        }

        fun SingleLeftAndMultipleRight(left: String, rights: ArrayList<String>): Table {


            val table = Table(2).apply {
                padding = 2f
                spacing = 0f
                width = 100f
                borderWidth = 0f
                spacing = 1f
            }

            val leftChunk: Chunk = Chunk(
                left,
                FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.BOLD, Color.BLACK)
            )
            val leftcell = Cell(Phrase(leftChunk)).apply {
                //isHeader = true
                //              setBackgroundColor(Color.LIGHT_GRAY)
                border = Rectangle.NO_BORDER
                setHorizontalAlignment(HorizontalAlignment.RIGHT)
            }

            val rightChunks : ArrayList<Chunk> = arrayListOf()
            rights.forEach { right ->
                rightChunks.add(Chunk(
                    right,
                    FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.NORMAL, Color.BLACK)
                ))
                val rightcell = Cell(Phrase(right)).apply {
                    border = Rectangle.NO_BORDER
                }

                table.addCell(leftcell)
                table.addCell(rightcell)
            }

            return table
        }

        fun multiplePairValue(pairs: ArrayList<Pair<String, String>>): Table {

            val table = Table(2).apply {
                padding = 2f
                spacing = 0f
                width = 120f
                borderWidth = 0f
            }
            table.setWidths(intArrayOf(35 as Int, 65 as Int))
            pairs.forEach { pair ->
                val leftcell = Cell(Phrase(Chunk(pair.first,
                    FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.BOLDITALIC, Color.BLACK)))).apply {
                    //isHeader = true
      //              setBackgroundColor(Color.LIGHT_GRAY)
                    border = Rectangle.NO_BORDER
                    setHorizontalAlignment(HorizontalAlignment.RIGHT)
                }
                val rightcell = Cell(Phrase(Chunk(pair.second,
                    FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.NORMAL, Color.BLACK)))).apply {
                    border = Rectangle.NO_BORDER
                    setHorizontalAlignment(HorizontalAlignment.LEFT)
                }
                table.addCell(leftcell)
                table.addCell(rightcell)
            }
        return table
        }

        fun createTable(lines: ArrayList<ArrayList<Pair<String, String>>>): Table {

            val table = Table(lines[0].size).apply {
                padding = 2f
                spacing = 0f
                width = 100f
                borderWidth = 0f
            }

            // Header
            val headers = arrayListOf<Cell>()
            lines[0].forEach { pair ->
                table.addCell(Cell(Phrase(Chunk(pair.first))).apply {
                    isHeader = true
                    setHorizontalAlignment(HorizontalAlignment.CENTER)
                })
            }

            // Table
            val rows = arrayListOf<Cell>()
            lines.forEach { line ->
                line.forEach { pair ->
                    table.addCell(Cell(Phrase(Chunk(pair.second))).apply {
                        isHeader = false
                        setHorizontalAlignment(HorizontalAlignment.LEFT)
                    })
                }
            }
            return table
        }

        fun createPdfPTable(lines: ArrayList<ArrayList<Pair<String, String>>>): PdfPTable {

            val table = PdfPTable(lines[0].size)
            table.widthPercentage = 100f // set table width to 100% of page width

            // Header
            val headers = arrayListOf<Cell>()
            lines[0].forEach { pair ->
                val cell = PdfPCell(Phrase(Chunk(pair.first,
                    FontFactory.getFont(FontFactory.HELVETICA, 12f, Font.BOLD, Color.BLACK))))
                cell.setPadding((5f))
                table.addCell(cell)
            }

            // Table
            val rows = arrayListOf<Cell>()
            lines.forEach { line ->
                line.forEach { pair ->
                    val cell = PdfPCell(Phrase(Chunk(pair.second,
                        FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.NORMAL, Color.BLACK))))
                    cell.setPadding((5f))
                    table.addCell(cell)
                }
            }
            return table
        }

        fun newLine(space: Float): Paragraph {
            var chunk: Chunk = Chunk(
                "",
                FontFactory.getFont(FontFactory.HELVETICA, 8f, Font.NORMAL, Color.BLACK)
            )

            val p= Paragraph(chunk).apply {
                alignment = Element.ALIGN_RIGHT
                spacingAfter = space
                spacingBefore = space
            }

            return p
        }

    }
}