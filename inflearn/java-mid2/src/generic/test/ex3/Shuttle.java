package generic.test.ex3;

import generic.test.ex3.unit.BioUnit;

/**
 * `Shuttle` 클래스의 조건은 다음과 같다.
 *   제네릭 타입을 사용해야 한다.
 *   `showInfo()` 메서드를 통해 탑승한 유닛의 정보를 출력한다
 */
public class Shuttle<T extends BioUnit> {

    private T unit;

    public void in(T t) {
        unit = t;

    }

    public T out() {
        return unit;
    }

    public void showInfo() {
        System.out.println("unit.getHp() = " + unit.getHp());
        System.out.println("unit.getName() = " + unit.getName());
    }
}
