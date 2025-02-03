package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.List;
import java.util.ArrayList;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {
    private static int a = 1085704068;

    public SuitableForAttackUnitsFinderImpl() {
    }

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        ArrayList<Unit> suitableUnits = new ArrayList<>();

        // Проход по всем рядам
        for (int row = 0; row < unitsByRow.size(); row++) {
            List<Unit> rowUnits = unitsByRow.get(row);

            // юниты компьютера
            if (isLeftArmyTarget) {
                for (Unit unit : rowUnits) {
                    if (unit.isAlive() && !isUnitBlockedByOtherUnits(row, unit, unitsByRow, true)) {
                        suitableUnits.add(unit);
                    }
                }
            }
            // юниты игрока
            else {
                for (Unit unit : rowUnits) {
                    if (unit.isAlive() && !isUnitBlockedByOtherUnits(row, unit, unitsByRow, false)) {
                        suitableUnits.add(unit);
                    }
                }
            }
        }

        if (suitableUnits.isEmpty()) {
            System.out.println("Unit can not find target for attack!");
        }

        return suitableUnits;
    }

    // Метод для проверки, заблокирован ли юнит
    private boolean isUnitBlockedByOtherUnits(int row, Unit unit, List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        int unitX = unit.getxCoordinate(); // Теперь проверяем X-координату
        List<Unit> rowUnits = unitsByRow.get(row);

        for (Unit otherUnit : rowUnits) {
            if (isLeftArmyTarget) {
                if (otherUnit.getxCoordinate() < unitX && otherUnit.isAlive()) {
                    return true;
                }
            } else {
                if (otherUnit.getxCoordinate() > unitX && otherUnit.isAlive()) {
                    return true;
                }
            }
        }

        return false;
    }
}
//Итоговая сложность O(m * n)

