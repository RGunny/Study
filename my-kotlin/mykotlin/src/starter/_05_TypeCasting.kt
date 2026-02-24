package starter

fun main() {

    printModelIfCar()
}

fun printModelIfCar(obj: Any) {
    if (obj is Car) {
        val car = obj as Car
        println(car.model)
    }
}

// smart cast
// 코틀린 컴파일러가 컨텍스트를 분석해서 타입체크되어 이 타입으로 간주되는 조건을 인지함
fun printModelIfCar2(obj: Any) {
    if (obj is Car) {
        println(obj.model)
    }
}