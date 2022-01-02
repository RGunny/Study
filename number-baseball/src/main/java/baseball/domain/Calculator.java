package baseball.domain;

public class Calculator {
    public static int shareResult= 0;   // 공유 변수
                                        // static(공유 자원) <--> dynamic (만드는 인스턴스마다 값을 복사)
                                        // => static : 클래스 변수
                                        // 그렇다면, share는 하고 싶은데, 누가 변경하는 건 원치 않는다면?
                                        // 파이썬의 불변 컬렉션, 가변 컬렉션 같은 내용 말하는 거임
                                        // 사실 불변이냐 가변이냐에 따라 내부 동작 최적화 하는 방법도 다르다(JVM)
    // public static int shareBirthDay = 0414; // 공유는 하지만 절대 변해선 안 되는 변수 => 내 생일이야..
    public static final int SHARE_BIRTHDAY = 0414;  // final을 이용해 불변 변수를 만든다.
                                            // 클래스 변수이지만, 값이 변하지 않으므로 => `상수` => 상수는 대문자 스네이크로 표기

    public int result = 0;  // Calculator 클래스가 관리하는 외부에서 접근할 수 있는 변수
                            // 즉 Calculator 클래수 내부에 존자하는 변수 => 인스턴스 변수
                            // 복사를 할 뿐, 공유를 하진 않음 (instance)
                            // static X : 인스턴스 변수

    public Calculator() {
//        shareBirthDay = 1234; // Cannlt assign a value to final variable
    }

    /**
     * 무언가를 더하는 메서드
     * => 좌항과 우항이 필요
     * => int number1, int number2 를 파라미터로 받는다.
     * => 자바는 타입을 동적타입이 아닌, 명시적으로 타입을 지정해줘야 한다.
     *
     * 숫자 2개 더하니까 int로 반환하겠다.
     * add()는 숫자 2개를 받아서 그 합의 숫자를 반환하는 메서드이다.
     *
     * 함수이름(파라미터) : 메서드의 시그니처
     * => 메서드의 identify 정도로 이해
     */
    public int add(int number1, int number2) { // 인스턴스 메서드
        result = number1 + number2;
        shareResult = result;
        return result;
    }
}
