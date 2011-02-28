
package uk.org.whybrow.spaceinvader;

import javax.swing.JFrame;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * The core of the game, controls and stores all central information
 *
 * @author Marcus Whybrow
 */
public class Game extends JPanel {

	/** Lives remaining **/
	private int lives = 3;
	/** The score so far this level **/
	protected long levelScore = 0;
	/** Remains false if the invader does not get hit once in a single level **/
	protected boolean beenHit = false;

	/** The status the game is currently in, determins some actions **/
	protected int status = 0;
	/** Initialise entities for the next level, showing stats for previous, press space to begin **/
	protected static final int STARTING_LEVEL = 1;
	/** The player is currently playing a level, normal gameplay takes place **/
	protected static final int LEVEL_IN_PROGRESS = 2;
	/** The player has completed the level **/
	protected static final int LEVEL_COMPLETE = 3;
	/** The player has used all lives and the game is over **/
	protected static final int DEAD = 4;
	/** The game has just been started and is waiting to be started by the player **/
	protected static final int NEW_GAME = 5;

	/** The last time a game loop started **/
	private long lastLoopTime = System.currentTimeMillis();

	/** All entities (of different types) invloved in gameplay **/
	protected ArrayList entities = new ArrayList();
	/** All pop up text entities (PopEntity) that are involved in gameplay **/
	protected ArrayList <PopEntity>pops = new ArrayList();

	/** The computer controller tank entity **/
	protected TankEntity tank;
	/** The player controller invader entity **/
	protected InvaderEntity invader = new InvaderEntity(192, 0, this);
	/** The land that the tank moves around **/
	protected LandEntity land;
	/** Overlays full stage size images onto the stage **/
	protected SplashEntity splash = SplashEntity.get();

	/** The JFrame that contains the game **/
	private JFrame frame;
	/** The background image for the game **/
	protected Image image;

	/** The font used for all scores **/
	private Font scoreFont = new Font("Times", Font.BOLD, 20);
	/** The font used for pop up text **/
	private Font popFont = new Font("Times", Font.BOLD, 20);
	/** The current total score **/
	protected ScoreText currentScore = new ScoreText(0, scoreFont, 50, 30, this);
	/** The level current being played **/
	protected ScoreText currentLevel = new ScoreText(1, scoreFont, 308, 30, this);
	/** The current best score **/
	protected ScoreText currentBest = new ScoreText(currentScore.getScore(), scoreFont, 178, 30, this);

	/** The mutiplier to determin a bonus score this level **/
	private double multiplier;
	/** The final bonus for this round **/
	private int bonus;
	/** Used to display the ferect splash for a certain amoung of loop time **/
	private long perfectLoop;

	/**
	 * Ensures the game's frame is created and visible
	 */
	public Game() {

		//Get the background image for the stage

		try {
			URL url = getClass().getClassLoader().getResource("sprites/bg.png");
			image = ImageIO.read(url);
		} catch(Exception e) {
			System.err.println("Could not find image");
		}

		//Set the size of the JPanel

		setPreferredSize( new Dimension(image.getWidth(null), image.getHeight(null)) );

		//Enable the keyboard untility class, and initialise the positions
		//of entities on the stage
		
		Keyboard.init(this);
		initEntities();
		
		//JFrame operations to behave normaly

		frame = new JFrame();
		frame.setTitle("The Last Space Invader");
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		requestFocus();
    }

	/**
	 * Begins the game
	 */
	public void start() {
		status = NEW_GAME;

		while(true) {

			//Sleep for a bit

			try {
				Thread.sleep(2);
			} catch(Exception e) {				
			}

			//Repaint the stage

			repaint();
		}
	}

	/**
	 * Drawing for each frame, should not be called
	 *
	 * @param g The graphics context
	 */
	public void paintComponent(Graphics g) {

		//Set g2 rendering information

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		//Work out the time since the last loop to pass to movement calls

		long delta = System.currentTimeMillis() - lastLoopTime;
		lastLoopTime = System.currentTimeMillis();

		//

		super.paintComponent(g);

		//Draw background image

		g.drawImage(image, 0, 0, null);
		
		//Draw and move all entities

		for(int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			if(status != STARTING_LEVEL)
				entity.move(delta);
			entity.draw(g);
		}

		//Draw and move all score pops

		for(int i = 0; i < pops.size(); i++) {
			PopEntity pop = pops.get(i);
			pop.move(delta);
			pop.draw(g); //draw text
			pop.draw(g2); //draw image
		}

		//Render Scores

		currentScore.draw(g2);
		currentLevel.draw(g2);

		if(status == STARTING_LEVEL) {

			//Setup for displaying perfect sign

			if(!beenHit) {
				perfectLoop = System.currentTimeMillis();
				beenHit = true;
			}

			//Display either perfect sign or previous level status

			if(System.currentTimeMillis() - perfectLoop < 2000) {

				//Perfect sign

				if(Keyboard.isPressed(Keyboard.SPACE)) {
					perfectLoop = 0;
				} else {
					splash.changeSprite(splash.PERFECT);
					splash.draw(g);
				}

			} else {

				//Level stats

				splash.changeSprite(splash.NEXT_LEVEL);
				splash.draw(g);

				//Render all text, level score, multiplier, bonus, currrent score, current best

				TextLayout stats;

				stats = new TextLayout(levelScore + "", scoreFont, g2.getFontRenderContext());
				stats.draw(g2, 50, 100);
				stats = new TextLayout("x" + multiplier, scoreFont, g2.getFontRenderContext());
				stats.draw(g2, 50, 150);
				stats = new TextLayout(bonus + "", scoreFont, g2.getFontRenderContext());
				stats.draw(g2, 50, 200);

				stats = new TextLayout(currentScore.getScore() + "", scoreFont, g2.getFontRenderContext());
				stats.draw(g2, 50, 270);
				stats = new TextLayout(currentBest.getScore() + "", scoreFont, g2.getFontRenderContext());
				stats.draw(g2, 50, 320);

				//Press space to continue
				
				if(Keyboard.isPressed(Keyboard.SPACE)) {
					status = LEVEL_IN_PROGRESS;
					levelScore = 0;
					currentLevel.update(1);
				}

			}
		} else if(status == NEW_GAME) {

			//Splash the start screen

			splash.changeSprite(splash.START);
			splash.draw(g);

			//Press space to continue

			if(Keyboard.isPressed(Keyboard.SPACE)) {
				status = LEVEL_IN_PROGRESS;
				levelScore = 0;
			}
		}

		//Dispose of the graphics context

		g2.dispose();
		g.dispose();
	}

	/**
	 * Creates text at the given location that animates a popping motion and
	 * then disapears
	 *
	 * @param popText The text or score to display
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void scorePop(String popText, int x, int y) {

		pops.add( new PopEntity(popText, popFont, Color.WHITE, null, x, y, this) );
	}

	public void scorePop(String popText, int x, int y, Color colour) {

		pops.add( new PopEntity(popText, popFont, colour, null, x, y, this) );
	}

	/**
	 * Initialise the entities required for the start of a new level
	 */

	/**
	 * Initialise the entities required for the start of a new level
	 */
	public void initEntities() {

		//Entity starting positions

		invader.pos.x = (int) image.getWidth(null) / 2;
		invader.pos.y = 0;
		invader.setStatus(invader.NOT_HIT_ANY_SIDE);
		tank = new TankEntity(((int) image.getWidth(null)/2), 450, this);
		land = new LandEntity(-230, image.getHeight(null) -150, this);

		//Clear the array list of existing entities

		entities = new ArrayList();

		//Add new entities in render order

		entities.add(land);
		entities.add(tank);
		entities.add(invader);

		entities.add( EnergyEntity.get() );
		entities.add( ShieldEntity.get() );

		ShieldEntity.get().setGame(this);
    }

	/**
	 * Initiates the end of the game
	 */
	public void notifyDeath() {
		status = DEAD;
    }

	/**
	 * Initiates the end of the level
	 */
	public void notifyLevelComplete() {
		status = LEVEL_COMPLETE;
		currentScore.update(Math.round(levelScore * 0.5 * (int) (currentLevel.getScore() / 2)));

		//Calculate level bonus

		multiplier = currentLevel.getScore() / 100;
		bonus = (int) (multiplier * levelScore);

		if(!beenHit) {
			bonus *= 2;
		}

		//Ensure level bonus cannot be negative

		if(bonus < 0) {
			bonus = 0;
		}

		//Add the bonus to the score

		currentScore.update(bonus);
    }

	/**
	 * Start a new level
	 */
	public void startNewLevel() {
		status = STARTING_LEVEL;
		initEntities();
	}

	/**
	 * Update the score during gameplay
	 *
	 * @param score Ths score to add to the total (can be negative)
	 */
	public void modScore(long score) {

		levelScore += score;
		currentScore.update(score);

		if(score > 0) {
			currentScore.green();
		} else if(score < 0) {
			currentScore.red();
		}
	}

	/**
	 * Update the best score if needed
	 *
	 */
	public void checkBestScore() {
		if(currentScore.getScore() > currentBest.getScore()) {
			currentBest.setScore(currentScore.getScore());
		}
	}

	/**
	 * Starts the game
	 *
	 * @param args No current use
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
}

