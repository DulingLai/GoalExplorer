import android.goal.explorer.cmd.GlobalConfig;

public class TestConfig {
    /**
     * Gets the command line arguments for testing
     * @return the commend line arguments
     */
    static String[] getConfigForTest() {
        String apkPath = TestConstants.ZEDGE_DIR;
        String[] args = new String[4];
        args[0] = "ate";
        args[1] = "-d";
        args[2] = "-i";
        args[3] = apkPath;
        return args;
    }

    /**
     * Gets the command line arguments for testing
     * @param apkPath The path to the apk file
     * @return the commend line arguments
     */
    static String[] getConfigForTest(String apkPath) {
        String[] args = new String[6];
        args[0] = "ate";
        args[1] = "-d";
        args[2] = "-i";
        args[3] = apkPath;
        args[4] = "-p";
        args[5] = TestConstants.CONFIG_PATH;
        return args;
    }
}
