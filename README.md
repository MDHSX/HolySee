# HolySee

This repository contains Team 4141's Robot Vision solution.  For background information, see [Robot Vision Concepts](Robot Vision Concepts).

This repository contains the following projects:
1. c++ shared library which contains the C++ code that does the image processing work
2. Java application which acts as a container for the c++ library.
   * it embeds jetty to enable the vision computer to talk to the console and the RoboRio
   * it enables the operator to switch feeds
   * it uses the c++ library to process the images
1. A web app to simulate the operator interface

