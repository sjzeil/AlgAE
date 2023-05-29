/**
 *
 */
package edu.odu.cs.AlgAE.Common.Communications;

import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;

/**
 * Indicates that the animation has reached a breakpoint and has prepared a snapshot
 * of the current memory state for display by the client.
 *
 * @author zeil
 *
 */
public class SnapshotMessage extends ClientMessage {

    
    private Snapshot snapshot;
    private boolean menuItemCompleted;
    
    /**
     * Create a snapshot message
     */
    public SnapshotMessage(Snapshot snap, boolean menuItemCompleted) {
        super("Snapshot");
        snapshot = snap;
        this.menuItemCompleted = menuItemCompleted;
    }

    /**
     * Create a snapshot message
     */
    public SnapshotMessage() {
        super("Snapshot");
        snapshot = null;
        menuItemCompleted = false;
    }

    /**
     * @return the snapshot
     */
    public Snapshot getSnapshot() {
        return snapshot;
    }

    /**
     * @param snapshot the snapshot to set
     */
    public void setSnapshot(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Indicates whether this snapshot is the final one sent upon completion
     * of a function selected from the client's Algorithm menu
     *
     * @return the menuItemCompleted
     */
    public boolean isMenuItemCompleted() {
        return menuItemCompleted;
    }

    /**
     * Indicates whether this snapshot is the final one sent upon completion
     * of a function selected from the client's Algorithm menu
     *
     * @param menuItemCompleted the menuItemCompleted to set
     */
    public void setMenuItemCompleted(boolean menuItemCompleted) {
        this.menuItemCompleted = menuItemCompleted;
    }

    
    @Override
    public boolean equals(Object clientMessage) {
        if (clientMessage == null)
            return false;
        try {
            SnapshotMessage msg = (SnapshotMessage)clientMessage;
            return msg.menuItemCompleted == menuItemCompleted
                    && msg.snapshot.equals(snapshot);
        } catch (Exception e) {
            return false;
        }
    }

}
