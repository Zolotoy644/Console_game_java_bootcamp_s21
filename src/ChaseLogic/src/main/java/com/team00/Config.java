package com.team00;

import com.diogonunes.jcdp.color.api.Ansi;

import java.io.*;

public class Config {
    public char enemyChar;
    public char playerChar;
    public char wallChar;
    public char goalChar;
    public char emptyChar;
    public Ansi.BColor enemyColor;
    public Ansi.BColor playerColor;
    public Ansi.BColor wallColor;
    public Ansi.BColor goalColor;
    public Ansi.BColor emptyColor;

    public Config(String fileName) {
        try {
            parseConfig(fileName);
        } catch (IOException e) {
            System.out.println("Config file not found");
            System.exit(-1);
        }
    }

    public void parseConfig(String fileName) throws IOException {
        String[] stringValues = new String[10];
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split("=", 2);
                stringValues[count] = splitLine[1].trim();
                count++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.enemyChar = stringValues[0].charAt(0);
        this.playerChar = stringValues[1].charAt(0);
        this.wallChar = stringValues[2].charAt(0);
        this.goalChar = stringValues[3].charAt(0);
        if (stringValues[4] == "") {
            this.emptyChar = ' ';
        } else {
            this.emptyChar = stringValues[4].charAt(0);
        }
        this.enemyColor = Ansi.BColor.valueOf(stringValues[5]);
        this.playerColor = Ansi.BColor.valueOf(stringValues[6]);
        this.wallColor = Ansi.BColor.valueOf(stringValues[7]);
        this.goalColor = Ansi.BColor.valueOf(stringValues[8]);
        this.emptyColor = Ansi.BColor.valueOf(stringValues[9]);
    }
}
