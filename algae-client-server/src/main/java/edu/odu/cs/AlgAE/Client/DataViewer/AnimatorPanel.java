package edu.odu.cs.AlgAE.Client.DataViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.odu.cs.AlgAE.Client.DataViewer.Frames.DataShape;
import edu.odu.cs.AlgAE.Client.DataViewer.Frames.Frame;
import edu.odu.cs.AlgAE.Client.Layout.Anchors;
import edu.odu.cs.AlgAE.Client.Layout.Layout;
import edu.odu.cs.AlgAE.Client.SourceViewer.SourceViewer;
import edu.odu.cs.AlgAE.Common.Snapshot.Snapshot;

/**
 * A panel containing the animator: a canvas that portrays a series of changing
 *   data states over time and a set of controls for pausing, playing, and reversing
 *   the sequence of states portrayed.
 *
 * The animator can be presented a sequence of DataPictures, one at a time. It
 * "tweens" successive pictures to present a smooth animation of the transition from
 * one state to another.
 *
 * @author zeil
 *
 */
public class AnimatorPanel extends JPanel
{

    //private static Logger logger = Logger.getLogger(DataPane.class.getName());

    private JTextField status;
    private DataCanvas canvas;
    private Timer timer;
    private int DELAY = 75; // timer triggers 15x per second
    private FrameSwitcher switcher;

    private JButton pauseButton;
    private JButton stepButton;
    private JButton playButton;
    private JButton reverseStepButton;
    private JButton reverseButton;

    private JSlider speedControl;
    
    

    /**
     * Picture last added by addPicture but that has not yet
     * been placed into the futurePictures queue.
     */
    private Snapshot nextPicture;
    
    /**
     * Set to true when the animator is ready to receive a new picture from
     * the client.
     */
    private boolean waitingForNextPicture;
    
    /**
     * No more pictures will be added to this sequence
     */
    private boolean sequenceIsComplete;

    /**
     * The main playback state. Think of the current state as a sequence of
     * pictures starting with pastFrames, then the currentFrame,
     * then the futureFrames.
     */
    private LinkedList<Frame> pastFrames;
    private Frame currentFrame;
    private LinkedList<Frame> futureFrames;

    enum Direction {Reversed, StepReversed, Paused, Stepping, Playing};
    private Direction direction;

    int tweenFramesPerKey;
    static final int MaxTweenFramesPerKey = 50;
    static final int MinTweenFramesPerKey = 1;
    static final int InitialTweenFramesPerKey = 10;
    
    private Anchors anchors;
        

    public AnimatorPanel(SourceViewer source)
    {
        anchors = new Anchors();
        
        setLayout(new BorderLayout());

        canvas = new DataCanvas(source, new ShapeMover() {
            @Override
            public void moved(String id, double x, double y) {
                if (id.endsWith("__box")) {
                    id = id.substring(0, id.length() - 5);
                    moveShapeInFuture (id, x, y);
                }
            }            
        });


        JScrollPane scrolledCanvas = new JScrollPane(canvas);

        add (scrolledCanvas, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        add (controls, BorderLayout.SOUTH);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel controls2 = new JPanel();
        controls2.setLayout(new BoxLayout(controls2, BoxLayout.LINE_AXIS));


        byte[] rewindImage = loadImage("rewind.gif");
        if (rewindImage != null) {
            ImageIcon rewindIcon = new ImageIcon (rewindImage, "reverse");
            reverseButton = new JButton(rewindIcon);
        } else {
            reverseButton = new JButton ("reverse");
        }
        controls2.add (reverseButton);
        reverseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                directionChange(Direction.Reversed);
            }
        });

        byte[] rstepImage = loadImage("rstep.gif");
        if (rstepImage != null) {
            ImageIcon rstepIcon = new ImageIcon (rstepImage, "step back");
            reverseStepButton = new JButton(rstepIcon);
        } else {
            reverseStepButton = new JButton ("step back");
        }
        controls2.add (reverseStepButton);
        reverseStepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                directionChange(Direction.StepReversed);
            }
        });

        byte[] pauseImage = loadImage("pause.gif");
        if (pauseImage != null) {
            ImageIcon pauseIcon = new ImageIcon (pauseImage, "pause");
            pauseButton = new JButton(pauseIcon);
        } else {
            pauseButton = new JButton ("pause");
        }
        controls2.add (pauseButton);
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                directionChange(Direction.Paused);
            }
        });

        byte[] stepImage = loadImage("step.gif");
        if (stepImage != null) {
            ImageIcon stepIcon = new ImageIcon (stepImage, "step");
            stepButton = new JButton(stepIcon);
        } else {
            stepButton = new JButton ("step");
        }
        controls2.add (stepButton);
        stepButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                directionChange(Direction.Stepping);
            }
        });

        byte[] playImage = loadImage("play.gif");
        if (playImage != null) {
            ImageIcon playIcon = new ImageIcon (playImage, "play");
            playButton = new JButton(playIcon);
        } else {
            playButton = new JButton ("play");
        }
        controls2.add (playButton);
        playButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                directionChange(Direction.Playing);
            }
        });


        JPanel spacer = new JPanel();
        controls2.add(spacer);


        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.Y_AXIS));
        speedPanel.add (new JLabel("speed", SwingConstants.CENTER));
        speedControl = new JSlider(JSlider.HORIZONTAL, 0, MaxTweenFramesPerKey - MinTweenFramesPerKey,
                MinTweenFramesPerKey+MaxTweenFramesPerKey - InitialTweenFramesPerKey);
        speedControl.setMajorTickSpacing(5);
        speedControl.setPaintTicks(true);
        speedControl.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int value = (int)source.getValue();
                    setAnimationSpeed (MinTweenFramesPerKey + MaxTweenFramesPerKey - value);
                }
            }
        });
        speedPanel.add(speedControl);
        controls2.add (speedPanel);
        
        spacer = new JPanel();
        controls2.add(spacer);

        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.Y_AXIS));
        SpinnerNumberModel zoomModel = new SpinnerNumberModel(100.0, // initial
                0.0, // min
                5000.0, // max
                10.0); // increment
        JSpinner zoomControl = new JSpinner(zoomModel);
        zoomControl.setMaximumSize(new Dimension(80,40));
        zoomControl.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinner = (JSpinner)e.getSource();
                SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
                float zoom = model.getNumber().floatValue();
                canvas.setZoom (zoom);
            }
        });

        zoomPanel.add (new JLabel("Zoom", SwingConstants.CENTER));
        zoomPanel.add (zoomControl);
        controls2.add (zoomPanel);

        
        status = new JTextField("AlgAE", 60);
        status.setEditable(false);
        controls.add (status);
        controls.add (controls2);

        currentFrame = null;
        tweenFramesPerKey = 10;
        pastFrames = new LinkedList<Frame>();
        futureFrames = new LinkedList<Frame>();
        nextPicture = null;
        sequenceIsComplete = false;
        directionChange (Direction.Paused);
        waitingForNextPicture = false;

        switcher = new FrameSwitcher();
        timer = new Timer ("FrameSwitcher", true);
        timer.schedule(switcher, 1000, DELAY);

    }




    /**
     * Changes the direction state of the animator and enables/disables
     * the various playback buttons accordingly.
     * @param paused
     */
    private synchronized void directionChange(Direction dir) {
        if (direction != dir) {
            waitingForNextPicture = false;
        }
        direction = dir;
        switch (direction) {
        case Paused:
            pauseButton.setEnabled(false);
            boolean reversable = pastFrames.size() > 0;
            boolean canGoForward = (!sequenceIsComplete) || nextPicture != null
                || futureFrames.size() > 0;
            reverseButton.setEnabled(reversable);
            reverseStepButton.setEnabled(reversable);
            stepButton.setEnabled(canGoForward);
            playButton.setEnabled(canGoForward);
            canvas.setMovingEnabled(futureFrames.size() == 0);
            break;
        case Stepping:
        case StepReversed:
            pauseButton.setEnabled(false);
            reverseButton.setEnabled(false);
            reverseStepButton.setEnabled(false);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            canvas.setMovingEnabled(false);
            break;
        case Playing:
        case Reversed:
            pauseButton.setEnabled(true);
            reverseButton.setEnabled(false);
            reverseStepButton.setEnabled(false);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            canvas.setMovingEnabled(false);
            break;
        }
    }

    private byte[] loadImage(String fileName) {
        String resourceName = "/edu/odu/cs/AlgAE/Client/" + fileName;
        InputStream resourceIn = getClass().getResourceAsStream(resourceName);
        if (resourceIn == null)
            return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);

        byte[] bytes = new byte[512];
        int numBytesRead;
        byte[] imageBytes = null;
        try {
            while ((numBytesRead = resourceIn.read(bytes)) > 0) {
                outputStream.write(bytes, 0, numBytesRead);
            }
            imageBytes = outputStream.toByteArray();
        } catch (IOException e) {
            System.err.println ("Problem loading " + fileName + ": " + e);
            return null;
        } finally {
            try {
                resourceIn.close();
            } catch (IOException e) {
            }
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }    
        return imageBytes;
    }

    /**
     * Clears the record of all prior frames except for the final key frame
     * and if the animator is currently paused or reversed, enters Stepping mode.
     */
    public synchronized void clear()
    {
        if (futureFrames.size() > 0) {
            currentFrame = futureFrames.getLast();
        } else if (currentFrame == null && pastFrames.size() > 0) {
            currentFrame = pastFrames.getLast();
        }
        futureFrames.clear();
        pastFrames.clear();
        sequenceIsComplete = false;
        if (direction != Direction.Playing && direction != Direction.Stepping) {
            directionChange(Direction.Stepping);
        }
    }

    
    /**
     * Sets the animator to pause at each key frame.
     */
    public synchronized void startStepping()
    {
        directionChange(Direction.Stepping);
    }

    /**
     * Sets the animator to play continuously.
     */
    public synchronized void startPlay()
    {
        directionChange(Direction.Playing);
    }
    
    
    /**
     * Sets the number of tweened frames that will be generated for
     * each key frame (i.e., at each breakpoint). Increasing this
     * number will, in effect, slow the animation down.
     */
    public synchronized void setSpeed(int framesDisplayedPerKey)
    {
        setAnimationSpeed(framesDisplayedPerKey);
        speedControl.setValue(MaxTweenFramesPerKey - tweenFramesPerKey + MinTweenFramesPerKey);
    }

    /**
     * Sets the number of tweened frames that will be generated for
     * each key frame (i.e., at each breakpoint). Increasing this
     * number will, in effect, slow the animation down.
     */
    private synchronized void setAnimationSpeed(int framesDisplayedPerKey)
    {
        tweenFramesPerKey = Math.max(Math.min(framesDisplayedPerKey, MaxTweenFramesPerKey), MinTweenFramesPerKey);
    }
    
    /**
     * Adds a picture to serve as a new key frame for the animation.
     * This may block the caller's thread if the picture from the most
     * recent call to this same function has not yet been processed.
     * usually this is because the animation has been paused or reversed.
     *
     * @param newPicture
     * @throws InterruptedException
     */
    public synchronized void add (Snapshot newPicture) throws InterruptedException
    {
        while ((!waitingForNextPicture) || (nextPicture != null)) {
            wait();
        }
        nextPicture = newPicture;
    }

    /**
     * Signals that the last picture added will be the final one in
     * an animation sequence.
     */
    public synchronized void endofSequence ()
    {
        sequenceIsComplete = true;
        directionChange(direction);
    }


    /**
     * If the animation is not currently paused and if more frames are
     * available in the desired direction, request display of the next frame.
     *
     * @param newPicture
     * @throws InterruptedException
     */
    private synchronized void nextFrame()
    {
        try {
        if (canvas.isPainted() && direction != Direction.Paused) {
            Frame saved = currentFrame;
            if (direction == Direction.Playing || direction == Direction.Stepping) {
                // Moving forward
                //    If another frame is available for viewing, move current frame to past
                if (futureFrames.size() > 0 || nextPicture != null) {
                    if (currentFrame != null) {
                        pastFrames.add(currentFrame);
                        currentFrame = null;
                    }
                }
                // Try to update currentFrame
                if (futureFrames.size() > 0) {
                    // Take current frame from futureFrames queue
                    currentFrame = futureFrames.getFirst();
                    futureFrames.removeFirst();
                } else if (nextPicture != null) {
                    // FutureFrames queue was empty, but client has supplied the next
                    // key frame. Tween it and fill the tweened frames into the futureFrames queue.
                    Frame currentPicture = (pastFrames.size() > 0)? pastFrames.getLast() : null;
                    Layout currentLayout = (currentPicture != null) ? currentPicture.getKeyFor() : null;
                    Layout newLayout = new Layout (nextPicture, currentLayout, anchors);
                    Frame newKey = newLayout.toPicture();
                    Frame tweenedPics[] = tween(currentPicture, newKey);
                    for (int i = 0; i < tweenedPics.length; ++i) {
                        futureFrames.add(tweenedPics[i]);
                    }
                    currentFrame = futureFrames.getFirst();
                    futureFrames.removeFirst();
                    nextPicture = null;
                    waitingForNextPicture = false;
                } else {
                    // We have caught up with the client. Wait for another
                    // picture to be supplied.
                    nextPicture = null;
                    waitingForNextPicture = true;
                    notifyAll();
                }
            } else {
                // We are moving in a reverse direction. Move current frame back into futureFrames, and take
                // the most recent entry in pastFrames as the new current frame.
                if (currentFrame != null) {
                    futureFrames.addFirst(currentFrame);
                }
                if (pastFrames.size() > 0) {
                    currentFrame = pastFrames.getLast();
                    pastFrames.removeLast();
                } else  {
                    currentFrame = null;
                    directionChange (Direction.Paused);
                }
                
            }
            if (currentFrame != null && currentFrame != saved) {
                canvas.setPicture (currentFrame);
                getParent().repaint();
                status.setText(currentFrame.getMessage());
                if (currentFrame.isKey()) {
                    if (direction == Direction.Stepping || direction == Direction.StepReversed) {
                        directionChange (Direction.Paused);
                    }
                }
                getParent().repaint();
            }
        }
        } catch (NoSuchElementException e) {
            System.err.println (e);
            e.printStackTrace();
        }
    }

    /**
     * The user has dragged a shape to a new position on the screen.
     * (This should be possible only when the simulation is paused at a
     * key frame.)
     * Update the layout of the current frame and regenerate the frame.
     * Then discard all future frames, updating the layouts of all future key frames
     * and regenerating the frames derived from them.
     *
     * @param id   string equivalent ofthe entirty identifier of the box
     *                   that was dragged
     * @param x    x coordinate of new position of dragged box
     * @param y    y coordinate of new position of dragged box
     */
    private void moveShapeInFuture(String id, double x, double y) {
        Layout currentLayout = currentFrame.getKeyFor();
        currentLayout.anchorAt(id, new Point2D.Double(x,y));
        Layout movedLayout = new Layout(currentLayout, anchors);
        Frame movedFrame = movedLayout.toPicture();
        Frame tweenedPics[] = tween(currentFrame, movedFrame);
        for (int i = 0; i < tweenedPics.length; ++i) {
            futureFrames.addFirst(tweenedPics[tweenedPics.length - i - 1]);
        }
        directionChange(Direction.Stepping);
    }

    /**
     * Create 1 or more tween frames that interpolate between keyFrame and newPicture
     * @param newPicture
     * @return
     */
    private Frame[] tween(Frame oldPicture, Frame newPicture) {
        Frame[] frames = new Frame[tweenFramesPerKey+1];
        for (int i = 0; i < tweenFramesPerKey; ++i) {
            float blend = ((float)(i+1)) / (float)tweenFramesPerKey;
            frames[i] = tween(oldPicture, newPicture, blend);
        }
        frames[tweenFramesPerKey] = newPicture;
        return frames;
    }



    /**
     * Create a tween frame that interpolates between keyFrame and newPicture
     * @param newPicture
     * @param blend
     * @return
     */
    private Frame tween(Frame oldPicture, Frame newPicture, float blend) {
        HashMap<String, DataShape> shapes1 = new HashMap<String, DataShape>();
        if (oldPicture != null) {
            for (DataShape ds: oldPicture) {
                shapes1.put(ds.getID(), ds);
            }
        }
        HashMap<String, DataShape> shapes2 = new HashMap<String, DataShape>();
        for (DataShape ds: newPicture) {
            shapes2.put(ds.getID(), ds);
        }

        Frame result = new Frame(newPicture.getMessage(), newPicture.getLocation());
        if (oldPicture != null) {
            for (DataShape ds: oldPicture) {
                DataShape other = shapes2.get(ds.getID());
                DataShape tweened = ds.tween(other, blend);
                if (tweened != null)
                    result.add (tweened);
            }
        }
        for (DataShape ds: newPicture) {
            DataShape other = shapes1.get(ds.getID());
            if (other == null) {
                DataShape tweened = ds.tween(other, 1.0f - blend);
                if (tweened != null)
                    result.add (tweened);
            }
        }
        return result;
    }



    public void pauseAnimator()
    {
        timer.cancel();
        timer = null;
    }

    public void resumeAnimator()
    {
        if (timer == null) {
            timer = new Timer ("FrameSwitcher", true);
            timer.schedule(switcher, 1000, DELAY);
        }
    }

    public void showStatus (String message)
    {
        status.setText(message);
    }

    public void shutdown()
    {
        if (timer != null)
            timer.cancel();
    }


    
    
    
    private class FrameSwitcher extends TimerTask {

        public FrameSwitcher () {
        }

        @Override
        public void run() {
            nextFrame();
        }

    }

}
