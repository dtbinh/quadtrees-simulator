package master.sim.usermodel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import master.sim.AvatarRegistry;
import master.sim.dspalgorithm.MigrationListener;
import master.sim.dspalgorithm.QuadTree;
import master.sim.dspalgorithm.Quadrant;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class Avatar implements Runnable {
	private final Role role = Role.PREY;

	private Quadrant _quadrant = null;
	
	private List<MigrationListener> _migrationListeners = new ArrayList<MigrationListener>();
	
	private static final int SEPARATION_WEIGHT = 2;
	private static final int ALIGNMENT_WEIGHT = 4;
	private static final int COHESION_WEIGHT = 3;
	private static final double NEIGHBOUR_RADIUS = 20;
	private static final double MAX_FORCE = 0.05;
	private static final double DESIRED_SEPARATION = 2;
	private static final double WALL_REPULSION = 5;
	private Vector _position;
	private Vector _velocity;

	final private Thread _avatarThread;
	private volatile boolean _stop = false;
	final private int MAX_SPEED = 2;

	private final Rectangle _area;

	private Quadrant _quadTree;

	final private int _avatarSpeed = 25; //10

	public Avatar(int x, int y, Quadrant quadTree) {
		_quadTree = quadTree;
		_area = new Rectangle(_quadTree.getRegion().x, _quadTree.getRegion().y, _quadTree.getRegion().width, _quadTree.getRegion().height);
		_position = new Vector(x, y);
		
		_velocity = new Vector(new Random().nextDouble() * 2 - 1,
				new Random().nextDouble() * 2 - 1);
		
		_migrationListeners.addAll(AvatarRegistry.getInstance().getMigrationListeners());
		
		_avatarThread = new Thread(this);
		_avatarThread.start();
	}

	@Override
	public void run() {

//		_quadTree.add(this);
		
		while (!_stop) {
			List<Avatar> predators = new ArrayList<Avatar>();
			List<Avatar> neighbourhood = new ArrayList<Avatar>();
			for (Avatar avatar : AvatarRegistry.getInstance().getAvatars()) {
				if (isNeighbour(avatar)) {
					if(avatar.getRole() == Role.PREDATOR){
						predators.add(avatar);
					}else{
						neighbourhood.add(avatar);
					}
				}
			}
			Vector acceleration = flock(neighbourhood);
			if(predators.size() > 0){
				Vector predatorRepulsion = new Vector(0, 0);
				for (Avatar avatar : predators) {
					predatorRepulsion.add(Vector.subtract(_position, avatar.getPosition()).normalize());
				}
				predatorRepulsion.divide(predators.size());
				acceleration.add(predatorRepulsion);
			}
			move(acceleration);
			if(_quadrant != null && !_quadrant.contains(this)){
				for (MigrationListener listener : _migrationListeners) {
					_quadrant.leave(this);
					listener.avatarChangedQuadrant(this);
					_quadrant.enter(this);
				}
			}
			
			try {
				Thread.sleep(_avatarSpeed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Vector flock(List<Avatar> neighbourhood) {
		Vector separation = new Vector(0, 0);
		Vector alignment = new Vector(0, 0);
		Vector cohesion = new Vector(0, 0);
		Vector wall = new Vector(0,0);
		
		int separationCount = 0;
		int alignmentCount = 0;
		int cohesionCount = 0;
		
		for (Avatar avatar : neighbourhood) {
			double distance = _position.distance(avatar.getPosition());
			if (distance > 0) {
				if (distance < DESIRED_SEPARATION) {
					separation.add(Vector.subtract(_position, avatar.getPosition()).normalize().divide(distance));
					separationCount++;
				}
				if (distance < NEIGHBOUR_RADIUS) {
					alignment.add(avatar.getVelocity());
					alignmentCount++;
					cohesion.add(avatar.getPosition());
					cohesionCount++;
				}
			}
		}
		
		if(separationCount > 0){
			separation.divide(separationCount);
		}
		if(alignmentCount > 0){
			alignment.divide(alignmentCount);
		}
		if(cohesionCount >0){
			cohesion.divide(cohesionCount);
		}else{
			cohesion = new Vector(_position);
		}
		
		if(_position.x - WALL_REPULSION <= 0){
			double repulsionPower = WALL_REPULSION/_position.x;
			wall.add(new Vector(repulsionPower,0));
		}
		if(_position.x + WALL_REPULSION >= _area.width){
			double repulsionPower = WALL_REPULSION/(_area.width - _position.x);
			wall.add(new Vector(-repulsionPower, 0));
		}
		if(_position.y - WALL_REPULSION <= 0){
			double repulsionPower = WALL_REPULSION/_position.y;
			wall.add(new Vector(0, repulsionPower));
		}
		if(_position.y + WALL_REPULSION >= _area.height){
			double repulsionPower = WALL_REPULSION/(_area.height - _position.y);
			wall.add(new Vector(0, -repulsionPower));
		}
		
		
		Vector auxCohesion = Vector.subtract(cohesion, _position);
		cohesion = steerTo(auxCohesion);
		alignment.limit(MAX_FORCE);
		
		separation.multiply(SEPARATION_WEIGHT);
		alignment.multiply(ALIGNMENT_WEIGHT);
		cohesion.multiply(COHESION_WEIGHT);
		return separation.add(alignment).add(cohesion).add(wall);
	}

	private Vector steerTo(Vector auxCohesion) {
		Vector desired = auxCohesion;
		double d = desired.magnitude();
		Vector steer = new Vector(0,0);
		if( d > 0 ){
			desired.normalize();
			if(d < 100){
				desired.multiply(MAX_SPEED*(d/100.0));
			}else{
				desired.multiply(MAX_SPEED);
			}
			steer = Vector.subtract(desired, _velocity);
			steer.limit(MAX_FORCE);
		}
		
		return steer;
	}

	private Vector getVelocity() {
		return _velocity;
	}

	public boolean isNeighbour(Avatar avatar) {
		if (this == avatar) {
			return false;
		}
		Vector position = avatar.getPosition();
		double distance = java.awt.Point.distance(_position.x, _position.y,
				position.x, position.y);
		return distance < NEIGHBOUR_RADIUS;
	}

	public void move(Vector acceleration) {
		_velocity.add(acceleration).limit(MAX_SPEED);
		_position.add(_velocity);
	}

	synchronized public Vector getPosition() {
		return _position;
	}

	synchronized public void setPosition(Vector _position) {
		this._position = _position;
	}

	public void draw(GC gc) {
		Color foreground = gc.getForeground();
		Display current = Display.getCurrent();
		Color color = new Color(current, 255, 0, 0);
		gc.setForeground(color);
		gc.drawRectangle((int) getPosition().x, (int) getPosition().y, 1, 1);
		gc.setForeground(foreground);
		color.dispose();
	}

	public Thread getAvatarThread() {
		return _avatarThread;
	}

	public void stop() {
		_stop = true;
		_quadTree.remove(this);
	}

	public boolean isStop() {
		return _stop;
	}

	public void setStop(boolean _stop) {
		this._stop = _stop;
	}

	public Role getRole() {
		return role;
	}

	public Quadrant getQuadTree() {
		return _quadTree;
	}

	public void setQuadTree(QuadTree _quadTree) {
		this._quadTree = _quadTree;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj instanceof Avatar){
			Avatar avatar = (Avatar) obj;
			return _avatarThread.equals(avatar.getAvatarThread());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _avatarThread.hashCode();
	}

	public void setQuadrant(Quadrant quadrant) {
		_quadrant = quadrant;
	}

	public void addMigrationListener(MigrationListener listener) {
		_migrationListeners.add(listener);
	}

	public void removeMigrationListener(MigrationListener listener) {
		_migrationListeners.remove(listener);
	}

	public int getAvatarSpeed() {
		return _avatarSpeed;
	}
	
}
