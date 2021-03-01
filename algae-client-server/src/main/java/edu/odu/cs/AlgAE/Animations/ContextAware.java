/**
 *
 */
package edu.odu.cs.AlgAE.Animations;


/**
 * Denotes an animation object that is aware of which animation it
 * belongs to.
 *
 * @author zeil
 *
 */
public interface ContextAware {

    /**
     * Return the animation of which this is a part.
     *
     * @return animation context for this object
     */
    public AnimationContext context();
}
