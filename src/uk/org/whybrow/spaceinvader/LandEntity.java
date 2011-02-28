
package uk.org.whybrow.spaceinvader;

/**
 * The land that the invader will try to land on, can be moved for effect
 *
 * @author Marcus Whybrow
 */
public class LandEntity extends Entity {

	/**
	 * Create a new land entity, does nothing extra.
	 *
	 * @param x The horizontal position
	 * @param y The vertical position
	 * @param game The game the land is in
	 */
	public LandEntity(int x, int y, Game game) {
		super(x, y, "sprites/Land.png", game);
	}
}
