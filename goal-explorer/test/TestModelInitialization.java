import android.goal.explorer.STGExtractor;
import android.goal.explorer.cmd.CmdLineParser;
import android.goal.explorer.cmd.GlobalConfig;
import android.goal.explorer.model.App;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pmw.tinylog.Logger;
import soot.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestModelInitialization {
    @Test
    public void runTest(){

        Collection<File> files = FileUtils.listFiles(new File(TestConstants.BENCHMARK_DIR), new String[]{"apk"}, true);

        List<File> fileList = new ArrayList<>(files);

        Collections.sort(fileList);

        for (File file : fileList) {
            Logger.info("Working on: {}", file.getName());
            String[] args = TestConfig. getConfigForTest(file.getAbsolutePath());
            try {
                GlobalConfig config = CmdLineParser.parse(args);

                STGExtractor extractor = new STGExtractor(config);
                extractor.initialize();
            } catch (Exception e) {
                Logger.error("Failed: {}", e.getMessage());
            }
            App instance = App.v();
            assert Scene.v().getCallGraph()!=null;
            assert Scene.v().getCallGraph().size()>0;
            assert instance!=null;
            assert instance.getActivities()!=null;
        }
    }
}
