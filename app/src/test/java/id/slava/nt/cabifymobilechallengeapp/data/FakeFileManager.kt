package id.slava.nt.cabifymobilechallengeapp.data

import id.slava.nt.cabifymobilechallengeapp.data.local.files.FileManager

class FakeFileManager : FileManager {
    private val files = mutableMapOf<String, String>()
    private val resources = mutableMapOf<Int, String>()

    override fun saveToFile(filename: String, data: String) {
        files[filename] = data
    }

    override fun readFromFile(filename: String): String? {
        return files[filename]
    }

    override fun readFromRawResource(resourceId: Int): String? {
        return resources[resourceId]
    }

    // Helper method to simulate adding resources in tests
    fun addResource(resourceId: Int, data: String) {
        resources[resourceId] = data
    }
}
