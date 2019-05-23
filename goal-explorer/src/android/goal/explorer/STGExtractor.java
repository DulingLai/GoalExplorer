package android.goal.explorer;

import android.goal.explorer.cmd.GlobalConfig;
import android.goal.explorer.model.App;
import android.goal.explorer.model.stg.STG;
import org.pmw.tinylog.Logger;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

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

    // Screen transition graph
    private STG stg;

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

        // initialize the app model
        initialize();

        // initialize the STG with services and broadcast receivers
        stg = new STG();

        // step 1.
        Logger.debug("here");
    }

    /**
     * initialize the app model
     */
    public void initialize() {
        // Construct the callgraph
        app.constructCallgraph();

        // initialize the model
        App.v().initializeAppModel(app);
    }

    /**
     * Gets the manifest of the app
     * @return The manifest of the app
     */
    public ProcessManifest getManifest() {
        return app.getManifest();
    }

    /**
     * Gets the number of activities in the app manifest
     * @return The number of activities
     */
    public Integer getNumActInManifest() {
        return app.getManifest().getActivities().size();
    }

    /**
     * Gets the number of services in the app manifest
     * @return The number of services
     */
    public Integer getNumServiceInManifest() {
        return app.getManifest().getServices().size();
    }

    /**
     * Gets the number of broadcast receivers in the app manifest
     * @return The number of broadcast receivers
     */
    public Integer getNumReceiverInManifest() {
        return app.getManifest().getReceivers().size();
    }
}
