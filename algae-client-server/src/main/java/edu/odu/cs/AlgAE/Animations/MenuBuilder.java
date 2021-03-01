package edu.odu.cs.AlgAE.Animations;

public interface MenuBuilder {
    /**
     *  Supply a message to appear in the Help..About dialog.
     *  Typically, this indicates the origin of the source code
     *  being animated and the name of the person who prepared the
     *  animation.
     **/
    public String about();


    /**
     * Override this to call context.register to set up the menu items that will
     * be displayed in the Algorithms menu and optionally to call registerStartingAction
     * to set up code to be animated immediately upon launch.
     */
    public void buildMenu();
}
