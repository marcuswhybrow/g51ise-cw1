
package uk.org.whybrow.spaceinvader;

import java.awt.font.TextLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;

/**
 * Takes care of the scores allways viewable at the top of the game screen
 * during gameplay.
 * 
 * @author Marcus Whybrow
 */
public class ScoreText {

	/** The score currently **/
	private long score;
	/** The font to render the score in **/
	private Font font;
	/** The game this ScoreText is in **/
	private Game game;

	/** The horizontal postion to render at **/
	private int x;
	/** The vertical postion to render at **/
	private int y;
	/** The colour to render this font in **/
	private Color colour;
	/** Records when the colour of the text was changed **/
	private long colourChange;
	/** Time to keep the colour change before changing back **/
	private int changeTime = 300;

	/**
	 * Create a new score text
	 *
	 * @param score The current score
	 * @param font The font to render the text in
	 * @param x The horizontal postion
	 * @param y The vertical postion
	 * @param game The game that this score text is in
	 */
	public ScoreText(long score, Font font, int x,  int y, Game game) {

		this.score = score;
		this.font = font;
		this.x = x;
		this.y = y;
		this.game = game;
	}

	/**
	 * Draw the PopEntites text
	 *
	 * @param g2 The graphics context to draw onto
	 */
	public void draw(Graphics2D g2) {

		TextLayout t = new TextLayout(score + "", font, g2.getFontRenderContext());

		//Convert the colour back to white after the alloted time

		if(System.currentTimeMillis() - colourChange > changeTime) {
			colour = Color.WHITE;
		}

		//Draw the text to the screen

		g2.setColor(colour);
		t.draw(g2, x, y);
	}

	/**
	 * Highligh the score in red for a brief time
	 *
	 */
	public void red() {

		colourChange = System.currentTimeMillis();
		this.colour = Color.RED;
	}

	/**
	 * Highligh the score in geen for a brief time
	 *
	 */
	public void green() {

		colourChange = System.currentTimeMillis();
		this.colour = Color.GREEN;
	}

	/**
	 * Update the score this score text is handling
	 *
	 * @param score The amount to add to the score (can be negative)
	 */
	public void update(long score) {
		this.score += score;
		check();
	}

	/**
	 * Get the score currently held by this entity
	 *
	 * @return The current score
	 */
	public long getScore() {

		return score;
	}

	/**
	 * Set the score to a specific value
	 *
	 * @param score The values to set the score to
	 */
	public void setScore(long score) {

		this.score = score;
		check();
	}

	/**
	 * Check to see if the best score has been beaten, and if so update it
	 *
	 */
	private void check() {

		if(game.currentScore.getScore() > game.currentBest.getScore()) {
			game.currentBest.setScore(game.currentScore.getScore());
			game.currentBest.green();
		}
	}
}
