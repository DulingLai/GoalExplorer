package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.IntentFilter;
import android.goal.explorer.model.entity.Listener;
import android.goal.explorer.model.widget.AbstractWidget;
import android.goal.explorer.utils.AxmlUtils;
import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.callbacks.ComponentReachableMethods;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class Activity extends AbstractComponent {

    private Set<MethodOrMethodContext> menuOnCreateMethods;
    private Set<MethodOrMethodContext> menuCallbackMethods;

    // reachable methods
    private LinkedHashMap<MethodOrMethodContext, ComponentReachableMethods> menuReachableMethods;
    private LinkedHashMap<MethodOrMethodContext, ComponentReachableMethods> callbackReachableMethods;

    private Set<IntentFilter> intentFilters;
    private Set<Listener> listeners;
    private Set<Fragment> fragments;
    private Set<AbstractWidget> widgets;

    private Integer resourceId;
    private Integer mainXmlLayoutResId;
    private Set<Integer> addedXmlLayoutResId;
    private String alias = null;

    private String parentCompString;
    private AbstractComponent parentComp;
    private Set<String> childCompStrings;
    private Set<AbstractComponent> childComps;

    private boolean isExported = false;
    private boolean containLogin = false;

    public Activity(AXmlNode node, SootClass sc, String packageName) {
        super(node, sc, packageName);

        this.intentFilters = createIntentFilters(AxmlUtils.processIntentFilter(node, IntentFilter.Type.Action),
                AxmlUtils.processIntentFilter(node, IntentFilter.Type.Category));

        parentCompString = AxmlUtils.processNodeParent(node, packageName);
        isExported = AxmlUtils.processNodeExported(node);
        resourceId = -1;

        menuOnCreateMethods = new HashSet<>();
        menuCallbackMethods = new HashSet<>();

        listeners = new HashSet<>();
        fragments = new HashSet<>();
        widgets = new HashSet<>();
        childCompStrings = new HashSet<>();
        childComps = new HashSet<>();
    }

    /* ========================================
                Getters and setters
       ========================================*/

    /**
     * Gets the resource id of this activity
     * @return The resource id of the activity
     */
    public Integer getResourceId(){ return resourceId; }

    /**
     * Sets the resource id of this activity
     * @param resourceId The resource id of the activity
     */
    public void setResourceId(Integer resourceId){ this.resourceId = resourceId; }

    /**
     * Gets the intent filters of this activity
     * @return The intent filters
     */
    public Set<IntentFilter> getIntentFilters() {
        return intentFilters;
    }

    /**
     * Adds a new intent filter to this activity
     * @param intentFilter The intent filter to be added
     */
    public void addIntentFilter(IntentFilter intentFilter) {
        this.intentFilters.add(intentFilter);
    }

    /**
     * Adds new intent filter to this activity
     * @param intentFilters The intent filters to be added
     */
    public void addIntentFilters(Set<IntentFilter> intentFilters) {
        this.intentFilters.addAll(intentFilters);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuOnCreateMethods() {
        return menuOnCreateMethods;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuOnCreateMethods(Set<MethodOrMethodContext> menuMethods) {
        this.menuOnCreateMethods.addAll(menuMethods);
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuOnCreateMethod(MethodOrMethodContext menuMethod) {
        this.menuOnCreateMethods.add(menuMethod);
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

    // TODO check if we still need this flag
    public boolean isContainLogin() {
        return containLogin;
    }

    // TODO check if we still need this flag
    public void setContainLogin(boolean containLogin) {
        this.containLogin = containLogin;
    }

    /**
     * Gets the resource id of the main XML layout file of this activity
     * @return The resource id of the main XML layout file of this activity
     */
    public Integer getMainXmlLayoutResId() {
        return mainXmlLayoutResId;
    }

    /**
     * Sets the resource id of the main XML layout file of this activity
     * @param mainXmlLayoutResId The resource id of the main XML layout file of this activity
     */
    public void setMainXmlLayoutResId(Integer mainXmlLayoutResId) {
        this.mainXmlLayoutResId = mainXmlLayoutResId;
    }

    /**
     * Gets the resource ids of the added XML layout file of this activity
     * @return The resource ids of the added XML layout file of this activity
     */
    public Set<Integer> getAddedXmlLayoutResId() {
        return addedXmlLayoutResId;
    }

    /**
     * Sets the resource id of the added XML layout file of this activity
     * @param addedXmlLayoutResId The resource id of the added XML layout file of this activity
     */
    public void setAddedXmlLayoutResId(Set<Integer> addedXmlLayoutResId) {
        this.addedXmlLayoutResId = addedXmlLayoutResId;
    }

    /**
     * Gets the alias of this activity
     * @return The alias of this activity
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of this activity
     * @param target The alias of this activity
     */
    public void setAlias(String target) {
        alias = target;
    }

    /**
     * Gets all listeners of this activity
     * @return All listeners in this activity
     */
    public Set<Listener> getListeners() {
        return listeners;
    }

    /**
     * Adds new listeners to this activity
     * @param listeners The listeners to be added
     */
    public void addListeners(Set<Listener> listeners) {
        if (this.listeners == null)
            this.listeners = new HashSet<>();
        this.listeners.addAll(listeners);
    }

    /**
     * Adds a new listener to this activity
     * @param listener The listener to be added
     */
    public void addListener(Listener listener) {
        if (this.listeners == null)
            this.listeners = new HashSet<>();
        this.listeners.add(listener);
    }

    /**
     * Gets all fragments implemented in this activity
     * @return All fragments implemented in this activity
     */
    public Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Adds a new fragment to this activity
     * @param fragment The new fragment to be added
     */
    public void addFragment(Fragment fragment) {
        if (this.fragments == null)
            this.fragments = new HashSet<>();
        this.fragments.add(fragment);
    }

    /**
     * Gets all widgets implemented in this activity
     * @return All widgets implemented in this activity
     */
    public Set<AbstractWidget> getWidgets() {
        return widgets;
    }

    /**
     * Gets the parent component name in String
     * @return The parent component name in String
     */
    public String getParentCompString() {
        return this.parentCompString;
    }

    /**
     * Sets the parent component name in String
     * @param parent The parent component name in String
     */
    public void setParentCompString(String parent) {
        this.parentCompString = parent;
    }

    /**
     * Adds a new widget to this activity
     * @param widget The new widget to be added to this activity
     */
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    /**
     * Gets the parent component from manifest
     * @return The parent component from manifest
     */
    public AbstractComponent getParentComp() {
        return parentComp;
    }

    /**
     * Sets the parent component from manifest
     * @param parentComp The parent component from manifest
     */
    public void setParentComp(AbstractComponent parentComp) {
        this.parentComp = parentComp;
    }

    /**
     * Gets the child component (string) from manifest
     * @return The child component (string) from manifest
     */
    public Set<String> getChildCompStrings() {
        return childCompStrings;
    }

    /**
     * Adds a child component (string) from manifest to this activity
     * @param childCompString The child component (string) from manifest to be added
     */
    public void addChildCompString(String childCompString) {
        this.childCompStrings.add(childCompString);
    }

    /**
     * Gets the child component from manifest
     * @return The child component from manifest
     */
    public Set<AbstractComponent> getChildComps() {
        return childComps;
    }

    /**
     * Adds a child component from manifest
     * @param childComp The child component from manifest to be added
     */
    public void addChildComp(AbstractComponent childComp) {
        this.childComps.add(childComp);
    }

    /**
     * If the activity is exported in the manifest
     * @return True if the activity is exported in the manifest
     */
    public boolean isExported() {
        return isExported;
    }

    /**
     * Sets if the activity is exported in the manifest
     * @param  exported If the activity is exported in the manifest
     */
    public void setExported(boolean exported) {
        isExported = exported;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((intentFilters == null) ? 0 : intentFilters.hashCode());
        result = prime * result + ((listeners == null) ? 0 : listeners.hashCode());
        result = prime * result + ((fragments == null) ? 0 : fragments.hashCode());
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

        Activity other = (Activity) obj;

        if (intentFilters == null) {
            if (other.intentFilters != null)
                return false;
        } else if (!intentFilters.equals(other.intentFilters))
            return false;

        if (fragments == null) {
            if (other.fragments != null)
                return false;
        } else if (!fragments.equals(other.fragments))
            return false;

        if (widgets == null) {
            if (other.widgets != null)
                return false;
        } else if (!widgets.equals(other.widgets))
            return false;

        if (!resourceId.equals(other.resourceId))
            return false;
        return getName().equals(other.getName());
    }
}
