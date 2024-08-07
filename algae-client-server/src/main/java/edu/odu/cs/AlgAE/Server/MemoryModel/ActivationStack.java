package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Animations.ContextAware;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.CompoundRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.DefaultRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.ObjectRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.Index;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;
import edu.odu.cs.AlgAE.Server.Utilities.ArrayList;


/**
 * The activation stack represents the chain of active function calls
 * in the current algorithm state. The stack provides access to two sets
 * ov variables/values to be drawn: the top record corresponding to the
 * currently active function and the globals area containing data not particular
 * to any call.
 *
 *  * Many of the functions return a reference to the same activation record.
 * This is designed to permit chaining of calls.  For example, if we have just
 * entered a call to the function
 *     void foo (String a, int b, ArrayList<String> c);
 * we might set up the new function call like this:
 *     activationStack.activate().showParam("a", a).showParam("b", b).showParamAsRef("c", c);

 * @author zeil
 *
 */
public class ActivationStack implements CanBeRendered<ActivationStack>, Renderer<ActivationStack>, ContextAware {


    private HashMap<String, Renderer<?>> typeRenderers;
    private ArrayList<ActivationRecord> stack;
    
    
    private MemoryModel memory;

    /**
     * Allows for temporary suppression of breakpoint animations
     */
    private boolean suppressed;

    
    public ActivationStack (MemoryModel context) {
        memory = context;
        typeRenderers  = new HashMap<String, Renderer<?>>();
        stack = new ArrayList<ActivationRecord>();
        stack.renderHorizontally(false);
        stack.setColor(Color.darkGray);
        suppressed = false;
    }
    
    /**
     * This must be called at the beginning of each new function to signal
     * that a new empty record should be pushed onto the top of the stack.
     *
     * @param thisObj - A reference to an object of the class of
     *    which the current function is a member. Normally, "this"
     *    will do just fine.  thisObj is used to help locate
     *    the source code being animated, so, in a pinch (e.g., if
     *    animating a static function) another object whose source
     *    code lies in the same directory/folder will do.
     *
     * @return ActivationRecord for the new function call
     */
    public ActivationRecord activate (Object thisObj)
    {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        // Last two elements in the trace will be the Server thread run() function
        // and the selected() function from the Algorithm menu. These are not included
        // in the model.
        int firstUserCall = trace.length - 2;
        if (stack.size() == 0) {
            stack.add(new ActivationRecord(thisObj, "*main*", this));
        }
        // The most recent function to be included in the model will
        // be the one that called this function we are in now
        int lastUserCall = 0;
        while (true) {
            StackTraceElement ste = trace[lastUserCall];
            String className = ste.getClassName();
            if (className.equals(LocalServer.class.getName())) {
                break;
            }
            ++lastUserCall;
        }
        ++lastUserCall;
        String[] newStack = new String[firstUserCall - lastUserCall + 1];
        newStack[0] = "*main*";
        int j = 1;
        for (int call = firstUserCall - 1; call >= lastUserCall; call--) {
            newStack[j] = trace[call].getMethodName();
            ++j;
        }
        
        /**
         * If we already have calls on the stack, remove any that we appear to have
         * returned from.
         */
        while (stack.size() > newStack.length) {
            stack.remove(stack.size() - 1);
        }
        while (!stack.get(stack.size() - 1).getFunctionName().equals(newStack[stack.size() - 1])) {
            stack.remove(stack.size() - 1);            
        }
        /**
         * Then add in any new calls
         */
        for (int i = stack.size(); i < newStack.length; ++i) {
                stack.add(new ActivationRecord(thisObj, newStack[i], this));
        }
        return stack.get(stack.size() - 1);
    }
    
    
    /**
     * Provides access to the data area for the
     * currently executing function (i.e., the one identified
     * by the most recent call to activate()).
     *
     */
    public ActivationRecord topOfStack() {
        return stack.get(stack.size() - 1);
    }

    
    /**
     * Remove the topmost record from the stack
     */
    public void pop() {
        if (stack.size() > 0) {
            stack.remove (stack.size() - 1);
        }
    }

    /**
     * is the stack empty?
     */
    public boolean isEmpty() {
        return (stack.size() == 0);
    }
    
    /**
     * Empty the activation stack
     */
    public void clear() {
        stack.clear();
    }

        /**
     * Suppress animation of subsequent breakpoints (until resumeAnimation()
     * is called).
     */
    public void suppressAnimation() {
        suppressed = true;
    }

    /**
     * Resume animation of subsequent breakpoints.
     */
    public void resumeAnimation() {
        suppressed = false;
    }

    public boolean isSuppressed() {
        return suppressed;
    }

    /**
     * Get the collection of rules for rendering an object. Although this returns
     * a single rendering object, this object may represent the combination of
     * several distinct renderers applicable to the indicated object. The combination
     * is obtained by consultation with available renderers as follows (from highest to
     * lowest precedence):
     *   1) renderings established for specific objects
     *   2) getRendering(), for classes that implement CanBeRendered
     *   3) class renderings (see render(), below))
     *   4) class renderings established for superclasses of this one
     *   5) default rendering (displays toString() with no components or connections)
     *
     * @param obj
     * @return a list of renderers, in the order they should be consulted.
     */
    public <T> Renderer<T> getRenderer(T obj) {
        Identifier id = new Identifier(obj);
        CompoundRenderer<T> r = new CompoundRenderer<T>();
        r.add(new DefaultRenderer<T>(obj));
        addTypeRenderers (r, obj);
        if (obj instanceof CanBeRendered<?>) {    
            @SuppressWarnings("unchecked")
            CanBeRendered<T> cbr = (CanBeRendered<T>)obj;
            r.add (cbr.getRenderer());
        }
        addObjectRenderers (r, id, obj);
        return r;
    }


    /**
     * Establish a rendering for all objects of the indicated class.
     * Note that there are several ways to establish renderings, and that
     * these are resolved as described in getRenderer(), above.
     *
     * If a prior rendering has been established for this class, it is replaced by this call.
     * Unlike object renderings, class renderings are "global" and do not lose effect when
     * we return from an activation.
     *
     */
    public <T> ActivationStack render(Class<?> aClass, Renderer<T> newRendering) {
        typeRenderers.put (aClass.getName(), newRendering);
        return this;
    }

    

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     */
    public void globalVar(String label, int value) {
        globalVar(label, new Index(value));
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     */
    public void globalVar (String label, Object param)
    {
        memory.globalVar(label, param);
    }

    /**
     * Show a variable as a global value in all displays.
     * Variables portrayed by this call are shown as labeled
     * pointers to the actual value.
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     * @return a reference to this breakpoint
     */
    public void globalRefVar (String label, Object param)
    {
        SimpleReference ref = new SimpleReference(param);
        ref.setMinAngle(90.0);
        ref.setMaxAngle(180.0);
        globalVar (label, ref);
    }



//    /**
//     * Get a list of all local objects in the topmost activation record.
//     * @return list of local objects or null if no activation is in progress
//     */
//    public CanBeRendered getLocals() {
//        if (stack.size() > 0) {
//            ActivationRecordImpl topRec =  stack.get(stack.size() - 1);
//            boxOfLocals.setComponents(topRec.getLocals());    
//        } else {
//            boxOfLocals.setComponents(new LinkedList<Component>());
//        }
//        return boxOfLocals;
//    }
//
//    /**
//     * Get a list of all global objects shared by all activations
//     * @return list of global objects
//     */
//    public CanBeRendered getGlobals() {
//        return boxOfGlobals;
//    }

    


    
    public String toString() {
        return stack.toString();
    }
    


    
    <T> void addObjectRenderers(CompoundRenderer<T> r, Identifier id,
            T obj) {
        if (stack.size() > 0) {
            List<ObjectRenderer<T>> orList = stack.get(stack.size() - 1).getObjectRenderers(obj);
            for (ObjectRenderer<T> or: orList) {
                r.add (or);
            }
        }
    }

    private <T> void addTypeRenderers(CompoundRenderer<T> compound, T obj) {
        Class<?> c = obj.getClass();
        LinkedList<Renderer<T>> rList = new LinkedList<Renderer<T>>();
        while (c != null) {
            String className = c.getName();
            @SuppressWarnings("unchecked")
            Renderer<T> r = (Renderer<T>)typeRenderers.get(className);
            if (r != null)
                rList.addFirst (r);
            c = c.getSuperclass();
        }
        for (Renderer<T> r: rList) {
            compound.add(r);
        }
    }

    
    
    




    public void popDownTo(ActivationRecord aRec) {
        while (stack.size() > 0 && stack.get(stack.size() - 1) != aRec) {
            stack.remove (stack.size() - 1);
        }
    }



    @Override
    public Renderer<ActivationStack> getRenderer() {
        return this;
    }

    @Override
    public Color getColor(ActivationStack obj) {
        return Color.darkGray;
    }

    @Override
    public List<Component> getComponents(ActivationStack obj) {
        LinkedList<Component> components = new LinkedList<Component>();
        components.add (new Component(stack));
        return components;
    }

    @Override
    public List<Connection> getConnections(ActivationStack obj) {
        return new LinkedList<Connection>();
    }

    /*    Why 2?
    @Override
    public int getMaxComponentsPerRow(ActivationStack obj) {
        return 2;
    }
    */

    @Override
    public String getValue(ActivationStack obj) {
        return "";
    }

    @Override
    public AnimationContext context() {
        return memory.context();
    }


    public MemoryModel getMemoryModel() {
        return memory;
    }

    @Override
    public Directions getDirection() {
        return Directions.Horizontal;
    }

    @Override
    public Double getSpacing() {
        return Renderer.DefaultSpacing;
    }

    @Override
    public Boolean getClosedOnConnections() {
        return false;
    }


}
