package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.STG;
import android.goal.explorer.model.stg.edge.TransitionEdge;
import android.goal.explorer.model.stg.node.BroadcastReceiverNode;
import android.goal.explorer.model.stg.node.ScreenNode;
import android.goal.explorer.model.stg.node.ServiceNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XStreamAlias("ScreenTransitionGraph")
public class OutSTG {
    private List<OutTransitionEdge> transitionEdges;
    private Set<OutScreenNode> screenNodeSet;
    private Set<OutServiceNode> serviceNodeSet;
    private Set<OutBroadcastReceiverNode> broadcastReceiverNodeSet;

    public OutSTG(STG stg) {
        transitionEdges = new ArrayList<>();
        screenNodeSet = new HashSet<>();
        serviceNodeSet = new HashSet<>();
        broadcastReceiverNodeSet = new HashSet<>();

        for (TransitionEdge edge : stg.getTransitionEdges()) {
            if (edge.getSrcNode() == null || edge.getTgtNode() == null) {
                continue;
            }
            transitionEdges.add(new OutTransitionEdge(edge));
        }
        for (ScreenNode node : stg.getAllScreens()) {
            screenNodeSet.add((OutScreenNode) ConvertToOutput.convertNode(node));
        }
        for (ServiceNode node : stg.getAllServices()) {
            serviceNodeSet.add((OutServiceNode) ConvertToOutput.convertNode(node));
        }
        for (BroadcastReceiverNode node : stg.getAllBroadcastReceivers()) {
            broadcastReceiverNodeSet.add((OutBroadcastReceiverNode) ConvertToOutput.convertNode(node));
        }
    }
}
