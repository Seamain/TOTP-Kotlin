internal object Base32 {
    private val base32Lookup = intArrayOf(
        0xFF, 0xFF, 0x1A, 0x1B, 0x1C,
        0x1D, 0x1E, 0x1F,  // '0', '1', '2', '3', '4', '5', '6', '7'
        0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,  // '8', '9', ':',
        // ';', '<', '=',
        // '>', '?'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,  // '@', 'A', 'B',
        // 'C', 'D', 'E',
        // 'F', 'G'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,  // 'H', 'I', 'J',
        // 'K', 'L', 'M',
        // 'N', 'O'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,  // 'P', 'Q', 'R',
        // 'S', 'T', 'U',
        // 'V', 'W'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,  // 'X', 'Y', 'Z',
        // '[', '', ']',
        // '^', '_'
        0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,  // '`', 'a', 'b',
        // 'c', 'd', 'e',
        // 'f', 'g'
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,  // 'h', 'i', 'j',
        // 'k', 'l', 'm',
        // 'n', 'o'
        0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,  // 'p', 'q', 'r',
        // 's', 't', 'u',
        // 'v', 'w'
        0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF // 'x', 'y', 'z',
        // '{', '|', '}',
        // '~', 'DEL'
    )

    /**
     * Decodes the given Base32 String to a raw byte array.
     *
     * @param base32
     * @return Decoded `base32` String as a raw byte array.
     */
    fun decode(base32: String): ByteArray {
        var index: Int
        var lookup: Int
        var digit: Int
        val bytes = ByteArray(base32.length * 5 / 8)
        var i = 0
        index = 0
        var offset = 0
        while (i < base32.length) {
            lookup = base32[i].code - '0'.code
            /* Skip chars outside the lookup table */if (lookup < 0 || lookup >= base32Lookup.size) {
                i++
                continue
            }
            digit = base32Lookup[lookup]
            /* If this digit is not in the table, ignore it */if (digit == 0xFF) {
                i++
                continue
            }
            if (index <= 3) {
                index = (index + 5) % 8
                if (index == 0) {
                    bytes[offset] = (bytes[offset].toInt() or digit).toByte()
                    offset++
                    if (offset >= bytes.size) break
                } else {
                    bytes[offset] = (bytes[offset].toInt() or (digit shl 8 - index)).toByte()
                }
            } else {
                index = (index + 5) % 8
                bytes[offset] = (bytes[offset].toInt() or (digit ushr index)).toByte()
                offset++
                if (offset >= bytes.size) {
                    break
                }
                bytes[offset] = (bytes[offset].toInt() or (digit shl 8 - index)).toByte()
            }
            i++
        }
        return bytes
    }
}