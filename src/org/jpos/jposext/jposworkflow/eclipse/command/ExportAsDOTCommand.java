package org.jpos.jposext.jposworkflow.eclipse.command;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.jpos.jposext.jposworkflow.model.Graph;
import org.jpos.jposext.jposworkflow.model.ParticipantInfo;
import org.jpos.jposext.jposworkflow.service.support.GraphConverterServiceImpl;
import org.jpos.jposext.jposworkflow.service.support.GraphReducerImpl;
import org.jpos.jposext.jposworkflow.service.support.TxnMgrGroupsConverterImpl;

/**
 * @author dgrandemange
 *
 */
public class ExportAsDOTCommand extends Command {

	private Map<String, List<ParticipantInfo>> jPosTxnMgrGroups;

	private EditPart editPart;

	private String defaultName;

	private void createDOTFile() {

		TxnMgrGroupsConverterImpl txnMgrGroupsConverter = new TxnMgrGroupsConverterImpl();
		Graph genGraph = txnMgrGroupsConverter.toGraph(jPosTxnMgrGroups);

		Graph reducedGraph = (new GraphReducerImpl()).reduce(genGraph);

		GraphConverterServiceImpl graphConverterService = new GraphConverterServiceImpl();

		Shell shell = new Shell(editPart.getViewer().getControl().getDisplay());
		FileOutputStream result = null;
		PrintWriter pw = null;
		try {
			result = new FileOutputStream(getSaveFilePath(shell,
					(GraphicalViewer) editPart.getViewer()));
			pw = new PrintWriter(result);
			graphConverterService
					.convertGraphToDOT(
							"jPos Workflow Eclipse Plugin DOT Export",
							reducedGraph, pw);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			if (result != null) {
				try {
					result.flush();
					result.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public void setEditPart(EditPart editPart) {
		this.editPart = editPart;
	}

	@Override
	public void execute() {
		createDOTFile();
	}

	protected String getSaveFilePath(Shell shell, GraphicalViewer viewer) {
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);

		String[] filterExtensions = new String[] { "*.dot" };

		fileDialog.setFileName(defaultName + ".dot");
		fileDialog.setFilterExtensions(filterExtensions);

		String filePath = fileDialog.open();

		return filePath;
	}

	public void setJPosTxnMgrGroups(
			Map<String, List<ParticipantInfo>> jPosTxnMgrGroups) {
		this.jPosTxnMgrGroups = jPosTxnMgrGroups;
	}

	/**
	 * @param defaultName
	 *            the defaultName to set
	 */
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

}
