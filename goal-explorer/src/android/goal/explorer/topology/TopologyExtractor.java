package android.goal.explorer.topology;

import android.goal.explorer.data.android.AndroidClass;
import android.goal.explorer.data.android.constants.MethodConstants;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.*;
import org.pmw.tinylog.Logger;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;
import soot.jimple.infoflow.memory.IMemoryBoundedSolver;
import soot.jimple.infoflow.memory.ISolverTerminationReason;
import soot.jimple.infoflow.util.SystemClassHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static android.goal.explorer.utils.SootUtils.findAndAddMethod;

public class TopologyExtractor implements IMemoryBoundedSolver {

    private static final String TAG = "Topology";

    private App app;

    private final int timeout;
    private final int numThread;

    private Set<IMemoryBoundedSolverStatusNotification> notificationListeners = new HashSet<>();
    private ISolverTerminationReason isKilled = null;

    private enum AnalysisType {
        MENU, DRAWER, BOTH
    }

    // UI elements
//    private final Set<DialogEntity> dialogs = Collections.synchronizedSet(new HashSet<>());
//    private final Map<Integer, LayoutEntity> layouts = Collections.synchronizedMap(new HashMap<>());

    public TopologyExtractor(App app, int timeout, int numThread) {

        this.app = app;

        this.timeout = timeout;
        this.numThread = numThread;
    }

    /**
     * Collects the lifecycle methods
     */
    public void extractTopopology() {
        // multi-thread analysis -> each thread analyze a component
        ExecutorService classExecutor = Executors.newFixedThreadPool(numThread);
        Set<Future<Void>> classTasks = new HashSet<>();
        for (Activity activity : app.getActivities()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(activity);
                return null;
            }));
        }

        for (Service service : app.getServices()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(service);
                return null;
            }));
        }

        for (BroadcastReceiver receiver : app.getBroadcastReceivers()) {
            classTasks.add(classExecutor.submit(() -> {
                submitTopologyAnalysisTask(receiver);
                return null;
            }));
        }

        // Execute the task
        for (Future<Void> classTask : classTasks) {
            try {
                classTask.get(timeout, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Logger.warn("[{}] Interrupted analyzer from a parent thread", TAG);
                classTask.cancel(true);
            } catch (TimeoutException e) {
                Logger.warn("[{}] Timeout for analysis task: {}", TAG, classTask);
                classTask.cancel(true);
            } catch (Exception e) {
                Logger.error("[{}] Unknown error happened: {}", TAG, e.getMessage());
                classTask.cancel(true);
            }
        }
        classExecutor.shutdown();
    }

    /**
     * Submit the analysis as a task for multi-thread
     * @param comp The activity class to analyze
     */
    private void submitTopologyAnalysisTask(AbstractComponent comp) {
        Logger.debug("[{}] Collecting topology for: {}...", TAG, comp.getMainClass());

        // collect extended classes
        collectExtendedClasses(comp);

        // Collect lifecycle methods
        collectLifecycleMethods(comp);

        // Collect callback methods
        collectCallbackMethods(comp);

        Logger.debug("[{}] DONE collecting topology for: {}...", TAG, comp.getMainClass());

        // Collect reachable methods from lifecycle methods
//        for (MethodOrMethodContext method : comp.getLifecycleMethods()) {
//            ComponentReachableMethods rm = new ComponentReachableMethods(comp.getMainClass(), Collections.singletonList(method));
//            rm.update();
//            comp.addLifecycleReachableMethods(method, rm);
//        }
//
//        for (MethodOrMethodContext lifecycleMethod : comp.getLifecycleMethods()) {
//            ComponentReachableMethods rm = comp.getLifecycleReachableMethodsFrom(lifecycleMethod);
//            QueueReader<MethodOrMethodContext> reachableMethods = rm.listener();
//
//            // Analyze each reachable methods
//            while (reachableMethods.hasNext()) {
//                // Get the next reachable method
//                SootMethod method = reachableMethods.next().method();
//                // Do not analyze system classes
//                if (SystemClassHandler.isClassInSystemPackage(method.getDeclaringClass().getName()))
//                    continue;
//                if (!method.isConcrete())
//                    continue;
//
//                // Edges
//                Set<List<Edge>> edges = rm.getContextEdges(method);
//
//                // Analyze layout
//                if (lifecycleMethod.method().getName().contains("onCreate")) {
//                    // Analyze layout
//                    analyzeLayout(method, comp, edges);
//                }
//
//                // Analyze fragment transaction
//                Set<SootClass> fragmentClasses = analyzeFragmentTransaction(method, edges, comp);
//
//                // Create the fragments
//                for (SootClass sc : fragmentClasses) {
//                    App.v().createFragment(sc, comp);
//
//                    // process the fragment lifecycle methods
//                    Fragment fragment = App.v().getFragmentByName(sc.getName());
//
//                    // collect extended classes
//                    collectExtendedClassesForFragment(fragment);
//
//                    // collect lifecycle methods
//                    collectFragmentLifecycleMethods(fragment);
//
//                    // collect menu methods
//                    collectMenuMethods(fragment);
//
//                    // collect reachable methods
//                    LinkedList<MethodOrMethodContext> lifecycleMethods = new LinkedList<>(fragment.getLifecycleMethods());
//                    for (MethodOrMethodContext fragmentLifecycleMethod : lifecycleMethods) {
//                        ComponentReachableMethods rmFragment = new ComponentReachableMethods(fragment.getMainClass(),
//                                Collections.singletonList(fragmentLifecycleMethod));
//                        rmFragment.update();
//                        fragment.addLifecycleReachableMethods(fragmentLifecycleMethod, rmFragment);
//                    }
//                }
//            }
//        }
//
//        // Collect nodes with fragments
//        Screen screen = SSTG.v().getScreenNodeByName(comp.getName());
//        screen.addFragments(comp.getFragments());

        // Analyze drawer methods
//        analyzeMenuDrawerMethods(screen);
    }

    /**
     * Collects all extended classes of the component declared in the manifest
     */
    private void collectExtendedClasses(AbstractComponent comp){
        if (comp instanceof Activity) {
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (Scene.v().getActiveHierarchy().isClassSubclassOf(x, AndroidClass.v().osClassActivity) ||
                        Scene.v().getActiveHierarchy().isClassSubclassOf(x, AndroidClass.v().scSupportV7Activity) ||
                        Scene.v().getActiveHierarchy().isClassSubclassOf(x, AndroidClass.v().scSupportV4Activity)) {
                    if (!SystemClassHandler.isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }});
        } else if (comp instanceof Service) {
            // Services
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (Scene.v().getActiveHierarchy().isClassSubclassOf(x, AndroidClass.v().osClassService)){
                    if (!SystemClassHandler.isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }
            });
        } else if (comp instanceof BroadcastReceiver) {
            // Broadcast receivers
            Scene.v().getActiveHierarchy().getSuperclassesOf(comp.getMainClass()).forEach(x -> {
                if (Scene.v().getActiveHierarchy().isClassSubclassOf(x, AndroidClass.v().osClassBroadcastReceiver)) {
                    if (!SystemClassHandler.isClassInSystemPackage(x.getName()))
                        comp.addAddedClass(x);
                }
            });
        }
    }

    /**
     * collects the lifecycle methods for a component
     * @param comp The component to collect lifecycle methods
     */
    private void collectLifecycleMethods(AbstractComponent comp) {
        // lifecycle methods
        List<String> lifecycleMethods = Collections.emptyList();

        if (comp instanceof Activity) {
            lifecycleMethods = MethodConstants.Activity.getlifecycleMethodsPreRun();
        } else if (comp instanceof Service) {
            lifecycleMethods = MethodConstants.Service.getLifecycleMethodsPrerun();
        } else if (comp instanceof BroadcastReceiver) {
            lifecycleMethods = MethodConstants.BroadcastReceiver.getLifecycleMethods();
        } else if (comp instanceof Fragment) {
            lifecycleMethods = MethodConstants.Fragment.getLifecycleMethods();
        }

        // Collect the list of lifecycle methods (in order)
        lifecycleMethods.iterator().forEachRemaining(x -> {
            MethodOrMethodContext method = findAndAddMethod(x, comp);
            if (method!=null)
                comp.addLifecycleMethod(method);
        });
    }

    /**
     * Collects all callback methods of the component
     * @param comp The given component to collect callback methods
     */
    private void collectCallbackMethods(AbstractComponent comp) {
        Set<CallbackDefinition> callbacks = new HashSet<>(app.getCallbacksInSootClass(comp.getMainClass()));
        comp.addCallbacks(callbacks);

        // Menu callbacks
        collectMenuMethods(comp);
    }

    /**
     * Finds all menu creation and callback methods in a given component
     * @param comp The component to look for menu creation and callback methods
     */
    private void collectMenuMethods(AbstractComponent comp) {
        // Get the list of menu methods
        List<String> menuCreateMethods = Collections.emptyList();
        List<String> menuCallbackMethods = MethodConstants.Menu.getOptionMenuCallbackMethodList();
        if (comp instanceof Activity) {
            menuCreateMethods = MethodConstants.Menu.getOptionMenuCreateForActivity();
            // The list of menu methods (in order of create and callback)
            menuCreateMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Activity)comp).addMenuOnCreateMethod(method);
                }
            });

            menuCallbackMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null)
                    ((Activity)comp).addMenuCallbackMethod(method);
            });
        } else if (comp instanceof Fragment) {
            menuCreateMethods = MethodConstants.Menu.getOptionMenuCreateForFragment();
            // The list of menu methods (in order of create and callback)
            menuCreateMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null) {
                    ((Fragment)comp).addMenuRegistrationMethod(method);
                }
            });

            menuCallbackMethods.iterator().forEachRemaining(x -> {
                MethodOrMethodContext method = findAndAddMethod(x, comp);
                if (method!=null)
                    ((Fragment)comp).addMenuCallbackMethod(method);
            });
        }
    }

    @Override
    public void forceTerminate(ISolverTerminationReason reason) {
        this.isKilled = reason;
    }

    @Override
    public boolean isTerminated() {
        return isKilled != null;
    }

    @Override
    public boolean isKilled() {
        return isKilled != null;
    }

    @Override
    public void reset() {
        this.isKilled = null;
    }

    @Override
    public void addStatusListener(IMemoryBoundedSolverStatusNotification listener) {
        this.notificationListeners.add(listener);
    }

    @Override
    public ISolverTerminationReason getTerminationReason() {
        return isKilled;
    }
}
