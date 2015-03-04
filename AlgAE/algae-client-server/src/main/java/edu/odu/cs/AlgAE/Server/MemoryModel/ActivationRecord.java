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


    private ActivationStack stack;
    private Object thisObject;
    private String functionName;
    private Object thisParam;
    private Stack<ScopeImpl> scopes;
    private List<ObjectRenderer<?>> objectRenderers;

    private ActivationRenderer renderer;

    public ActivationRecord (Object thisObj, String function, ActivationStack stack) {
        this.stack = stack;
        thisObject = thisObj;
        if (thisObj instanceof Class<?>)
            thisParam = null;
        else {
            SimpleReference ref = new SimpleReference(thisObj);
            ref.setMinAngle(90.0);
            ref.setMaxAngle(180.0);
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
     */
    public <T> ActivationRecord highlight (T param) {
        return render (new HighlightRenderer<T>(param, context()));
    }

    /**
     * Highlight the rendering of dataValue by altering its color.
     * This change remains in effect until we leave the current activation.  
     */
    public <T> ActivationRecord highlight (T param, Color c)
    {
        return render (new HighlightRenderer<T>(param, c, context()));
    }

    /**
     * Alter the rendering of dataValue.
     * 
     * This change remains in effect until we leave the current activation,
     * until an alternate rendering overrides it, or until clearRenderings()
     * is called.  
     */
    public <T> ActivationRecord render (ObjectRenderer<T> newRendering)
    {
        objectRenderers.add(newRendering);
        return this;
    }


    public String toString()
    {
        return functionName;
    }

    @Override
    public Renderer<ActivationRecord> getRenderer() {
        return renderer;
    }


    /**
     * What is the name of the function whose call is described?
     * 
     */
    public String getFunctionName() {
        return functionName;
    }



    /**
     * Clears the effect of all prior highlight/render calls in this activation.
     */
    public void clearRenderings() {
        objectRenderers.clear();
    }

    @SuppressWarnings("unchecked")
    public
    <T> List<ObjectRenderer<T>> getObjectRenderers (T obj) {
        LinkedList<ObjectRenderer<T>> rlist = new LinkedList<ObjectRenderer<T>>();
        for (ObjectRenderer<?> r: objectRenderers) {
            if (r.appliesTo() == obj) {
                rlist.addLast((ObjectRenderer<T>)r);
            }
        }
        return rlist;
    }


    /**
     * Establishes a breakpoint at which a new picture of the
     * current data state will be drawn and, depending on the interactive
     * controls, execution may be paused. 
     * @param thisObj - A reference to an object of the class of
     *    which the current function is a member. Normally, "this"
     *    will do just fine.  thisObj is used to help locate
     *    the source code being animated, so, in a pinch (e.g., if
     *    animating a static function) another object whose source
     *    code lies in the same directory/folder will do.
     * @param message    A message to appear in the status line of the newly
     *    drawn picture of the data state. 
     */
    public void breakHere(String message) {
        while (!stack.isEmpty() && stack.topOfStack() != this) {
            stack.pop();
        }
        SourceLocation location = null;
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement activeCall = trace[2];
        Object obj = getMethodOwner();
        String className = activeCall.getClassName();
        String fileName = className.replaceFirst("\\$.*$", "").replaceAll("\\.", "/") + ".java";
        context().sendSourceToClient(fileName);
        if (obj != null) {
            if (obj instanceof Class<?>)
                location = new SourceLocation(fileName, activeCall.getLineNumber());
            else
                location = new SourceLocation(fileName, activeCall.getLineNumber());
        }

        Snapshot snapshot = stack.getMemoryModel().renderInto(message, location);

        context().sendToClient (snapshot, false);

    }



    private Object getMethodOwner() {
        return thisObject;
    }




    /**
     * Get the ordered list of all parameters for this activation.
     *  
     * @return the params
     */
    private List<Component> getParams() {
        ArrayList<Component> params = new ArrayList<Component>();
        for (int i = 0; i < scopes.size(); ++i) {
            List<Component> scopeParams = scopes.get(i).getParams();
            for (Component param: scopeParams) {
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
    public List<Component> getLocals() {
        ArrayList<Component> locals = new ArrayList<Component>();
        for (int i = 0; i < scopes.size(); ++i) {
            List<Component> scopeLocals = scopes.get(i).getLocals();
            for (Component local: scopeLocals) {
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
     * Return the stack on which this record resides
     * 
     * @return the activation stack
     */
    ActivationStack getStack() {
        return stack;
    }



    private class ActivationRenderer implements Renderer<ActivationRecord> {

        private FunctionHeader functionHeader;
        private FunctionLocals locals;


        public ActivationRenderer() {
            functionHeader = new FunctionHeader();
            locals = new FunctionLocals();
        }

        @Override
        public Color getColor(ActivationRecord obj) {
            return Color.LIGHT_GRAY;
        }

        @Override
        public List<Component> getComponents(ActivationRecord obj) {
            List<Component> components = new LinkedList<Component>();
            if (obj != getStack().topOfStack()) {
                components = getComponents(false);
            } else {
                Component header = new Component(functionHeader);
                components.add (header);
                if (getLocals().size() > 0) {
                    locals.setLocals(getLocals());
                    Component localsComp = new Component(locals);
                    components.add (localsComp);
                }
            }
            return components;
        }

        public List<Component> getComponents(boolean atTop) {
            LinkedList<Component> components = new LinkedList<Component>();
            if (thisParam != null) {
                components.add (new Component(((atTop) ? thisParam : ""), "this"));
                components.add (new Component('.'));
            }
            components.add(new Component(functionName));
            components.add(new Component('('));
            int i = 1;
            for (Component param: getParams()) {
                if (i > 1)
                    components.add(new Component(','));
                components.add(param);
                ++i;
            }
            components.add(new Component(')'));
            return components;
        }


        @Override
        public List<Connection> getConnections(ActivationRecord obj) {
            return new LinkedList<Connection>();
        }

        @Override
        public int getMaxComponentsPerRow(ActivationRecord obj) {
            if (obj != getStack().topOfStack())
                return 12;
            else
                return 1;
        }

        @Override
        public String getValue(ActivationRecord obj) {
            return "";
        }




        public class FunctionHeader implements CanBeRendered<FunctionHeader>, Renderer<FunctionHeader> {

            public FunctionHeader() {
            }

            @Override
            public String getValue(FunctionHeader obj) {
                return "";
            }

            @Override
            public Color getColor(FunctionHeader obj) {
                return Color.LIGHT_GRAY;
            }

            @Override
            public List<Component> getComponents(FunctionHeader obj) {
                return ActivationRenderer.this.getComponents(true);
            }

            @Override
            public List<Connection> getConnections(FunctionHeader obj) {
                return new LinkedList<Connection>();
            }

            @Override
            public int getMaxComponentsPerRow(FunctionHeader obj) {
                return 12;
            }

            @Override
            public Renderer<FunctionHeader> getRenderer() {
                return this;
            }

        }

    }


    public class FunctionLocals implements CanBeRendered<FunctionLocals>, Renderer<FunctionLocals> {

        private List<Component> locals;


        public void setLocals(List<Component> locals) {
            this.locals = locals;
        }

        @Override
        public String getValue(FunctionLocals obj) {
            return "";
        }

        @Override
        public Color getColor(FunctionLocals obj) {
            return Color.LIGHT_GRAY;
        }

        @Override
        public List<Component> getComponents(FunctionLocals obj) {
            return locals;
        }

        @Override
        public List<Connection> getConnections(FunctionLocals obj) {
            return new LinkedList<Connection>();
        }

        @Override
        public int getMaxComponentsPerRow(FunctionLocals obj) {
            return 0;
        }

        @Override
        public Renderer<FunctionLocals> getRenderer() {
            return this;
        }

    }

    @Override
    public AnimationContext context() {
        return stack.context();
    }


    /**
     * Replacement for System.out
     * 
     */
    public SimulatedPrintStream out() {
        return stack.context().sysout();
    }

    /**
     * Pops up a dialog box prompting for an input, pausing the
     * animation until a satisfactory input value is obtained from the
     * human operator.
     * 
     * @param prompt  Text of the prompt message to be displayed
     * @param requiredPattern regular expression describing an acceptable input value
     * @return a human-entered string matching the requiredPattern
     */
    public String promptForInput(String prompt, String requiredPattern) {
        LocalServer anim = LocalServer.algae();
        JPanel screenContext = anim.getClient(); 
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
    public String promptForInput(String prompt) {
        return promptForInput(prompt, ".*");
    }


    /**
     * Show a variable as a parameter to the modeled function call.
     * Variables portrayed by this call are shown "in-line".
     * 
     * @param label  the formal parameter name (optional, but recommended)
     * @param param  the actual parameter
     * @return a reference to this breakpoint
     */
    public ActivationRecord param(String label, Object value) {
        boolean found = false;
        Component newparam = new Component(value, label);
        for (int i = 0; i < scopes.size(); ++i) {
            List<Component> scopeParams = scopes.get(i).getParams();
            for (ListIterator<Component> p = scopeParams.listIterator(); (!found) && p.hasNext();) {
                Component param = p.next();
                if (label.equals(param.getLabel())) {
                    found = true;
                    p.set(newparam);
                }	
            }
        }
        if (!found) {
            scopes.peek().getParams().add(newparam);
        }
        return this;
    }




    /**
     * Show a variable as a parameter to the modeled function call.
     * Variables portrayed by this call are shown as an in-line
     * pointer/reference to the actual value, drawn elsewhere on the screen.
     * 
     * @param label  the formal parameter name (optional, but recommended)
     * @param param  the actual parameter
     * @return a reference to this breakpoint
     */
    public ActivationRecord refParam(String label, Object value) {
        SimpleReference ref = new SimpleReference(value);
        ref.setMinAngle(90.0);
        ref.setMaxAngle(180.0);
        param (label, ref);
        return this;
    }


    /**
     * Show a variable as a local value in the function call.
     * Variables portrayed by this call are shown "in-line".
     * 
     * @param label  the variable name (optional, can be "" or null)
     * @param param  the variable/value
     * @return a reference to this breakpoint
     */
    public ActivationRecord var(String label, Object value) {
        boolean found = false;
        Component newVar = new Component(value, label);
        for (int i = 0; i < scopes.size(); ++i) {
            List<Component> scopeLocals = scopes.get(i).getLocals();
            for (ListIterator<Component> p = scopeLocals.listIterator(); (!found) && p.hasNext();) {
                Component local = p.next();
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
     * @param param  the variable/value
     * @return a reference to this breakpoint
     */
    public ActivationRecord refVar(String label, Object value) {
        SimpleReference ref = new SimpleReference(value);
        ref.setMinAngle(90.0);
        ref.setMaxAngle(180.0);
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
     * of parameter and variable labels are "shared" across all scopes for a given
     * activation record. If a variable/parameter labeled "X" is added to a scope,
     * and if a prior variable/parameter with the same label already exists in the
     * same or an outer scope, then the new variable/parameter is considered a
     * replacement of the old one.  This behavior is intended to simplify the
     * updated display of primitives and of variables whose identity has changed.  
     * 
     * @return newly created scope
     */
    public ActivationRecord pushScope() {
        scopes.push(new ScopeImpl());
        return this;
    }


    /**
     * Discards the most recently created scope, together with any
     * instructions given in that scope regarding parameters 
     * and variables to be displayed.
     */
    public void popScope() {
        scopes.pop();
    }

}
