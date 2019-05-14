import android.goal.explorer.cmd.CmdLineParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestModelInitialization {
    @Test
    public void runTest(){

        Collection<File> files = FileUtils.listFiles(new File(TestConstants.ZEDGE_DIR), new String[]{"apk"}, true);

        List<File> fileList = new ArrayList<>(files);

        Collections.sort(fileList);

        for (File file : fileList) {
            Logger.info("Working on: {}", file.getName());
            String[] args = TestConfig. getConfigForTest(file.getAbsolutePath());
            try {
                CmdLineParser.main(args);
            } catch (Exception e) {
                Logger.error("Failed: {}", e.getMessage());
            }
        }
    }
}
