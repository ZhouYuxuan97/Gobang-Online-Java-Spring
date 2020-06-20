package cn.zyx.chatroom;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint("/websocket")
@Component
public class WebSocketTest {

    //record online count
    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<WebSocketTest> user = new CopyOnWriteArraySet<WebSocketTest>();

    private Session session;


    @OnOpen
    public void onOpen(Session session) throws IOException{
        this.session = session;
        user.add(this);
        this.sendMessage("Your name:" + session.getId());
        addOnlineCount();
        System.out.println("Build Connection, sessionID:" + session.getId());
        this.sendMessagetoAll("Now " + getOnlineCount() + " people online.");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException{

       for(WebSocketTest myWebSocket : user){
           myWebSocket.session.getBasicRemote().sendText(session.getId());
       }
        System.out.println("Receive message:" + message);
    }

    @OnClose
    public void onClose(Session session) throws IOException{
        user.remove(this);
        subOnlineCount();
        this.sendMessagetoAll("One connection closed! Now " + getOnlineCount() + " people online.");
        System.out.println("Close Connection");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("Error");
        error.printStackTrace();
    }

    private void sendMessage(String message) {
        try{
            this.session.getBasicRemote().sendText(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessagetoAll(String message) {
        for(WebSocketTest myWebSocket : user){
            try{
                myWebSocket.session.getBasicRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketTest.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketTest.onlineCount--;
    }
}


