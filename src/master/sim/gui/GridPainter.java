package master.sim.gui;
import master.sim.dspalgorithm.Quadrant;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;


public class GridPainter implements PaintListener {

	private Quadrant _quadTree;
	private Canvas _canvas;
	private Image _bufferImage;
	
	public GridPainter(Quadrant quadTree, Canvas canvas) {
		_quadTree = quadTree;
		_canvas = canvas;
	}

	@Override
	synchronized public void paintControl(PaintEvent e) {
		_bufferImage = new Image(_canvas.getDisplay(), _canvas.getClientArea().width, _canvas.getClientArea().height);
		GC gc = new GC(_bufferImage);
		_quadTree.draw(gc);
		
		e.gc.drawImage(_bufferImage, 0, 0);
		
		e.gc.dispose();
		gc.dispose();
		_bufferImage.dispose();
		
		_canvas.redraw();
	}

	synchronized public void setQuadTree(Quadrant quadTree) {
		_quadTree = quadTree;
	}

}
