package com.team00;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import java.util.*;

public class ChaseLogic {

    public static void nextMoveCoordinates(char[][] fieldArray, Participant enemy, Participant player, Config config) {
        int oldX = enemy.x;
        int oldY = enemy.y;
        int[] start = {enemy.y, enemy.x};
        int[] target = {player.y, player.x};
        if (checkMove(fieldArray, enemy, config)) {
            List<int[]> path = findShortestPath(fieldArray, start, target, config);
            if (!path.isEmpty()) {
                enemy.x = path.get(1)[1];;
                enemy.y = path.get(1)[0];
                fieldArray[oldY][oldX] = config.emptyChar;
                fieldArray[enemy.y][enemy.x] = config.enemyChar;
            }
        }

    }

    public static List<int[]> findShortestPath(char[][] fieldArray, int[] start, int[] target, Config config) {
        int rows = fieldArray.length;
        int cols = fieldArray[0].length;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        Queue<int[]> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[rows][cols];
        int[][] prev = new int[rows][cols];

        queue.offer(start);
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            if (current[0] == target[0] && current[1] == target[1]) {
                List<int[]> path = new ArrayList<>();
                int[] curr = target;

                while (curr[0] != start[0] || curr[1] != start[1]) {
                    path.add(0, curr);
                    int parent = prev[curr[0]][curr[1]];
                    curr = new int[]{parent / cols, parent % cols};
                }
                path.add(0, start);
                return path;
            }

            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && fieldArray[newRow][newCol] != config.wallChar && fieldArray[newRow][newCol] != config.goalChar && fieldArray[newRow][newCol] != config.enemyChar && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                    prev[newRow][newCol] = current[0] * cols + current[1];
                }
            }
        }

        return new ArrayList<>();
    }

    public static boolean checkMove(char[][] fieldArray, Participant enemy, Config config) {
        boolean flag = true;
        int size = fieldArray.length - 1;
        int row = enemy.y;
        int col = enemy.x;
        int count = 0;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i == row - 1) && (j == col - 1) || (i == row + 1) && (j == col + 1) || (i == row - 1) && (j == col + 1) || (i == row + 1) && (j == col - 1)) continue;
                if (i < 0 || j < 0 || i > size || j > size) continue;
                if (fieldArray[(i + size) % size][(j + size) % size] == config.emptyChar) {
                    count += 1;
                }
            }
        }

        if (count == 0) flag = false;
        return flag;
    }

}
