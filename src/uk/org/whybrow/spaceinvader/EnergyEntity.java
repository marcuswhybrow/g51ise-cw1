
package uk.org.whybrow.spaceinvader;

import java.awt.Image;
import javax.imageio.ImageIO;
import java.net.URL;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;

/**
 * Displays and keeps track of the energy bar
 *
 * @author Marcus Whybrow
 */
public class EnergyEntity extends Entity {

	/** The single instance of this class **/
	private static EnergyEntity single = new EnergyEntity();

	/** The type of energy bar currently in use **/
	private static int type;
	/** Has a continous grid **/
	public static final int CONTINUOUS = 0;
	/** Has a segmented grid **/
	public static final int SEGMENTED = 1;

	/** The current energy level **/
	private static double energy = 100;
	/** The graphical bar that represents the energy level **/
	private static Image bar;
	/** The transformation that represents the length (scale) of the bar **/
	private static AffineTransform transform = new AffineTransform();

	/** True if the bar should be displayed **/
	private static boolean isVisible = false;

	/**
	 * Creates the single instance of this class
	 *
	 */
	private EnergyEntity() {

		super(350, 100, "sprites/shield_bar_grid.png");

		try {
			URL url = getClass().getClassLoader().getResource("sprites/shield_bar.png");
			bar = ImageIO.read(url);
		} catch(Exception e) {
			System.err.println("Could not find image: sprites/shield_bar.png");
		}
	}

	/**
	 * Get the single and only instance of this class
	 *
	 * @return The only instance of this class
	 */
	public static EnergyEntity get() {

		return single;
	}

	/**
	 * Draw the grid and the bar to represent the current energy
	 *
	 * @param g The graphics context to draw to
	 */
	public void draw(Graphics g) {

		if(isVisible) {

			Graphics2D g2 = (Graphics2D) g;

			g2.drawImage(bar, transform, null);
			g2.drawImage(sprite, pos.x, pos.y, null);
		}
	}

	/**
	 * This class uses its move to update the scale of the energy bar
	 *
	 * @param delta The time since the last game loop
	 */
	public void move(long delta) {

		transform.setToIdentity();
		transform.translate(pos.x, pos.y + sprite.getHeight(null));
		transform.scale(1, - energy * sprite.getHeight(null) / 100 );
	}

	/**
	 * Set the type of gride to use
	 *
	 * @param type The type of grid to use, CONTINUOUS or SEGMENTED
	 */
	public void setType(int type) {

		if(type == CONTINUOUS || type == SEGMENTED) {
			this.type = type;
			energy = 100;
		}

		switch(type) {

			case CONTINUOUS:
				changeSprite("sprites/speed_bar.png");
				break;

			case SEGMENTED:
				changeSprite("sprites/shield_bar_grid.png");
				break;

			default:
		}
	}

	/**
	 * Decrease the amount of energy by a certain amount
	 *
	 * @param amount The amount to reduce the energy by
	 */
	public void decreaseEnergy(double amount) {

		energy -= amount;

		if(energy < 0) {
			energy = 0;
		}
	}

	/**
	 * Get the current level of energy
	 *
	 * @return The current energy level
	 */
	public double getEnergy() {

		return energy;
	}

	/**
	 * Set weather or not the the energy bar should render
	 *
	 * @param isVisible True if the energy bar should render
	 */
	public void setIsVisible(boolean isVisible) {

		this.isVisible = isVisible;
	}

	/**
	 * Set the energy level to a specific value
	 *
	 * @param energy The specific value to set the energy level to
	 */
	public void setEnergy(double energy) {

		if(energy >= 0 && energy <= 100)
			this.energy = energy;
	}
}