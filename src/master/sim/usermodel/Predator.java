package master.sim.usermodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import master.sim.AvatarRegistry;
import master.sim.dspalgorithm.Quadrant;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class Predator extends Avatar{
	
	private final Role role = Role.PREDATOR;
	
	private final int PREY_RANGE = 50;
	private int killFrenzy = 60;
	private int killSilent = 60;

	public Predator(int x, int y, Quadrant quadTree) {
		super(x, y, quadTree);
	}

	private static int MAX_SPEED = 2;
	
	@Override
	public void run() {
		
		while (!isStop()) {
			List<Avatar> neighbourhood = new ArrayList<Avatar>();
			List<Avatar> preys = new ArrayList<Avatar>();
			for (Avatar avatar : AvatarRegistry.getInstance().getAvatars()) {
				double distance = getPosition().distance(avatar.getPosition());
				if(distance < PREY_RANGE && avatar.getRole() == Role.PREY){
					preys.add(avatar);
				}else if (isNeighbour(avatar)) {
					neighbourhood.add(avatar);
				}
			}
			Vector acceleration = new Vector(0,0);
			if(preys.size() == 0 || killSilent > 0){
				MAX_SPEED = 2;
				acceleration = flock(neighbourhood);
				killSilent--;
			}else if(killFrenzy > 0){
				MAX_SPEED = 4;
				Vector chaseVector = new Vector(0,0);
				for (Avatar prey : preys) {
					chaseVector.add(Vector.subtract(prey.getPosition(), getPosition()));
				}
				chaseVector.divide(preys.size());
				acceleration.add(chaseVector);
				killFrenzy--;
			}else{
				killFrenzy = new Random().nextInt(100);
				killSilent = new Random().nextInt(100);
			}
			move(acceleration);

			try {
				Thread.sleep(getAvatarSpeed());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public Role getRole() {
		return role;
	}
	
	public void draw(GC gc) {
		Color foreground = gc.getForeground();
		Display current = Display.getCurrent();
		Color color = new Color(current, 0, 0, 255);
		gc.setForeground(color);
		gc.drawRectangle((int) getPosition().x, (int) getPosition().y, 1, 1);
		gc.setForeground(foreground);
		color.dispose();
	}

}
