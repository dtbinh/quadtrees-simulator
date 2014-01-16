package master.sim.dspalgorithm;
import java.util.List;
import java.util.Set;

import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;


public interface Quadrant {

	public Quadrant add(Avatar avatar);
	public Quadrant remove(Avatar avatar);
	public Quadrant search(Avatar avatar);
	public boolean contains(Avatar avatar);
	public void draw(GC gc);
	public int getPopulation();
	public Set<Avatar> getAvatars();
	public List<Quadrant> getChildren();
	public boolean isRoot();
	public boolean isLeaf();
	public void setChildren(List<Quadrant> children);
	public Rectangle getRegion();
	public void update(Avatar avatar);
	public int getCrossingCounter();
	public int getNumberOfLeaves();
	public int getReconfigurationCounter();
	public void leave(Avatar avatar);
	public void enter(Avatar avatar);
	public int getTotalOverload();
	public int getThreshold();
	public int getNumberOfEntities();
	void init();
	int getNumberOfEmptyLeaves();
	public int getMaxOverload();
}
