package starter


fun main() {
    elvisOperator(null)
    elvisOperator("AAAA")

    println("--------------")
    elvisOperator2(null)
    elvisOperator2(10)
}

fun elvisOperator(str: String?) {
    println("===================")
    println(str?.length ?: -1)
}

fun elvisOperator2(i: Int?) {
    i ?: 0 + 100
    (i ?: 0) + 100
}

fun notNullOperator(str: String?): Int {
    return str!!.length
}
