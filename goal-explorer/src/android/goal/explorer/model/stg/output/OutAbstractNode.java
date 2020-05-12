package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.component.AbstractComponent;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Node")
public class OutAbstractNode {
    private String name;

    public OutAbstractNode(String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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

        OutAbstractNode other = (OutAbstractNode) obj;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public String toString() {
        return name;
    }
}
