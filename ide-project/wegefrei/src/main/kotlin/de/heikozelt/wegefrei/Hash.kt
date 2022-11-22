package de.heikozelt.wegefrei

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

class Hash {

    companion object {

        private const val BUFFER_SIZE = 1024

        /**
         * 20 bytes binary
         */
        fun sha1(path: String): ByteArray {
            val file = File(path)
            return sha1(file)
        }

        fun sha1(file: File): ByteArray {
            val digest = MessageDigest.getInstance("SHA-1")
            val fis = FileInputStream(file)
            val byteArray: ByteArray
            fis.use {
                updateDigest(digest, fis)
                byteArray = digest.digest()
            }
            return byteArray
        }

        private fun updateDigest(digest: MessageDigest, input: InputStream) {
            val buffer = ByteArray(BUFFER_SIZE)
            var read = input.read(buffer, 0, BUFFER_SIZE)
            while (read > -1) {
                digest.update(buffer, 0, read)
                read = input.read(buffer, 0, BUFFER_SIZE)
            }
        }

        /**
         * konvertiert 20 Bytes binär in einen 40 Zeichen langen hexadezimal codierten String
         */
        fun hex(bytes: ByteArray): String {
            return bytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
        }
    }
}