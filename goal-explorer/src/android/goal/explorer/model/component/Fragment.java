package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.Listener;
import android.goal.explorer.model.widget.AbstractWidget;
import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.jimple.infoflow.android.callbacks.ComponentReachableMethods;

import java.util.*;

public class Fragment extends AbstractComponent{

    private Set<Listener> listeners;
    private Set<AbstractWidget> widgets;

    private Integer resourceId;
    private Set<Activity> parentActivities;

    private LinkedList<MethodOrMethodContext> lifecycleMethods;
    private Set<MethodOrMethodContext> menuRegistrationMethods;
    private Set<MethodOrMethodContext> menuCallbackMethods;
    // reachable methods
    private LinkedHashMap<MethodOrMethodContext, ComponentReachableMethods> lifecycleReachableMethods;

    public Fragment(SootClass sootClass, Activity parentActivity) {
        super(sootClass.getName(), sootClass);
        this.parentActivities = new HashSet<>(Collections.singletonList(parentActivity));
        this.lifecycleMethods = new LinkedList<>();
        this.menuRegistrationMethods = new HashSet<>();
        this.menuCallbackMethods = new HashSet<>();
        this.lifecycleReachableMethods = new LinkedHashMap<>();
    }

    public Fragment(SootClass sootClass) {
        super(sootClass.getName(), sootClass);
    }

    public Fragment(SootClass sootClass, Integer resourceId) {
        super(sootClass.getName(), sootClass);
        this.resourceId = resourceId;
    }

    /*
    Getters and setters
     */

    public Set<Listener> getListeners() {
        return listeners;
    }

    public Set<AbstractWidget> getWidgets() {
        return widgets;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public Set<Activity> getParentActivities() {
        return parentActivities;
    }

    /**
     * Adds a listener to the fragment
     * @param listener The listener to be added
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    /**
     * Adds a widget to the fragment
     * @param widget The widget to be added
     */
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    /**
     * Sets the resource id of the fragment
     * @param resourceId The resource id of the fragment
     */
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Adds the parent activity to the fragment. This method adds another activity
     * @param parentActivity The parent activity
     */
    public void AddParentActivity(Activity parentActivity) {
        this.parentActivities.add(parentActivity);
    }

    /**
     * Gets the lifecycle methods of this fragment
     * @return The lifecycle methods
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethods() {
        return lifecycleMethods;
    }

    /**
     * Adds the lifecycle methods to this fragment
     * @param lifecycleMethods The lifecycle methods to be set
     */
    public void addLifecycleMethods(LinkedList<MethodOrMethodContext> lifecycleMethods) {
        this.lifecycleMethods.addAll(lifecycleMethods);
    }

    /**
     * Adds a lifecycle method to this fragment
     * @param lifecycleMethod The lifecycle method to be added
     */
    public void addLifecycleMethod(MethodOrMethodContext lifecycleMethod) {
        this.lifecycleMethods.add(lifecycleMethod);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuRegistrationMethods() {
        return menuRegistrationMethods;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuRegistrationMethods(Set<MethodOrMethodContext> menuMethods) {
        this.menuRegistrationMethods.addAll(menuMethods);
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuRegistrationMethod(MethodOrMethodContext menuMethod) {
        this.menuRegistrationMethods.add(menuMethod);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuCallbackMethods() {
        return menuCallbackMethods;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuCallbackMethods(Set<MethodOrMethodContext> menuMethods) {
        this.menuCallbackMethods.addAll(menuMethods);
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuCallbackMethod(MethodOrMethodContext menuMethod) {
        this.menuCallbackMethods.add(menuMethod);
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


    @Override
    public String toString(){
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((listeners == null) ? 0 : listeners.hashCode());
        result = prime * result + ((widgets == null) ? 0 : widgets.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        Fragment other = (Fragment) obj;

        if (listeners == null) {
            if (other.listeners != null)
                return false;
        } else if (!listeners.equals(other.listeners))
            return false;

        if (parentActivities == null) {
            if (other.parentActivities != null)
                return false;
        } else if (!parentActivities.equals(other.parentActivities))
            return false;

        if (widgets == null) {
            if (other.widgets != null)
                return false;
        } else if (!widgets.equals(other.widgets))
            return false;

        return resourceId.equals(other.resourceId);
    }
}
