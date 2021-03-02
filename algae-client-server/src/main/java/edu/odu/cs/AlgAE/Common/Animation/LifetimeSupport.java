/**
 *
 */
package edu.odu.cs.AlgAE.Common.Animation;

/**
 * Describes a class supporting a lifetime protocol (modeled after old Java applets)
 *
 * @author zeil
 *
 */
public interface LifetimeSupport {

    public void init();
    public void start();
    public void stop();
    public void destroy();
}
