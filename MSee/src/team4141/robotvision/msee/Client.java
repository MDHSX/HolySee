package team4141.robotvision.msee;

public class Client implements Runnable {

	private SocketClientHandler handler;
	public Client(SocketClientHandler handler){
		this.handler = handler;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		handler.onSocketClientInitialized();
	}
}
