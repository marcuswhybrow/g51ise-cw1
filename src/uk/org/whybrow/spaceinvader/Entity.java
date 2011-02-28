
package uk.org.whybrow.spaceinvader;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.net.URL;

import java.awt.Rectangle;

/**
 * The base class for most drawable objects in the game, retains positional
 * information, as well as an image, hitbox and how to draw a basic image to
 * the stage.
 *
 * Is abstarct and therefor can only be extended, to enforce consistancy
 *
 * @author Marcus Whybrow
 */
public abstract class Entity {

	/** The current image to display for this entity */
	protected Image sprite;
	/** The position in x y space **/
	protected Point pos;
	/** The game this entity is in **/
	protected Game game;
	/** A rectangle representing the hitable area for this entties sprite **/
	protected Rectangle hitbox = new Rectangle();

	/**
	 * Create an entity without a reference to a game or an image
	 *
	 * @param x Horizontal positional value
	 * @param y Vertical positional value
	 */
	public Entity(int x, int y) {
		pos = new Point(x,y);
	}

	/**
	 * Create an entity with an image and reference to the game it resides in
	 *
	 * @param x Horizontal positional value
	 * @param y Vertical positional value
	 * @param ref Reference string to path of image
	 * @param game The game this entity is being used in
	 */
	public Entity(int x, int y, String ref, Game game) {
		this(x,y);
		if(ref != null) {
			try {
				URL url = getClass().getClassLoader().getResource(ref);
				sprite = ImageIO.read(url);
			} catch(Exception e) {
				System.err.println("Could not find image: " + ref);
			}
		}
		this.game = game;
	}

	/**
	 * Create an entity with anb image, but no refference to the game it is in
	 *
	 * @param x Horizontal positional value
	 * @param y Vertical positional value
	 * @param ref Reference string to path of image
	 */
	public Entity(int x, int y, String ref) {

		this(x, y, ref, null);
	}

	/**
	 * Change the image for this entitiy to a new image
	 *
	 * @param ref Reference string, path to the new image's location
	 */
	public void changeSprite(String ref) {

		try {
			URL url = getClass().getClassLoader().getResource(ref);
			sprite = ImageIO.read(url);
		} catch(Exception e) {
			System.err.println("Could not find image: " + ref);
		}
	}

	/**
	 * This entities movement and action phase is calculated here when called
	 *
	 * @param delta The time since the last loop
	 */
	public void move(long delta) {
		
	}

	/**
	 * Draws this entities sprite to the screen
	 *
	 * @param g The graphics context to draw to
	 */
	public void draw(Graphics g) {
		g.drawImage(sprite, pos.x, pos.y, null);
	}

	/**
	 * Updates and return the hitbox for this entities sprite
	 *
	 * @return The rectangle representing this entities sprite
	 */
	public Rectangle getHitbox() {
		hitbox.setBounds(pos.x, pos.y, sprite.getWidth(null), sprite.getHeight(null));
		return hitbox;
	}

	/**
	 * Takes two entities and checks whether the two sprites contained within
	 * those entitis collide
	 *
	 * @param other The other entity to check against
	 * @return True if a collision has ocoured
	 */
	public boolean collidesWith(Entity other) {
		if(getHitbox().intersects(other.getHitbox())) {
			return true;
		} else {
			return false;
		}
	}
}

