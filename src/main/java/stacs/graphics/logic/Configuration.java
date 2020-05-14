package stacs.graphics.logic;

import org.apache.commons.cli.*;

import java.util.Arrays;

public class Configuration {

    public static final String DEPTH_TEST_PAINTERS = "painters";
    public static final String DEPTH_TEST_ZBUFFER = "zbuffer";
    private static final String DEPTH_TEST_OPTION = "depth-test-method";
    private static final String[] DEPTH_TEST_OPTIONS = new String[]{DEPTH_TEST_PAINTERS, DEPTH_TEST_ZBUFFER};
    private static final String HELP_OPTION = "help";

    private String depthTestMethod = DEPTH_TEST_ZBUFFER;

    public Configuration(String[] args) {
        var options = new Options();
        var help = new Option("h", HELP_OPTION, false, "See usage");
        options.addOption(help);
        var depthTestMethodOption = new Option("d", DEPTH_TEST_OPTION, true,
                "Depth test method. " +
                        "Default: " + depthTestMethod +
                        "Choose from: " + Arrays.toString(DEPTH_TEST_OPTIONS));
        options.addOption(depthTestMethodOption);

        var cliParser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = cliParser.parse(options, args);
            if (cmd.getOptionValue(HELP_OPTION, null) != null) {
                handleParseException(args[0], null, options);
            }
            depthTestMethod = cmd.getOptionValue(DEPTH_TEST_OPTION, depthTestMethod);
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
