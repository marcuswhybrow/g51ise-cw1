
package uk.org.whybrow.spaceinvader;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * The tank is contolled by the computer, it can move horizontally only, and has
 * multiple choices when firing at the invader (player). Firing is controlled by
 * probability, where the higher the level the higher the chance of a special
 * atack being performed, ensuring the difficulty increases as the level does.
 *
 * @author Marcus Whybrow
 */
public class TankEntity extends Entity {

	private long lastFrameChange;
	private long frameDuration = 200;
	private int frameNumber;

	/** The current velocity of the tank **/
	private int currentVelocity = 0;
	/** The acceleration per second when moving **/
	private int acceleration = 1200;
	/** The resistane to motion per second (even when not accelerating) **/
	private int resistance = 400;
	/** The maximum obtainable speed **/
	private int maxSpeed = 300;

	/** The x value of the invader **/
	private int shipX;
	/** The y value of the invader **/
	private int shipY;

	/** True if the ship is to the right of the tank **/
	private boolean shipIsRight;
	/** True if in the last game loop the invader was to the right of the tank **/
	private boolean lastShipIsRight;

	/** The x postion the last time the tank changed direcction **/
	private int lastFlipPos;
	/** The velocity og the tank the last game loop **/
	private int lastVelocity;

	/** The most recent time when the tank fired **/
	private long lastFired;
	/** True if the right barrel of the tank fired last **/
	private boolean rightLastFired = true;
	/** A list of all the shells used in game fired by the tank **/
	protected ArrayList <ShellEntity>shells = new ArrayList();

	/** The currently employed fire mode **/
	private int fireMode = NORMAL;
	/** Normal single shot, the easiest to avoid **/
	private static final int NORMAL = 1;
	/** Fires multiple shots in a single burst **/
	private static final int BURST = 2;
	/** Fires multipe shots in different directions **/
	private static final int ARRAY = 3;

	/** The time when firing became possible again **/
	private double then = 0;
	/** The random time to wait until firing, once it is possible **/
	private double time;
	/** The last time we attempted to do a special move **/
	private double lastCheck;
	/** The time delay between checking to do a special move **/
	private int specialDelay = 5000 - (int) (game.currentLevel.getScore() * 50);

	/** The number of shells fired so far, whilst in burst fire mode **/
	private int burstCount = 0;
	/** The maximum shells to fire in burst mode **/
	private int burstLimit = 5;
	/** The number of shells to fire in array fire mode **/
	private int arrayCount = 3;

	/**
	 * Create the tank entity
	 *
	 * @param x X position on stage
	 * @param y Y position on stage
	 * @param game The game being used
	 */
	public TankEntity(int x, int y, Game game) {
		
		super(x, y, "sprites/Turret_03.png", game);
		pos.x -= sprite.getWidth(null)/2;
	}

	/**
	 * Movement calculations
	 *
	 * @param delta Time since last loop
	 */
	public void move(long delta) {

		/** Holds the random value, 0 to 100 that determins what fire mode should be used **/
		double chance;
		/** The probability of burst fire being chosen **/
		double burstProb;
		/** The probability of array fire being chosen **/
		double arrayProb;

		/** On the scale of 0 to 100, the top of the segment for burst **/
		double burstTop;
		/** On the scale of 0 to 100, the bottom of th segment for burst **/
		double burstBottom;
		/** On the scale of 0 to 100, the top of the segment for array fire **/
		double arrayTop;
		/** On the scale of 0 to 100, the bottom of the segment for array fire **/
		double arrayBottom;

		//Move logic

		normalMove(delta);

		//Fire logic

		if(game.status == game.LEVEL_IN_PROGRESS) {

			//Chance of special fire mode

			if(System.currentTimeMillis() - lastCheck >= specialDelay && fireMode == NORMAL) {

				chance = Math.random() * 100;

				//Probability of special attacks as a percentage

				burstProb = 3 + (int) (game.currentLevel.getScore() / 2);
				arrayProb = 2 + (int) (game.currentLevel.getScore() / 2);

				if(burstProb > 50)
					burstProb = 50;
				if(arrayProb > 40)
					arrayProb = 50;

				burstTop = 100;
				burstBottom = burstTop - burstProb;

				arrayTop = burstBottom;
				arrayBottom = burstBottom - arrayProb;

				//Selection of special attack

				if( chance >= burstBottom )  {
					fireMode = BURST;
				} else if( chance < arrayTop && chance >= arrayBottom) {
					fireMode = ARRAY;
				}

				if(chance > 90) {
					game.entities.add( new PowerEntity(game) );
				}

				lastCheck = System.currentTimeMillis();
			}
			
			//Fire current fire mode

			switch (fireMode) {
				case NORMAL:
					normalFire();
					break;
				case BURST:
					burstFire();
					break;
				case ARRAY:
					arrayFire();
					break;
			}
		}

		//Actually move after the logic has completed

		pos.x += (currentVelocity * delta) / 1000;

		//Reset sprite after shot

		if(System.currentTimeMillis() - lastFired > 70) {

			//TODO make tank sprite change more efficient
			changeSprite("sprites/Turret_03.png");
		}

		lastVelocity = currentVelocity;
	}

	/**
	 * When normal fire is the active fire mode, this is called each game loop,
	 * fires a single shot after a small random wait, if all current shells are
	 * 'done'.
	 */
	private void normalFire() {

		//Work out position

		boolean noneActive = true;

		//Determining whether firing is possible

		for(int i = 0; i < shells.size(); i++) {
			
			if(shells.get(i).isDone()) {
				shells.remove(i);
			} else {
				noneActive = false;
				break;
			}
		}

		//If can fire, think about firing

		if(noneActive) {

			if(then == 0) {
				then = System.currentTimeMillis();
				time = Math.random() * 1000 + 200;
			}

			if(System.currentTimeMillis() - then > time) {

				//fire

				then = 0;
				fire(false);
			}
		}
	}

	/**
	 * Fire a burst of shells
	 *
	 */
	private void burstFire() {

		if(burstCount < burstLimit && System.currentTimeMillis() - lastFired > 10) {
			fire(false);
			burstCount ++;
		} else {
			burstCount = 0;
			fireMode = NORMAL;
		}
	}

	/**
	 * Fire multiple shells at different angles
	 *
	 */
	private void arrayFire() {

		for(int i = 0; i < arrayCount; i++) {
			fire(true);
		}

		fireMode = NORMAL;
	}

	/**
	 * Fire a single shell from the location of the tank
	 *
	 * @param randRotate True if the shell should have a random angle on
	 * creation
	 */
	private void fire(boolean randRotate) {

		int x, y;			//x and y offset to fire the shell from
		ShellEntity shell;  //The shell entity created

		//Alternatly fire the left, then the right barrel of the tank

		if(rightLastFired) {
			x = pos.x + 6;
			y = pos.y;
			changeSprite("sprites/Turret_01.png");
			rightLastFired = false;
		} else {
			x = pos.x + 19;
			y = pos.y;
			changeSprite("sprites/Turret_02.png");
			rightLastFired = true;
		}

		//Create the shell

		shell = new ShellEntity(x, y, game);

		//If applicable randomly rotate the shell

		if(randRotate)
			shell.randRotate();

		//Add the shell to the render list and the shell list

		game.entities.add(shell);
		shells.add(shell);

		lastFired = System.currentTimeMillis();
	}

	/**
	 * Normal movement of the tank, used in move()
	 *
	 * @param delta Time since last game loop
	 */
	private void normalMove(long delta) {

		//Get the ships hitbox (actual stage position)

		Rectangle shipHitbox = game.invader.getHitbox();
		shipX = (int) shipHitbox.getCenterX();
		shipY = (int) shipHitbox.getCenterY();

		//Move in the correct direction

		int diff = shipX - (int) getHitbox().getCenterX();

		if(game.status == game.LEVEL_IN_PROGRESS) {

			if(diff < 0 && diff < -10) {
				adjustAcceleration(false, delta);
			} else if (diff > 0 && diff > 10) {
				adjustAcceleration(true, delta);
			}
		}

		//Update the knowledge of which side the ship is on (left or right)

		setShipIsRight();

		//Apply correct resistance

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

		//If we changed direction this loop, take note of the x position

		if(dirHasChanged()) {
			lastFlipPos = (int) pos.x;
		}

		//If we know we should be going the other way, but we just changed
		//direction, dont bother.

		if(tryingToChange()) {
			if(Math.abs(pos.x - lastFlipPos) < 100) {
				currentVelocity = 0;
			}
		}

		//Do not go off the end of the rails

		if((currentVelocity > 0 && pos.x > game.image.getWidth(null) - sprite.getWidth(null)) || (currentVelocity < 0 && pos.x < 0)) {
			currentVelocity = 0;
		}
	}

	/**
	 * Tank reversed its movement direction in this game loop, by comparison to
	 * the last loop
	 *
	 * @return True if direction has just changed
	 */
	private boolean dirHasChanged() {
		if((currentVelocity > 0 && lastVelocity < 0) || (currentVelocity < 0 && lastVelocity > 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determins whether the ship moved to the other side of the tank
	 *
	 * @return True if ship changed side
	 */
	private boolean tryingToChange() {
		if((shipIsRight && (!lastShipIsRight)) || ((!shipIsRight) && lastShipIsRight)) {
			lastShipIsRight = shipIsRight;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds or subtracts speed based on current velocity
	 *
	 * @param right True to move right, False to move left
	 * @param delta Time since last game loop
	 */
	private void adjustAcceleration(boolean right, long delta) {

		if(right) {
			currentVelocity += (acceleration * delta) / 1000;
		} else {
			currentVelocity -= (acceleration * delta) / 1000;
		}
		
		if(currentVelocity > maxSpeed) {
			currentVelocity = maxSpeed;
		} else if(currentVelocity < -maxSpeed) {
			currentVelocity = -maxSpeed;
		}
	}

	/**
	 * Update the shipIsRight variable
	 */
	public void setShipIsRight() {
		if(shipX < (int) pos.x + (sprite.getWidth(null) / 2) ) {
			shipIsRight = false;
		} else if (shipX > (int) pos.x + (sprite.getWidth(null) / 2) ) {
			shipIsRight = true;
		}
	}
}

