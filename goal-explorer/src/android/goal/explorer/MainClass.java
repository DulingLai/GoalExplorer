package android.goal.explorer;

import android.goal.explorer.cmd.CmdLineParser;
import android.goal.explorer.cmd.GlobalConfig;

public class MainClass {
    public static void main(String[] args) throws Exception {
        GlobalConfig config = CmdLineParser.parse(args);

        // analyze the app and construct STG
        STGExtractor extractor = new STGExtractor(config);

        // run the analysis
        extractor.constructSTG();
    }
}
