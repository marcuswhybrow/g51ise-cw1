
package uk.org.whybrow.spaceinvader;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * A keyboard utility system, inspired by a online tutorial
 *
 * @author Marcus Whybrow
 */
public class Keyboard {
	/** The status of the keys on the keyboard */
	private static boolean[] keys = new boolean[1024];

	public static final int LEFT = KeyEvent.VK_LEFT;
	public static final int RIGHT = KeyEvent.VK_RIGHT;
	public static final int DOWN = KeyEvent.VK_DOWN;

	public static final int SPACE = KeyEvent.VK_SPACE;
	
	/**
	 * Initialise the central keyboard handler
	 */
	public static void init() {
		Toolkit.getDefaultToolkit().addAWTEventListener( new KeyHandler(), AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * Initialise the central keyboard handler
	 *
	 * @param c The component to listen to
	 */
	public static void init(Component c) {
		c.addKeyListener( new KeyHandler() );
	}

	/**
	 * Checks if the specific key is pressed
	 *
	 * @param key The code of the key to check
	 * @return True if key is pressed
	 */
	public static boolean isPressed(int key) {
		return keys[key];
	}

	/**
	 * Set the status of the key
	 *
	 * @param key The code of the specific key to set
	 * @param pressed The new status of the key
	 */
	public static void setPressed(int key, boolean pressed) {
		keys[key] = pressed;
	}

	/**
	 * A Class that respondes to keypresses (on a global scale)
	 */
	private static class KeyHandler extends KeyAdapter implements AWTEventListener {

		/**
		 * Notification of a keypress
		 *
		 * @param e The event details
		 */
		public void keyPressed(KeyEvent e) {
			if(e.isConsumed()) {
				return;
			}
			keys[e.getKeyCode()] = true;
		}

		/**
		 * Notification of key release
		 *
		 * @param e The even details
		 */
		public void keyReleased(KeyEvent e) {
			if(e.isConsumed()) {
				return;
			}

			KeyEvent nextPress = (KeyEvent) Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent(KeyEvent.KEY_PRESSED);

			if((nextPress == null) || (nextPress.getWhen() != e.getWhen())) {
				keys[e.getKeyCode()] = false;
			}
		}

		/**
		 * Notification that an event in the AWT event system has occoured
		 *
		 * @param e
		 */
		public void eventDispatched(AWTEvent e) {
			if(e.getID() == KeyEvent.KEY_PRESSED) {
				keyPressed((KeyEvent) e);
			}
			if(e.getID() == KeyEvent.KEY_RELEASED) {
				keyReleased((KeyEvent) e);
			}
		}
	}
}
