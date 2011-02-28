
package uk.org.whybrow.spaceinvader;

import java.awt.Rectangle;

/**
 * The PowerEntity is a power up for the play to colect invoking special
 * abilities, there are two kinds, the speed boost power up, and the pulse
 * shield.
 *
 * In game pressing space actiates the active powerup. If the speed boost is
 * active, the maximum speed of the invader is increased and the acceleration
 * and resistance to motion increase masking the invader faster. If the pulse
 * shield is active presseing space deflects any shells near to the invader
 * preventing them from damaging the invader.
 *
 * @author Marcus Whybrow
 */
public class PowerEntity extends Entity {

	/** The speed of the entity as it falls down the screen **/
	private int speed = 200;
	/** True if this power up is the speed boost, false if its the shield **/
	private boolean isSpeed;

	/**
	 * Creates a powerup entity
	 * 
	 * @param isSpeed True if speed_boost, false if pulse_shield
	 * @param game the game this power up is in
	 */
	public PowerEntity(Game game) {

		super(0, 0, null);

		//Get the correct image

		isSpeed = (Math.random() > 0.5) ? true : false;

		if(isSpeed) {
			changeSprite("sprites/speed_boost.png");
			pos.x -= 7;
		} else {
			changeSprite("sprites/pulse_shield.png");
			
		}

		//Create at a random viewable horizontal position

		pos.x = (int) ( Math.random() * ( game.image.getWidth(null) - getHitbox().getWidth() ));
		pos.y = - sprite.getHeight(null);

		this.game = game;
	}

	/**
	 * Move down the screen
	 *
	 * @param delta The time since the last game loop
	 */
	public void move(long delta) {

		//Move

		pos.y += (speed * delta) / 1000;
		
		//If the power up intersects with invader, activate the correct power up
		//and remove the power up from gameplay

		if(getHitbox().intersects(game.invader.getHitbox())) {
			if(isSpeed) {
				game.invader.setPowerup(game.invader.SPEED_BOOST);
			} else {
				game.invader.setPowerup(game.invader.PULSE_SHIELD);
			}
			game.entities.remove(this);
		}

		//Remove the entity if offscreen

		if(getHitbox().getMinY() > game.image.getHeight(null)) {
			game.entities.remove(this);
		}
 
	}

	/**
	 * Get the hotable area depending on the power up type
	 *
	 * @return The hitbox in rectanlge form
	 */
	public Rectangle getHitbox() {

		if(isSpeed) {
			hitbox.setBounds(pos.x + 7, pos.y, sprite.getWidth(null) - 23, sprite.getHeight(null));
		} else {
			hitbox.setBounds(pos.x, pos.y + 4, sprite.getWidth(null) - 1, sprite.getHeight(null) - 4);
		}
		return hitbox;
	}
}
