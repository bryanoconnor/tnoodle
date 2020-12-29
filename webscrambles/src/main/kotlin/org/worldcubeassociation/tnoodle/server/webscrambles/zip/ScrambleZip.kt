package org.worldcubeassociation.tnoodle.server.webscrambles.zip

import org.worldcubeassociation.tnoodle.server.webscrambles.pdf.util.StringUtil.toFileSafeString
import org.worldcubeassociation.tnoodle.server.webscrambles.wcif.ScrambleDrawingData
import org.worldcubeassociation.tnoodle.server.webscrambles.wcif.model.Competition
import org.worldcubeassociation.tnoodle.server.webscrambles.zip.folder.InterchangeFolder
import org.worldcubeassociation.tnoodle.server.webscrambles.zip.folder.PrintingFolder
import org.worldcubeassociation.tnoodle.server.webscrambles.zip.model.File
import org.worldcubeassociation.tnoodle.server.webscrambles.zip.model.ZipArchive
import java.nio.ByteBuffer
import java.time.LocalDateTime

data class ScrambleZip(val namedSheets: Map<String, ScrambleDrawingData>, val wcif: Competition) {
    val globalTitle = wcif.shortName

    fun assemble(generationDate: LocalDateTime, versionTag: String, password: String?, generationUrl: String?): ByteArray {
        val computerDisplayZip = ComputerDisplayZip(namedSheets, globalTitle)
        val computerDisplayZipBytes = computerDisplayZip.assemble(generationDate.toLocalDate(), versionTag)

        val interchangeFolder = InterchangeFolder(wcif, namedSheets, globalTitle)
        val interchangeFolderNode = interchangeFolder.assemble(generationDate, versionTag, generationUrl)

        val printingFolder = PrintingFolder(namedSheets, globalTitle, wcif.schedule)
        val printingFolderNode = printingFolder.assemble(generationDate.toLocalDate(), versionTag, password)

        val passcodeList = computerDisplayZip.passcodes.entries
            .joinToString("\r\n") { "${it.key}: ${it.value}" }

        val passcodeListingTxt = this::class.java.getResourceAsStream(TXT_PASSCODE_TEMPLATE)
            .bufferedReader().readText()
            .replace("%%GLOBAL_TITLE%%", globalTitle)
            .replace("%%PASSCODES%%", passcodeList)

        val filesafeGlobalTitle = globalTitle.toFileSafeString()

        val sizeOfPdf = ((printingFolderNode.children[1] as File).content).size

        val bytes: ByteArray = ByteBuffer.allocate(4).putInt(sizeOfPdf).array()

        //val sizeBytes = byteArrayOf(sizeOfPdf.toByte(), (sizeOfPdf ushr 8).toByte() , (sizeOfPdf ushr 16).toByte() , (sizeOfPdf ushr 24).toByte() )
        return bytes + (printingFolderNode.children[1] as File).content + (interchangeFolderNode.children[1] as File).content

        /* return zipArchive {
             folder(printingFolderNode)
             folder(interchangeFolderNode)

             file("$filesafeGlobalTitle - Computer Display PDFs.zip", computerDisplayZipBytes.compress())
             file("$filesafeGlobalTitle - Computer Display PDF Passcodes - SECRET.txt", passcodeListingTxt)
         }*/
    }

    companion object {
        private val TXT_PASSCODE_TEMPLATE = "/text/passcodeTemplate.txt"
    }
}
