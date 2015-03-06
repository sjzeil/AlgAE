/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;

import java.util.Arrays;

/**
 * Sent as an animation is starting, this message tells the client
 * what text should appear in the Help->About menu and what menu items
 * should be placed in the Algorithm menu listing functions that users
 * can select to see animations of.
 *
 * @author zeil
 *
 */
public class MenuMessage extends ClientMessage {
	
	private String about;
	private String[] menuItems;

	/**
	 * @param about text to appear in the Help menu
	 * @param menuItems list of strings to be used as menu items
	 */
	public MenuMessage(String about, String[] menuItems) {
		super("Menu");
		this.about = about;
		this.menuItems = Arrays.copyOf(menuItems, menuItems.length);
	}

	public MenuMessage() {
		super("Menu");
		this.about = "*No information provided*";
		this.menuItems = new String[0];
	}

	
	/**
	 * @return the help about string
	 */
	public String getAbout() {
		return about;
	}

	/**
	 * Set the help about string
	 */
	public void setAbout(String about) {
		this.about = about;
	}


	/**
	 * @return the menu items
	 */
	public String[] getMenuItems() {
		return menuItems;
	}

	/**
	 * @param menuItems the menu items to set
	 */
	public void setMenuItems(String[] menuItems) {
		this.menuItems = menuItems;
	}

	@Override
	public boolean equals(Object clientMessage) {
		if (clientMessage == null)
			return false;
		try {
			MenuMessage msg = (MenuMessage)clientMessage;
			return msg.about.equals(about)
					&& Arrays.equals(msg.menuItems, menuItems);
		} catch (Exception e) {
			return false;
		}
	}

}
