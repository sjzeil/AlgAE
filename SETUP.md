# Basic Instructions

1. Unpack the .zip file.
2. Start with the AlgAE directory. Build the basic AlgAE system with the command
        ant
3. You can then move to any of the other directories and give the same command to build
   the examples. To run an example, take note of which .java files have main(...) functions
   and give the command
   
        java -cp ../AlgAE/AlgAE.3.0.jar mainClassName
        
    or use appletviewer to load one of the HTML files in the example project directory.
   
# Eclipse Setup

Each of the provided directories can be treated as a separate Eclipse project. It
is best if you follow the basic instructions above first to set up a baseline build
before creating Eclipse projects.

Build path info:

  - Set up the main AlgAE project first.
  - All projects except the main AlgAE directory should be set 
    up as dependent on the AlgAE project. 
  - All projects should add the jhbasic.jar file to their build path.
  - For the default output folder, use the project's own src directory.
    This places the .class files into the same directories as their accompanying
    source code. That's important because a running animation needs to find and
    display its source while running.   (We could handle that during the construction
    of the jar files so that deployed animations would display their source code, but
    then the behavior during debugging would be different than when running form the jar.)
     
# Deploying Animations

The build.xml files have a "deploy" target which can be used to copy the their .jar
.html files to a desired destination directory. 

    ant deploy

Edit the "deploy.dest" property in the build.xml file to select the appropriate
deployment location. The HTML files provided in the sample animation projects
assume that they have been deployed into the same directory as their
own .jar file and the main AlgAE .jar files can be found relative to there
in ../AlgAE/