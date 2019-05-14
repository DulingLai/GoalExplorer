package android.goal.explorer;

import android.goal.explorer.cmd.GlobalConfig;
import android.goal.explorer.model.App;
import org.pmw.tinylog.Logger;
import soot.jimple.infoflow.android.SetupApplication;

public class STGExtractor {
    // Logging tags
    private static final String RESOURCE_PARSER = "ResourceParser";
    private static final String FRAGMENT_ANALYZER = "FragmentAnalyzer";
    private static final String SCREEN_ANALYZER = "ScreenAnalyzer";
    private static final String CALLBACK_ANALYZER = "CallbackAnalyzer";
    private static final String CLASS_ANALYZER = "JimpleAnalyzer";
    private static final String ICC_PARSER = "IccParser";
    private static final String GRAPH_BUILDER = "GraphBuilder";

    // Configuration
    private GlobalConfig config;

    // Setup application
    private SetupApplication app;

    /**
     * Default constructor
     * @param config The configuration file
     */
    public STGExtractor(GlobalConfig config) {
        // Setup analysis config
        this.config = config;

        // Setup the app using FlowDroid
        app = new SetupApplication(
                config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir(),
                config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile());
    }


    /**
     * Perform the whole analysis
     */
    public void constructSTG() {

        // Construct the callgraph
        app.constructCallgraph();

        // initialize the model
        App.v().initializeAppModel(app);

        // step 1.
        Logger.debug("here");

    }


}
