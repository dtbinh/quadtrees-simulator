package master.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import master.sim.dspalgorithm.MigrationListener;
import master.sim.usermodel.Avatar;


public class AvatarRegistry {

	private static List<Avatar> _AVATARS = new ArrayList<Avatar>();
	private static AvatarRegistry _INSTANCE = new AvatarRegistry();
	private static final int _MAX_SPEED;
	private static final int _RADIUS;
	private static int _machines;
	
	static{
		_RADIUS = 50;
		_MAX_SPEED = 2;
		_machines = 16;
	}

	private List<MigrationListener> _migrationListeners = new ArrayList<MigrationListener>();

	public void registerMigrationListener(MigrationListener listener){
		_migrationListeners.add(listener);
	}
	
	public void unregisterMigrationListener(MigrationListener listener){
		_migrationListeners.remove(listener);
	}
	public void clearMigrationListeners(){
		_migrationListeners.clear();
	}
	
	private AvatarRegistry(){
	}
	
	public static AvatarRegistry getInstance() {
		return _INSTANCE;
	}

	public void add(Avatar avatar){
		_AVATARS.add(avatar);
	}

	synchronized public List<Avatar> getAvatars() {
		List<Avatar> avatarsCopy = new ArrayList<Avatar>(_AVATARS);
		return avatarsCopy;
	}

	public static int getRadius() {
		return _RADIUS;
	}

	public static int getMaxSpeed() {
		return _MAX_SPEED;
	}

	public void setMachines(int machines) {
		_machines = machines;
	}
	
	public int getMachines(){
		return _machines;
	}

	public List<MigrationListener> getMigrationListeners() {
		return _migrationListeners;
	}

	synchronized public void remove(Avatar avatar) {
		_AVATARS.remove(avatar);
	}

	synchronized public void clearAvatars() {
		_AVATARS.clear();
	}
	
}
