package org.jpos.jposext.jposworkflow.eclipse;

import java.util.List;
import java.util.Map;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrConfigParserImpl;


public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchAdvisor#postStartup()
     */
    @Override
    public void postStartup() {
        try {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            TxnMgrConfigParserImpl txnMgrConfigParserImpl = new TxnMgrConfigParserImpl();
            //Map<String, List<ParticipantInfo>> parsed = txnMgrConfigParserImpl.parse(this.getClass().getResource("/org/jpos/jposext/jposworkflow/sample/cfg/deploy/txMgrConfigWithTransitions.xml"));            
            Map<String, List<ParticipantInfo>> parsed = txnMgrConfigParserImpl.parse(this.getClass().getResource("/org/jpos/jposext/jposworkflow/sample/cfg/deploy/Financial.inc"));
            page.openEditor(new MyEditorInput("JPosWorkFlowGraph", parsed), MyGraphicalEditor.ID, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String PERSPECTIVE_ID = "org.jpos.jposext.jposworkflow.eclipse.perspective";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }
}
