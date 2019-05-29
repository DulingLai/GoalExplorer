package android.goal.explorer;

import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.App;
import android.goal.explorer.model.stg.STG;
import android.goal.explorer.topology.TopologyExtractor;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

public class STGExtractor {
    // Logging tags
    private static final String RESOURCE_PARSER = "ResourceParser";
    private static final String FRAGMENT_ANALYZER = "FragmentAnalyzer";
    private static final String CALLBACK_ANALYZER = "CallbackAnalyzer";
    private static final String CLASS_ANALYZER = "JimpleAnalyzer";
    private static final String ICC_PARSER = "IccParser";
    private static final String GRAPH_BUILDER = "GraphBuilder";

    // Configuration
    private GlobalConfig config;

    // Manifest
    private ProcessManifest manifest;

    // App model
    private App app;

    // Setup application
//    private SetupApplication app;

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
        SetupApplication setupApplication = new SetupApplication(
                config.getFlowdroidConfig().getAnalysisFileConfig().getAndroidPlatformDir(),
                config.getFlowdroidConfig().getAnalysisFileConfig().getTargetAPKFile());

        // initialize app model
        initialize(setupApplication);
    }


    /**
     * Perform the whole analysis
     */
    public void constructSTG() {

        // initialize the STG with services and broadcast receivers
        stg = new STG(app);

        // topology - time out per component (minute), number of thread (default: 16)
        TopologyExtractor topologyExtractor = new TopologyExtractor(app, config.getTimeout(),config.getNumThread());
        topologyExtractor.extractTopopology();

        // build the initial screens

    }


    /**
     * initialize the app model
     */
    public void initialize(SetupApplication setupApplication) {
        // Construct the callgraph
        setupApplication.constructCallgraph();

        // initialize the model
        app = new App();
        app.initializeAppModel(setupApplication);
    }

    /**
     * Gets the manifest of the app
     * @return The manifest of the app
     */
    public ProcessManifest getManifest() {
        return manifest;
    }

    /**
     * Gets the number of activities in the app manifest
     * @return The number of activities
     */
    public Integer getNumActInManifest() {
        return manifest.getActivities().size();
    }

    /**
     * Gets the number of services in the app manifest
     * @return The number of services
     */
    public Integer getNumServiceInManifest() {
        return manifest.getServices().size();
    }

    /**
     * Gets the number of broadcast receivers in the app manifest
     * @return The number of broadcast receivers
     */
    public Integer getNumReceiverInManifest() {
        return manifest.getReceivers().size();
    }
}
