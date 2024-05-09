package id.slava.nt.cabifymobilechallengeapp.data.local.files

import android.content.Context
import java.io.BufferedReader
import java.io.IOException

interface FileManager {
    fun saveToFile(filename: String, data: String)
    fun readFromFile(filename: String): String?

    fun readFromRawResource(resourceId: Int): String?
}

class AndroidFileManager(private val context: Context) : FileManager {
    override fun saveToFile(filename: String, data: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(data.toByteArray())
        }
    }

    override fun readFromFile(filename: String): String? {
        return try {
            context.openFileInput(filename).use { inputStream ->
                inputStream.bufferedReader().use(BufferedReader::readText)
            }
        } catch (e: IOException) {
            null
        }
    }

    override fun readFromRawResource(resourceId: Int): String? {
        return try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                inputStream.bufferedReader().use(BufferedReader::readText)
            }
        } catch (e: Exception) {
            null
        }
    }
}
