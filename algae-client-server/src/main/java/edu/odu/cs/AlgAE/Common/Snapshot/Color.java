package edu.odu.cs.AlgAE.Common.Snapshot;

/**
 * A look-alike class for java.awt.Color, introduced because direct uses of
 * java.awt.Color in ClientMessage's breaks JSON translation under the new
 * Java modularity rules.
 */
  public class Color implements java.io.Serializable {
 
     // Named colors 
     public final static java.awt.Color transparent
        = new java.awt.Color(255,255,255,255);

 
     private int r;
     private int g;
     private int b;
     private int a;
 
    private static final long serialVersionUID 
        = "edu.odu.cs.AlgAE.Common.Snapshot.Color".hashCode();
 
 
     /**
      * Creates an opaque sRGB color with the specified red, green,
      * and blue values in the range (0 - 255).
      * @param r the red component
      * @param g the green component
      * @param b the blue component
      * @see #getRed
      * @see #getGreen
      * @see #getBlue
      * @see #getRGB
      */
     public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
     }
 
     /**
      * Creates an sRGB color with the specified red, green, blue, and alpha
      * values in the range (0 - 255).
      * @param r the red component
      * @param g the green component
      * @param b the blue component
      * @param a the alpha component
      */
     public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
     }
 
     /**
      * Create a color from its java.awt equivalent.
      * @param color a color
      */
     public Color(java.awt.Color color) {
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
     }

     /**
      * Convert to an AWT color.
      * @return the java.awt.Color equivalent of this one.
      */
     public java.awt.Color toAWTColor() {
        return new java.awt.Color(r,g,b,a);
     }

     /**
      * Returns the red component in the range 0-255 in the default sRGB
      * space.
      * @return the red component.
      */
     public int getRed() {
         return r;
     }
 
     /**
      * Returns the green component in the range 0-255 in the default sRGB
      * space.
      * @return the green component.
      */
     public int getGreen() {
         return g;
     }
 
     /**
      * Returns the blue component in the range 0-255 in the default sRGB
      * space.
      * @return the blue component.
      */
     public int getBlue() {
         return b;
     }
 
     /**
      * Returns the alpha component in the range 0-255.
      * @return the alpha component.
      * @see #getRGB
      */
     public int getAlpha() {
        return a;
     }
 
     /**
      * @return the RGB value of the color
      */
     public int getRGB() {
         return 255*(255*((255 * a) + r) + g) + b;
     }
 
     /**
      * Creates a new <code>Color</code> that is a brighter version of this
      * <code>Color</code>.
      */
     public Color brighter() {
        return new Color(toAWTColor().brighter());
     }
 
     /**
      * Creates a new <code>Color</code> that is a darker version of this
      * <code>Color</code>.
      */
     public Color darker() {
        return new Color(toAWTColor().darker());
     }
 
     /**
      * Computes the hash code for this <code>Color</code>.
      * @return     a hash code value for this object.
      * @since      JDK1.0
      */
     public int hashCode() {
         return getRGB();
     }
 
     /**
      * Determines whether another object is equal to this
      * <code>Color</code>.
      * @param       obj   the object to test for equality with this
      *                          <code>Color</code>
      * @return      <code>true</code> if the objects are the same;
      *                             <code>false</code> otherwise.
      * @since   JDK1.0
      */
     public boolean equals(Object obj) {
         if (!(obj instanceof Color))
            return false;
        Color color = (Color)obj;
        return r == color.r 
            && g == color.g
            && b == color.b
            && a == color.a;
     }
 
     /**
      * Returns a string representation of this <code>Color</code>. This
      * method is intended to be used only for debugging purposes.  The
      * content and format of the returned string might vary between
      * implementations. The returned string might be empty but cannot
      * be <code>null</code>.
      *
      * @return  a string representation of this <code>Color</code>.
      */
     public String toString() {
         return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + ",a=" + getAlpha()+ "]";
     }
 


 }