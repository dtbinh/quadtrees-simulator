import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import master.sim.AvatarRegistry;
import master.sim.dspalgorithm.QuadTree;
import master.sim.dspalgorithm.Quadrant;
import master.sim.usermodel.Avatar;

import org.eclipse.swt.graphics.Rectangle;
import org.junit.Test;



public class QuadTreeTest {

	@Test
	public void shouldHaveAvatarsOnlyOnLeaves(){
		
		int threshold = 2;
		QuadTree quadTree = new QuadTree(null, new Rectangle(0, 0, 512, 512), threshold);
		AvatarRegistry avatarRegistry = AvatarRegistry.getInstance();
		
		int machines = 16;
		avatarRegistry.setMachines(machines);		
		avatarRegistry.add(new Avatar(50, 49, quadTree));
		avatarRegistry.add(new Avatar(50, 50, quadTree));
		avatarRegistry.add(new Avatar(50, 51, quadTree));
		avatarRegistry.add(new Avatar(50, 52, quadTree));
		avatarRegistry.add(new Avatar(50, 62, quadTree));
		
		List<Avatar> avatars = avatarRegistry.getAvatars();
		for (int i = 0; i < avatars.size(); i++) {
			Quadrant added = quadTree.add(avatars.get(i));
			Quadrant search = quadTree.search(avatars.get(i));
			
			System.out.println(added);
			assertTrue(added.isLeaf());
			assertTrue(search.isLeaf());
		}
	}

	@Test
	public void shouldAddAvatarAfterRemovingIt(){
		int threshold = 2;
		QuadTree quadTree = new QuadTree(null, new Rectangle(0, 0, 512, 512), threshold);
		AvatarRegistry avatarRegistry = AvatarRegistry.getInstance();
		
		int machines = 16;
		avatarRegistry.setMachines(machines);		
		avatarRegistry.add(new Avatar(50, 49, quadTree));
		avatarRegistry.add(new Avatar(50, 50, quadTree));
		avatarRegistry.add(new Avatar(50, 51, quadTree));
		avatarRegistry.add(new Avatar(50, 52, quadTree));
		avatarRegistry.add(new Avatar(50, 62, quadTree));
		
		List<Avatar> avatars = avatarRegistry.getAvatars();

		for (Avatar avatar : avatars) {
			quadTree.add(avatar);
		}

		for (Avatar avatar : avatars) {
			quadTree.remove(avatar);
		}
		for (Avatar avatar : avatars) {
			Quadrant add = quadTree.add(avatar);
			assertNotNull(add);
		}
		
	}
	
}
