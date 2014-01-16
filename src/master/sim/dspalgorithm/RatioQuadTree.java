package master.sim.dspalgorithm;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.Rectangle;


public class RatioQuadTree extends QuadTree{
	
	private List<Integer> _timeSeries = new LinkedList<Integer>();
	final private int _interval = 500;
	private Thread _leafThread;
	private volatile boolean _isRunning = true;
	private int _timeWindow = 50;
	private int _hardThreshold;
	private double _ratioThreshold = 5;
	
	public RatioQuadTree(QuadTree parent, Rectangle area, int timeWindow, int softThreshold, int hardThreshold) {
		super(parent, area, softThreshold);
		_timeWindow = timeWindow;

		_hardThreshold = hardThreshold;
	}

	public RatioQuadTree(QuadTree parent, int x, int y, int width, int height, int timeWindow, int softThreshold, int hardThreshold) {
		this(parent, new Rectangle(x, y, width, height), timeWindow, softThreshold, hardThreshold);
	}
	
	@Override
	synchronized protected void split() {

		double predictedValue = predictedValue();
//		System.out.println(predictedValue);
		if(predictedValue < _ratioThreshold){
			setReconfigurationCounter(getReconfigurationCounter()+1);
			List<Quadrant> children = new ArrayList<Quadrant>();
			children.add(new RatioQuadTree(this, getRegion().x, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new RatioQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new RatioQuadTree(this, getRegion().x + getRegion().width / 2, 
					getRegion().y + getRegion().height / 2, 
					getRegion().width / 2, 
					getRegion().height / 2, _timeWindow, getThreshold(), _hardThreshold));
			
			children.add(new RatioQuadTree(this, getRegion().x, 
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
		}
	}
	
	@Override
	synchronized public Quadrant add(Avatar avatar) {
		if (!contains(avatar)) {
			return null;
		}
		if (getChildren() == null){
			if(getAvatars().size() < getThreshold()){
				getAvatars().add(avatar);
				avatar.setQuadrant(this);
				return this;
			}else{
				if(isDividable() && getCurrentDivision() < getMachines()){
					split();
					if(getChildren() != null){
						setCurrentDivision(getCurrentDivision() + 3);
						for (Quadrant child : getChildren()) {
							Quadrant added = child.add(avatar);
							if(added != null){
								return added;
							}
						}
					}else{
						getAvatars().add(avatar);
						avatar.setQuadrant(this);
						return this;						
					}
				}else{
					getAvatars().add(avatar);
					avatar.setQuadrant(this);
					return this;
				}
			}
		}else{
			for (Quadrant child : getChildren()) {
				Quadrant added = child.add(avatar);
				if(added != null){
					return added;
				}
			}
		}
		return null;
	}

	private double predictedValue() {
		synchronized (_timeSeries) {
			double in = 1;
			double out = 1;
			
			for (Integer value : _timeSeries) {
				if(value > 0){
					in++;
				}else if(value < 0){
					out++;
				}
			}
			
			for (Integer value : _timeSeries) {
	//			System.out.print(value + " ");
			}
	//		System.out.println();
	//		System.out.println(in + " " + out);
			
			return in/out;
		}
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
	public void leave(Avatar avatar) {
		synchronized (_timeSeries) {
			if(_timeSeries.size() > _timeWindow){
				_timeSeries.remove(0);
			}
			_timeSeries.add(new Integer(-1));
		}
	}
	
	@Override
	public void enter(Avatar avatar) {
		synchronized (_timeSeries) {
			if(_timeSeries.size() > _timeWindow){
				_timeSeries.remove(0);
			}
			_timeSeries.add(new Integer(1));
		}
	}
	
}
