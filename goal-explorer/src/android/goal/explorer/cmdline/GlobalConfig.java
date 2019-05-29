package android.goal.explorer.cmdline;

import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;

public class GlobalConfig {


    // FlowDroid configuration
    private InfoflowAndroidConfiguration flowdroidConfig;

    // Target API level
    private Integer targetApi;

    // The max timeout when analyzing each component
    private Integer timeout;

    // The number of threads used in parallel analysis
    private Integer numThread;

    public GlobalConfig() {
        flowdroidConfig = new InfoflowAndroidConfiguration();
        targetApi = -1;
        timeout = 5;
        numThread = 16;
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

    /**
     * Gets the max timeout analyzing each component
     * @return The max timeout to analyze each component
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the max timeout analyzing each component
     * @param maxTimeout The max timeout analyzing each component
     */
    public void setTimeout(Integer maxTimeout) {
        this.timeout = maxTimeout;
    }

    /**
     * Gets the number of threads used in parallel analysis
     * @return The number of threads used in parallel analysis
     */
    public Integer getNumThread() {
        return numThread;
    }

    /**
     * Sets the number of threads used in parallel analysis
     * @param numThread The number of threads used in parallel analysis
     */
    public void setNumThread(Integer numThread) {
        this.numThread = numThread;
    }
}
