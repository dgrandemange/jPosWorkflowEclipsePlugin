package org.jpos.jposext.jposworkflow.eclipse;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author dgrandemange
 *
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.application.WorkbenchAdvisor#postStartup()
     */
    @Override
    public void postStartup() {
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
