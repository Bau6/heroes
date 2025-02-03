package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // 4 основные направления

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // создание координат всех занятых клеток
        Set<String> obstacles = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit != attackUnit && unit != targetUnit && unit.isAlive()) {
                obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Очередь с приоритетом для поиска пути (с использованием алгоритма Дейкстры)
        PriorityQueue<EdgeDistance> openList = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        boolean[][] visited = new boolean[WIDTH][HEIGHT]; // посещенные клетки
        int[][] distance = new int[WIDTH][HEIGHT]; // хранение расстояния от старта
        Edge[][] previous = new Edge[WIDTH][HEIGHT]; // для восстановления пути

        // Инициализация
        for (int i = 0; i < WIDTH; i++) {
            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        distance[startX][startY] = 0;
        openList.add(new EdgeDistance(startX, startY, 0));

        while (!openList.isEmpty()) {
            EdgeDistance current = openList.poll();
            int currentX = current.getX();
            int currentY = current.getY();

            if (currentX == targetUnit.getxCoordinate() && currentY == targetUnit.getyCoordinate()) {
                break;
            }

            if (visited[currentX][currentY]) {
                continue;
            }
            visited[currentX][currentY] = true;

            for (int[] direction : DIRECTIONS) {
                int newX = currentX + direction[0];
                int newY = currentY + direction[1];

                if (isValid(newX, newY, obstacles)) {
                    int newDistance = distance[currentX][currentY] + 1;

                    if (newDistance < distance[newX][newY]) {
                        distance[newX][newY] = newDistance;
                        previous[newX][newY] = new Edge(currentX, currentY);
                        openList.add(new EdgeDistance(newX, newY, newDistance));
                    }
                }
            }
        }

        // Восстановление пути
        List<Edge> path = new ArrayList<>();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        // Если не удалось найти путь
        if (previous[targetX][targetY] == null) {
            return path;
        }

        // Восстанавливаем путь от цели к началу
        while (targetX != startX || targetY != startY) {
            path.add(new Edge(targetX, targetY));
            Edge prev = previous[targetX][targetY];
            targetX = prev.getX();
            targetY = prev.getY();
        }

        path.add(new Edge(startX, startY));
        Collections.reverse(path);
        return path;
    }

    // Проверка валидности клетки
    private boolean isValid(int x, int y, Set<String> obstacles) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !obstacles.contains(x + "," + y);
    }
}
// запутался... есть сложность O(w * h)
