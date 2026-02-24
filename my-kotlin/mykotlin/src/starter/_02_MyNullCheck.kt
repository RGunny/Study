package starter

fun main() {

    // 1. Safe Call
    val str: String? = "A"
    val nullStr: String? = null
//    str.length // safe call
    str?.length

    // 2. Elvis 연산자
    // str == null ? -1 : str.length()
    println(str?.length ?: -1)
    println(nullStr?.length ?: -1)
}

fun startsWithA1(str: String?): Boolean{
    if (str == null) {
        throw IllegalArgumentException("str must not be null")
    }
    return str.startsWith("A")
}

fun startsWithA2(str: String?): Boolean?{
    if (str == null)
        return null
    return str.startsWith("A")
}

fun startsWithA3(str: String?): Boolean?{
    if (str == null)
        return false
    return str.startsWith("A")
}