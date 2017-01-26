package team4141.robotvision.msee;

public interface CSeeHandler {
	void onCSeeInitialized(ICSee csee);
	String getName();
	void onCSeeStopped();
}
