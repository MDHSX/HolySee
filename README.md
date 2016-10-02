# HolySee

This repository contains Team 4141's Robot Vision solution.  For background information, see [Robot Vision Concepts](https://github.com/MDHSRobotics/TeamWiki/wiki/Robot%20Vision%20Concepts).

This repository contains the following projects:

1. __MSee__ a Java application which acts as a container for the c++ library.
   * it embeds jetty to enable the vision computer to talk to the console and the RoboRio
   * it enables the operator to switch feeds
   * it uses the c++ library to process the images

1. __CSee__  a c++ shared library which contains the C++ code that does the image processing work

1. __ISee__  a javascript application that can be embedded in the operator interface to receive video streams and control feed selection


