package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.IntentFilter;
import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;
import soot.jimple.infoflow.android.callbacks.ComponentReachableMethods;

import java.util.*;

import static android.goal.explorer.utils.AxmlUtils.processNodeName;

public class AbstractComponent {
    private String name;
    private SootClass mainClass;
    private Set<SootClass> addedClasses;
    private Set<CallbackDefinition> callbacks;

    private LinkedList<MethodOrMethodContext> lifecycleMethods;
    private LinkedHashMap<MethodOrMethodContext, ComponentReachableMethods> lifecycleReachableMethods;

    public AbstractComponent(String name) {
        this.name = name;
    }

    public AbstractComponent(AXmlNode node, String packageName) {
        this.name = processNodeName(node, packageName);
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
        lifecycleReachableMethods = new LinkedHashMap<>();
    }

    public AbstractComponent(AXmlNode node, SootClass sc, String packageName) {
        this.name = processNodeName(node, packageName);
        this.mainClass = sc;
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
        lifecycleReachableMethods = new LinkedHashMap<>();
    }

    public AbstractComponent(String name, SootClass sc) {
        this.name = name;
        this.mainClass = sc;
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
        lifecycleReachableMethods = new LinkedHashMap<>();
    }

    /**
     * Creates the intent filters from the intent filter string
     * @param action list of action intent filters
     * @param category list of category intent filters
     * @return Set of IntentFilters
     */
    Set<IntentFilter> createIntentFilters(List<String> action, List<String> category) {
        Set<IntentFilter> intentFilters = new HashSet<>();
        if (action != null && !action.isEmpty()) {
            for (String actionFilter : action) {
                intentFilters.add(new IntentFilter(actionFilter, IntentFilter.Type.Action));
            }
        }

        if (category != null && !category.isEmpty()) {
            for (String categoryFilter : category) {
                intentFilters.add(new IntentFilter(categoryFilter, IntentFilter.Type.Category));
            }
        }
        return intentFilters;
    }

    /* =======================================
              Getters and setters
     =========================================*/
    /**
     * Gets the name of this component
     * @return The name of this component
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component
     * @param name The component name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the parse SootClass of this component
     * @return The parse SootClass
     */
    public SootClass getMainClass() {
        return mainClass;
    }

    /**
     * Sets the parse SootClass of this component
     * @param mainClass The parse soot class
     */
    public void setMainClass(SootClass mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Gets the added SootClasses of this component
     * @return The added SootClasses of this component
     */
    public Set<SootClass> getAddedClasses() {
        return addedClasses;
    }

    /**
     * Adds the added SootClasses to this component
     * @param addedClasses The added SootClasses
     */
    public void addAddedClasses(Set<SootClass> addedClasses) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.addAll(addedClasses);
    }

    /**
     * Adds the added SootClass of this component
     * @param addedClass The added SootClasses
     */
    public void addAddedClass(SootClass addedClass) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.add(addedClass);
    }

    /**
     * Gets the lifecycle methods of this activity
     * @return The lifecycle methods
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethods() {
        return lifecycleMethods;
    }

    /**
     * Adds the lifecycle methods to this activity
     * @param lifecycleMethods The lifecycle methods to be set
     */
    public void addLifecycleMethods(LinkedList<MethodOrMethodContext> lifecycleMethods) {
        this.lifecycleMethods.addAll(lifecycleMethods);
    }

    /**
     * Adds a lifecycle method to this activity
     * @param lifecycleMethod The lifecycle method to be added
     */
    public void addLifecycleMethod(MethodOrMethodContext lifecycleMethod) {
        this.lifecycleMethods.add(lifecycleMethod);
    }

    /**
     * Adds reachable methods
     * @param method The lifecycle method
     * @param rm The reachable methods
     */
    public void addLifecycleReachableMethods(MethodOrMethodContext method, ComponentReachableMethods rm) {
        lifecycleReachableMethods.put(method, rm);
    }

    /**
     * Gets the reachable methods from lifecycle method
     * @param method The lifecycle method
     * @return The reachable methods
     */
    public ComponentReachableMethods getLifecycleReachableMethodsFrom(MethodOrMethodContext method) {
        return lifecycleReachableMethods.get(method);
    }

    /**
     * Gets the reachable methods map
     * @return The reachable methods mapped to lifecycle methods
     */
    public LinkedHashMap<MethodOrMethodContext, ComponentReachableMethods> getLifecycleReachableMethodsMap() {
        return lifecycleReachableMethods;
    }

    /**
     * Gets the callback definitions in the current component
     * @return The callback definitions
     */
    public Set<CallbackDefinition> getCallbacks() {
        if (callbacks!=null & !callbacks.isEmpty()){
            return callbacks;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Adds the callback definition to the current component
     * @return true if we have successfully added the callbacks
     */
    public boolean addCallbacks(Set<CallbackDefinition> callbacks) {
        return this.callbacks.addAll(callbacks);
    }

    /**
     * Adds the callback definition to the current component
     * @return true if we have successfully added the callbacks
     */
    public boolean addCallback(CallbackDefinition callback) {
        return this.callbacks.addAll(Collections.singleton(callback));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((mainClass == null) ? 0 : mainClass.hashCode());
        result = prime * result + ((addedClasses == null) ? 0 : addedClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        AbstractComponent other = (AbstractComponent) obj;
        if (mainClass == null) {
            if (other.mainClass != null)
                return false;
        } else if (!mainClass.equals(other.mainClass))
            return false;
        if (addedClasses == null) {
            if (other.addedClasses != null)
                return false;
        } else if (!addedClasses.equals(other.addedClasses))
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public String toString() {
        return name;
    }
}
