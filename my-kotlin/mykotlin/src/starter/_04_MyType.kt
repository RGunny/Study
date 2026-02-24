package starter

fun myType() {

    val number1 = 10
//    val number2: Long = number1 // Compile Error Occurred
    // Initializer type mismatch: expected 'Long', actual 'Int'.
    val number2: Long = number1.toLong()

    println(number1 + number2)


    val number3: Int? = 10
    val number4: Long? = number3?.toLong() // Safe call
    val number5: Long = number3?.toLong() ?: 0L // Safe Call + Elvis
}