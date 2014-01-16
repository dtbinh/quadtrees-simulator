package master.sim.dspalgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import master.sim.AvatarRegistry;
import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;


public class QuadTree implements Quadrant {
	
	private int _threshold;
	private Rectangle _region;
	private Set<Avatar> _avatars;
	private List<Quadrant> _children = null;
	private Quadrant _parent = null;
	
	private static int _crossingCounter = 0;
	private static int _reconfigurationCounter = 0;
	private final int _machines;
	private final boolean _isDividable;
	private static int _currentDivision = 1;
	
	@Override
	public void init(){
		_crossingCounter = 0;
		_reconfigurationCounter = 0;
		_currentDivision = 1;
	}
	
	public QuadTree(QuadTree parent, Rectangle area, int threshold, boolean isDividable) {
		_threshold = threshold;
		_region = area;
//		_avatars = new HashSet<Avatar>();
		_avatars = Collections.synchronizedSet(new HashSet<Avatar>());
		_parent = parent;
		_machines  = AvatarRegistry.getInstance().getMachines();
		_isDividable = isDividable;
	}

	public QuadTree(QuadTree parent, int x, int y, int width, int height, int threshold) {
		this(parent, new Rectangle(x, y, width, height), threshold, true);
	}

	public QuadTree(QuadTree parent, Rectangle area, int threshold) {
		this(parent, area, threshold, true);
	}


	@Override
	synchronized public Quadrant add(Avatar avatar) {
		if (!contains(avatar)) {
			return null;
		}
		if (_children == null){
			if(_avatars.size() < _threshold){
				_avatars.add(avatar);
				avatar.setQuadrant(this);
				return this;
			}else{
				if(isDividable() && getCurrentDivision() < getMachines()-3){
					split();
					if(_children != null){
						setCurrentDivision(getCurrentDivision() + 3);
						for (Quadrant child : _children) {
							Quadrant added = child.add(avatar);
							if(added != null){
								return added;
							}
						}
					}else{
						_avatars.add(avatar);
						avatar.setQuadrant(this);
						return this;						
					}
				}else{
					_avatars.add(avatar);
					avatar.setQuadrant(this);
					return this;
				}
			}
		}else{
			for (Quadrant child : _children) {
				Quadrant added = child.add(avatar);
				if(added != null){
					return added;
				}
			}
		}
		return null;
	}

	synchronized protected void split() {
		_reconfigurationCounter++;
//		_children = new ArrayList<Quadrant>();
		_children = Collections.synchronizedList(new ArrayList<Quadrant>());
		_children.add(new QuadTree(this, _region.x, 
										 _region.y, 
										 _region.width / 2, 
										 _region.height / 2, _threshold));
		
		_children.add(new QuadTree(this, _region.x + _region.width / 2, 
										 _region.y, 
										 _region.width / 2, 
										 _region.height / 2, _threshold));
		
		_children.add(new QuadTree(this, _region.x + _region.width / 2, 
										 _region.y + _region.height / 2, 
										 _region.width / 2, 
										 _region.height / 2, _threshold));
		
		_children.add(new QuadTree(this, _region.x, 
										 _region.y + _region.height / 2, 
										 _region.width / 2, 
										 _region.height / 2, _threshold));
		for (Avatar avatar : _avatars) {
			for (Quadrant child : _children) {
				child.add(avatar);
			}
		}
		_avatars.clear();
	}

	@Override
	synchronized public Quadrant remove(Avatar avatar) {
		if(!_avatars.remove(avatar)){
			if(_children != null){
				for (Quadrant child : _children) {
					Quadrant removed = child.remove(avatar);
					if(removed != null){
						return removed;
					}
				}
			}
			return null;
		}else{
			
			if(isDividable() && !isRoot()){
				int population = _parent.getPopulation();
				if(population < _threshold){
					for (Quadrant child : _parent.getChildren()) {
						_parent.getAvatars().addAll(child.getAvatars());
					}
					_reconfigurationCounter++;
					_parent.setChildren(null);
					setCurrentDivision(getCurrentDivision() - 3);
				}
			}
			return this;
		}
	}

	@Override
	public Quadrant search(Avatar avatar) {
		if(_avatars.contains(avatar)){
			return this;
		}else{
			if(_children != null){
				for (Quadrant child : _children) {
					Quadrant found = child.search(avatar);
					if(found != null){
						return found;
					}
				}
				return null;
			}
		}
		return null;
	}

	@Override
	public boolean contains(Avatar avatar) {
//		Rectangle fullRegion = new Rectangle(_region.x-1, _region.y-1, _region.width+2, _region.height+2);
//		return fullRegion.contains(avatar.getPosition().toPoint());
		return _region.contains(avatar.getPosition().toPoint());
	}

	@Override
	synchronized public void draw(GC gc) {

		if(_avatars != null){

			List<Avatar> toRemove = new ArrayList<Avatar>();
			for (Avatar avatar : _avatars) {
				if(avatar.getAvatarThread().isAlive() && contains(avatar)){
					avatar.draw(gc);
				}else{
					toRemove.add(avatar);
				}
			}
			_avatars.removeAll(toRemove);

			gc.drawRectangle(_region);
			gc.drawString(String.valueOf(_avatars.size()), _region.x+2, _region.y+2);
			//return;
		}

		if(_children != null){
			for (Quadrant child : _children) {
				child.draw(gc);
			}
		}
		
		if(isRoot()){
			gc.drawString(String.valueOf(AvatarRegistry.getInstance().getAvatars().size()), _region.x+2, _region.y+2 + _region.height);
		}
	}

	public boolean isRoot(){
		return _parent == null;
	}

	synchronized public boolean isLeaf(){
		return _children == null;
	}
	
	public Rectangle getRegion() {
		return _region;
	}

	public void setRegion(Rectangle _region) {
		this._region = _region;
	}

	synchronized public void update(Avatar avatar) {
		Quadrant removed = remove(avatar);
		Quadrant added = add(avatar);
		if(removed != added){
			_crossingCounter++;
		}
	}

	@Override
	synchronized public int getPopulation() {
		if(_children == null){
			return _avatars.size();
		}else{
			int sumOfAvatars = 0;
			for (Quadrant child : _children) {
				sumOfAvatars += child.getPopulation();
			}
			return sumOfAvatars;
		}
	}

	@Override
	public Set<Avatar> getAvatars() {
		return _avatars;
	}

	@Override
	public List<Quadrant> getChildren() {
		return _children;
	}

	synchronized public int getCrossingCounter() {
		return _crossingCounter;
	}
	
	@Override
	public String toString() {
		return "Quadrant: (" + _region.x + ", " + _region.y + ") " + _region.width + ", " + _region.height;
	}

	@Override
	synchronized public void setChildren(List<Quadrant> children) {
		_children = children;
	}

	@Override
	synchronized public int getNumberOfLeaves() {
		int numberOfLeaves = 0;
		if(_children == null){
			return 0;
		}
		for (Quadrant child : _children) {
			if(child.isLeaf()){
				numberOfLeaves++;
			}else{
				numberOfLeaves += child.getNumberOfLeaves();
			}
		}
		
		return numberOfLeaves;
	}

	@Override
	synchronized public int getNumberOfEmptyLeaves() {
		int numberOfEmptyLeaves = 0;
		if(isLeaf()){
			if(getAvatars().isEmpty()){
				return 1;
			}
		}else{
			for (Quadrant child : _children) {
				numberOfEmptyLeaves += child.getNumberOfEmptyLeaves();
			}
		}
		return numberOfEmptyLeaves;
	}
	
	@Override
	synchronized public int getTotalOverload(){
		int overload = 0;
		if(_children == null){
			return (_avatars.size() - getThreshold() > 0 ? _avatars.size() - getThreshold() : 0);
		}
		for (Quadrant child : _children) {
			overload += child.getTotalOverload();
		}
		return overload;
	}
	
	@Override
	public int getThreshold() {
		return _threshold;
	}

	public void setThreshold(int threshold) {
		_threshold = threshold;
	}

	public int getMachines() {
		return _machines;
	}

	public boolean isDividable() {
		return _isDividable;
	}

	public int getCurrentDivision() {
		return _currentDivision;
	}

	public void setCurrentDivision(int currentDivision) {
		_currentDivision = currentDivision;
	}

	@Override
	synchronized public void leave(Avatar avatar) {
	}

	@Override
	synchronized public void enter(Avatar avatar) {
	}
	
	@Override
	synchronized public int getReconfigurationCounter() {
		return _reconfigurationCounter;
	}

	public void setReconfigurationCounter(int reconfigurationCounter) {
		_reconfigurationCounter = reconfigurationCounter;
	}

	@Override
	public int getNumberOfEntities() {
		return AvatarRegistry.getInstance().getAvatars().size();
	}

	@Override
	public int getMaxOverload() {
		if(isLeaf()){
			return getAvatars().size() - getThreshold() > 0 ? getAvatars().size() - getThreshold() : 0;
		}else{
			int max = -1;
			for (Quadrant child : getChildren()) {
				int overload = child.getMaxOverload();
				if(overload > max){
					max = overload;
				}
			}
			return max;
		}
	}

}
