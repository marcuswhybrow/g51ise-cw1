
package uk.org.whybrow.spaceinvader;

import java.awt.font.TextLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The pop entity literally pops some text up on the screen for a short period
 * and then deletes itself, It extends the Entity class and therefor can have
 * a image (sprite) which will apear behind the text (which is the added
 * functionality)
 *
 * @author Marcus Whybrow
 */
public class PopEntity extends Entity {

	/** The popup text to display **/
	private String text;
	/** The font to use **/
	private Font font;
	/** The colour of the text **/
	private Color colour;
	/** The path to the optional sprite, can be null **/
	private String ref;

	/** After traveling this distance the entity will delete itself **/
	private int popDist = 100;
	/** The position on the stage at which the entity was created **/
	private int start;


	/**
	 * Create a new pop entity
	 *
	 * @param text The text to display
	 * @param font The font to render the text in
	 * @param colour The colour to render the text in
	 * @param ref The path to the backgroun image, can be null
	 * @param x The horizontal postion
	 * @param y The vertical position
	 * @param game The game that this text is in
	 */
	public PopEntity(String text, Font font, Color colour, String ref, int x, int y, Game game) {

		super(x, y, ref, game);

		//Record the starting vertical position

		start = y;

		//If there is an image, center the image

		if(ref != null) {

			pos.x -= (int) sprite.getWidth(null)/2;
			pos.y -= (int) sprite.getHeight(null)/2;
		} else {
			this.ref = ref;
		}
		
		this.text = text;
		this.font = font;
		this.colour = colour;
	}

	/**
	 * Animate the text
	 * 
	 * @param delta The time since the last game loop
	 */
	public void move(long delta) {

		pos.y -= (200 * delta) / 1000;

		//remove the text after the specified travel distance

		if(start - pos.y > popDist) {
			game.pops.remove(this);
		}
	}

	/**
	 * Draw the PopEntites text
	 *
	 * @param g2 The graphics context to draw onto
	 */
	public void draw(Graphics2D g2) {

		int x = pos.x, y = pos.y;

		//Create the TextLayout to render the text with

		TextLayout t = new TextLayout(text, font, g2.getFontRenderContext());

		//If there was an image, center the text on that image

		if(ref != null) {
			x += (int) sprite.getWidth(null)/2;
			y += (int) sprite.getHeight(null)/2;
		}

		x -= (int) t.getBounds().getWidth()/2;
		y += (int) t.getBounds().getHeight()/2;

		//Render the text

		g2.setColor(colour);
		t.draw(g2, x, y);
	}
}
