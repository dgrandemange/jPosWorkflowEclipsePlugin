package org.jpos.jposext.jposworkflow.eclipse.command;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ExportAsImageCommand extends Command {

	class MyIntWrapper {
		private int wrapped;

		MyIntWrapper(int val) {
			this.wrapped = val;
		}

		public int getWrapped() {
			return wrapped;
		}

		public void setWrapped(int wrapped) {
			this.wrapped = wrapped;
		}

	}

	enum SwtImageFormatEnum {
		JPEG(SWT.IMAGE_JPEG), PNG(SWT.IMAGE_PNG), BMP(SWT.IMAGE_BMP);

		private int swtFormat;

		private SwtImageFormatEnum(int swtFormat) {
			this.swtFormat = swtFormat;
		}

		public int getSwtFormat() {
			return swtFormat;
		}

	}

	private EditPart editPart;

	private void createImage() {

		Device device = editPart.getViewer().getControl().getDisplay();
		IFigure figure = ((LayerManager) editPart)
				.getLayer(LayerConstants.PRINTABLE_LAYERS);
		Rectangle bounds = figure.getBounds();
		Image image = new Image(device, bounds.width, bounds.height);
		Shell shell = new Shell(editPart.getViewer().getControl().getDisplay());
		FileOutputStream result = null;
		try {
			MyIntWrapper imgFormat = new MyIntWrapper(-1);
			try {
				result = new FileOutputStream(getSaveFilePath(shell,
						(GraphicalViewer) editPart.getViewer(), imgFormat));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}

			GC gc = null;
			Graphics g = null;
			try {
				gc = new GC(image);
				g = new SWTGraphics(gc);
				g.translate(bounds.getLocation().negate());

				figure.paint(g);

				ImageLoader imageLoader = new ImageLoader();
				imageLoader.data = new ImageData[] { image.getImageData() };
				imageLoader.save(result, imgFormat.getWrapped());
			} finally {
				if (g != null) {
					g.dispose();
				}
				if (gc != null) {
					gc.dispose();
				}
				if (image != null) {
					image.dispose();
				}
			}
		} finally {
			if (null != result) {
				try {
					result.flush();
					result.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void setEditPart(EditPart editPart) {
		this.editPart = editPart;
	}

	@Override
	public void execute() {
		createImage();
	}

	protected String getSaveFilePath(Shell shell, GraphicalViewer viewer,
			MyIntWrapper format) {
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);

		String[] filterExtensions = new String[SwtImageFormatEnum.values().length];
		String fileName = "";
		for (SwtImageFormatEnum swtImageFormat : SwtImageFormatEnum.values()) {
			filterExtensions[swtImageFormat.ordinal()] = "."
					+ swtImageFormat.name().toLowerCase();
			fileName += "*" + filterExtensions[swtImageFormat.ordinal()] + ";";
		}
		fileDialog.setFilterExtensions(filterExtensions);
		//fileDialog.setFileName(fileName);

		String filePath = fileDialog.open();

		Pattern pattern = Pattern.compile("(?<=\\.).*$");
		Matcher matcher = pattern.matcher(filePath);
		if (matcher.find()) {
			String fileExtension = matcher.group();
			SwtImageFormatEnum selectedSwtImageFormatEnum = SwtImageFormatEnum
					.valueOf(fileExtension.toUpperCase());
			format.setWrapped(selectedSwtImageFormatEnum.getSwtFormat());

			return filePath;
		} else {
			throw new RuntimeException("Unknonwn file extension");
		}

	}

}
