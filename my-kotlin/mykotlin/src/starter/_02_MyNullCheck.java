package starter;

public class _02_MyNullCheck {

    public static void main(String[] args) {

        String str = "A";
        String nullStr = null;
        System.out.println(str == null ? -1 : str.length());
        System.out.println(nullStr == null ? -1 : str.length());
    }

    public boolean startsWithA1(String s) {
        if (s == null) {
            throw new IllegalArgumentException("s must not be null");
        }
        return s.startsWith("A"); // NPE possible
    }

    public Boolean startsWithA2(String s) {
        if (s == null) {
            return null;
        }
        return s.startsWith("A");
    }

    public boolean startsWithA3(String s) {
        if (s == null) {
            return false;
        }
        return s.startsWith("A");
    }


}
