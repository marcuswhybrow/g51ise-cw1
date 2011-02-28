
package uk.org.whybrow.spaceinvader;

import java.awt.Graphics;

/**
 *
 * @author Marcus Whybrow
 */
public class ShieldEntity extends Entity {

	/** The amount of time a single segment will last **/
	private static int time = 3000;
	/** The percentage to last for **/
	private static int percentage = 20;

	private static ShieldEntity single = new ShieldEntity();

	private ShieldEntity() {

		super(0, 0, "sprites/shield.png");

		EnergyEntity.get().setType(EnergyEntity.SEGMENTED);
	}

	public static ShieldEntity get() {

		return single;
	}

	public void setGame(Game game) {

		this.game = game;
	}

	public void draw(Graphics g) {

		if(game.invader.getShieldIsOn() && EnergyEntity.get().getEnergy() > 0) {
			EnergyEntity.get().setIsVisible(true);

			g.drawImage(sprite, pos.x, pos.y, null);
		} else {
			game.invader.setShieldIsOn(false);
		}
	}

	public void move(long delta) {

		//if(game.invader.getPowerup() == game.invader.PULSE_SHIELD);

		pos.x = game.invader.pos.x - 55;
		pos.y = game.invader.pos.y + 5;

		double amount = (double) (percentage * delta) / time;

		for(int i = 80; i >= 0; i -= 20) {

			if(EnergyEntity.get().getEnergy() > i && EnergyEntity.get().getEnergy() - amount < i) {
				EnergyEntity.get().setEnergy(i);
				game.invader.setShieldIsOn(false);
				break;
			}
		}

		if(game.invader.getShieldIsOn())
			EnergyEntity.get().decreaseEnergy(amount);

		if(EnergyEntity.get().getEnergy() <= 0) {
			EnergyEntity.get().setIsVisible(false);
		}
	}

}