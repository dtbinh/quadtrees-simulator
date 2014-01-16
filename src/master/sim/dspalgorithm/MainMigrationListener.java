package master.sim.dspalgorithm;
import master.sim.usermodel.Avatar;


public class MainMigrationListener implements MigrationListener {

	private Quadrant _quadTree;

	public MainMigrationListener(Quadrant quadTree) {
		_quadTree = quadTree;
	}

	@Override
	public void avatarChangedQuadrant(Avatar avatar) {
		_quadTree.update(avatar);
	}

}
