//============================================================================
// Name        : SimpleVideoPipilineRecorder.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include <string>
#include <gst/gst.h>

int main(int argc, char *argv[]) {
	std::cout << "Simple recording video pipeline." << std::endl; // prints !!!Hello World!!!
  GstElement *pipeline;
  GstBus *bus;
  GstMessage *msg;

  /* Initialize GStreamer */
  gst_init (&argc, &argv);

  /* Build the pipeline */
  //std::string pipeline = "videotestsrc ! video/x-raw,width=1280,height=720 ! autovideosink";
  std::string pipelineString = "videotestsrc ! autovideosink";
//  std::string pipelineString = "v4l2src device=/dev/video1 ! video/x-raw,format=YUY2,height=480,framerate=15/1 ! autovideosink";
//  std::string pipelineString = "v4l2src device=/dev/video1 ! video/x-raw,format=YUY2,height=480,framerate=15/1 ! videoconvert ! ffmpegcolorspace ! jpegenc ! queue ! avimux ! filesink location=/home/team4141/Videos/video.avi";

  pipeline = gst_parse_launch (pipelineString.c_str(), NULL);

  /* Start playing */
  gst_element_set_state (pipeline, GST_STATE_PLAYING);

  /* Wait until error or EOS */
  bus = gst_element_get_bus (pipeline);
  msg = gst_bus_timed_pop_filtered (bus, GST_CLOCK_TIME_NONE, (GstMessageType)(GST_MESSAGE_ERROR | GST_MESSAGE_EOS));

  /* Free resources */
  if (msg != NULL)
    gst_message_unref (msg);
  gst_object_unref (bus);
  gst_element_set_state (pipeline, GST_STATE_NULL);
  gst_object_unref (pipeline);
  return 0;
}

