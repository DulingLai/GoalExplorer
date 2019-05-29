import android.goal.explorer.cmdline.CmdLineParser;
import org.apache.commons.io.FileUtils;

import org.junit.Test;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.util.*;

public class TestCmdLinParser {
    @Test
    public void runTest(){

        Collection<File> files = FileUtils.listFiles(new File(TestConstants.ZEDGE_DIR), new String[]{"apk"}, true);

        List<File> fileList = new ArrayList<>(files);

        Collections.sort(fileList);

        for (File file : fileList) {
            Logger.info("Working on: {}", file.getName());
            String[] args = TestConfig. getConfigForTest(file.getAbsolutePath());
            try {
                CmdLineParser.parse(args);
            } catch (Exception e) {
                Logger.error("Failed: {}", e.getMessage());
            }
        }
    }
}
