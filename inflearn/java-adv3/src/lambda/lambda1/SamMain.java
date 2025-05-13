package lambda.lambda1;

public class SamMain {

    public static void main(String[] args) {
        SamInterface samInterface = () -> {
            System.out.println("sam");
        };
        samInterface.run();

        // Compile error : Multiple non-overriding abstract methods found in interface lambda. lambda1.NoSamInterface
        /*
        NoSamInterface noSamInterface = () -> {
            System.out.println("no sam");
        };
        noSamInterface.go();
        noSamInterface.run();
        */
    }
}
