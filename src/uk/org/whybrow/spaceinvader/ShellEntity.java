
package uk.org.whybrow.spaceinvader;

import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * The shell entity is created (fired) by the TankEntity which is controlled
 * by the computer.
 *
 * Because the shell can be fired at an angle, AffineTransform is used to take
 * care of the rotation of the graphics context as the image is drawn. It is
 * therefor easier for the AffineTransform to also hold the positional
 * information for this entity, in the form of a translational transformation.
 *
 * As a result of using AffineTransform to draw the sprite, getting the hitbox
 * effectively converts between the 2 graphics context, i.e. the shells
 * transformed grid, back to the standard grid for other entities to be able to
 * interact with it.
 *
 * @author Marcus Whybrow
 */
public class ShellEntity extends Entity {

	/** The speed of the shell **/
	private int speed = 400;
	/** True if the shell cannot hurt the invader and is useless **/
	private boolean isDone = false;

	/** The transform to apply before drawing, allows for angular fire **/
	private AffineTransform transform = new AffineTransform();

	/**
	 * Create a new shell entity
	 *
	 * @param x The horizontal postion
	 * @param y The vertical postion
	 * @param game The game this shell is in
	 */
	public ShellEntity(int x, int y, Game game) {
		
		super(x, y, "sprites/Turret_Shell.png", game);

		//The postional information is stored within a translation transformation

		transform.translate(x, y);
	}

	/**
	 * Move the shell
	 *
	 * @param delta The time since the last game loop
	 */
	public void move(long delta) {

		transform.translate(0, -(speed * delta) / 1000);

		//Check whether the shell 'is done' this game loop

		isDone = (getHitbox().getMaxY()  < game.invader.getHitbox().getMinY() || getHitbox().getMaxY() < 0) ? true : false ;

		//Check whether the shell has hit the invader

		checkHit();

		//Remove references if off screen

		if(getHitbox().getMaxY() < 0) {
			game.entities.remove(this);
			game.tank.shells.remove(this);
		}
	}

	/**
	 * Draw the shell using the AffineTransform
	 *
	 * @param g The graphics context to draw to
	 */
	public void draw(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(sprite, transform, null);
		
	}

	/**
	 * Check whether the shell has hit the invader
	 *
	 */
	private void checkHit() {

		if(getHitbox().intersects(game.invader.getHitbox())) {

			if(!game.invader.getShieldIsOn()) {

				//Update the score
				
				game.scorePop("-100", (int) getHitbox().getCenterX(), (int) getHitbox().getCenterY(), Color.RED);
				game.modScore(-100);

				//Deny the player the 'perfect' bonus this level

				game.beenHit = true;
			} else {
				game.scorePop("x", (int) getHitbox().getCenterX(), (int) getHitbox().getCenterY(), Color.GREEN);
			}

			//Remove reference to this shell

			game.tank.shells.remove(this);
			game.entities.remove(this);
		}
	}

	/**
	 * Create the random angle for the 'arrayFire' mode that the tank sometimes
	 * employes
	 *
	 */
	public void randRotate() {
		
		transform.rotate((Math.random() * 90 - 45) * Math.PI / 180);
		pos.x = pos.y = 0;
	}

	/**
	 * Get the shells hitbox, obtained by transforming a shell shaped rectangle
	 * using the AffinTransform.
	 *
	 * @return The htibox for this shell in normal space
	 */
	public Rectangle getHitbox() {
		hitbox.setBounds(0, 0, sprite.getWidth(null), sprite.getHeight(null));
		return transform.createTransformedShape(hitbox).getBounds();
	}

	/**
	 * Get the isDone value of this shell
	 *
	 * @return The 'isDone' value
	 */
	public boolean isDone() {
		
		return isDone;
	}
}

