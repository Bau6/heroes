package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army army = new Army();
        int usedPoints = 0;

        // Сортировка юнитов
        //Сложность сортировки: O(n log n).
        unitList.sort((unit1, unit2) -> {
            double efficiency1 = (unit1.getBaseAttack() + unit1.getHealth()) / (double) unit1.getCost();
            double efficiency2 = (unit2.getBaseAttack() + unit2.getHealth()) / (double) unit2.getCost();
            return Double.compare(efficiency2, efficiency1);  // Сортируем по убыванию эффективности
        });

        Map<String, Integer> unitCounts = new HashMap<>();
        Random random = new Random();
        int[][] battlefield = new int[21][27];

        // рандомизация
        Collections.shuffle(unitList, random);

        // Добавление юнитов в армию
        // сложность O(n)
        for (Unit unit : unitList) {
            if (usedPoints >= maxPoints) break;

            int unitCost = unit.getCost();
            String unitType = unit.getUnitType();

            // Ограничение на максимальное количество юнитов каждого типа
            int unitLimit = 11;
            int countToAdd = Math.min(unitLimit, (maxPoints - usedPoints) / unitCost);

            for (int i = 0; i < countToAdd; i++) {
                if (usedPoints + unitCost > maxPoints) break;

                // Генерация случайных координат для юнита
                int[] coords = findAvailableCoordinates(battlefield, unit, random);
                if (coords != null) {
                    int x = coords[0];
                    int y = coords[1];

                    // Новый юнит
                    Unit newUnit = new Unit(
                            unit.getUnitType() + " " + (unitCounts.getOrDefault(unitType, 0) + 1),
                            unit.getUnitType(),
                            unit.getHealth(),
                            unit.getBaseAttack(),
                            unitCost,
                            unit.getAttackType(),
                            unit.getAttackBonuses(),
                            unit.getDefenceBonuses(),
                            x, y
                    );

                    // Добавление юнита в армию
                    army.getUnits().add(newUnit);
                    unitCounts.put(unitType, unitCounts.getOrDefault(unitType, 0) + 1); // Увеличиваем счетчик юнитов этого типа
                    usedPoints += unitCost;
                }
            }
        }

        System.out.println("Использовано очков: " + usedPoints);
        return army;
    }

    // Функция для поиска случайных доступных координат
    private int[] findAvailableCoordinates(int[][] battlefield, Unit unit, Random random) {
        List<int[]> availableCoordinates = new ArrayList<>();

        int startCol = 0;
        int endCol = 0;

        if (unit.getUnitType().equals("Archer")) {
            startCol = 0;
            endCol = 0;
        } else if (isHeavyUnit(unit)) {
            startCol = 2;
            endCol = 2;
        } else {
            startCol = 1;
            endCol = 1;
        }

        for (int x = startCol; x <= endCol; x++) {
            for (int y = 0; y < battlefield.length; y++) {
                if (battlefield[x][y] == 0) {
                    availableCoordinates.add(new int[]{x, y});
                }
            }
        }

        // Выбор случайной доступной клетки ( работает фигово )
        if (!availableCoordinates.isEmpty()) {
            int[] selectedCoord = availableCoordinates.get(random.nextInt(availableCoordinates.size()));
            battlefield[selectedCoord[0]][selectedCoord[1]] = 1;
            return selectedCoord;
        }

        return null;
    }

    // проверка на юнит (впереди - сзади местоположение)
    private boolean isHeavyUnit(Unit unit) {
        double totalDefenceBonus = unit.getDefenceBonuses().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        return (unit.getHealth() + totalDefenceBonus) > 20;
    }
}
//Итоговая сложность алгоритма O(n log n)
