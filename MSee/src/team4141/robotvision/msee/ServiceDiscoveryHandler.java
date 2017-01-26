package team4141.robotvision.msee;

public interface ServiceDiscoveryHandler {
	void onServiceFound(String name, String address, int port);
}