package team4141.robotvision.msee;

import java.net.Inet4Address;

public interface ConnectionHandler {
	void onConnect(String serviceName, Inet4Address addresses, int port);
}
