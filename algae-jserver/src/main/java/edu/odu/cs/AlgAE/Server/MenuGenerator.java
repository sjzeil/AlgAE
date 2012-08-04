package edu.odu.cs.AlgAE.Server;


/**
 *  Interface to classes that can be used for generating AlgAE algorithm
 *  menus.
 **/
public interface MenuGenerator
{

  /**
   *  Each animation overrides this function to set up a menu of
   *  algorithms that can be selected for animation.
   */
  public void buildMenu();


  /**
   *  Called from buildMenu to add an item to the Algorithm menu.
   */
  public void register(String menuItem, MenuFunction action);

  /**
   *  Called from buildMenu to register an initial action to be
   *  run at the start of the animation, before any selections
   *  from the menu.
   */
  public void registerStartingAction (MenuFunction action);

  /**
   *  Supply a message to appear in the Help..About dialog.
   *  Typically, this indicates the origin of the source code
   *  being animated and the name of the person who prepared the
   *  animation.
   *  
   *  This can also be a useful way to supply hints about what you
   *  would like people running the animation to try.
   *  
   **/
  public String about();
  


}

  
