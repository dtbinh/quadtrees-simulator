package master.sim.dspalgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.Rectangle;


public class MAQuadTree extends QuadTree implements DSPRunnable{
	
	private List<Integer> _timeSeries = new LinkedList<Integer>();
	final private int _interval = 500;
	private Thread _leafThread;
	private volatile boolean _isRunning = true;
	private int _timeWindow = 5;
	private int _hardThreshold;
	
	public MAQuadTree(QuadTree parent, Rectangle area, int timeWindow, int softThreshold, int hardThreshold) {
		super(parent, area, softThreshold);
		_timeWindow = timeWindow;

		_hardThreshold = hardThreshold;
		startRunning();
	}

	private void startRunning() {
		_leafThread = new Thread(this);
		_leafThread.start();
		synchronized (this) {
			_isRunning = true;
		}
	}

	@Override
	public void run() {
		
		while(_isRunning){
			synchronized (_timeSeries) {
				if(_timeSeries.size() > _timeWindow ){
					_timeSeries.remove(0);
				}
				_timeSeries.add(getAvatars().size());
			}
//			System.out.println("S: " + _timeSeries.size());

			try {
				Thread.sleep(_interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public MAQuadTree(QuadTree parent, int x, int y, int width, int height, int timeWindow, int softThreshold, int hardThreshold) {
		this(parent, new Rectangle(x, y, width, height), timeWindow, softThreshold, hardThreshold);
	}
	
	@Override
	synchronized protected void split() {

		if(predictedValue() > _hardThreshold){
//			System.out.println("SPLIT");
			setReconfigurationCounter(getReconfigurationCounter()+1);
			List<Quadrant> children = new ArrayList<Quadrant>();
			children.add(new MAQuadTree(this, getRegion().x, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new MAQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new MAQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y + getRegion().height / 2, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new MAQuadTree(this, getRegion().x, 
					getRegion().y + getRegion().height / 2, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			for (Avatar avatar : getAvatars()) {
				for (Quadrant child : children) {
					child.add(avatar);
				}
			}
			getAvatars().clear();
			setChildren(children);
			stopRunning();
		}
	}

	private int predictedValue() {
		synchronized (_timeSeries) {
			if(_timeSeries.size() < _timeWindow){
				return getAvatars().size();
			}
			
			int sum = 0;
			
			for (Integer seriesValue : _timeSeries) {
	//			System.out.println("V: " + seriesValue);
				sum += seriesValue;
			}
			int mean = sum/_timeWindow;
			return mean;
		}
//		System.err.println("Mean = " + mean + "; avatars = "+ getAvatars().size() + " hardThreshold = " + _hardThreshold);
	}

	@Override
	synchronized public void setChildren(List<Quadrant> children) {
		if(children == null){
			List<Quadrant> childrenToRemove = getChildren();
			for (Quadrant child : childrenToRemove) {
				((DSPRunnable)child).stopRunning();
			}
			startRunning();
		}
		
		super.setChildren(children);
	}

	@Override
	synchronized public void stopRunning() {
		_isRunning = false;
	}

	public List<Integer> getTimeSeries() {
		return _timeSeries;
	}

	public int getTimeWindow() {
		return _timeWindow;
	}

	public int getSoftThreshold() {
		return _hardThreshold;
	}
	
}
