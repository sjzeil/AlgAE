package edu.odu.cs.AlgAE.Demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.odu.cs.AlgAE.Animations.LocalJavaAnimation;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity.Directions;
import edu.odu.cs.AlgAE.Server.MenuFunction;
import edu.odu.cs.AlgAE.Server.MemoryModel.Component;
import edu.odu.cs.AlgAE.Server.MemoryModel.Connection;
import edu.odu.cs.AlgAE.Server.Rendering.CanBeRendered;
import edu.odu.cs.AlgAE.Server.Rendering.Renderer;

public class ConnectionsDemo extends LocalJavaAnimation {

	public ConnectionsDemo() {
		super("Connections Demo");
	}

	@Override
	public String about() {
		return "This is a\ndemo of connections.";
	}

	
    public class TreeNode implements CanBeRendered<TreeNode>, Renderer<TreeNode> {

        public int data;
        public TreeNode left;
        public TreeNode right;

        public TreeNode (int data, TreeNode left, TreeNode right) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        @Override
        public String getValue(TreeNode obj) {
            return "" + data;
        }

        @Override
        public Color getColor(TreeNode obj) {
            return null;
        }

        @Override
        public List<Component> getComponents(TreeNode obj) {
            return null;
        }

        @Override
        public List<Connection> getConnections(TreeNode obj) {
            ArrayList<Connection> connections = new ArrayList<>();
            connections.add(new Connection(left));
            connections.add(new Connection(right));
            return connections;
        }

        @Override
        public Directions getDirection() {
            return Directions.Horizontal;
        }

        @Override
        public Double getSpacing() {
            return null;
        }

        @Override
        public Boolean getClosedOnConnections() {
            return false;
        }

        @Override
        public Renderer<TreeNode> getRenderer() {
            return this;
        }
    }

	
    TreeNode root;

	@Override
	public void buildMenu() {
		
		
		
		registerStartingAction(new MenuFunction() {

			@Override
			public void selected() {

				root = new TreeNode(1, 
                    new TreeNode(2, null, null), 
                    new TreeNode(3, 
                        new TreeNode(4, null, null),
                        new TreeNode(5, null, null)));
				globalVar("tree", root);
			}
			
		});
		
		
	}

	
	
	public static void main (String[] args) {
		ConnectionsDemo demo = new ConnectionsDemo();
		demo.runAsMain();
	}

}
