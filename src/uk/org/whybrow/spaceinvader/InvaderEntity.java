
package uk.org.whybrow.spaceinvader;

import java.awt.Rectangle;
import java.awt.Point;

/**
 * Extends the entity class as the invader is a drawable object of the game,
 * The player controls this entity using the keyboard
 *
 * @author Marcus Whybrow
 */
public class InvaderEntity extends Entity {

	/** Current velocity of the invader **/
	private int currentVelocity = 0;
	/** acceleration per second **/
	private int acceleration = 1000;
	/** constant resistance per second (even when not accelerating) **/
	private int resistance = 300;
	/** The maximum obtainable speed **/
	private int maxSpeed = 300;

	/** The amount to drop down by in pixels when a side is hit **/
	private int dropDist = 15;
	/** Holds the x position of the invader from the last game loop **/
	private int lastX = 0;
	
	/** The last legitimate move **/
	private int lastMove = NOT_HIT_ANY_SIDE;
	/** The level has just started, any side can be hit first **/
	public static final int NOT_HIT_ANY_SIDE = 0;
	/** Just hit the left side, the right side must be hit next **/
	public static final int JUST_HIT_LEFT = 1;
	/** Just hit the right side, the left side must bt hit next **/
	public static final int JUST_HIT_RIGHT = 2;

	/** The current power up in use **/
	private int powerUp = NO_POWERUP;
	/** No power up is being used **/
	public static final int NO_POWERUP = 0;
	/** The speed boost powerup is in use **/
	public static final int SPEED_BOOST = 1;
	/** The pulse shield power up is in use **/
	public static final int PULSE_SHIELD = 2;

	/** True if the shield is on, thus no damage can be recieved **/
	private boolean shieldIsOn = false;

	/**
	 * Create an invader entity
	 *
	 * @param x The horizontal position to place the invader at
	 * @param y The vertical position to place the invader at
	 * @param game The game that the invader is in
	 */
	public InvaderEntity(int x, int y, Game game) {

		super(x, y, "sprites/Alien_normal.png", game);
		pos.x -= sprite.getWidth(null)/2;
	}

	public void setStatus(int status) {

		if(status == NOT_HIT_ANY_SIDE || status == JUST_HIT_LEFT || status == JUST_HIT_RIGHT) {

			lastMove = status;
		}
	}

	/**
	 * Set the powerup in use
	 *
	 * @param powerUp The power up to use, see public static variables
	 */
	public void setPowerup(int powerUp) {

		if(powerUp == NO_POWERUP || powerUp == SPEED_BOOST || powerUp == PULSE_SHIELD) {
			this.powerUp = powerUp;

			if(powerUp == SPEED_BOOST) {
				EnergyEntity.get().setType(EnergyEntity.CONTINUOUS);
			} else if(powerUp == PULSE_SHIELD) {
				EnergyEntity.get().setType(EnergyEntity.SEGMENTED);
			}

			EnergyEntity.get().setIsVisible(true);
		}
	}

	/**
	 * The invaders move in the game loop
	 *
	 * @param delta The time since the last game loop
	 */
	public void move(long delta) {

		//Record last position befor changes

		lastX = pos.x;

		//Movement logic - only if the level is in progress

		if(game.status == game.LEVEL_IN_PROGRESS) {

			//Left and right actions

			if(Keyboard.isPressed(Keyboard.LEFT)) {
				accel(false, delta);
				changeSprite("sprites/Alien_left.png");
			} else if(Keyboard.isPressed(Keyboard.RIGHT)) {
				accel(true, delta);
				changeSprite("sprites/Alien_right.png");
			} else {
				//TODO make sprite changing more efficient
				changeSprite("sprites/Alien_normal.png");
			}

			//Space key pressed action

			if(Keyboard.isPressed(Keyboard.SPACE)) {
				
				switch (powerUp) {
						
					case SPEED_BOOST:
						maxSpeed = 500;
						acceleration = 1400;
						shieldIsOn = false;

						EnergyEntity.get().decreaseEnergy((double) (100 * delta) / 5000);
						if(EnergyEntity.get().getEnergy() <= 0) {
							setPowerup(NO_POWERUP);
							EnergyEntity.get().setIsVisible(false);
						}
						break;
						
					case PULSE_SHIELD:
						shieldIsOn = true;
						break;
					
					case NO_POWERUP:
					default:
						maxSpeed = 300;
						acceleration = 1000;
				}

			} else {
				maxSpeed = 300;
				acceleration = 1000;
			}

		}

		//Apply resistance

		if(currentVelocity > 0) {
			currentVelocity -= (resistance * delta) / 1000;
			if(currentVelocity < 0) {
				currentVelocity = 0;
			}
		} else if(currentVelocity < 0) {
			currentVelocity += (resistance * delta) / 1000;
			if(currentVelocity > 0) {
				currentVelocity = 0;
			}
		}
		
		//Change the x position based on previous calculations
		
		pos.x += (currentVelocity * delta) / 1000;

		//Move the invader off the stage if the level was completed

		if(game.status == game.LEVEL_COMPLETE) {
			pos.y += (500 * delta) / 1000;

			//Once off the stage start the next level

			if(pos.y > game.image.getHeight(null)) {
				game.startNewLevel();
			}
		} else {

			//Post checks

			checkSides();
			checkLanded();
		}
	}

	/**
	 * Get the hitbox for the invader - the hitable area for projectiles to
	 * interact with
	 *
	 * @return The hitbox in rectangle form
	 */
	public Rectangle getHitbox() {
		hitbox.setBounds(pos.x, pos.y + sprite.getHeight(null)/2, sprite.getWidth(null), sprite.getHeight(null)/2);
		return hitbox;
	}

	/**
	 * Determins whether the invader has landed on the ground
	 */
	private void checkLanded() {

		//If touching the ground the invader has landed

		if(pos.y + sprite.getHeight(null) > game.image.getHeight(null) - 75) {
			game.notifyLevelComplete();
		}
	}

	/**
	 * Drops the invader down according to edge calculation
	 */
	private void checkSides() {

		//When a side is hit reverse the velocity and check to see if dropping
		//down is valid

		if(pos.x <= 0) {
			pos.x = lastX;
			currentVelocity = Math.abs(currentVelocity);

			checkDrop(JUST_HIT_LEFT);
		} else if(pos.x >= 384 - this.sprite.getWidth(null)) {
			pos.x = lastX;
			currentVelocity = Math.abs(currentVelocity) * -1;

			checkDrop(JUST_HIT_RIGHT);
		}
	}

	/**
	 * Checks whether the invader should drop down, and performs the act
	 *
	 * @param status The action to be tested
	 */
	private void checkDrop(int status) {

		//True states

		if( (lastMove == NOT_HIT_ANY_SIDE && (status == JUST_HIT_LEFT || status == JUST_HIT_RIGHT)) ||
			(lastMove == JUST_HIT_LEFT && status == JUST_HIT_RIGHT) ||
			(lastMove == JUST_HIT_RIGHT && status == JUST_HIT_LEFT) ) {

			//Drop the invader down, pop the score, add the score, update last
			//legimate move

			pos.y += dropDist;

			game.scorePop("+50", pos.x + (int) sprite.getWidth(null)/2, pos.y + sprite.getHeight(null)/2);
			game.modScore(50);
			lastMove = status;
		}
	}
	
	/**
	 * Adjust invader speed based on current velocity
	 *
	 * @param right The direction of the accleration
	 * @param delta The time since the last game loop
	 */
	private void accel(boolean right, long delta) {

		//Accelerate

		if(right) {
			currentVelocity += (acceleration * delta) / 1000;
		} else {
			currentVelocity -= (acceleration * delta) / 1000;
		}

		//Cap the speed at the max speed

		if(currentVelocity > maxSpeed) {
			currentVelocity = maxSpeed;
		} else if(currentVelocity < -maxSpeed) {
			currentVelocity = -maxSpeed;
		}
		
	}

	/**
	 * Set whether the shield is on or off
	 *
	 * @param shieldIsOn True of the shield is on
	 */
	public void setShieldIsOn(boolean shieldIsOn) {
		
		this.shieldIsOn = shieldIsOn;
	}

	/**
	 * Get the value of shieldIsOn
	 *
	 * @return True if the shield is on
	 */
	public boolean getShieldIsOn() {

		return shieldIsOn;
	}

	/**
	 * Get the current power which is active for the invader
	 *
	 * @return The power up type
	 */
	public int getPowerup() {

		return powerUp;
	}
}

