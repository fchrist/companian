package de.fct.companian.model.uml;

import junit.framework.Assert;
import junit.framework.TestCase;
import de.fct.companian.model.binding.umlact.UMLActBehaviorDlProxy;
import de.fct.fdmm.architecturedl.behaviordl.EndStep;
import de.fct.fdmm.architecturedl.behaviordl.HotSpotGroupCall;
import de.fct.fdmm.architecturedl.behaviordl.Process;
import de.fct.fdmm.architecturedl.behaviordl.StartStep;
import de.fct.fdmm.architecturedl.behaviordl.Step;
import de.fct.fdmm.architecturedl.behaviordl.SubProcess;

public class UMLActivityTest extends TestCase {

    private static final String[] TEST_FILES = {"../testdata/activitydiagrams/activity-test-1/activity-test-1.uml"};

    public void testLoadViaProxy() {
        for (String file : TEST_FILES) {
            UMLActBehaviorDlProxy proxy = new UMLActBehaviorDlProxy();
            Process p = proxy.getProcess(file);
            Assert.assertNotNull(p);            
        }
    }
    
    public void testTraverse() {
        for (String file : TEST_FILES) {
            UMLActBehaviorDlProxy proxy = new UMLActBehaviorDlProxy();
            Process p = proxy.getProcess(file);
            Assert.assertNotNull(p); 
            assertNotNull(p.getName());
            System.out.println("Process " + p.getName());
            assertNotNull(p.getSteps());
            for (Step s : p.getSteps()) {
                if (s instanceof StartStep) {
                    System.out.println("starting at " + s.getName());
                    assertNotNull(s.getNextStep());
                    traverseStep(s.getNextStep(), "");
                    break;
                }
            }
        }
    }
    
    private void traverseStep(Step s, String prefix) {
        assertNotNull(s.getName());
        if (!(s instanceof EndStep)) {
            if (s instanceof SubProcess) {
                System.out.print(prefix + "branching sub process in " + s.getName());
                prefix += "  ";
                Process subProcess = ((SubProcess) s).getSubProcess();
                assertNotNull(subProcess);
                assertNotNull(subProcess.getName());
                System.out.println(" calling " + subProcess.getName());
                for (Step ss : subProcess.getSteps()) {
                    if (ss instanceof StartStep) {
                        System.out.println(prefix + "starting sub process at " + ss.getName());
                        assertNotNull(ss.getNextStep());
                        traverseStep(ss.getNextStep(), prefix);
                        break;
                    }
                }
                prefix = prefix.substring(0, prefix.length() - 2);
            }
            else if (s instanceof HotSpotGroupCall) {
                HotSpotGroupCall hsgc = (HotSpotGroupCall)s;
                assertNotNull(hsgc.getHotSpotGroups());
                for (String hsg : hsgc.getHotSpotGroups()) {
                    System.out.println(prefix + "calling hot spot group " + hsg);
                }
            }
            else {
                System.out.println(prefix + "visiting " + s.getName());
                assertNotNull(s.getNextStep());
            }
            traverseStep(s.getNextStep(), prefix);
        }
        else {
            assertNull(s.getNextStep());
            System.out.println(prefix + "ending at " + s.getName());
        }
    }

    
}
