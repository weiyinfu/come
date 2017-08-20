package come;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat")
public class Chat {
private static final Set<Chat> connections = new CopyOnWriteArraySet<Chat>();
private Session session;

@OnOpen
public void onOpen(Session session) {
    this.session = session;
    connections.add(this);
}

@OnClose
public void onClose() {
    connections.remove(this);
}

@OnMessage
public void onMessage(String message) throws SQLException {
    JSONObject json = JSON.parseObject(message);
    String event = json.getString("event");
    if (event.equals("register")) {
        String nick = json.getString("nick");
        String openid = json.getString("openid");
        DB.insert(nick, openid);
    } else if (event.equals("click")) {
        String openid = json.getString("openid");
        int day = json.getInteger("day");
        if (day < 1 || day > 5) return;
        int nowDay = LocalDate.now().getDayOfWeek().ordinal();
        if (nowDay < 5 && day <= nowDay) return;//如果今天不是周六周日，过去的不能更改
        if (!DB.valid(openid)) return;
        DB.reverseCome(openid, day);
        broadCast(DB.getTable());
    }
}

@OnError
public void onError(Throwable throwable) {
    System.out.println(throwable.getMessage());
}

static void broadCast(String message) {
    for (Chat chat : connections) {
        try {
            synchronized (chat) {
                chat.session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            connections.remove(chat);
            try {
                chat.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
}