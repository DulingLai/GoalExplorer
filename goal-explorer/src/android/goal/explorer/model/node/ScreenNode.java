package android.goal.explorer.model.node;

import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.entity.AbstractEntity;

import java.util.HashSet;
import java.util.Set;

public class ScreenNode extends AbstractNode {

    private Set<Fragment> fragments;
    private AbstractEntity abstractEntity;

    public ScreenNode(Activity activity) {
        super(activity);
        fragments = new HashSet<>();
    }

    /**
     * A copy constructor
     * @param toClone The screen to clone
     */
    public ScreenNode(ScreenNode toClone) {
        super(toClone.getComponent());
        if (toClone.getFragments()!=null && !toClone.getFragments().isEmpty()) {
            fragments = toClone.getFragments();
        } else {
            fragments = new HashSet<>();
        }
        abstractEntity = toClone.getAbstractEntity();
    }

    /**
     * Clone the screen
     * @param origScreenNode The original screen
     * @return The new screen which is a copy of the original screen
     */
    public ScreenNode clone(ScreenNode origScreenNode){
        ScreenNode screenNode = new ScreenNode((Activity) origScreenNode.getComponent());
        screenNode.addFragments(origScreenNode.fragments);
        if (origScreenNode.abstractEntity !=null)
            screenNode.setAbstractEntity(origScreenNode.abstractEntity);
        return screenNode;
    }

    /**
     * Gets the fragments
     * @return The fragments
     */
    public Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Adds a set of fragments to the activity
     * @param fragments The set of fragments to be added
     */
    public void addFragments(Set<Fragment> fragments) {
        this.fragments.addAll(fragments);
    }

    /**
     * Adds a fragment to the activity
     * @param fragment The fragment to be added
     */
    public void addFragment(Fragment fragment) {
        this.fragments.add(fragment);
    }

    /**
     * Gets the entity of this screen node
     * @return The entity
     */
    public AbstractEntity getAbstractEntity() {
        return abstractEntity;
    }

    /**
     * Sets the entity of this screen node
     * @param abstractEntity The entity
     */
    public void setAbstractEntity(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    @Override
    public String toString(){
        return getComponent().getName() + " fragments: " + fragments + " entity: " + abstractEntity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fragments == null) ? 0 : fragments.hashCode());
        result = prime * result + ((abstractEntity == null) ? 0 : abstractEntity.hashCode());
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

        ScreenNode other = (ScreenNode) obj;

        if (fragments == null) {
            if (other.fragments != null)
                return false;
        } else if (!fragments.equals(other.fragments))
            return false;

        if (abstractEntity == null) {
            return other.abstractEntity == null;
        } else return abstractEntity.equals(other.abstractEntity);
    }
}
