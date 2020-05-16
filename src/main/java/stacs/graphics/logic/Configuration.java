package stacs.graphics.logic;

import org.apache.commons.cli.*;

import java.util.Arrays;

public class Configuration {

    public static final String DEPTH_TEST_PAINTERS = "painters";
    public static final String DEPTH_TEST_ZBUFFER = "zbuffer";
    private static final String DEPTH_TEST_OPTION = "depth-test-method";
    private static final String[] DEPTH_TEST_OPTIONS = new String[]{DEPTH_TEST_PAINTERS, DEPTH_TEST_ZBUFFER};

    public static final String SHADING_FLAT = "flat";
    public static final String SHADING_GOURAUD = "gouraud";
    private static final String SHADING_OPTION = "shading";
    private static final String[] SHADING_OPTIONS = new String[]{SHADING_FLAT, SHADING_GOURAUD};

    private static final String HELP_OPTION = "help";

    private String depthTestMethod = DEPTH_TEST_ZBUFFER;
    private String shadingMethod = SHADING_FLAT;

    public Configuration(String[] args) {
        var options = new Options();
        var help = new Option("h", HELP_OPTION, false, "See usage");
        options.addOption(help);
        var depthTestMethodOption = new Option("d", DEPTH_TEST_OPTION, true,
                "Depth test method. " +
                        "\nDefault: " + depthTestMethod +
                        "\nChoose from: " + Arrays.toString(DEPTH_TEST_OPTIONS) +
                        "\nNote: Pass -Xss100m as JVM argument if using painter as quicksort will stackoverflow otherwise");
        options.addOption(depthTestMethodOption);

        var shadingMethodOption = new Option("s", SHADING_OPTION, true,
                "Shading method. " +
                        "\nDefault: " + shadingMethod +
                        "\nChoose from: " + Arrays.toString(SHADING_OPTIONS));
        options.addOption(shadingMethodOption);

        var cliParser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = cliParser.parse(options, args);
            if (cmd.getOptionValue(HELP_OPTION, null) != null) {
                handleParseException(args[0], null, options);
            }
            depthTestMethod = cmd.getOptionValue(DEPTH_TEST_OPTION, depthTestMethod);
            shadingMethod = cmd.getOptionValue(SHADING_OPTION, shadingMethod);
        } catch (ParseException e) {
            handleParseException(args[0], e, options);
        }
    }

    private void handleParseException(String howCalled, ParseException e, Options options) {
        if (e != null) {
            System.err.println(e.getMessage());
        }

        var helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(howCalled, options);
        System.exit(1);
    }

    public String getDepthTestMethod() {
        return depthTestMethod;
    }
}
