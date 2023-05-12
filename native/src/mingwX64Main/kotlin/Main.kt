import kotlinx.cinterop.*
import platform.posix.IID
import platform.windows.*
import kotlin.native.concurrent.freeze

fun main(args: Array<String>) {
    memScoped {
        val result = alloc<tagOFNA>()
        SecureZeroMemory?.invoke(result.ptr, sizeOf<tagOFNA>().toULong())

        val szFile = ByteArray(MAX_PATH)
        szFile[0] = 0
        val pinnedSzFile = szFile.pin()
        val filter = "All$NUL*.*${NUL}Text$NUL*.TXT$NUL".encodeToByteArray().pin()

        result.apply {
            lStructSize = sizeOf<tagOFNA>().toUInt()
            hwndOwner = null
            lpstrFile = pinnedSzFile.addressOf(0)
            nMaxFile = szFile.size.toUInt()
            lpstrFilter = filter.addressOf(0)
            nFilterIndex = 1u;
            lpstrFileTitle = null
            nMaxFileTitle = 0u
            lpstrInitialDir = null
            Flags = (OFN_PATHMUSTEXIST or OFN_FILEMUSTEXIST).toUInt()
        }

        val isFilePicked = GetOpenFileNameA(result.ptr)
        if (isFilePicked == 1) {
            println(szFile.decodeToString())
        } else {
            println("something went wrong")
        }
    }
}

const val NUL = '\u0000'
const val MAX_PATH = 260
