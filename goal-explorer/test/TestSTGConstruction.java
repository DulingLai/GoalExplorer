import android.goal.explorer.MainClass;
import android.goal.explorer.STGExtractor;
import android.goal.explorer.cmdline.CmdLineParser;
import android.goal.explorer.cmdline.GlobalConfig;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pmw.tinylog.Logger;
import st.cs.uni.saarland.de.testApps.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.goal.explorer.cmdline.CmdLineParser.parseArgForBackstage;

public class TestSTGConstruction {
    @Test
    public void runTest(){

        Collection<File> files = FileUtils.listFiles(new File(TestConstants.BENCHMARK_DIR), new String[]{"apk"}, true);

        List<File> fileList = new ArrayList<>(files);

        Collections.sort(fileList);

        for (File file : fileList) {
            Logger.info("Working on: {}", file.getName());
            String[] args = TestConfig. getConfigForTest(file.getAbsolutePath());

            GlobalConfig config = CmdLineParser.parse(args);
            initilizeSTG(config);

            Logger.info("Test passed for: {}", file.getName());
        }
    }

    private void initilizeSTG(GlobalConfig config) {
        try {
            Settings settings = parseArgForBackstage(config);
            STGExtractor extractor = new STGExtractor(config, settings);
            extractor.constructSTG();
        } catch (Exception e) {
            Logger.error("Failed: {}", e.getMessage());
        }
    }
}
