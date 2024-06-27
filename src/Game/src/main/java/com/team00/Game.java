package com.team00;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

import java.util.*;

public class Game {

    private char[][] fieldArray;

    public char[][] getFieldArray() {
        return fieldArray;
    }

    private Participant player;
    private Participant target;

    private Participant[] enemies;
    private Participant[] obstacles;

    public Config config;

    public Game(int size, String configPath) {
        fieldArray = new char[size][size];
        config = new Config(configPath);
    }

    public static void main(String[] args) throws IllegalParametersException {
        ArgCommander commander = parseArgs(args);
        String fileName = null;
        if (commander.profile.equals("production")) {
            fileName = "application-production.properties";
        } else if (commander.profile.equals("development")) {
            fileName = "application-development.properties";
        }
        Game printer = new Game(commander.size, fileName);
        Scanner in = new Scanner(System.in);
        initField(printer, commander);
        makeParticipantMove(commander, in, printer);

    }

    public static void initField(Game printer, ArgCommander commander) {
        printer.fillFieldArray(commander.size, commander.enemiesCount, commander.wallsCount);
        printer.printField();
        if (!printer.playerPathCheck()) {
            printError("No possible path!");
        }
    }

    public static ArgCommander parseArgs(String[] args) throws IllegalParametersException {
        ArgCommander commander = new ArgCommander();
        try {
            JCommander.newBuilder().addObject(commander).build().parse(args);
        } catch (ParameterException e) {
            printError("Wrong arguments!");
        }
        if (Math.pow(commander.size, 2) < commander.enemiesCount + commander.wallsCount + 2) {
            throw new IllegalParametersException("Too many parameters!");
        }
        return commander;
    }

    public static void makeParticipantMove(ArgCommander commander, Scanner in, Game printer) {
        while(true) {
            int direction = 0;
            while (direction != 9 && direction != 1 && direction != 2 && direction != 3 && direction != 4) {
                System.out.println("Make your move (1 - left, 2 - upward, 3 - right, 4 - downward, 9 - end game):");
                direction = in.nextInt();
            }
            if (direction == 9) {
                printEndGame("You lose!");
            }
            printer.playerMove(direction);
            printer.printField();
            System.out.println();
            if (printer.getPlayer().x == printer.getTarget().x && printer.getPlayer().y == printer.getTarget().y) {
                printEndGame("You win!");
            }

            if (!enemiesCheck(printer.getPlayer().y, printer.getPlayer().x, printer.getFieldArray(), printer.config)) {
                printEndGame("You lose!");
            }

            for (Participant enemy : printer.getEnemies()) {
                if (commander.profile.equals("production")) {
                    System.out.print("\033[H\033[2J");
                    enemyMovement(commander, printer, enemy);
                } else if (commander.profile.equals("development")) {
                    int enemyMoveNum = 0;
                    while (enemyMoveNum != 8) {
                        System.out.println("To make enemy move, enter 8:");
                        enemyMoveNum = in.nextInt();
                    }
                    enemyMovement(commander, printer, enemy);
                }
            }

        }
    }

    public static void enemyMovement(ArgCommander commander, Game printer, Participant enemy) {

        ChaseLogic.nextMoveCoordinates(printer.getFieldArray(), enemy, printer.getPlayer(), printer.config);
        printer.printField();
        System.out.println();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



    public void fillFieldArray(int size, int enemiesCount, int wallsCount) {
        Random random = new Random(System.currentTimeMillis());
        enemies = new Participant[enemiesCount];
        obstacles = new Participant[wallsCount];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                fieldArray[i][j] = ' ';
            }
        }
        player = new Participant(random.nextInt(size), random.nextInt(size));
        fieldArray[player.y][player.x] = this.config.playerChar;
        target = new Participant(random.nextInt(size), random.nextInt(size));
        fieldArray[target.y][target.x] = this.config.goalChar;
        fillParticipantArray(enemies, fieldArray, size, enemiesCount, this.config.enemyChar, config);
        fillParticipantArray(obstacles, fieldArray, size, wallsCount, this.config.wallChar, config);

    }

    public static void fillParticipantArray(Participant[] participants, char[][] fieldArray, int size, int participantsCount, char character, Config config) {
        Random random = new Random(System.currentTimeMillis());
        int count = 0;
        while (count < participantsCount) {
            int col = random.nextInt(size);
            int row = random.nextInt(size);
            if (neighboursCheck(row, col, fieldArray, config)) {
                fieldArray[row][col] = character;
                participants[count] = new Participant(col, row);
                count += 1;
            }
        }
    }

    public void printField() {
        ColoredPrinter printer = new ColoredPrinter();
        if (fieldArray != null) {
            for (char[] line : fieldArray) {
                for (char val : line) {
                    if (val == this.config.playerChar) {
                        printer.print(val, Ansi.Attribute.NONE,
                                Ansi.FColor.BLACK, this.config.playerColor);
                    } else if (val == this.config.enemyChar) {
                        printer.print(val, Ansi.Attribute.NONE,
                                Ansi.FColor.BLACK, this.config.enemyColor);
                    } else if (val == this.config.wallChar) {
                        printer.print(val, Ansi.Attribute.NONE,
                                Ansi.FColor.BLACK, this.config.wallColor);
                    } else if (val == this.config.goalChar) {
                        printer.print(val, Ansi.Attribute.NONE,
                                Ansi.FColor.BLACK, this.config.goalColor);
                    } else {
                        printer.print(val, Ansi.Attribute.NONE,
                                Ansi.FColor.BLACK, this.config.emptyColor);
                    }
                }
                System.out.println();
            }
        } else {
            System.out.println("File not found");
        }
    }

    public static boolean neighboursCheck(int row, int col, char[][] fieldArray, Config config) {
        boolean flag = true;
        int size = fieldArray.length - 1;

        if (fieldArray[row][col] == ' ') {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (fieldArray[(i + size) % size][(j + size) % size] == config.playerChar) {
                        flag = false;
                        break;
                    }
                }
            }
        } else {
            flag = false;
        }

        return flag;
    }

    public void playerMove(int direction) {
        int oldX = player.x;
        int oldY = player.y;
        if (direction == 1) {
            player.x = player.x - 1;
        } else if (direction == 2) {
            player.y = player.y - 1;
        } else if (direction == 3) {
            player.x = player.x + 1;
        } else if (direction == 4) {
            player.y = player.y + 1;
        }

        if (player.x < 0 || player.y < 0 || (player.x > fieldArray.length - 1) || (player.y > fieldArray.length - 1) || fieldArray[player.y][player.x] == '#') {
            player.x = oldX;
            player.y = oldY;
            System.out.println("Wrong way! Make another move!");
        }
        fieldArray[oldY][oldX] = config.emptyChar;
        fieldArray[player.y][player.x] = config.playerChar;

    }

    public Participant getPlayer() {
        return this.player;
    }

    public Participant getTarget() {
        return this.target;
    }

    public Participant[] getEnemies() {
        return enemies;
    }

    public Participant[] getObstacles() {
        return obstacles;
    }

    public static boolean enemiesCheck(int row, int col, char[][] fieldArray, Config config) {
        boolean flag = true;
        int size = fieldArray.length - 1;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i == row - 1) && (j == col - 1) || (i == row + 1) && (j == col + 1) || (i == row - 1) && (j == col + 1) || (i == row + 1) && (j == col - 1)) continue;
                if (fieldArray[(i + size) % size][(j + size) % size] == config.enemyChar) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    }

    public static void printError(String message) {
        System.out.println(message);
        System.exit(-1);
    }

    public static void printEndGame(String message) {
        System.out.println(message);
        System.exit(0);
    }

    public boolean playerPathCheck() {
        int rows = fieldArray.length;
        int cols = fieldArray[0].length;
        int[] start = {this.player.y, this.player.x};
        int[] target = {this.target.y, this.target.x};
        char[][] fieldArray = this.fieldArray;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        Queue<int[]> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[rows][cols];

        queue.offer(start);
        visited[start[0]][start[1]] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            if (current[0] == target[0] && current[1] == target[1]) {
                return true;
            }

            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && fieldArray[newRow][newCol] != this.config.wallChar && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol});
                    visited[newRow][newCol] = true;
                }
            }
        }

        return false;
    }
}
