package master.sim.dspalgorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;


public class StaticPartition implements Quadrant{

	private QuadTree _quadTree;
	
	public StaticPartition(Rectangle area, int rows, int columns, int threshold) {
		_quadTree = new QuadTree(null, area, 0, false);
		_quadTree.init();
		
		int rowWidth = area.width/rows;
		int rowHeight = area.height/columns;
		
		int quadX = 0;
		int quadY = 0;
		List<Quadrant> children = new ArrayList<Quadrant>();
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				QuadTree child = new QuadTree(_quadTree, new Rectangle(quadX+rowWidth*i, quadY+rowHeight*j, rowWidth, rowHeight), threshold, false);
				children.add(child);
			}
		}
		_quadTree.setChildren(children);
	}
	
	@Override
	public Quadrant add(Avatar avatar) {
		return _quadTree.add(avatar);
	}

	@Override
	public Quadrant remove(Avatar avatar) {
		return _quadTree.remove(avatar);
	}

	@Override
	public Quadrant search(Avatar avatar) {
		return _quadTree.search(avatar);
	}

	@Override
	public boolean contains(Avatar avatar) {
		return _quadTree.contains(avatar);
	}

	@Override
	public void draw(GC gc) {
		_quadTree.draw(gc);
	}

	@Override
	public int getPopulation() {
		return _quadTree.getPopulation();
	}

	@Override
	public Set<Avatar> getAvatars() {
		return _quadTree.getAvatars();
	}

	@Override
	public List<Quadrant> getChildren() {
		return _quadTree.getChildren();
	}

	@Override
	public boolean isRoot() {
		return _quadTree.isRoot();
	}

	@Override
	public boolean isLeaf() {
		return _quadTree.isLeaf();
	}

	@Override
	public void setChildren(List<Quadrant> children) {
		_quadTree.setChildren(children);
	}

	@Override
	public Rectangle getRegion() {
		return _quadTree.getRegion();
	}

	@Override
	public void update(Avatar avatar) {
		_quadTree.update(avatar);
	}

	@Override
	public int getCrossingCounter() {
		return _quadTree.getCrossingCounter();
	}

	@Override
	public int getNumberOfLeaves() {
		return _quadTree.getNumberOfLeaves();
	}

	@Override
	public void leave(Avatar avatar) {
	}

	@Override
	public void enter(Avatar avatar) {
	}

	@Override
	public int getReconfigurationCounter() {
		return _quadTree.getReconfigurationCounter();
	}

	@Override
	public int getTotalOverload() {
		return _quadTree.getTotalOverload();
	}

	@Override
	public int getThreshold() {
		return _quadTree.getThreshold();
	}

	@Override
	public int getNumberOfEntities() {
		return _quadTree.getNumberOfEntities();
	}

	@Override
	public void init() {
		_quadTree.init();
	}

	@Override
	public int getNumberOfEmptyLeaves() {
		return _quadTree.getNumberOfEmptyLeaves();
	}

	@Override
	public int getMaxOverload() {
		return _quadTree.getMaxOverload();
	}

}
