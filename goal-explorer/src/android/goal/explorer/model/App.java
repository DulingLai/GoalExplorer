package android.goal.explorer.model;

import android.goal.explorer.data.value.ResourceValueProvider;
import android.goal.explorer.model.component.*;
import android.goal.explorer.model.entity.Dialog;
import android.goal.explorer.model.entity.Menu;
import android.goal.explorer.utils.AxmlUtils;
import org.pmw.tinylog.Logger;
import soot.SootClass;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointUtils;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class App {

    // Instance of the class
    private static volatile App instance;

    // Package name of the APK file
    private String packageName;

    // initial activity
    private Set<Activity> launchActivities;

    // application class
    private Application application;

    // Android components
    private Set<Activity> activities;
    private Set<Service> services;
    private Set<BroadcastReceiver> broadcastReceivers;
    private Set<ContentProvider> contentProviders;

    // Other UI elements
    private Set<Fragment> fragments;
    private Set<Menu> menus;
    private Set<Dialog> dialogs;

    // layout parser
    private LayoutManager layoutManager;

    private App(){
        reset();
    }

    public static App v() {
        if (instance == null) {
            synchronized (App.class) {
                if (instance == null)
                    instance = new App();
            }
        }
        return instance;
    }

    /**
     * Resets all components of the app
     */
    private void reset() {
        launchActivities = Collections.synchronizedSet(new HashSet<>());
        activities = Collections.synchronizedSet(new HashSet<>());
        services = Collections.synchronizedSet(new HashSet<>());
        broadcastReceivers = Collections.synchronizedSet(new HashSet<>());
        contentProviders = Collections.synchronizedSet(new HashSet<>());
        fragments = Collections.synchronizedSet(new HashSet<>());
        menus = Collections.synchronizedSet(new HashSet<>());
        dialogs = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Initialize the model of the app
     * @param app The SetupApplication instance from FlowDroid
     */
    public void initializeAppModel(SetupApplication app) {
        reset();

        this.packageName = app.getPackageName();

        // initialize the resources
        ResourceValueProvider.v().initializeResources(app.getResources());

        // initialize all the components
        initializeComponents(app.getEntrypointClasses(), app.getManifest());

        // set the launch activity according to the manifest
        setLaunchActivities(app.getManifest().getLaunchableActivities());
    }

    /**
     * Initialize the components of the app
     * @param entrypointClasses The entrypoint classes of the app
     * @param manifest The manifest of the app
     */
    private void initializeComponents(Set<SootClass> entrypointClasses, ProcessManifest manifest) {
        AndroidEntryPointUtils entryPointUtils = new AndroidEntryPointUtils();
        for (SootClass comp : entrypointClasses) {
            AndroidEntryPointUtils.ComponentType compType = entryPointUtils.getComponentType(comp);
            switch (compType) {
                case Activity:
                    AXmlNode activityNode = manifest.getActivity(comp.getName());
                    if (activityNode != null)
                        this.activities.add(new Activity(activityNode, comp, packageName));
                    else
                        Logger.error("Failed to find activity in the manifest: {}", comp.getName());
                    break;
                case Service:
                    AXmlNode serviceNode = manifest.getService(comp.getName());
                    if (serviceNode != null)
                        this.services.add(new Service(serviceNode, comp, packageName));
                    else
                        Logger.error("Failed to find service in the manifest: {}", comp.getName());
                    break;
                case BroadcastReceiver:
                    AXmlNode receiverNode = manifest.getReceiver(comp.getName());
                    if (receiverNode != null)
                        this.broadcastReceivers.add(new BroadcastReceiver(receiverNode,
                            comp, packageName));
                    else
                        Logger.error("Failed to find broadcast receiver in the manifest: {}", comp.getName());
                    break;
                case ContentProvider:
                    AXmlNode providerNode = manifest.getProvider(comp.getName());
                    if (providerNode != null)
                        this.contentProviders.add(new ContentProvider(providerNode, comp, packageName));
                    else
                        Logger.error("Failed to find content provider in the manifest: {}", comp.getName());
                    break;
                case Fragment:
                    this.fragments.add(new Fragment(comp));
                    break;
                case Application:
                    AXmlNode applicationNode = manifest.getApplication();
                    if (applicationNode != null)
                        this.application = new Application(applicationNode, comp, packageName);
                    else
                        Logger.error("Failed to find content provider in the manifest: {}", comp.getName());
                    break;
                case ServiceConnection:
                case GCMBaseIntentService:
                case GCMListenerService:
                case Plain:
                    Logger.debug("This should not happen. Let's see what are those classes.");
            }
        }
    }

    /* ====================================
               Getters and setters
     ======================================*/
    /**
     * Gets the package name of the app
     * @return The package name of the app
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets all activities
     * @return All activities
     */
    public synchronized Set<Activity> getActivities() {
        return activities;
    }

    /**
     * Gets the specific activity by name
     * @param activityName The activity name
     * @return The activity of specific name
     */
    public synchronized Activity getActivityByName(String activityName) {
        for(Activity activity : activities){
            if (activity.getName().equals(activityName)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Adds an activity to the model
     * @param activity The activity to be added
     */
    public synchronized void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    /**
     * Adds a set of activities to the model
     * @param activities The activities to be added
     */
    public synchronized void addActivities(Set<Activity> activities) {
        activities.remove(null);
        this.activities.addAll(activities);
    }

    /**
     * Gets the launch activity of the app
     * @return The launch activities
     */
    public synchronized Set<Activity> getLaunchActivities() {
        return this.launchActivities;
    }

    /**
     * Set the launch activity of the app
     * @param launchActivityNodes The launch activities in form of AXML nodes
     */
    private synchronized void setLaunchActivities(Set<AXmlNode> launchActivityNodes) {
        for (AXmlNode node : launchActivityNodes) {
            String activityName = AxmlUtils.processNodeName(node, packageName);
            Activity activity = getActivityByName(activityName);
            if (activity!=null)
                this.launchActivities.add(activity);
            else
                Logger.error("Failed to find activity: {}", activityName);
        }
    }

    /**
     * Gets all services of the app
     * @return All services
     */
    public synchronized Set<Service> getServices() {
        return services;
    }

    /**
     * Adds a service to the model
     * @param service The service to be added
     */
    public synchronized void addService(Service service) {
        this.services.add(service);
    }

    /**
     * Adds a set of services to the model
     * @param services The services to be added
     */
    public synchronized void addServices(Set<Service> services) {
        services.remove(null);
        this.services.addAll(services);
    }

    /**
     * Gets all broadcast receivers of the app
     * @return All broadcast receivers of the app
     */
    public synchronized Set<BroadcastReceiver> getBroadcastReceivers() {
        return broadcastReceivers;
    }

    /**
     * Adds a broadcast receiver to the app
     * @param broadcastReceiver All broadcast receiver to be added
     */
    public synchronized void addBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        this.broadcastReceivers.add(broadcastReceiver);
    }

    /**
     * Adds a set of broadcast receivers to the model
     * @param broadcastReceivers The broadcast receivers to be added
     */
    public synchronized void addBroadcastReceivers(Set<BroadcastReceiver> broadcastReceivers) {
        broadcastReceivers.remove(null);
        this.broadcastReceivers.addAll(broadcastReceivers);
    }

    /**
     * Gets all content providers of the app
     * @return All content providers
     */
    public synchronized Set<ContentProvider> getContentProviders() {
        return contentProviders;
    }

    /**
     * Adds the content provider to the app
     * @param contentProvider The content provider to be added
     */
    public synchronized void addContentProvider(ContentProvider contentProvider) {
        this.contentProviders.add(contentProvider);
    }

    /**
     * Adds a set of content providers to the model
     * @param contentProviders The content providers to be added
     */
    public synchronized void addContentProviders(Set<ContentProvider> contentProviders) {
        contentProviders.remove(null);
        this.contentProviders.addAll(contentProviders);
    }

    /**
     * Gets all fragments of the app
     * @return All fragments
     */
    public synchronized Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Gets a fragment by name
     * @param name The name of the fragment
     * @return The fragment of given name
     */
    public synchronized Fragment getFragmentByName(String name) {
        for (Fragment fragment : fragments) {
            if (name.equals(fragment.getName()))
                return fragment;
        }
        return null;
    }

    /**
     * Gets a fragment by resource id
     * @param resId The resource id of the fragment
     * @return The fragment of given name
     */
    public synchronized Fragment getFragmentByResId(Integer resId) {
        for (Fragment fragment : fragments) {
            if (resId.equals(fragment.getResourceId()))
                return fragment;
        }
        return null;
    }

    /**
     * Adds a fragment to the model
     * @param fragment The fragment to be added
     */
    public synchronized void addFragment(Fragment fragment) {
        this.fragments.add(fragment);
    }

    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     * @param parentActivity The parent activity of this fragment
     */
    public synchronized void createFragment(SootClass sootClass, Activity parentActivity) {
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null) {
            fragment = new Fragment(sootClass, parentActivity);
            addFragment(fragment);
        }
        // Set up the parent activity
        addFragmentToActivity(fragment, parentActivity);
    }

    /**
     * Add a fragment to activity
     * @param fragment The fragment
     * @param parentActivity The parent activity
     */
    private synchronized void addFragmentToActivity(Fragment fragment, Activity parentActivity) {
        fragment.AddParentActivity(parentActivity);
        parentActivity.addFragment(fragment);
    }

    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     */
    public synchronized void createFragment(SootClass sootClass) {
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null)
            addFragment(new Fragment(sootClass));
    }

    /**
     * Creates a fragment to the model
     * @param sc The soot class of this fragment
     * @param resId The resource id of the fragment to be added
     */
    public synchronized void createFragment(SootClass sc, Integer resId) {
        Fragment fragment = getFragmentByResId(resId);
        if (fragment==null)
            addFragment(new Fragment(sc, resId));
    }

    /**
     * Gets the layout file manager
     * @return The layout manager
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Sets the layout file manager
     * @param lfp THe layout file manager
     */
    public void setLayoutManager(LayoutManager lfp) {
        this.layoutManager = lfp;
    }

//    /**
//     * Gets the ICFG
//     * @return ICFG
//     */
//    public JimpleBasedInterproceduralCFG getIcfg() {
//        return icfg;
//    }
//
//    /**
//     * Sets the ICFG
//     * @param icfg The ICFG to be set
//     */
//    public void setIcfg(JimpleBasedInterproceduralCFG icfg) {
//        this.icfg = icfg;
//    }
}
