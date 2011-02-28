
package uk.org.whybrow.spaceinvader;

/**
 * The moon can be moved around the screen for effect
 *
 * @author Marcus Whybrow
 */
public class MoonEntity extends Entity {

	/**
	 * Create a new moon entity
	 *
	 * @param x The horizontal position
	 * @param y The vertical position
	 * @param game The game the moon is in
	 */
	public MoonEntity(int x, int y, Game game) {
		super(x, y, "sprites/Moon.png", game);
	}
}
