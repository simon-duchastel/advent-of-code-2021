package commandline.runner

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

/**
 * Encapsulates code for reading from files.
 */
object FileReader {
    /**
     * Take as an input a file path, and read the contents of the file in its entirety into a Sequence.
     * Each line in the file is a separate element of the Sequence.
     * Since a Sequence is evaluated lazily, this allows for efficient management of large files.
     */
    fun readFromFile(pathToFile: String): Sequence<String>? {
        val file = fopen(pathToFile, READ_ONLY_MODE)
        if (file == null) {
            println("Unable to open file. Please ensure you provided a valid file path and the file is not in use.")
            return null
        }

        return sequence {
            try {
                memScoped {
                    val buffer = allocArray<ByteVar>(READ_BUFFER_LENGTH)
                    var line = fgets(buffer, READ_BUFFER_LENGTH, file)?.toKString()
                    while (line != null) {
                        yield(line!!) // even though the ide says otherwise, we need the !! since this is a var that could have changed to be null
                        line = fgets(buffer, READ_BUFFER_LENGTH, file)?.toKString()
                    }
                }
            } finally {
                fclose(file)
            }
        }
    }

    private const val READ_ONLY_MODE = "r"
    private const val READ_BUFFER_LENGTH = 1024 * 32
}