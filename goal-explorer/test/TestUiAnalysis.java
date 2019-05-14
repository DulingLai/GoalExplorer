import org.junit.BeforeClass;
import st.cs.uni.saarland.de.testApps.TestApp;

import java.io.File;

public class TestUiAnalysis {
    @BeforeClass
    public static void setUp() throws Exception {
        String [] params = {"-apk",
                TestConstants.ZEDGE_DIR,
                "-androidJar",
                TestConstants.ANDROID_JAR,
                "-apkToolOutput",
                TestConstants.ZEDGE_DIR,
                "-rAnalysis",
                "-test",
                "-cgAlgo",
                "RTA",
                "-rLifecycle",
                "-processMenus"};

        File oldResults = new File("results"+File.separator+"app-debug.apk_forward_apiResults_1.xml");
        if(oldResults.exists()) {
            if (!oldResults.delete()) {
                throw new Exception("Can not delete file with results");
            }
        }

        File resultFile = new File("testApps" + File.separator + "app-debug" + File.separator + "outputForTestLayout.xml");
        if(resultFile.exists()) {
            if (!resultFile.delete()) {
                throw new Exception("Can not delete file with results");
            }
        }


        TestApp.main(params);


    }
}
