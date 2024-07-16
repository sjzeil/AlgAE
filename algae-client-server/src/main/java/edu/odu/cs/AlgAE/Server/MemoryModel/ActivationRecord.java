package edu.odu.cs.AlgAE.Server.MemoryModel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.odu.cs.AlgAE.Animations.AnimationContext;
import edu.odu.cs.AlgAE.Animations.ContextAware;
import edu.odu.cs.AlgAE.Animations.SimulatedPrintStream;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;
import edu.odu.cs.AlgAE.Server.LocalServer;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.HighlightRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.ObjectRenderer;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;
import edu.odu.cs.AlgAE.Server.Utilities.SimpleReference;

/**
 * An ActivationRecord describes a function call/scope being animated.
 *
 * Operations available include selecting which variables will be drawn
 * at frame points within that scope.
 *
 * Many of the functions return a reference to the same activation record.
 * This is designed to permit chaining of calls.  For example, if activationRec
 * is modeling a call to
 *     void foo (String a, int b, ArrayList<String> c);
 * we might say
 *     activationRec.showParam("a", a).showParam("b", b).showParamAsRef("c", c);
 *
 * @author zeil
 *
 */
public class ActivationRecord
implements ContextAware, CanBeRendered<ActivationRecord> {

    /**
     * What stack is the record a part of?
     */
    private final ActivationStack stack;

    /**
     * For member function calls, the object denoting the "this"
     * parameter of the simulated call.
     */
    private final Object thisObject;

    /**
     * The name of the function being called.
     */
    private final String functionName;

    /**
     * A reference to the "this" parameter object, if one exists and is
     * being rendered.
     */
    private Object thisParam;

    /**
     * Any nested scopes within this activation (i.e., { ... } statement lists
     * with one or more local variables.
     */
    private final Stack<ScopeImpl> scopes;

    /**
     * Renderers registered in this function for specific objects.
     */
    private final List<ObjectRenderer<?>> objectRenderers;

    /**
     * A renderer for this activation record.
     */
    private final ActivationRenderer renderer;

    /**
     * Number of degrees in a right angle.
     */
    private static final double RIGHTANGLE = 90.0;

    /**
     * Number of degrees in a straight line.
     */
    private static final double STRAIGHTANGLE = 180.0;

    /**
     * Construct an activation record. This simulates a function call:
     *   thisObj.function(...)
     *
     *
     * @param thisObj the "this" parameter to the call (null for non-member
     *                functions)
     * @param function the name of the function being called
     * @param stack0 the activation stack to hold this new record.
     */
    public ActivationRecord (final Object thisObj, final String function,
            final ActivationStack stack0) {
        this.stack = stack0;
        thisObject = thisObj;
        if (thisObj instanceof Class<?>) {
            thisParam = null;
        } else {
            final SimpleReference ref = new SimpleReference(thisObj);
            ref.setMinAngle(RIGHTANGLE);
            ref.setMaxAngle(STRAIGHTANGLE);
            thisParam = ref;
        }
        functionName = function;
        scopes = new Stack<ScopeImpl>();
        scopes.push(new ScopeImpl());
        objectRenderers  = new LinkedList<ObjectRenderer<?>>();
        renderer = new ActivationRenderer();
    }


    /**
     * Highlight the rendering of dataValue by altering its color.
     * This change remains in effect until we leave the current activation,
     * until an alternate rendering overrides it, or until clearRenderings()
     * is called.
     *
     * @param <T> data type being highlighted
     * @param dataValue a data value to be highlighted
     * @return the activation record to which this was applied
     */
    public final <T> ActivationRecord highlight (final T dataValue) {
        return render (new HighlightRenderer<T>(dataValue, context()));
    }

    /**
     * Suppress animation of subsequent breakpoints (until resumeAnimation()
     * is called).
     */
    public void suppressAnimation() {
        stack.suppressAnimation();
    }

    /**
     * Resume animation of subsequent breakpoints.
     */
    public void resumeAnimation() {
        stack.resumeAnimation();
    }

    /**
     * Highlight the rendering of dataValue by altering its color.
     * This change remains in effect until we leave the current activation.
     *
     * @param <T> data type being highlighted
     * @param dataValue a data value to be highlighted
     * @param c color to use in highlighting
     * @return the activation record to which this was applied
     */
    public final <T> ActivationRecord highlight (final T dataValue,
            final Color c) {
        return render (new HighlightRenderer<T>(dataValue, c, context()));
    }

    /**
     * Alter the rendering of dataValue.
     *
     * @param newRendering new rendering for this value
     * @param <T> the type of the data being rendered
     * @return the activation record to which this was applied
     */
    public final <T> ActivationRecord render (
            final ObjectRenderer<T> newRendering) {
        objectRenderers.add(newRendering);
        return this;
    }


    /**
     * Display the function name.
     */
    @Override
    public final String toString() {
        return functionName;
    }

    @Override
    public final Renderer<ActivationRecord> getRenderer() {
        return renderer;
    }


    /**
     * What is the name of the function whose call is described?
     *
     * @return the name of the function
     */
    public final String getFunctionName() {
        return functionName;
    }



    /**
     * Clears the effect of all prior highlight/render calls in this activation.
     */
    public final void clearRenderings() {
        objectRenderers.clear();
    }

    /**
     * Get the list of renderers suitable for use on an object.
     *
     * @param <T> data type of object being rendered
     * @param obj object to be rendered
     * @return the list of renders
     */
    @SuppressWarnings("unchecked")
    public final
    <T> List<ObjectRenderer<T>> getObjectRenderers (final T obj) {
        final LinkedList<ObjectRenderer<T>> rList = new LinkedList<>();
        for (final ObjectRenderer<?> r: objectRenderers) {
            if (r.appliesTo() == obj) {
                rList.addLast((ObjectRenderer<T>) r);
            }
        }
        return rList;
    }


    /**
     * Establishes a breakpoint at which a new picture of the
     * current data state will be drawn and, depending on the interactive
     * controls, execution may be paused.
     *
     * @param message    A message to appear in the status line of the newly
     *    drawn picture of the data state.
     */
    public final void breakHere(final String message) {
        while (!stack.isEmpty() && stack.topOfStack() != this) {
            stack.pop();
        }
        if (!stack.isSuppressed()) {
            SourceLocation location = null;
            final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            final StackTraceElement activeCall = trace[2];
            final Object obj = getMethodOwner();
            final String className = activeCall.getClassName();
            final String fileName = className.replaceFirst(
                    "\\$.*$", "").replaceAll("\\.", "/") + ".java";
            context().sendSourceToClient(fileName);
            if (obj != null) {
                location = new SourceLocation(fileName, activeCall.getLineNumber());
            }

            final Snapshot snapshot = stack.getMemoryModel().renderInto(message,
                    location);

            context().sendToClient(snapshot, false);
        }
    }


    /**
     * Get the object on which this function is invoked.
     * @return the "this" object.
     */
    private Object getMethodOwner() {
        return thisObject;
    }




    /**
     * Get the ordered list of all parameters for this activation.
     *
     * @return the params
     */
    private List<Component> getParams() {
        final ArrayList<Component> params = new ArrayList<Component>();
        for (int i = 0; i < scopes.size(); ++i) {
            final List<Component> scopeParams = scopes.get(i).getParams();
            for (final Component param: scopeParams) {
                boolean found = false;
                for (int j = 0; (!found) && j < params.size(); ++j) {
                    if (params.get(j).getLabel().equals(param.getLabel())) {
                        found = true;
                        params.set(j, param);
                    }
                }
                if (!found) {
                    params.add(param);
                }
            }
        }
        return params;
    }


    /**
     * Get the ordered list of all local variables for this activation.
     *
     * @return the local variables for this activation
     */
    public final List<Component> getLocals() {
        final ArrayList<Component> locals = new ArrayList<Component>();
        for (int i = 0; i < scopes.size(); ++i) {
            final List<Component> scopeLocals = scopes.get(i).getLocals();
            for (final Component local: scopeLocals) {
                boolean found = false;
                for (int j = 0; (!found) && j < locals.size(); ++j) {
                    if (locals.get(j).getLabel().equals(local.getLabel())) {
                        found = true;
                        locals.set(j, local);
                    }
                }
                if (!found) {
                    locals.add(local);
                }
            }
        }
        return locals;
    }


    /**
     * Return the stack on which this record resides.
     *
     * @return the activation stack
     */
    public final ActivationStack getStack() {
        return stack;
    }

    /**
     * A rendering class for activation records.
     *
     * @author zeil
     */
    private class ActivationRenderer implements Renderer<ActivationRecord> {

        /**
         * An activation record is rendered in two parts: a function header,
         * and a set of local variables. This object holds the function header
         * state.
         */
        private final FunctionHeader functionHeader;

        /**
         * An activation record is rendered in two parts: a function header
         * and a set of local variables. This object holds the local
         * variables.
         */
        private final FunctionLocals locals;

        /**
         * Create the renderer.
         */
        public ActivationRenderer() {
            functionHeader = new FunctionHeader();
            locals = new FunctionLocals();
        }

        @Override
        public Color getColor(final ActivationRecord obj) {
            return Color.LIGHT_GRAY;
        }

        @Override
        public List<Component> getComponents(final ActivationRecord obj) {
            List<Component> components = new LinkedList<Component>();
            //if (obj != getStack().topOfStack()) {
                //components = getComponents(false);
            //} else {
                final Component header = new Component(functionHeader);
                components.add (header);
                if (getLocals().size() > 0) {
                    locals.setLocals(getLocals());
                    final Component localsComp = new Component(locals);
                    components.add (localsComp);
                }
            //}
            return components;
        }


        /**
         * Get the components that appear inside the activation record box.
         * @param atTop true if this is the top (currently active) call. Less
         *     detail may be shown for activation record below the top.
         * @return the list of component objects.
         */
        public List<Component> getComponents(final boolean atTop) {
            final LinkedList<Component> components
                = new LinkedList<Component>();
            if (thisParam != null) {
                components.add (new Component(((atTop) ? thisParam : ""),
                        "this"));
                components.add (new Component('.'));
            }
            components.add(new Component(functionName));
            components.add(new Component('('));
            int i = 1;
            for (final Component param: getParams()) {
                if (i > 1) {
                    components.add(new Component(','));
                }
                components.add(param);
                ++i;
            }
            components.add(new Component(')'));
            return components;
        }


        @Override
        public List<Connection> getConnections(final ActivationRecord obj) {
            return new LinkedList<Connection>();
        }

        @Override
        public String getValue(final ActivationRecord obj) {
            return "";
        }



        /**
         * Object denoting a function header. Has a function name and
         * actual parameters to the call.
         *
         * @author zeil
         *
         */
        public class FunctionHeader
            implements CanBeRendered<FunctionHeader>,
                Renderer<FunctionHeader> {

            /**
             * Create a function header.
             */
            public FunctionHeader() {
            }

            @Override
            public String getValue(final FunctionHeader obj) {
                return "";
            }

            @Override
            public Color getColor(final FunctionHeader obj) {
                return Color.LIGHT_GRAY;
            }

            @Override
            public List<Component> getComponents(final FunctionHeader obj) {
                return ActivationRenderer.this.getComponents(true);
            }

            @Override
            public List<Connection> getConnections(final FunctionHeader obj) {
                return new LinkedList<Connection>();
            }

            @Override
            public Renderer<FunctionHeader> getRenderer() {
                return this;
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



        @Override
        public Directions getDirection() {
            return Directions.Vertical;
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


    /**
     * A box of local variables.
     *
     * @author zeil
     */
    public class FunctionLocals implements
        CanBeRendered<FunctionLocals>, Renderer<FunctionLocals> {

        /**
         * The local variables.
         */
        private List<Component> locals;

        /**
         * Assign the list of local variables.
         * @param locals0 list of local variables
         */
        public final void setLocals(final List<Component> locals0) {
            this.locals = locals0;
        }

        @Override
        public final String getValue(final FunctionLocals obj) {
            return "";
        }

        @Override
        public final Color getColor(final FunctionLocals obj) {
            return Color.LIGHT_GRAY;
        }

        @Override
        public final List<Component> getComponents(final FunctionLocals obj) {
            java.util.ArrayList<Component> components = new java.util.ArrayList<>();
            components.addAll(locals);
            return components;
        }

        @Override
        public final
        List<Connection> getConnections(final FunctionLocals obj) {
            return new LinkedList<Connection>();
        }

        @Override
        public final Renderer<FunctionLocals> getRenderer() {
            return this;
        }

        @Override
        public Directions getDirection() {
            return Directions.Square;
        }

        @Override
        public Double getSpacing() {
            return 2.0 * Renderer.DefaultSpacing;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return false;
        }

    }

    @Override
    public final AnimationContext context() {
        return stack.context();
    }


    /**
     * Replacement for System.out. Instead
     *    of appearing on the system console, this output will be
     *    displayed by the Client GUI.
     * @return a PrintStream to which output can be sent.
     */
    public final SimulatedPrintStream out() {
        return stack.context().sysout();
    }

    /**
     * Pops up a dialog box prompting for an input, pausing the
     * animation until a satisfactory input value is obtained from the
     * human operator.
     *
     * @param prompt  Text of the prompt message to be displayed
     * @param requiredPattern regular expression describing an
     *           acceptable input value
     * @return a human-entered string matching the requiredPattern
     */
    public final String promptForInput(
            final String prompt,
            final String requiredPattern) {
        final LocalServer anim = LocalServer.algae();
        final JPanel screenContext = anim.getClient();
        String response = JOptionPane.showInputDialog(screenContext, prompt,
                "Input Requested", JOptionPane.QUESTION_MESSAGE);
        while (!response.matches(requiredPattern)) {
            response = JOptionPane.showInputDialog(screenContext, prompt,
                    "Try again", JOptionPane.WARNING_MESSAGE);
        }
        return response;
    }

    /**
     * Pops up a dialog box prompting for an input, pausing the
     * animation until a satisfactory input value is obtained from the
     * human operator.
     *
     * @param prompt  Text of the prompt message to be displayed
     * @return a human-entered string
     */
    public final String promptForInput(final String prompt) {
        return promptForInput(prompt, ".*");
    }


    /**
     * Show a variable as a parameter to the modeled function call.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the formal parameter name (optional, but recommended)
     * @param value  the actual parameter
     * @return a reference to this breakpoint
     */
    public final ActivationRecord param(
            final String label,
            final Object value) {
        boolean found = false;
        final Component newParam = new Component(value, label);
        for (int i = 0; i < scopes.size(); ++i) {
            final List<Component> scopeParams = scopes.get(i).getParams();
            for (final ListIterator<Component> p = scopeParams.listIterator();
                    (!found) && p.hasNext();) {
                final Component param = p.next();
                if (label.equals(param.getLabel())) {
                    found = true;
                    p.set(newParam);
                }
            }
        }
        if (!found) {
            scopes.peek().getParams().add(newParam);
        }
        return this;
    }




    /**
     * Show a variable as a parameter to the modeled function call.
     * Variables portrayed by this call are shown as an in-line
     * pointer/reference to the actual value, drawn elsewhere on the screen.
     *
     * @param label  the formal parameter name (optional, but recommended)
     * @param value  the actual parameter
     * @return a reference to this breakpoint
     */
    public final ActivationRecord refParam(
            final String label,
            final Object value) {
        final SimpleReference ref = new SimpleReference(value);
        ref.setMinAngle(RIGHTANGLE);
        ref.setMaxAngle(STRAIGHTANGLE);
        param (label, ref);
        return this;
    }


    /**
     * Show a variable as a local value in the function call.
     * Variables portrayed by this call are shown "in-line".
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param value  the variable/value
     * @return a reference to this breakpoint
     */
    public final ActivationRecord var(final String label, final Object value) {
        boolean found = false;
        final Component newVar = new Component(value, label);
        for (int i = 0; i < scopes.size(); ++i) {
            final List<Component> scopeLocals = scopes.get(i).getLocals();
            for (final ListIterator<Component> p = scopeLocals.listIterator();
                    (!found) && p.hasNext();) {
                final Component local = p.next();
                if (label.equals(local.getLabel())) {
                    found = true;
                    p.set(newVar);
                }
            }
        }
        if (!found) {
            scopes.peek().getLocals().add(newVar);
        }
        return this;
    }


    /**
     * Show a variable as a local value in the function call.
     * Variables portrayed by this call are shown as labeled
     * pointers to the actual value.
     *
     * @param label  the variable name (optional, can be "" or null)
     * @param value  the variable/value
     * @return a reference to this breakpoint
     */
    public final ActivationRecord refVar(
            final String label,
            final Object value) {
        final SimpleReference ref = new SimpleReference(value);
        ref.setMinAngle(RIGHTANGLE);
        ref.setMaxAngle(STRAIGHTANGLE);
        var (label, ref);
        return this;
    }



    /**
     * Creates a new, inner scope within the current one. (A newly
     * created activation record begins with a single scope.)
     *
     *  A scope is a container of information about parameters and variables
     *   to be displayed. A Scope describes a portion of Java code inside {...}
     * where locals can be declared.
     *
     * Scopes are created from and associated with activation records. The set
     * of parameter and variable labels are "shared" across all scopes for a
     * given activation record. If a variable/parameter labeled "X" is added to
     * a scope, and if a prior variable/parameter with the same label already
     * exists in the same or an outer scope, then the new variable/parameter
     * is considered a replacement of the old one.  This behavior is intended
     * to simplify the updated display of primitives and of variables whose
     * identity has changed.
     *
     * @return newly created scope
     */
    public final ActivationRecord pushScope() {
        scopes.push(new ScopeImpl());
        return this;
    }


    /**
     * Discards the most recently created scope, together with any
     * instructions given in that scope regarding parameters
     * and variables to be displayed.
     */
    public final void popScope() {
        scopes.pop();
    }

}
