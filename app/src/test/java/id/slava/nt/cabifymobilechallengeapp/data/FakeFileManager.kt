package id.slava.nt.cabifymobilechallengeapp.data

import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager

class FakeFileManager : FileManager {
    var isFileSaved = false
    private var fileContent: String? = null
    private var rawContent: String? = null

    override fun readFromFile(filename: String): String? {
        return fileContent
    }

    override fun saveToFile(filename: String, data: String) {
        isFileSaved = true
    }

    override fun readFromRawResource(resourceId: Int): String? {
        return rawContent
    }

    fun setLocalFileContent(content: String?) {
        fileContent = content
    }

    fun setRawResourceContent(content: String?) {
        rawContent = content
    }
}

