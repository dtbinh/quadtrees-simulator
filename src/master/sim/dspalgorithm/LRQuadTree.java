package master.sim.dspalgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.Rectangle;


public class LRQuadTree extends QuadTree implements DSPRunnable{
	
	private List<Integer> _timeSeries = new LinkedList<Integer>();
	final private int _interval = 500;
	private Thread _leafThread;
	private volatile boolean _isRunning = true;
	private int _timeWindow = 5;
	private int _hardThreshold;
	
	public LRQuadTree(QuadTree parent, Rectangle area, int timeWindow, int softThreshold, int hardThreshold) {
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
			if(_timeSeries.size() > _timeWindow ){
				_timeSeries.remove(0);
			}
			_timeSeries.add(getAvatars().size());
//			System.out.println("S: " + _timeSeries.size());

			try {
				Thread.sleep(_interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public LRQuadTree(QuadTree parent, int x, int y, int width, int height, int timeWindow, int softThreshold, int hardThreshold) {
		this(parent, new Rectangle(x, y, width, height), timeWindow, softThreshold, hardThreshold);
	}
	
	@Override
	synchronized protected void split() {

		if(predictedValue() > _hardThreshold){
			setReconfigurationCounter(getReconfigurationCounter()+1);
			List<Quadrant> children = new ArrayList<Quadrant>();
			children.add(new LRQuadTree(this, getRegion().x, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, super.getThreshold(), _hardThreshold));
			
			children.add(new LRQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, super.getThreshold(), _hardThreshold));
			
			children.add(new LRQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y + getRegion().height / 2, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, super.getThreshold(), _hardThreshold));
			
			children.add(new LRQuadTree(this, getRegion().x, 
					getRegion().y + getRegion().height / 2, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, super.getThreshold(), _hardThreshold));
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
		if(_timeSeries.size() < _timeWindow){
			return getAvatars().size();
		}
		
		double a = 0;
		double b = 0;

		List<Integer> copySeries = new ArrayList<Integer>(_timeSeries);
		
		double b1 = 0;
		double b2 = 0;
		double b3 = 0;
		double b4 = 0;
		double a1 = 0;
		double a2 = 0;
		for(int i = 1; i <= _timeWindow; i++){
			b1 += (i * copySeries.get(i-1));
			b2 += copySeries.get(i-1);
			b3 += i*i;
		}
		a1 = b2;
		a2 = ((_timeWindow /2.0) * (_timeWindow+1));
		b2 *= ((_timeWindow /2.0) * (_timeWindow+1));
		b2 /= (double)_timeWindow;
		b4 = ((_timeWindow/2.0) * (_timeWindow+1)) * ((_timeWindow/2.0) * (_timeWindow+1));
		b4 /= (double)_timeWindow;
		
		b = (b1 - b2) / (b3 - b4);
		a = (a1 - b * a2)/(double) _timeWindow;
		
		int prediction = (int) (a + b*(_timeWindow+1) );
//		System.out.println("y = " + a + " + " + b + "*x; Prediction: " + prediction + " size: " + getAvatars().size());
		return prediction;
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

	public int getHardThreshold() {
		return _hardThreshold;
	}
	
	@Override
	public int getThreshold() {
		return _hardThreshold;
	}
	
}
