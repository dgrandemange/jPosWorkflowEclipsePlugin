package org.jpos.jposext.jposworkflow.eclipse.popup.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jpos.jposext.jposworkflow.eclipse.MyEditorInput;
import org.jpos.jposext.jposworkflow.eclipse.MyGraphicalEditor;
import org.jpos.jposext.jposworkflow.eclipse.service.support.ContextMgmtInfoPopulatorEclipsePluginImpl;
import org.jpos.jposext.jposworkflow.helper.GraphHelper;
import org.jpos.jposext.jposworkflow.model.EntityRefInfo;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.Node;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.model.SelectCriterion;
import org.jpos.jposext.jposworkflow.model.SubFlowInfo;
import org.jpos.jposext.jposworkflow.service.support.GraphReducerImpl;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrConfigParserImpl;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrGroupsConverterImpl;

/**
 * @author dgrandemange
 * 
 */
public class ViewGraphNotExpanded implements IObjectActionDelegate {

	private static final String ROOT_KEY = "<root>";

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public ViewGraphNotExpanded() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		/*
		 * for (Iterator it= structuredSelection.iterator();it.hasNext();) {
		 * Object iterated = it.next(); MessageBox msgBox = new MessageBox(new
		 * Shell(), SWT.OK | SWT.ICON_INFORMATION);
		 * msgBox.setMessage(iterated.getClass().getSimpleName());
		 * msgBox.open(); }
		 */
		IResource iresource = (IResource) structuredSelection.getFirstElement();

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IFile selectedFile = (IFile) iresource;
		IProject project = selectedFile.getProject();

		Graph rootGraph = null;
		Map<String, Graph> graphByEntityRef = new HashMap<String, Graph>();

		TxnMgrConfigParserImpl txnMgrConfigParserImpl = new TxnMgrConfigParserImpl();
		txnMgrConfigParserImpl.setGraphByEntityRef(graphByEntityRef);
		TxnMgrGroupsConverterImpl converter = new TxnMgrGroupsConverterImpl();
		GraphReducerImpl reducer = new GraphReducerImpl();
		ContextMgmtInfoPopulatorEclipsePluginImpl ctxMgmtInfoPopulator = new ContextMgmtInfoPopulatorEclipsePluginImpl(
				project);

		try {
			URL selectedUrl = selectedFile.getLocationURI().toURL();
			txnMgrConfigParserImpl.useXmlDocType(selectedUrl);

			Map<String, EntityRefInfo> entityRefs = new HashMap<String, EntityRefInfo>();
			Map<String, List<String>> listEntityRefsInterDependencies = txnMgrConfigParserImpl.listEntityRefsInterDependencies(selectedUrl, entityRefs);
			
			List<EntityRefInfo> entityRefsTopologicalSort = txnMgrConfigParserImpl.sortEntityRefsTopologicalOrder(entityRefs, listEntityRefsInterDependencies); 
			entityRefsTopologicalSort.add(new EntityRefInfo(ROOT_KEY,
					selectedUrl));

			for (EntityRefInfo entityRef : entityRefsTopologicalSort) {

				URL entityRefURL = entityRef.getUrl();

				Map<String, List<ParticipantInfo>> jPosTxnMgrGroups = txnMgrConfigParserImpl
						.parse(entityRefURL);

				ctxMgmtInfoPopulator
						.processParticipantAnnotations(jPosTxnMgrGroups);
				Graph graphInter1 = converter.toGraph(jPosTxnMgrGroups);
				Graph graphInter2 = reducer.reduce(graphInter1);

				String entityRefName = entityRef.getName();
				graphByEntityRef.put(entityRefName, graphInter2);

//				GraphHelper.dumpGraph(graphInter2, new PrintWriter(System.out));
			}

			Map<String, List<Node>> unresolvedNodesByEntityRefName = new HashMap<String, List<Node>>();
			for (Entry<String, Graph> entry : graphByEntityRef.entrySet()) {
				Graph currGraph = entry.getValue();
				List<Node> unresolvedNodes = GraphHelper
						.lookForUnresolvedNodes(currGraph);
				unresolvedNodesByEntityRefName.put(entry.getKey(),
						unresolvedNodes);
			}

			for (Entry<String, List<Node>> entry : unresolvedNodesByEntityRefName
					.entrySet()) {
				List<String> currentFlowDependencies = listEntityRefsInterDependencies
						.get(entry.getKey());
				for (Node unresolved : entry.getValue()) {
					String unResolvedGroupName = unresolved
							.getParticipant().getGroupName();
					
					Graph referencedGraph = graphByEntityRef.get(unResolvedGroupName);
					
					if (null != referencedGraph) {
						if (!(currentFlowDependencies.contains(unResolvedGroupName))) {
							currentFlowDependencies.add(unResolvedGroupName);
						}

						// Replace unresolved participant info with a subflow
						// info
						SubFlowInfo subFlowInfo = new SubFlowInfo(unResolvedGroupName,
								referencedGraph,
								new HashMap<String, SelectCriterion>());
						unresolved.setParticipant(subFlowInfo);
					}
				}
			}
			
			List<EntityRefInfo> finalEntityRefsTopologicalSort = txnMgrConfigParserImpl.sortEntityRefsTopologicalOrder(entityRefs, listEntityRefsInterDependencies); 
			finalEntityRefsTopologicalSort.add(new EntityRefInfo(ROOT_KEY,
					selectedUrl));

			for (EntityRefInfo entityRef : finalEntityRefsTopologicalSort) {				
				ctxMgmtInfoPopulator.updateReducedGraph(graphByEntityRef.get(entityRef.getName()));				
			}			

		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		rootGraph = graphByEntityRef.get(ROOT_KEY);

		if (null != rootGraph) {
			MyEditorInput editorInput = new MyEditorInput(
					selectedFile.getName(), rootGraph);
			editorInput.setProject(project);
			try {
				MyGraphicalEditor openEditor = (MyGraphicalEditor) page
						.openEditor(editorInput, MyGraphicalEditor.ID, false);
				openEditor.setPartName(selectedFile.getName());
			} catch (PartInitException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
