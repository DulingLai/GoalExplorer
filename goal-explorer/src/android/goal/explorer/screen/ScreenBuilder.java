//package android.goal.explorer.screen;
//
//import android.goal.explorer.analysis.value.identifiers.Argument;
//import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
//import android.goal.explorer.data.android.AndroidClass;
//import android.goal.explorer.data.android.constants.MethodConstants;
//import android.goal.explorer.model.App;
//import android.goal.explorer.model.component.AbstractComponent;
//import android.goal.explorer.model.component.Activity;
//import android.goal.explorer.model.component.Fragment;
//import android.goal.explorer.topology.TopologyExtractor;
//import org.pmw.tinylog.Logger;
//import soot.*;
//import soot.jimple.InstanceInvokeExpr;
//import soot.jimple.InvokeExpr;
//import soot.jimple.Stmt;
//import soot.jimple.infoflow.android.callbacks.ComponentReachableMethods;
//import soot.jimple.infoflow.util.SystemClassHandler;
//import soot.jimple.toolkits.callgraph.Edge;
//import soot.toolkits.graph.UnitGraph;
//import soot.toolkits.graph.pdg.HashMutablePDG;
//import soot.toolkits.graph.pdg.PDGNode;
//import soot.toolkits.graph.pdg.ProgramDependenceGraph;
//import soot.util.MultiMap;
//import soot.util.queue.ChunkedQueue;
//import soot.util.queue.QueueReader;
//
//import java.util.*;
//
//import static android.goal.explorer.analysis.AnalysisUtils.extractIntArgumentFrom;
//import static android.goal.explorer.utils.SootUtils.findAndAddMethod;
//
//public class ScreenBuilder {
//
//    /**
//     * Default constructor
//     * @param app The application model
//     */
//    public ScreenBuilder(App app) {
//        // Check if we did not initialize the soot class
//        for (Activity activity : app.getActivities()) {
//            SootClass sc = activity.getMainClass();
//            if (sc == null) {
//                sc = Scene.v().getSootClassUnsafe(activity.getName());
//                if (sc != null)
//                    activity.setMainClass(sc);
//                else
//                    Logger.error("Failed to find activity class: {}", activity.getName());
//            }
//
//            ComponentReachableMethods rm =
//            sc.getMethods()
//        }
//
//        // register propagation analysis
//        registerPropagationAnalysis();
//
//        MultiMap fragmentsMap = app.getLayoutManager().getFragments();
//
//        // step 1.
//        if (fragmentsMap!=null && !fragmentsMap.isEmpty())
//            Logger.debug("here");
//    }
//
//    /**
//     * Analyze the screen for menu/drawer registration
//     * @param screen The screen node
//     */
//    private void analyzeMenuDrawerMethods(Screen screen) {
//        Activity activity = screen.getContainerActivity();
//        Set<Fragment> fragments = screen.getFragments();
//
//        analyzeMenuDrawerInComp(activity, screen);
//        for (Fragment fragment : fragments){
//            analyzeMenuDrawerInComp(fragment, screen);
//        }
//    }
//
//    /**
//     * Analyze the activity for menu/drawer registration
//     * @param component The activity or fragment
//     */
//    private void analyzeMenuDrawerInComp(AbstractComponent component, Screen screen) {
//        Set<MethodOrMethodContext> menuRegs;
//        Set<MethodOrMethodContext> menuCallbacks;
//
//        if (component instanceof Activity) {
//            menuRegs = ((Activity) component).getMenuOnCreateMethods();
//            menuCallbacks = ((Activity) component).getMenuCallbackMethods();
//        } else if (component instanceof Fragment) {
//            menuRegs = ((Fragment) component).getMenuRegistrationMethods();
//            menuCallbacks = ((Fragment) component).getMenuCallbackMethods();
//        } else throw new IllegalArgumentException("Analyzing menu/drawer for non-activity/fragment class!");
//
//        if (menuRegs != null && !menuRegs.isEmpty()) {
//            if (menuCallbacks != null && !menuCallbacks.isEmpty()) {
//                analyzeMenuDrawerRegistration(component.getMainClass(), menuRegs, screen, menuCallbacks,
//                        TopologyExtractor.AnalysisType.BOTH);
//            } else
//                analyzeMenuDrawerRegistration(component.getMainClass(), menuRegs, screen, Collections.emptySet(),
//                        TopologyExtractor.AnalysisType.MENU);
//        } else if (menuCallbacks != null && !menuCallbacks.isEmpty()) {
//            analyzeMenuDrawerRegistration(component.getMainClass(), Collections.emptySet(), screen, menuCallbacks,
//                    TopologyExtractor.AnalysisType.DRAWER);
//        } else {
//            Logger.debug("[{}] No menu methods found for: {} in screen {}", component.getName(), screen.getName());
//        }
//    }
//
//    /**
//     * Analyze drawer menu registration
//     * @param mainClass The main class of the component
//     * @param regMethods The menu registration method found in the component
//     * @param screen The screen node
//     * @param callbackMethods The menu callbacks method found in the component
//     * @param analysisType The analysis type (menu, drawer, both)
//     */
//    private void analyzeMenuDrawerRegistration(SootClass mainClass, Set<MethodOrMethodContext> regMethods, Screen screen,
//                                               Set<MethodOrMethodContext> callbackMethods, TopologyExtractor.AnalysisType analysisType) {
//
//        if (analysisType == TopologyExtractor.AnalysisType.BOTH || analysisType == TopologyExtractor.AnalysisType.MENU) {
//            analyzeMenuMethods(mainClass, regMethods, screen, callbackMethods, analysisType);
//            if (analysisType == TopologyExtractor.AnalysisType.BOTH) {
//                analyzeDrawerMethods(mainClass, screen, callbackMethods, analysisType);
//            }
//        } else {
//            analyzeDrawerMethods(mainClass, screen, callbackMethods, analysisType);
//        }
//    }
//
//    /**
//     * Analyze the menu methods
//     * @param mainClass
//     * @param regMethods
//     * @param screen
//     * @param callbackMethods
//     * @param analysisType
//     */
//    private void analyzeMenuMethods(SootClass mainClass, Set<MethodOrMethodContext> regMethods, Screen screen,
//                                    Set<MethodOrMethodContext> callbackMethods, TopologyExtractor.AnalysisType analysisType){
//        for (MethodOrMethodContext menuReg : regMethods) {
//            ComponentReachableMethods rm = new ComponentReachableMethods(mainClass,
//                    Collections.singletonList(menuReg));
//            rm.update();
//            QueueReader<MethodOrMethodContext> reachableMethods = rm.listener();
//            while (reachableMethods.hasNext()) {
//                SootMethod method = reachableMethods.next().method();
//
//                Body b = method.retrieveActiveBody();
//
//                for (Unit u : b.getUnits()) {
//                    Stmt stmt = (Stmt) u;
//                    if (stmt.containsInvokeExpr()) {
//                        InvokeExpr inv = stmt.getInvokeExpr();
//                        if (invokesGetItemId(inv)) {
//                            Map<Integer, List<PDGNode>> conditionalMapping = PDGUtils.findConditionalMapping(u,
//                                    new HashMutablePDG((UnitGraph)
//                                            AnalysisParameters.v().getIcfg().getOrCreateUnitGraph(b)));
//
//                            for (Integer resId : conditionalMapping.keySet()) {
//                                analyzeCallbackConditionalFlows(resId, conditionalMapping,
//                                        mainClass, screen, callbackMethods, true);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Analyze the
//     * @param mainClass
//     * @param screen
//     * @param callbackMethods
//     * @param analysisType
//     */
//    private void analyzeDrawerMethods(SootClass mainClass, Screen screen,
//                                      Set<MethodOrMethodContext> callbackMethods, TopologyExtractor.AnalysisType analysisType){
//        for (MethodOrMethodContext menuCallbacks : callbackMethods) {
//            ComponentReachableMethods rm = new ComponentReachableMethods(mainClass,
//                    Collections.singletonList(menuCallbacks));
//            rm.update();
//            QueueReader<MethodOrMethodContext> reachableMethods = rm.listener();
//            while (reachableMethods.hasNext()) {
//                SootMethod method = reachableMethods.next().method();
//
//                Body b = method.retrieveActiveBody();
//
//                for (Unit u : b.getUnits()) {
//                    Stmt stmt = (Stmt) u;
//                    if (stmt.containsInvokeExpr()) {
//                        InvokeExpr inv = stmt.getInvokeExpr();
//                        if (invokesGetItemId(inv)) {
//                            Map<Integer, List<PDGNode>> conditionalMapping = PDGUtils.findConditionalMapping(u,
//                                    new HashMutablePDG((UnitGraph)
//                                            AnalysisParameters.v().getIcfg().getOrCreateUnitGraph(b)));
//
//                            for (Integer resId : conditionalMapping.keySet()) {
//                                analyzeCallbackConditionalFlows(resId, conditionalMapping,
//                                        mainClass, screen, callbackMethods, false);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Analyze the conditional flow from getItemId
//     * This is to reveal which button leads to what consequence, such as menu open, drawer open
//     * @param resId The resource id of the button
//     * @param conditionalMapping The conditional mapping from each resource id to the create the menu or drawer
//     */
//    private void analyzeCallbackConditionalFlows(Integer resId, Map<Integer, List<PDGNode>> conditionalMapping,
//                                                 SootClass mainClass, Screen screen,
//                                                 Set<MethodOrMethodContext> menuCallbacks, boolean analyzeMenu) {
//        List<PDGNode> dependents = conditionalMapping.get(resId);
//
//        ChunkedQueue<PDGNode> pdgNodes = new ChunkedQueue<>();
//        QueueReader unprocessedNodes = pdgNodes.reader();
//
//        dependents.forEach(pdgNodes::add);
//
//        while (unprocessedNodes.hasNext()) {
//            PDGNode nextNode = (PDGNode) unprocessedNodes.next();
//            Iterator<Unit> unitIterator = PDGUtils.unitIteratorOfPDGNode(nextNode);
//            if (!analyzeMenu) {
//                if (findDrawerOpenFromUnits(unitIterator)) {
//                    Drawer drawer = new Drawer(resId);
//                    Screen newScreen = new Screen(screen);
//                    newScreen.setDrawer(drawer);
//                    SSTG.v().addScreen(newScreen);
//                }
//            } else {
//                if (findMenuInflateFromUnits(unitIterator) != null) {
//                    Unit menuInflateUnit = findMenuInflateFromUnits(unitIterator);
//                    createNewScreenWithMenuFromUnits(menuInflateUnit, resId, mainClass, menuCallbacks, screen);
//                }
//            }
//
//            // Inter-proc analysis
//            // analyze reachable methods for each unit
//            Set<Unit> unitsForFurtherAnalysis = new HashSet<>();
//            unitIterator.forEachRemaining(unitsForFurtherAnalysis::add);
//            UnitsReachableMethods unitRM = new UnitsReachableMethods(mainClass,
//                    unitsForFurtherAnalysis);
//            unitRM.update();
//            QueueReader<MethodOrMethodContext> reachables = unitRM.listener();
//            while (reachables.hasNext()) {
//                UnitGraph unitGraph = (UnitGraph) AnalysisParameters.v().getIcfg().
//                        getOrCreateUnitGraph(reachables.next().method());
//                ProgramDependenceGraph newPdg = new HashMutablePDG(unitGraph);
//                newPdg.iterator().forEachRemaining(pdgNodes::add);
//            }
//        }
//    }
//
//
//    private void createNewScreenWithMenuFromUnits(Unit u, Integer resId, SootClass mainClass,
//                                                  Set<MethodOrMethodContext> callbacks, Screen screen) {
//        InvokeExpr inv = ((Stmt)u).getInvokeExpr();
//        Argument arg = extractIntArgumentFrom(inv);
//        Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, u, null);
//        if (values!=null && !values.isEmpty()) {
//            Object value = values.iterator().next();
//            if (value instanceof Integer) {
//                Menu menu = App.v().getLayoutManager().getMenu((int) value);
//                menu.setButton(ResourceValueProvider.v().getStringById(resId));
//                menu.addCallbackMethods(callbacks);
//                menu.setParentClass(mainClass);
//                Screen newScreen = new Screen(screen);
//                newScreen.setMenu(menu);
//                SSTG.v().addScreen(newScreen);
//            }
//        }
//    }
//
//
//    /**
//     * Finds drawer from units
//     * @param unitIterator The unit iterator
//     * @return True if the drawer can be found, false otherwise
//     */
//    private boolean findDrawerOpenFromUnits(Iterator<Unit> unitIterator) {
//        while (unitIterator.hasNext()) {
//            Unit unit = unitIterator.next();
//            if (unit instanceof Stmt) {
//                if (((Stmt) unit).containsInvokeExpr()) {
//                    InvokeExpr invMethod = ((Stmt) unit).getInvokeExpr();
//                    if (invokesDrawerOpen(invMethod)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Finds menu from units iterator
//     * @param unitIterator The unit iterator
//     * @return True if the menu can be found, false otherwise
//     */
//    private Unit findMenuInflateFromUnits(Iterator<Unit> unitIterator) {
//        while (unitIterator.hasNext()) {
//            Unit unit = unitIterator.next();
//            if (unit instanceof Stmt) {
//                if (((Stmt) unit).containsInvokeExpr()) {
//                    InvokeExpr invMethod = ((Stmt) unit).getInvokeExpr();
//                    if (invokesMenuInflate(invMethod)) {
//                        return unit;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Finds method registration within given method
//     * @param sm The method to look up
//     * @return true if the given method registers a menu
//     */
//    private boolean findMenuRegistration(SootMethod sm, SootClass mainClass, Screen screen,
//                                         Set<MethodOrMethodContext> callbacks) {
//        for (Unit u : sm.retrieveActiveBody().getUnits()) {
//            Stmt stmt = (Stmt) u;
//            if (stmt.containsInvokeExpr()) {
//                InvokeExpr inv = stmt.getInvokeExpr();
//                // if it invokes setContentView or inflate
//                if (invokesMenuInflate(inv)) {
//                    Argument arg = extractIntArgumentFrom(inv);
//                    Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, u, null);
//                    if (values!=null && !values.isEmpty()) {
//                        Object value = values.iterator().next();
//                        if (value instanceof Integer) {
//                            Menu menu = App.v().getLayoutManager().getMenu((int) value);
//                            menu.addCallbackMethods(callbacks);
//                            menu.setParentClass(mainClass);
//                            Screen newScreen = new Screen(screen);
//                            newScreen.setMenu(menu);
//                            SSTG.v().addScreen(newScreen);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//
//    /**
//     * Finds all system callback methods
//     * @param fragment The fragment class
//     */
//    private void collectMenuMethods(Fragment fragment) {
//        // Get the list of menu methods
//
//
//        // The list of menu methods (in order of create and callback)
//        menuCreateMethods.iterator().forEachRemaining(x -> {
//            MethodOrMethodContext method = findAndAddMethod(x, fragment);
//            if (method!=null)
//                fragment.addMenuRegistrationMethod(method);
//        });
//
//        menuCallbackMethods.iterator().forEachRemaining(x -> {
//            MethodOrMethodContext method = findAndAddMethod(x, fragment);
//            if (method!=null)
//                fragment.addMenuCallbackMethod(method);
//        });
//    }
//
//    /**
//     * Checks whether the method assigns a resource id to the activity
//     * @param method The method to check for resource id
//     * @param activity The activity
//     */
//    private void analyzeLayout(SootMethod method, Activity activity, Set<List<Edge>> edges) {
//        for (Unit u : method.retrieveActiveBody().getUnits()) {
//            Stmt stmt = (Stmt) u;
//            if (stmt.containsInvokeExpr()) {
//                InvokeExpr inv = stmt.getInvokeExpr();
//                // if it invokes setContentView or inflate
//                if (invokesSetContentView(inv) || invokesInflate(inv)) {
//                    Argument arg = extractIntArgumentFrom(inv);
//                    Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, u, edges);
//                    if (values.size()==1) {
//                        Object value = values.iterator().next();
//                        if (value instanceof Integer)
//                            activity.setResourceId((int)value);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Analyze the methods for fragment transaction
//     * @param method The reachable method from the lifecycle method
//     * @param edges The edges for context sensitive point-to analysis
//     * @param activity The activity
//     */
//    private Set<SootClass> analyzeFragmentTransaction(SootMethod method, Set<List<Edge>> edges, Activity activity) {
//        if (AndroidClass.v().scFragment == null || AndroidClass.v().scFragmentTransaction == null) {
//            if (AndroidClass.v().scSupportFragment == null || AndroidClass.v().scSupportFragmentTransaction == null) {
//                Logger.warn("[{}] Soot classes have not been initialized!", TAG);
//                return Collections.emptySet();
//            }
//        }
//
//        Set<SootClass> fragmentClasses = new HashSet<>();
//
//        // first check if there is a Fragment manager, a fragment transaction
//        // and a call to the add method which adds the fragment to the transaction
//        boolean isFragmentManager = false;
//        boolean isFragmentTransaction = false;
//        boolean isAddTransaction = false;
//        boolean isReplaceTransaction = false;
//        boolean isRemoveTransaction = false;
//
//        // Check if the parameter is Fragment or Fragment Manager
//        for (Type type : method.getParameterTypes()){
//            if (Scene.v().getOrMakeFastHierarchy().canStoreType(type, AndroidClass.v().scFragmentManager.getType()) ||
//                    Scene.v().getOrMakeFastHierarchy().canStoreType(type, AndroidClass.v().scSupportFragmentManager.getType())) {
//                isFragmentManager = true;
//            }
//        }
//
//        if (!method.hasActiveBody() || !method.isConcrete())
//            return Collections.emptySet();
//
//        for (Unit u : method.getActiveBody().getUnits()) {
//            Stmt stmt = (Stmt) u;
//            if (stmt.containsInvokeExpr()) {
//                final String methodName = stmt.getInvokeExpr().getMethod().getName();
//                switch (methodName) {
//                    case "getFragmentManager":
//                        isFragmentManager = true;
//                        break;
//                    case "beginTransaction":
//                        isFragmentTransaction = true;
//                        break;
//                    case "add":
//                    case "attach":
//                    case "show":
//                        isAddTransaction = true;
//                        break;
//                    case "replace":
//                        isReplaceTransaction = true;
//                        break;
//                    case "remove":
//                    case "detach":
//                    case "hide":
//                        isRemoveTransaction = true;
//                        break;
//                }
//            }
//        }
//
//        // now get the fragment class from the second argument of the add method
//        // from the transaction
//        if (isFragmentManager && isFragmentTransaction && (isAddTransaction || isReplaceTransaction
//                || isRemoveTransaction)) {
//            for (Unit u : method.getActiveBody().getUnits()) {
//                Stmt stmt = (Stmt) u;
//                if (stmt.containsInvokeExpr()) {
//                    InvokeExpr invExpr = stmt.getInvokeExpr();
//                    if (invExpr instanceof InstanceInvokeExpr) {
//                        InstanceInvokeExpr iinvExpr = (InstanceInvokeExpr) invExpr;
//
//                        // Make sure that we referring to the correct class and method
//                        isFragmentTransaction = AndroidClass.v().scFragmentTransaction != null && Scene.v().getFastHierarchy()
//                                .canStoreType(iinvExpr.getBase().getType(), AndroidClass.v().scFragmentTransaction.getType());
//                        isFragmentTransaction |= AndroidClass.v().scSupportFragmentTransaction != null && Scene.v().getFastHierarchy()
//                                .canStoreType(iinvExpr.getBase().getType(), AndroidClass.v().scSupportFragmentTransaction.getType());
//                        isAddTransaction = stmt.getInvokeExpr().getMethod().getName().equals("add")
//                                || stmt.getInvokeExpr().getMethod().getName().equals("attach")
//                                || stmt.getInvokeExpr().getMethod().getName().equals("show");
//                        isReplaceTransaction = stmt.getInvokeExpr().getMethod().getName().equals("replace");
//                        isRemoveTransaction = stmt.getInvokeExpr().getMethod().getName().equals("remove")
//                                || stmt.getInvokeExpr().getMethod().getName().equals("detach")
//                                || stmt.getInvokeExpr().getMethod().getName().equals("hide");
//
//                        // add fragment
//                        if (isFragmentTransaction && (isAddTransaction || isReplaceTransaction || isRemoveTransaction)) {
//                            // We take all fragments passed to the method
//                            for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++) {
//                                Value br = stmt.getInvokeExpr().getArg(i);
//
//                                // Is this a fragment?
//                                if (br.getType() instanceof RefType) {
//                                    RefType rt = (RefType) br.getType();
//
//                                    boolean isParamFragment = AndroidClass.v().scFragment != null
//                                            && Scene.v().getFastHierarchy().canStoreType(rt, AndroidClass.v().scFragment.getType());
//                                    isParamFragment |= AndroidClass.v().scSupportFragment != null && Scene.v().getFastHierarchy()
//                                            .canStoreType(rt, AndroidClass.v().scSupportFragment.getType());
//                                    if (isParamFragment) {
//                                        if (br instanceof Local) {
//                                            for (List<Edge> edgeList : edges) {
//                                                Edge[] edgeContext = new Edge[edgeList.size()];
//                                                edgeContext = edgeList.toArray(edgeContext);
//
//                                                // Reveal the possible types of this fragment
//                                                Set<Type> possibleTypes = TypeAnalysis.v().
//                                                        getContextPointToPossibleTypes(br, edgeContext);
//                                                Type possibleType = null;
//                                                if (possibleTypes.size() < 1) {
//                                                    Logger.warn("[{}] Unable to retrieve point-to info for: {}", TAG, stmt);
//                                                    return Collections.emptySet();
//                                                } else if (possibleTypes.size() == 1) {
//                                                    possibleType = possibleTypes.iterator().next();
//                                                } else {
//                                                    Iterator<Type> typeIterator = possibleTypes.iterator();
//                                                    Type lastType = typeIterator.next();
//                                                    while (typeIterator.hasNext()) {
//                                                        possibleType = typeIterator.next();
//                                                        if (Scene.v().getFastHierarchy().canStoreType(lastType, possibleType)) {
//                                                            possibleType = lastType;
//                                                        } else if (!Scene.v().getFastHierarchy().canStoreType(possibleType, lastType)) {
//                                                            Logger.warn("[{}] Multiple possible fragment types detected in: {}", TAG, stmt);
//                                                        }
//                                                        lastType = possibleType;
//                                                    }
//                                                }
//                                                if (possibleType instanceof RefType) {
//                                                    if (isAddTransaction) {
//                                                        fragmentClasses.add(((RefType) possibleType).getSootClass());
//                                                    } else if (isRemoveTransaction) {
//                                                        fragmentClasses.remove(((RefType) possibleType).getSootClass());
//                                                    } else if (isReplaceTransaction) {
//                                                        fragmentClasses.add(((RefType) possibleType).getSootClass());
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return fragmentClasses;
//    }
//
//    //    /**
////     * Analyze the menu drawer registration
////     * @param screen The screen node
////     * @return true if the screen declares a menu
////     */
////    private boolean analyzeMenuMethods(Screen screen) {
////        Activity containerActivity = screen.getContainerActivity();
////        Set<Fragment> fragments = screen.getFragments();
////
////        // Collects all menu registration methods and menu callback methods
////        if (containerActivity.getMenuOnCreateMethods() != null &&
////                !containerActivity.getMenuOnCreateMethods().isEmpty()) {
////            if (containerActivity.getMenuCallbackMethods() != null &&
////                    !containerActivity.getMenuCallbackMethods().isEmpty()) {
////                return analyzeMenuDrawerRegistration(containerActivity.getMainClass(), containerActivity.getMenuOnCreateMethods()
////                        , screen, containerActivity.getMenuCallbackMethods());
////            } else {
////                Logger.warn("[{}] Menu registration found without callbacks to handle click events: {}",
////                        TAG, containerActivity.getName());
////            }
////        }
////
////        for (Fragment fragment : fragments) {
////            if (fragment.getMenuOnCreateMethods() != null &&
////                    !fragment.getMenuOnCreateMethods().isEmpty()) {
////                if (fragment.getMenuCallbackMethods() != null &&
////                        !fragment.getMenuCallbackMethods().isEmpty()) {
////                    return analyzeMenuDrawerRegistration(fragment.getMainClass(), fragment.getMenuOnCreateMethods(), screen,
////                            fragment.getMenuCallbackMethods());
////                } else {
////                    Logger.warn("[{}] Menu registration found without callbacks to handle click events: {}",
////                            TAG, fragment.getName());
////                }
////            }
////        }
////        return false;
////    }
//
//    /**
//     * Registers propagation analysis
//     */
//    private void registerPropagationAnalysis() {
//        AnalysisParameters.v().setIcfg(new PropagationIcfg());
//
//        // Register field analysis
//        FieldTransformerManager.v().registerDefaultFieldTransformerFactories();
//
//        // Register value analysis
//        ArgumentValueManager.v().registerDefaultArgumentValueAnalyses();
//
//        // Register method return value analysis
//        MethodReturnValueManager.v().registerDefaultMethodReturnValueAnalyses();
//
//        // Add application classes (other classes will be ignored during the propagation)
//        Set<String> analysisClasses = new HashSet<>();
//        Scene.v().getApplicationClasses().snapshotIterator().forEachRemaining(x -> {
//            String className = x.getName();
//            if (!SystemClassHandler.isClassInSystemPackage(className)) {
//                analysisClasses.add(className);
//            }
//        });
//        AnalysisParameters.v().addAnalysisClasses(analysisClasses);
//    }
//}
