package com.team00;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = "=")
public class ArgCommander {
    @Parameter(names = "--enemiesCount", required = true)
    public int enemiesCount;

    @Parameter(names = "--wallsCount", required = true)
    public int wallsCount;

    @Parameter(names = "--size", required = true)
    public int size;

    @Parameter(names = "--profile", required = true)
    public String profile;

}
