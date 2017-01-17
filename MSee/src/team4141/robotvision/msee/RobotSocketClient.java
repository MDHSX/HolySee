package team4141.robotvision.msee;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;


@WebSocket
public class RobotSocketClient {
    private Session session;


    public RobotSocketClient()
    {

    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
        this.session = null;
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.printf("Connected!\n");
        this.session = session;
        String message = "{\"type\":\"remoteIdentification\", \"id\":\"tegra\"}";
        try {
			session.getRemote().sendString(message);
		} catch (IOException e) {
			System.err.println("unable to send: "+message);
			e.printStackTrace();
		}

    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        System.out.printf("Got msg: %s%n",msg);
    }
    
    public void send(String message){
    	if(session!=null){
    		try {
				session.getRemote().sendString(message);
			} catch (IOException e) {
				System.out.println("unable to send message: "+message);
				e.printStackTrace();
			}
    	}
    }
}