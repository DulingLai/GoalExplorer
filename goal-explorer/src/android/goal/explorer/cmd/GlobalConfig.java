package android.goal.explorer.cmd;

import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;

public class GlobalConfig {

    // FlowDroid configuration
    private InfoflowAndroidConfiguration flowdroidConfig;

    // Other params
    private Integer targetApi;

    public GlobalConfig() {
        flowdroidConfig = new InfoflowAndroidConfiguration();
        targetApi = -1;
    }

    /**
     * Gets FlowDroid configuration
     * @return FlowDroid configuration
     */
    public InfoflowAndroidConfiguration getFlowdroidConfig() {
        return flowdroidConfig;
    }

    /**
     * Sets FlowDroid configuration
     * @param flowdroidConfig FlowDroid configuration object ({@link InfoflowAndroidConfiguration})
     */
    public void setFlowdroidConfig(InfoflowAndroidConfiguration flowdroidConfig) {
        this.flowdroidConfig = flowdroidConfig;
    }

    /**
     * Gets the target api level
     * @return The target api level
     */
    public Integer getTargetApi() {
        return targetApi;
    }

    /**
     * Sets the target api level
     * @param targetApi The target api level
     */
    public void setTargetApi(Integer targetApi) {
        this.targetApi = targetApi;
    }

}
