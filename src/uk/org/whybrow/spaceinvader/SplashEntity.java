
package uk.org.whybrow.spaceinvader;

/**
 * The splash entity takes an image and places it at (0,0) in nomral space, it
 * is assumed that the image is the size of the stage, but it can be any size.
 *
 * The images are preselected.
 *
 * Also the splash entity is a singleton class, as it controlls all splashes to
 * the screen
 *
 * @author Marcus Whybrow
 */
public class SplashEntity extends Entity {

	/** The single instance of this class **/
	private static SplashEntity single = new SplashEntity();

	/** The start splash screen image **/
	public static final String START = "sprites/start.png";
	/** The next level splash screen image - for displaying scores **/
	public static final String NEXT_LEVEL = "sprites/next.png";
	/** The perfect level splash **/
	public static final String PERFECT = "sprites/perfect.png";

	/**
	 * The constuctor is private to ensure it cannot be instantiated from
	 * ouside this calss
	 *
	 */
	private SplashEntity() {

		super(0, 0, null);
	}

	/**
	 * Get the single instance of this class
	 *
	 * @return The single instance of SplashEntity
	 */
	public static SplashEntity get() {

		return single;
	}
}
