<?xml version='1.0' encoding='ISO−8859−1' ?>
<!DOCTYPE helpset
      PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<helpset version="2.0">
  <!−− title −−>
  <title>AlgAE Algorithm Animation Engine − Help</title>
  <!−− maps −−>
  <maps>
    <homeID>top</homeID>
    <mapref location="helpset.jhm" />
  </maps>
  <!−− views −−>
  <view xml:lang="en" mergetype="javax.help.UniteAppendMerge">
    <name>TOC</name>
    <label>Table Of Contents</label>
    <type>javax.help.TOCView</type>
    <data>algaeTOC.xml</data>
  </view>
  <!−− presentation windows −−>
  <!−− This window is the default one for the helpset. It
       * is a tri−paned window because displayviews, not
       * defined, defaults to true and because a toolbar is defined.
       * The toolbar has a back arrow, a forward arrow, and
       * a home button that has a user−defined image.
       −−>
  <presentation default=true>
    <name>main window</name>
    <size width="400" height="400" />
    <location x="200" y="200" />
    <title>AlgAE Help</title>
    <toolbar>
      <helpaction>javax.help.BackAction</helpaction>
      <helpaction>javax.help.ForwardAction</helpaction>
      <helpaction image="homeicon">javax.help.HomeAction</helpaction>
    </toolbar>
  </presentation>
  <!−− implementation section −−>
  <impl>
    <helpsetregistry helpbrokerclass="javax.help.DefaultHelpBroker" />
    <viewerregistry viewertype="text/html"
		    viewerclass="com.sun.java.help.impl.CustomKit" />
    <viewerregistry viewertype="text/xml"
                    viewerclass="com.sun.java.help.impl.CustomXMLKit" />
  </impl>
</helpset>

