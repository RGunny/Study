package starter;

public class _05_TypeCasting {

    public static void main(String[] args) {

        printModelIfCar(new Car("Tesla", "White"));
        printModelIfCar(new Object());


    }

    public static void printModelIfCar(Object obj) {
        if (obj instanceof Car) {
            Car car = (Car) obj;
            System.out.println(car.getModel());
        }
    }


}