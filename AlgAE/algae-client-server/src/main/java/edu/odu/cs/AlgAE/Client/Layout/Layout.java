package edu.odu.cs.AlgAE.Client.Layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Arrow;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.ArrowToSelf;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Box;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Frame;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Text;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.BoundedRegion;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.ClosestPointOnPerimeter;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Dimension2DDouble;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.FreeOrFixed;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Location;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.PerimeterPoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.Point;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint;
import edu.odu.cs.AlgAE.Client.Layout.Coordinates.RelativePoint.Connections;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.OptimizationProblem;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Optimizer;
import edu.odu.cs.AlgAE.Client.Layout.Optimization.Variable;
import edu.odu.cs.AlgAE.Common.Snapshot.Connector;
import edu.odu.cs.AlgAE.Common.Snapshot.Entity;
import edu.odu.cs.AlgAE.Common.Snapshot.EntityIdentifier;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;
import edu.odu.cs.AlgAE.Common.Snapshot.SourceLocation;

/**
 * A layout is a snapshot in which all entities and connections have been assigned a location.
 *
 * A layout is created from a snapshot and a (possibly null) prior layout, so that entities that
 * have already been assigned a location in the prior layout can retain those positions in the
 * subsequent layout.
 *
 *
 * @author zeil
 *
 */
public class Layout  {
	
	private HashMap <EntityIdentifier, Entity> entities;
	private HashMap <EntityIdentifier, LocationInfo> locations;
	private LinkedList<LocationInfo> movable;
	private String descriptor;
	private SourceLocation sourceLoc;
	private Anchors anchors;
	
	
	/**
	 *  A subset of the total objects of the snapshot, consisting of those objects that
	 *  are not components of other objects (i.e., container == null)
	 *
	 */
	private HashSet<EntityIdentifier> baseObjectIDs;

	private class LocationInfo  implements BoundedRegion {
		private Dimension2DDouble size;
		private Location loc;
		private int height;
		private Entity describes;
		

		public LocationInfo(Entity e)
		{
			size = null;
			loc = null;
			describes = e;
			height = 0;
		}

		/**
		 * @param size the size to set
		 */
		public void setSize(Dimension2DDouble size) {
			this.size = size;
		}

		/**
		 * @return the size
		 */
		public Dimension2DDouble getSize() {
			return size;
		}

		/**
		 * @param loc the location to set
		 */
		public void setLoc(Location loc) {
			this.loc = loc;
		}

		/**
		 * @return the location
		 */
		public Location getLoc() {
			if (loc == null)
				return new Point(0.0, 0.0);
			else
				return loc;
		}
		
		/**
		 * True if this entity has been assigned a position
		 *
		 */
		public boolean hasBeenPositioned()
		{
			return loc != null;
		}


		/**
		 * @return the entity described by this location
		 */
//		public Entity getEntity() {
//			return describes;
//		}

		@Override
		public Rectangle2D getBBox() {
			Point2D location = getLoc().getCoordinates();
			Dimension2DDouble sz = getSize();
			return new Rectangle2D.Double(location.getX(), location.getY(), sz.getWidth(), sz.getHeight());
		}
		
		
		@Override
		public boolean isFixed(IdentityHashMap<FreeOrFixed, Boolean> alreadyChecked) {
			if (loc == null || size == null)
				return false;
			else if (alreadyChecked == null) {
				return isFixed(new IdentityHashMap<FreeOrFixed, Boolean>());
			} else if (alreadyChecked.containsKey(this))
				return false;
			else {
				alreadyChecked.put(this, true);
				return loc.isFixed(alreadyChecked);
			}
		}

		/**
		 * The height of an entity is the number of containers
		 * it lies inside of.
		 *
		 * @param h the height to set
		 */
		public void setHeight(int h) {
			this.height = h;
		}

		/**
		 * @return the height
		 */
		public int getHeight() {
			return height;
		}
		
		
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append ("describes:");
			result.append(describes.getEntityIdentifier());
			result.append (", height:");
			result.append(height);
			result.append(", loc:");
			result.append(loc);
			result.append(", size:");
			result.append(size);
			return result.toString();
		}
	}
	
	
	/**
	 * Create a new layout.
	 * @param description
	 * @param loc
	 */
	public Layout(Snapshot current, Layout previous, Anchors anchors) {
		this.anchors = anchors;
		entities = new HashMap<EntityIdentifier, Entity>();
		locations = new HashMap<EntityIdentifier, LocationInfo>();
		baseObjectIDs = new HashSet<EntityIdentifier>();
		movable = new LinkedList<LocationInfo>();
		descriptor = current.getDescriptor();
		sourceLoc = current.getBreakpointLocation();
		
		loadEntities (current);
		positionComponents();
		positionAnchoredEntities();
		positionFixedEntities(current);
		boolean anyNew = (previous == null);
		if (previous != null) {
			anyNew = positionOldEntities(previous);
		}
		anyNew = true;
		if (anyNew) {
			positionGlobals (current.getGlobals(), ReservedHorizontalMargin);
			positionNewEntities();
			repositionAllEntities();
		}
	}
	
	
	/**
	 * Create a new layout, similar to basedOn but reflecting any
	 * anchorAt calls since the former layout was created.
	 *
	 * @param basedOn an existing layout
	 */
	public Layout(Layout basedOn, Anchors anchors) {
		this.anchors = anchors;
		entities = basedOn.entities;
		locations = new HashMap<EntityIdentifier, LocationInfo>();
		baseObjectIDs = basedOn.baseObjectIDs;
		movable = new LinkedList<LocationInfo>();
		descriptor = basedOn.descriptor;
		sourceLoc = basedOn.sourceLoc;
		
		loadEntities (basedOn);
		positionComponents();
		positionAnchoredEntities();
		positionOldEntities(basedOn);
		repositionAllEntities();
	}
	
	
	/**
	 * Force an entity to always be drawn at a fixed position
	 * in any future scenes where it appears.
	 *
	 * @param eids  String equivalent of an entity identifier
	 * @param position
	 */
	public void anchorAt (String eids, Point2D position)
	{
		for (EntityIdentifier eid: entities.keySet()) {
			if (eid.toString().equals(eids)) {
				anchors.anchorAt(eid, position);
				break;
			}
		}
	}

	
	/**
	 * Set the locations of any items that are anchored.
	 */
	private void positionAnchoredEntities() {
		for (EntityIdentifier eid: baseObjectIDs) {
			Point2D p = anchors.getAnchor(eid);
			if (p != null) {
				LocationInfo loc = locations.get(eid);
				loc.setLoc(new Point(p.getX(), p.getY()));
			}
		}
	}



	/**
	 * Load entities from the snapshot into this scene, with
	 * placeholders for the location info.
	 *
	 * @param current
	 */
	private void loadEntities(Snapshot snapshot) {
		for (Entity e: snapshot) {
			EntityIdentifier eid = e.getEntityIdentifier();
			entities.put (eid, e);
			locations.put(eid, new LocationInfo(e));
			if (e.getContainer() == null)
				baseObjectIDs.add (eid);
		}
	}

	/**
	 * Load entities from the snapshot into this scene, with
	 * placeholders for the location info.
	 *
	 * @param current
	 */
	private void loadEntities(Layout snapshot) {
		for (EntityIdentifier eid: snapshot.entities.keySet()) {
			Entity e = snapshot.entities.get(eid);
			locations.put(eid, new LocationInfo(e));
		}
	}

	/**
	 * Set the locations of all entities that are components of larger
	 * entities.
	 */
	private void positionComponents()
	{
		for (EntityIdentifier eid: baseObjectIDs) {
			positionComponentsOf(eid);
		}
	}
	
	private static final double VerticalMargin = 0.25;
	private static final double VerticalSpacing = 0.25;
	private static final double HorizontalMargin = 0.5;
	private static final double HorizontalSpacing = 0.25;
	

	/**
	 * Position all internal components of entity eid
	 *
	 * @param eid  identifier of a container of zero or more components
	 *
	 * @return the max height of all components, -1 if it has none
	 */
	private int positionComponentsOf(EntityIdentifier eid)
	{
		Entity e = entities.get(eid);
		//System.err.println ("Layout: positioning components of " + eid);
		int maxHeight = -1;
		for (EntityIdentifier ceid: e.getComponents()) {
			int h = positionComponentsOf(ceid);
			maxHeight = Math.max(maxHeight, h);
		}
		double yOffset = VerticalMargin;
		double xOffset = HorizontalMargin;
		double minWidth = 0;
		double yAddition = 0.0;
		String description = e.getDescription();
		if (description.length() > 0) {
			// Leave room at the top for the label and value
			yOffset = 1.0 + VerticalMargin + VerticalSpacing;
			yAddition = 1.0 + VerticalSpacing;
			minWidth = 2* HorizontalMargin + description.length();
		}
		LocationInfo loc = locations.get(eid);
		loc.setHeight(1+maxHeight);
		Dimension2DDouble sz =
			layoutComponents (e.getComponents(), e.getMaxComponentsPerRow(),
				loc, xOffset, yOffset-VerticalMargin);
		sz.setSize(Math.max(minWidth, sz.getWidth())+HorizontalMargin, sz.getHeight()+yAddition);
		loc.setSize(sz);
		return maxHeight+1;
	}
	
	
	
	private void positionFixedEntities(Snapshot snapshot) {
/*		// Put the global variables into the upper left corner
		EntityIdentifier globals = snapshot.getGlobalEntities();
		LocationInfo gloc = locations.get(globals);
		gloc.setLoc (new Point(0.0, 0.0));
		anchors.put(globals, new Point2D.Double(0.0, 0.0));

		// Put the local variables just beneath them
		EntityIdentifier locals = snapshot.getLocalEntities();
		LocationInfo lloc = locations.get(locals);
		lloc.setLoc (new RelativePoint(0.0, 1.0, Connections.LL, gloc));

		// Put the activation stack to the right of these
		Dimension2DDouble globalsSize = gloc.getSize();
		Dimension2DDouble localsSize = lloc.getSize();
		
		double w = Math.max(globalsSize.getWidth(), localsSize.getWidth());
		EntityIdentifier stack = snapshot.getStackEntity();
		LocationInfo sloc = locations.get(stack);
		sloc.setLoc (new Point(w+2.0, 0.0));
		anchors.put(stack, new Point2D.Double(w+2.0, 0.0));
*/
		EntityIdentifier stack = snapshot.getActivationStack();
		LocationInfo sloc = locations.get(stack);
		if (sloc != null) {
			sloc.setLoc (new Point(0.25, 0.0));
			anchors.anchorAt(stack, new Point2D.Double(0.0, 0.0));
		}
		
		// Put an invisible box over part of this area.
/*		GlassBox g = new GlassBox();
		Identifier boid = Identifier.getIdentifierFor(g);
		EntityIdentifier beid = new EntityIdentifier(boid);
		Entity be = new Entity(boid);
		entities.put(beid, be);
		LocationInfo bloc = new LocationInfo(be);
		be.setColor (g.getColor(g));
		locations.put(beid, bloc);
		bloc.setLoc(new Point(0.0, 0.0));
		bloc.setSize(new Dimension2DDouble(w+4.0, 1.0 + Math.min(sloc.getHeight(), gloc.getHeight() + 2.0 + lloc.getHeight())));
*/
	}


	/**
	 * Assign initial positions to any unfixed globals
	 * at top of screen alongside the activation stack
	 * @param set
	 * @param xOffset
	 */
	private void positionGlobals (Set<EntityIdentifier> set, double xOffset) {
		int count = 0;
		double y = 0.0;
		double height = 0.0;
		double x = 0.0;
		double maxWidth = 0.0;
		for (EntityIdentifier eid: set) {
			LocationInfo loc = locations.get(eid);
			if (loc != null && !loc.hasBeenPositioned()) {
				++count;
				Dimension2DDouble sz = loc.getSize();
				if (count > 2 && x + sz.getWidth() > maxWidth) {
					y += VerticalMargin + height;
					height = 0.0;
					x = HorizontalMargin;
				} else {
					x += HorizontalMargin;
				}
				loc.setLoc(new Point(x + ReservedHorizontalMargin, y));
				movable.add (loc);
				x += sz.getWidth();
				maxWidth = Math.max(maxWidth, x);
				height = Math.max(height, sz.getHeight());
			}
		}
	}


	/**
	 * Check to see if any of the base entities in this scene were
	 * present in a prior layout. If so, copy their former locations into
	 * this layout.
	 *
	 * As a side effect, builds the list movable of all base objects that
	 * have not already been assigned a location.
	 *
	 * @param previous a prior scene
	 * @return true if any base entities in this scene were not in the prior scene
	 *                 and therefore still have no location
	 */
	private boolean positionOldEntities(Layout previous) {
		if (previous != null) {
			boolean anyNew = false;
			for (EntityIdentifier eid: baseObjectIDs) {
				LocationInfo newLocationInfo = locations.get(eid);
				boolean hasOldPosition = previous.baseObjectIDs.contains(eid);
				anyNew = anyNew || !hasOldPosition;
				if (!newLocationInfo.hasBeenPositioned()) {
					movable.add (newLocationInfo);
					if (hasOldPosition) {
						Location formerLocation = previous.locations.get(eid).getLoc();
						Location newLocation = new Point(formerLocation.getCoordinates());
						newLocationInfo.setLoc(newLocation);
					}
				}
			}
			return anyNew;
		} else {
			return true;
		}
	}


	static private Random random = new Random();
	static private final double InitialXRange = 100.0;
	static private final double InitialYRange = 10.0;

	private class MinimizePositionScore implements OptimizationProblem {

		private ArrayList<Variable> variables;
		
		public MinimizePositionScore (ArrayList<Variable> vars)
		{
			variables = vars;
		}
		
		@Override
		public ArrayList<Variable> getVariables() {
			return variables;
		}

		@Override
		public double objectiveFunction() {
			return positionScore();
		}
		
	}
	
	
	private void positionNewEntities() {
		ArrayList<Variable> newEntityVariables = new ArrayList<Variable>();
		HashSet<EntityIdentifier> newEntities = new HashSet<EntityIdentifier>();
		// Collect all the new entities
		for (EntityIdentifier eid: baseObjectIDs) {
			LocationInfo locInfo = locations.get(eid);
			if (!locInfo.hasBeenPositioned()) {
				newEntities.add (eid);
			}
		}
		
		boolean done = false;
		while (!done) {
			// Try to find a good position for at least one new entity
			done = true;
			for (EntityIdentifier eid: entities.keySet()) {
				LocationInfo locInfo = locations.get(eid);
				if (locInfo.isFixed(null)) {
					// See if this already positioned entity has a connector to
					// any of the new entities
					Entity oldEntity = entities.get(eid);
					for (Connector c: oldEntity.getConnections()) {
						if (newEntities.contains(c.getDestination())) {
							double angle = c.getMinAngle() + random.nextDouble() * (c.getMaxAngle() - c.getMinAngle());
							PerimeterPoint pp = new PerimeterPoint(angle, locInfo);
							Point2D p = pp.getCoordinates();
							double dx = p.getX() - locInfo.getBBox().getCenterX();
							double dy = p.getY() - locInfo.getBBox().getCenterY();
							double d = Math.sqrt(dx*dx + dy*dy);
							if (d == 0.0)
								d = 1.0;
							dx = dx / d;
							dy = dy / d;
							double x = p.getX() + c.getPreferredLength() * dx;
							double y = p.getY() + c.getPreferredLength() * dy;
							LocationInfo newInfo = locations.get(c.getDestination());
							newInfo.setLoc(new Point(x,y));
							done = false;
							newEntities.remove(c.getDestination());
							break;
						}
					}
				}
				
			}
			
			if (done) {
				// Fallback: random initial position
				for (EntityIdentifier eid: newEntities) {
					LocationInfo locInfo = locations.get(eid);
					double x = random.nextDouble() * (InitialXRange - ReservedHorizontalMargin) + ReservedHorizontalMargin;
					double y = random.nextDouble() * InitialYRange + InitialYRange/2.0;
					Point p = new Point(x,y);
					locInfo.setLoc (p);
					for (Variable v: p.getVariables()) {
						newEntityVariables.add (v);
					}
					done = false;
					newEntities.remove(eid);
					break;
				}
			}
		}
		// Now solve the optimization problems to get a better position
		// for all new entities.
		MinimizePositionScore optProblem = new MinimizePositionScore(newEntityVariables);
		Optimizer opt = new Optimizer(optProblem);
		opt.solve(20.0, 0.25, 10000);
	}

	private void repositionAllEntities() {
		ArrayList<Variable> entityVariables = new ArrayList<Variable>();
		// Copy the locations of all non-anchored base objects into
		for (LocationInfo locInfo: movable) {
			Location loc = locInfo.getLoc ();
			for (Variable v: loc.getVariables()) {
				entityVariables.add (v);
			}
		}
		// Now solve the optimization problems to get a better position
		// for all entities.
		MinimizePositionScore optProblem = new MinimizePositionScore(entityVariables);
		//System.err.println ("repositionAllEntities: " + entityVariables.size() + " variables");
		Optimizer opt = new Optimizer(optProblem);
		opt.solve(20.0, 0.25, 10000);
	}





		
	/**
	 * Arranges a list of components to pack a rectangular space whose upper left corner is
	 *    specified.
	 * @param variables  list of components to be inserted into the snapshot
	 * @param upperLeft  upper left corner of the region where the variables should be placed
	 * @return dimension of the bounding rectangle of the placed variables
	 */
	private Dimension2DDouble layoutComponents
	       (List<EntityIdentifier> variables,
			int maxComponentsPerRow,
			BoundedRegion relativeTo, double xOffset, double yOffset) {
		
		ArrayList<LinkedList<EntityIdentifier>> rows = new ArrayList<LinkedList<EntityIdentifier>>();

		// Apportion the components to different rows.
		if (maxComponentsPerRow > 0 && maxComponentsPerRow < Integer.MAX_VALUE) {
			// Arrange components into several rows, each with
			// maxComponentsPerRow columns.
			int r = -1;
			int c = maxComponentsPerRow;
			for (EntityIdentifier eid: variables) {
				if (c >= maxComponentsPerRow) {
					++r;
					rows.add (new LinkedList<EntityIdentifier>());
					c = 0;
				}
				rows.get(r).add (eid);
				++c;
			}
		} else {
			// Arranges a list of components to pack a rectangular space whose upper left corner is
			//   specified.
			int numRows = 0;
			int r = 0;
			rows.add(new LinkedList<EntityIdentifier>());
			for (EntityIdentifier eid: variables) {
				int nextr = r+1;
				rows.get(r).add (eid);
				if (r >= numRows) {
					++numRows;
					rows.add(new LinkedList<EntityIdentifier>());
					nextr = 0;
				}
				r = nextr;
			}
		}
		
		// Compute the actual position within each row
		if (maxComponentsPerRow < Integer.MAX_VALUE) {
			double y = VerticalMargin;
			double width = 0;
			for (int i = 0; i < rows.size(); ++i) {
				LinkedList<EntityIdentifier> row = rows.get(i);
				if (i > 0 && row.size() > 0) {
					y += VerticalSpacing;
				}
				double x = HorizontalMargin - HorizontalSpacing;
				double height = 0.0;
				if (row.size() > 0) {
					for (EntityIdentifier eid: row) {
						x += HorizontalSpacing;
						LocationInfo loc = locations.get(eid);
						Dimension2DDouble sz = loc.getSize();
						loc.setLoc(new RelativePoint(x+xOffset, y+yOffset, Connections.LU, relativeTo));
						x += sz.getWidth();
						height = Math.max(height, sz.getHeight());
					}
					width = Math.max(width, x + HorizontalMargin);
					y += height;
				}
			}
			width += HorizontalMargin;
			y += VerticalMargin;
			return new Dimension2DDouble(width, y);
		} else {
			// for maxComponentsPerRow < 0, transpose so that we lay things out in columns
			// instead of rows
			double x = HorizontalMargin;
			double height = 0;
			for (int i = 0; i < rows.size(); ++i) {
				LinkedList<EntityIdentifier> row = rows.get(i);
				if (i > 0 && row.size() > 0) {
					x += 4/*HorizontalSpacing*/;
				}
				double y = VerticalMargin - VerticalSpacing;
				double width = 0.0;
				if (row.size() > 0) {
					for (EntityIdentifier eid: row) {
						y += VerticalSpacing;
						LocationInfo loc = locations.get(eid);
						Dimension2DDouble sz = loc.getSize();
						loc.setLoc(new RelativePoint(x+xOffset, y+yOffset, Connections.LU, relativeTo));
						y += sz.getHeight();
						width = Math.max(width, sz.getWidth());
					}
					height = Math.max(height, y + VerticalMargin);
					x += width;
				}
			}
			height += VerticalMargin;
			x += HorizontalMargin;
			return new Dimension2DDouble(x, height);
			
		}
	}
	
	
	
	
	
	private double positionScore()
	{
		double obScore = 0.0;
		double connScore = 0.0;
		for (EntityIdentifier eid: baseObjectIDs) {
			obScore += baseObjectScore(eid);
		}
		for (EntityIdentifier eid: entities.keySet()) {
			Entity e = entities.get(eid);
			for (Connector conn: e.getConnections()) {
				connScore += connectorScore(conn);
			}
		}
		double score = OverlapForceMultiplier * obScore + connScore;
		return score;
	}





	private static double OverlapForceMultiplier = 10.0;
	private static double OverlapGutter = 1.0;
	private static double Gravity = 0.01;
	private static final float TextColorThreshold = 1.5f;
	
	private final static double ReservedHorizontalMargin = 40;
	
	private double baseObjectScore (EntityIdentifier eid) {
		/*
		 * This is a piecewise linear function that peaks at M1*M2
		 * when the center of two objects overlap and that falls to 0
		 * when their centers are far enough apart that the objects are
		 * separated by OverlapGutter.
		 */
		LocationInfo loc = locations.get(eid);
		Rectangle2D bbox = loc.getBBox();
		double score = 0.0;
		for (EntityIdentifier other : baseObjectIDs) {
			if (!other.equals(eid)) {
				LocationInfo otherLoc = locations.get(other);
				Rectangle2D obox = otherLoc.getBBox();

				double dx0 = bbox.getWidth()/2.0 + obox.getWidth()/2.0 + OverlapGutter;
				double dy0 = bbox.getHeight()/2.0 + obox.getHeight()/2.0 + OverlapGutter;
				
				double dx = Math.abs(bbox.getCenterX() - obox.getCenterX());
				double dy = Math.abs(bbox.getCenterY() - obox.getCenterY());
				
				if (dx < dx0 && dy < dy0) {
					// Boxes overlap or nearly overlap
					double fx = 1.0 - dx / dx0;
					double fy = 1.0 - dy / dy0;

					score += fx + fy;
				}
			}
		}
		
		// Artificial collision calculation to keep coordinates positive
		double dx = 0.0;
		double dy = 0.0;
		if (bbox.getX() < ReservedHorizontalMargin)
			dx = ReservedHorizontalMargin-bbox.getX();
		else
			dx = 0.0;
		if (bbox.getY() < 0)
			dy = -bbox.getY();
		else
			dy = 0.0;
		score += dx + dy;
		
		// Gravity pulls all objects gently towards 0,0
		double gravity = Math.abs(bbox.getX() - HorizontalMargin) + Math.abs(bbox.getY() - VerticalMargin);
		score += Gravity * gravity;
		
		return score;
	}

	

	private static final double TorsionTensionRatio = 5.0;

	private double connectorScore(Connector conn)
	{
		EntityIdentifier destination = conn.getDestination();
		EntityIdentifier source = conn.getSource();
		
		if (destination.equals(EntityIdentifier.nullID()) || destination.equals(source))
			return 0.0;
		
		LocationInfo destInfo = locations.get(destination);
		LocationInfo sourceInfo = locations.get(source);
		
		Location destCenter = new RelativePoint(0.0, 0.0, Connections.CC, destInfo);
		Location sourceCenter = new RelativePoint(0.0, 0.0, Connections.CC, sourceInfo);

		Location sourceExitLoc = new ClosestPointOnPerimeter(sourceInfo, destCenter, conn.getMinAngle(), conn.getMaxAngle());
		Location destEntryLoc = new ClosestPointOnPerimeter(destInfo, sourceCenter, 0.0, 360.0);

		Point2D sourceCenterPt = new Point2D.Double (sourceInfo.getBBox().getCenterX(), sourceInfo.getBBox().getCenterY());
		Point2D sourceExit = sourceExitLoc.getCoordinates();
		Point2D destEntry = destEntryLoc.getCoordinates();

		double len = sourceExit.distance(destEntry);
		double tension = Math.abs (len - conn.getPreferredLength());
		
		double dx1 = sourceExit.getX() - sourceCenterPt.getX();
		double dy1 = sourceExit.getY() - sourceCenterPt.getY();
		
		double d1 = Math.sqrt(dx1*dx1 + dy1*dy1);
		if (d1 == 0.0)
			d1 = 1.0;
		
		double dx2 = destEntry.getX() - sourceExit.getX();
		double dy2 = destEntry.getY() - sourceExit.getY();
		
		double d2 = Math.sqrt(dx2*dx2 + dy2*dy2);
		if (d2 == 0.0)
			d2 = 1.0;
		
		double torsion = 1.0 - (dx1*dx2 + dy1*dy2) / (d1*d2);
		return TorsionTensionRatio * torsion + tension / conn.getElasticity();
	}
	
	
	

	


	/**
	 * Convert this state model to a DataPicture suitable for tweening and drawing.
	 *
	 * @return a DataPicture of this snapshot
	 */
	public Frame toPicture() {
		Frame result = new Frame(descriptor, sourceLoc, this);
		for (EntityIdentifier eid: entities.keySet()) {
			renderInto (eid, result);
		}
		return result;
	}

	/**
	 * Render this entity, and its outgoing connections in a frame
	 * @param eid  ID of the entity to be rendered
	 * @param frame  the frame into which the entity should be rendered
	 */
	private void renderInto(EntityIdentifier eid, Frame frame) {
		Entity e = entities.get(eid);
		LocationInfo loc = locations.get(eid);
		
		Point2D p = loc.getLoc().getCoordinates();
		float xx = (float)p.getX();
		float yy = (float)p.getY();
		Dimension2DDouble size = loc.getSize();
		int depth = loc.getHeight();
		Color color = e.getColor();
		String description = e.getDescription();
		
		// background box
		frame.add(
				new Box(eid + "__box",
					xx, yy, (float)size.getWidth(), (float)size.getHeight(),
					color, 2*depth+2));


		float[] colorComponents = color.getRGBColorComponents(null);
		Color textColor = (colorComponents[0] + colorComponents[1] + colorComponents[2] >= TextColorThreshold )
			? Color.black : Color.white;
		
		// Text value
		if (description.length() > 0) {
			frame.add(
					new Text(eid + "__value", description,
							xx + (float)size.getWidth()/2.0f - ((float)description.length())/2.0f,
							yy + (float)VerticalMargin,
							textColor, 2*depth+1));
		}
		int connectorNum = 0;
		for (Connector connector: e.getConnections()) {
			renderInto (connector, connectorNum, frame);
			++connectorNum;
		}
	}

	/**
	 * Render this connector in a frame
	 * @param eid  ID of the entity to be rendered
	 * @param frame  the frame into which the entity should be rendered
	 */
	private void renderInto(Connector connector, int connectorNum, Frame frame) {
		EntityIdentifier destination = connector.getDestination();
		EntityIdentifier source = connector.getSource();
		
		int componentNum = connector.getComponentIndex();
		if (componentNum >= 0 && !destination.equals(EntityIdentifier.nullID())) {
			// This pointer is actually for an internal component of the destination object.
			Entity destEntity = entities.get(destination);
			List<EntityIdentifier> destComponents = destEntity.getComponents();
			if (componentNum < destComponents.size()) {
				destination = destComponents.get(componentNum);
			}
		}
		
		LocationInfo sourceInfo = locations.get(source);
		Location sourceCenterLoc = new RelativePoint(0.0, 0.0, Connections.CC, sourceInfo);
		Point2D sourceCenter = sourceCenterLoc.getCoordinates();
		
		String value = connector.getValue();
		if (value == null)
			value = "";
		String label = connector.getLabel();
		if (label == null)
			label = "";
		Color color = connector.getColor();
		String id = source + "[" + connectorNum + "]=>";
		
		double minA = connector.getMinAngle();
		double maxA = connector.getMaxAngle();
		if (minA > maxA)
			maxA += 360.0;

		if (destination.equals(EntityIdentifier.nullID())) {
			double angle = random.nextDouble() * (maxA - minA) + minA;
			Location sourceExitLoc = new PerimeterPoint(angle, sourceInfo);
			Point2D exitPoint = sourceExitLoc.getCoordinates();
			if ((maxA - minA <= 45.0))
				frame.add (new Arrow(id, true,
						exitPoint, null, color, 0));
			if (label.length() > 0 || value.length() > 0) {
				String description = label + ": " + value;
				Point2D pLabel = getLabelLoc(exitPoint, null, sourceCenter);
				frame.add(new Text(id + "*label", description,
						(float)pLabel.getX(), (float)pLabel.getY(),
						Color.black, 0));
			}
		} else if (destination != source) {
			LocationInfo destInfo = locations.get(destination);
			
			Location destCenterLoc = new RelativePoint(0.0, 0.0, Connections.CC, destInfo);

			Location sourceExitLoc = new ClosestPointOnPerimeter(sourceInfo, destCenterLoc,
						connector.getMinAngle(), connector.getMaxAngle());
			Location destEntryLoc = new ClosestPointOnPerimeter(destInfo, sourceCenterLoc,
						0.0, 360.0);

			Point2D exitPoint = sourceExitLoc.getCoordinates();
			Point2D entryPoint = destEntryLoc.getCoordinates();

			frame.add (new Arrow(id, (maxA - minA <= 45.0),
					exitPoint, entryPoint, color, 0));
			if (label.length() > 0) {
				Point2D pLabel = getLabelLoc(exitPoint, entryPoint, sourceCenter);
				frame.add(new Text(id + "*label", label + ":",
						(float)pLabel.getX(), (float)pLabel.getY(),
						Color.black, 0));
			}
			if (value.length() > 0) {
				Point2D pValue = getValueLoc(exitPoint, entryPoint, sourceCenter);
				frame.add(new Text(id + "*value", value,
						(float)pValue.getX(), (float)pValue.getY(),
						Color.black, 0));
			}
		} else {
			double angle = random.nextDouble() * (maxA - minA) + minA;
			Location sourceExitLoc = new PerimeterPoint(angle, sourceInfo);
			Point2D exitPoint = sourceExitLoc.getCoordinates();
			int sense = 0;
			Rectangle2D sourceBox = sourceInfo.getBBox();
			if (exitPoint.getX() >= sourceBox.getMaxX())
				sense = 1;
			else if (exitPoint.getX() <= sourceBox.getMinX())
				sense = 3;
			else if (exitPoint.getY() >= sourceBox.getMaxY())
				sense = 2;
			frame.add (new ArrowToSelf(id, (maxA - minA <= 45.0),
						exitPoint, sense, color, 0));
			if (label.length() > 0 || value.length() > 0) {
				String description = label + ": " + value;
				Point2D pLabel = getLabelLoc(exitPoint, exitPoint, sourceCenter);
				frame.add(new Text(id + "*label", description,
						(float)pLabel.getX(), (float)pLabel.getY(),
						Color.black, 0));
			}
		}
	}

	private Point2D getLabelLoc(Point2D exitPoint, Point2D entryPoint, Point2D sourceCenter) {
		double dx = 0.5;
		double dy = 0.5;
		if (exitPoint.getX() < sourceCenter.getX() + 0.1)
			dx = -dx;
		if (exitPoint.getY() < sourceCenter.getY() + 0.1)
			dy = -dy;
		return new Point2D.Double (exitPoint.getX() + dx, exitPoint.getY() + dy);
	}

	private Point2D getValueLoc(Point2D exitPoint, Point2D entryPoint, Point2D sourceCenter) {
		double frac = 0.67;
		if (entryPoint == null || entryPoint.equals(exitPoint))
			return getLabelLoc(exitPoint, entryPoint, sourceCenter);
		else {
			double x = exitPoint.getX() + frac * (entryPoint.getX() - exitPoint.getX());
			double y = exitPoint.getY() + frac * (entryPoint.getY() - exitPoint.getY());
			return new Point2D.Double (x, y);
		}
	}


	/**
	 * Provided for testing purposes (and therefore restricted to package visibility)
	 * @return the entities
	 */
	HashMap<EntityIdentifier, Entity > getEntities() {
		return entities;
	}

	/**
	 * Provided for testing purposes (and therefore restricted to package visibility)
	 * @return the entities
	 */
	Dimension2DDouble getSizeOf(EntityIdentifier eid) {
		LocationInfo loc = locations.get(eid);
		return loc.getSize();
	}

	
	
}
