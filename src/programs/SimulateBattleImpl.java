package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        int round = 0;

        // список юнитов
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits().stream().filter(Unit::isAlive).toList());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits().stream().filter(Unit::isAlive).toList());

        if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
            System.out.println("No battle: one of the armies is empty.");
            return;
        }

        HashSet<Unit> alreadyAttacked = new HashSet<>();

        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            round++;
            System.out.println();
            System.out.println("Round " + round + " is starting!");

            // работа с юнитами
            playerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            computerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            Queue<Unit> playerQueue = new LinkedList<>();
            Queue<Unit> computerQueue = new LinkedList<>();

            playerQueue.addAll(playerUnits);
            computerQueue.addAll(computerUnits);

            // атака
            while (!playerQueue.isEmpty() || !computerQueue.isEmpty()) {
                if (!playerQueue.isEmpty()) {
                    Unit attackingUnit = playerQueue.poll();
                    if (!alreadyAttacked.contains(attackingUnit)) {
                        Unit targetUnit = unitAttack(attackingUnit);
                        if (targetUnit != null && !targetUnit.isAlive()) {
                            alreadyAttacked.add(targetUnit);
                        }
                        alreadyAttacked.add(attackingUnit);
                    }
                }

                if (!computerQueue.isEmpty()) {
                    Unit attackingUnit = computerQueue.poll();
                    if (!alreadyAttacked.contains(attackingUnit)) {
                        Unit targetUnit = unitAttack(attackingUnit);
                        if (targetUnit != null && !targetUnit.isAlive()) {
                            alreadyAttacked.add(targetUnit);
                        }
                        alreadyAttacked.add(attackingUnit);
                    }
                }
            }

            // лог
            System.out.println("Round " + round + " is over!");
            int playerTotalHealth = playerUnits.stream().mapToInt(Unit::getHealth).sum();
            int playerTotalAttack = playerUnits.stream().mapToInt(Unit::getBaseAttack).sum();
            int computerTotalHealth = computerUnits.stream().mapToInt(Unit::getHealth).sum();
            int computerTotalAttack = computerUnits.stream().mapToInt(Unit::getBaseAttack).sum();

            System.out.println("Player army has " + playerUnits.size() + " units left, total health: " + playerTotalHealth + ", total attack: " + playerTotalAttack);
            System.out.println("Computer army has " + computerUnits.size() + " units left, total health: " + computerTotalHealth + ", total attack: " + computerTotalAttack);
            System.out.println("Player army has " + playerUnits.size() + " units left.");
            System.out.println("Computer army has " + computerUnits.size() + " units left.");

            // списки живых
            playerUnits = new ArrayList<>(playerArmy.getUnits().stream().filter(Unit::isAlive).toList());
            computerUnits = new ArrayList<>(computerArmy.getUnits().stream().filter(Unit::isAlive).toList());

            // вин луз
            if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
                break;
            }

            // обновление списка атакующих юнитов
            alreadyAttacked.clear();
        }

        // Конец битвы
        System.out.println("Battle is over!");
        if (playerUnits.isEmpty()) {
            System.out.println("Computer wins!");
        } else {
            System.out.println("Player wins!");
        }
    }

    private Unit unitAttack(Unit attackingUnit) throws InterruptedException {
        Unit targetUnit = attackingUnit.getProgram().attack();
        if (targetUnit != null) {
            // лог атаки
            printBattleLog.printBattleLog(attackingUnit, targetUnit);
        }
        return targetUnit;
    }
}
//итоговая сложность неизвестна, примерно O(m * n)

