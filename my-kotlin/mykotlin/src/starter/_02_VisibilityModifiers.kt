package starter

class _02_VisibilityModifiers {

    protected fun protectedFun() {
//        public val a: Int = 10
    }
    fun defaultFun() {
        protectedFun()

        //        private fun localFun() {}
//        public class LocalClass {}
    }

    protected class NestedClass {

    }
}
class MyClass {
    fun myFun(b: _02_VisibilityModifiers) {
//        b.protectedFun()
        b.defaultFun()

    }
}