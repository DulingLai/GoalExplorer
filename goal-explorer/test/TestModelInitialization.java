import android.goal.explorer.STGExtractor;
import android.goal.explorer.cmdline.CmdLineParser;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.BroadcastReceiver;
import android.goal.explorer.model.component.Service;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.pmw.tinylog.Logger;
import soot.Scene;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestModelInitialization {
    @Test
    public void runTest(){

        Collection<File> files = FileUtils.listFiles(new File(TestConstants.BENCHMARK_DIR), new String[]{"apk"}, true);

        List<File> fileList = new ArrayList<>(files);

        Collections.sort(fileList);

        for (File file : fileList) {
            Logger.info("Working on: {}", file.getName());
            String[] args = TestConfig. getConfigForTest(file.getAbsolutePath());
            Integer numActivity = 0;
            Integer numService = 0;
            Integer numReceiver = 0;
            ProcessManifest manifest = null;

            try {
                GlobalConfig config = CmdLineParser.parse(args);

                STGExtractor extractor = new STGExtractor(config);
                extractor.initialize();

                // update numbers
                numActivity = extractor.getNumActInManifest();
                numService = extractor.getNumServiceInManifest();
                numReceiver = extractor.getNumReceiverInManifest();
                manifest = extractor.getManifest();
            } catch (Exception e) {
                Logger.error("Failed: {}", e.getMessage());
            }

            // assertions
            App instance = App.v();
            assert Scene.v().getCallGraph()!=null;
            assert Scene.v().getCallGraph().size()>0;
            assert instance!=null;
            assert instance.getActivities()!=null;
            assert manifest != null;
            // Check for activities
            if (instance.getActivities().size() < numActivity) {
                for (AXmlNode activityNode : manifest.getActivities()) {
                    // Only check for enabled components
                    AXmlAttribute<?> attrEnabled = activityNode.getAttribute("enabled");
                    if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
                        Activity activity = instance.getActivityByName((String) activityNode.
                                getAttribute("name").getValue());
                        assert activity != null;
                    }
                }
            } else if (instance.getActivities().size() > numActivity) {
                for (Activity activity : instance.getActivities()) {
                    AXmlNode activityNode = manifest.getActivity(activity.getName());
                    assert activityNode != null;
                }
            }

            // Check for services
            if (instance.getServices().size() < numService) {
                for (AXmlNode serviceNode : manifest.getServices()) {
                    AXmlAttribute<?> attrEnabled = serviceNode.getAttribute("enabled");
                    if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
                        Service service = instance.getServiceByName((String) serviceNode.
                                getAttribute("name").getValue());
                        assert service != null;
                    }
                }
            } else if (instance.getServices().size() > numService) {
                for (Service service : instance.getServices()) {
                    AXmlNode serviceNode = manifest.getService(service.getName());
                    assert serviceNode != null;
                }
            }

            // Check for broadcast receivers
            if (instance.getBroadcastReceivers().size() < numReceiver) {
                for (AXmlNode receiverNode : manifest.getReceivers()) {
                    AXmlAttribute<?> attrEnabled = receiverNode.getAttribute("enabled");
                    if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
                        BroadcastReceiver receiver = instance.getReceiverByName((String) receiverNode.
                                getAttribute("name").getValue());
                        assert receiver != null;
                    }
                }
            } else if (instance.getBroadcastReceivers().size() > numReceiver) {
                for (BroadcastReceiver receiver : instance.getBroadcastReceivers()) {
                    AXmlNode receiverNode = manifest.getReceiver(receiver.getName());
                    assert receiverNode != null;
                }
            }

//            // Check for services
//            if (instance.getServices().size() != numService) {
//                for (AXmlNode serviceNode : manifest.getServices()) {
//                    boolean found = false;
//                    for (Service service : instance.getServices()) {
//                        String nodeName = (String) serviceNode.getAttribute("name").getValue();
//                        // check if the node name starts with ".", and trim it
//                        if (nodeName.startsWith("."))
//                            nodeName = nodeName.substring(1);
//
//                        // check if we have found the service in the manifest
//                        if (service.getName().equals(nodeName) || service.getShortName().equals(nodeName))
//                            found = true;
//                    }
//                    assert found;
//                }
//            }
//
//            // check for broadcast receivers
//            if (instance.getBroadcastReceivers().size() != numReceiver) {
//                for (AXmlNode receiverNode : manifest.getReceivers()) {
//                    boolean found = false;
//                    for (BroadcastReceiver receiver : instance.getBroadcastReceivers()) {
//                        String nodeName = (String) receiverNode.getAttribute("name").getValue();
//                        // check if the node name starts with ".", and trim it
//                        if (nodeName.startsWith("."))
//                            nodeName = nodeName.substring(1);
//
//                        // check if we have found the service in the manifest
//                        if (receiver.getName().equals(nodeName) || receiver.getShortName().equals(nodeName))
//                            found = true;
//                    }
//                    assert found;
//                }
//            }
//
//            assert instance.getServices().size() == numService;
//            assert instance.getBroadcastReceivers().size() == numReceiver;
            Logger.info("Test passed for: {}", file.getName());
        }
    }
}
