package android.goal.explorer.model.edge;

import android.goal.explorer.model.component.AbstractComponent;

public class EdgeTag {

    private AbstractComponent prevComp;

    public EdgeTag(AbstractComponent prevComp) {
        this.prevComp = prevComp;
    }

    public AbstractComponent getPrevComp() {
        return prevComp;
    }
}
