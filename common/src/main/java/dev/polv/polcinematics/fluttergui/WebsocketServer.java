package dev.polv.polcinematics.fluttergui;

import com.google.gson.JsonObject;
import dev.polv.polcinematics.PolCinematics;
import dev.polv.polcinematics.cinematic.Cinematic;
import dev.polv.polcinematics.cinematic.compositions.core.Composition;
import dev.polv.polcinematics.cinematic.compositions.core.Timeline;
import dev.polv.polcinematics.exception.InvalidCinematicException;
import dev.polv.polcinematics.utils.GsonUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketServer extends Thread {

    public final ConcurrentHashMap<Session, UUID> sessionPlayerRelation = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Session, Cinematic> sessionCinematicRelation = new ConcurrentHashMap<>();

    static class JsonPacket {
        public int type;
        public JsonObject data;
    }

    enum EPacketType {
        /*
        CONNECTED,
  AUTH,
  INVALID_AUTH,
  LOGGED,
  PING,
  PONG,
  GET_CINEMATICS_LIST,
  RESPONSE_CINEMATICS_LIST,
  GET_CINEMATIC,
  RESPONSE_CINEMATIC,
  RESPONSE_INVALID_CINEMATIC,
  UPDATE_COMPOSITION,
  UPDATE_TIMELINE_TIME, // TO UPDATE THE CAMERA ON THE CLIENT
  DISCONNECT,
         */

        CONNECTED(),
        DISCONNECT(),
        AUTH(),
        INVALID_AUTH(),
        LOGGED(),
        PING(),
        PONG(),
        GET_CINEMATICS_LIST(),
        RESPONSE_CINEMATICS_LIST(),
        GET_CINEMATIC(),
        RESPONSE_CINEMATIC(),
        RESPONSE_INVALID_CINEMATIC(),
        UPDATE_COMPOSITION(),
        UPDATE_TIMELINE_TIME(),
        ;

        EPacketType() {
        }

        public int getId() {
            return this.ordinal();
        }

        public static EPacketType fromId(int id) {
            return EPacketType.values()[id];
        }
    }

    class WSEndpoint extends WebSocketAdapter {
        private static final Logger LOG = LoggerFactory.getLogger(WSEndpoint.class);

        @Override
        public void onWebSocketConnect(Session sess) {
            super.onWebSocketConnect(sess);
            LOG.debug("Endpoint connected: {}", sess);
            getSession().getRemote().sendString("{\"type\": 0, \"data\": {}}", null);
        }

        private void sendPacket(EPacketType type, String data) {
            getSession().getRemote().sendString("{\"type\": " + type.getId() + ", \"data\": " + data + "}", null);
        }

        @Override
        public void onWebSocketText(String message) {
            super.onWebSocketText(message);
            LOG.debug("Received TEXT message: {}", message);
            JsonPacket packet = GsonUtils.gson.fromJson(message, JsonPacket.class);
            EPacketType packetType = EPacketType.fromId(packet.type);

            switch (packetType) {
                case AUTH -> {  // Auth packet
                    String editorPassword = packet.data.get("password").getAsString();
                    UUID playerUUID = FlutterGuiManager.INSTANCE.playerPasswords.get(editorPassword);
                    if (playerUUID == null) {
                        sendPacket(EPacketType.INVALID_AUTH, "{\"message\": \"Invalid password\"}");
                        return;
                        // Auth success
                    }
                    ServerPlayerEntity player = PolCinematics.SERVER.getPlayerManager().getPlayer(playerUUID);
                    if (player == null) {
                        sendPacket(EPacketType.INVALID_AUTH, "{\"message\": \"Invalid player\"}");
                        return;
                    }
                    sessionPlayerRelation.put(getSession(), player.getUuid());
                    sendPacket(EPacketType.LOGGED, "{\"playerName\": \"" + player.getName() + "\", \"playerUUID\": \"" + player.getUuid() + "\"}");
                }
                case PING -> { // PING
                    sendPacket(EPacketType.PONG, "{\"timestamp\": " + System.currentTimeMillis() + "}");
                }
                case GET_CINEMATICS_LIST -> { // GET CINEMATICS LIST
                    List<String> simpleCinematics = PolCinematics.CINEMATICS_MANAGER.getFileCinematics().stream().map(c -> "{\"uuid\": \"" + c.getUuid().toString() + "\", \"name\":\"" + c.getName() + "\"").toList();
                    sendPacket(EPacketType.RESPONSE_CINEMATICS_LIST, "{\"cinematics\": [" + String.join("\", \"", simpleCinematics) + "]}");
                }
                case GET_CINEMATIC -> { // GET CINEMATIC
                    String cinematicUUID = packet.data.get("uuid").getAsString();
                    try {
                        Cinematic cinematic = PolCinematics.CINEMATICS_MANAGER.loadCinematic(cinematicUUID);
                        sessionCinematicRelation.put(getSession(), cinematic);
                        getRemote().sendString("{\"type\": 9, \"data\": {\"cinematic\": " + cinematic.toJson().toString() + "}}", null);
                    } catch (InvalidCinematicException e) {
                        getRemote().sendString("{\"type\": 10, \"data\": {\"error\": \"" + e.getMessage() + "\"}}", null);
                    }
                }
                case UPDATE_COMPOSITION -> {  // UPDATE COMPOSITION
                    Cinematic cinematic = sessionCinematicRelation.get(getSession());
                    if (cinematic == null) {
                        // Maybe send error packet?
                        return;
                    }
                    var pairtc = cinematic.getTimelineAndComposition(UUID.fromString(packet.data.get("compositionUUID").getAsString()));
                    Timeline timeline = pairtc.getLeft();
                    Composition composition = pairtc.getRight();

                    if (packet.data.has("duration")) {
                        long newDuration = packet.data.get("duration").getAsLong();
                        timeline.changeDuration(composition.getUuid(), newDuration);
                    } else if (packet.data.has("delete")) {
                        timeline.remove(composition.getUuid());
                    }
                }
                case UPDATE_TIMELINE_TIME -> {  // TIMELINE POS UPDATE (TO UPDATE PLAYER CAMERA WHILE EDITING)
                    // TODO: Implement
                }
                case DISCONNECT -> { // DISCONNECT
                    // TODO
                    sessionPlayerRelation.remove(getSession());
                    sessionCinematicRelation.remove(getSession());
                }
            }

        }

        @Override
        public void onWebSocketClose(int statusCode, String reason)
        {
            super.onWebSocketClose(statusCode, reason);
            LOG.debug("Socket Closed: [{}] {}", statusCode, reason);
        }

        @Override
        public void onWebSocketError(Throwable cause)
        {
            super.onWebSocketError(cause);
            cause.printStackTrace(System.err);
        }
    }

    class WSEndpointCreator implements JettyWebSocketCreator {
        @Override
        public Object createWebSocket(JettyServerUpgradeRequest jettyServerUpgradeRequest, JettyServerUpgradeResponse jettyServerUpgradeResponse) {
            return new WSEndpoint();
        }
    }

    private final String bind = "127.0.0.1";
    private final int port;


    public WebsocketServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        /*try {
            ServerSocket server = new ServerSocket(this.port);
            System.out.println("Server has started on 127.0.0.1:" + port + ".\r\nWaiting for a connection…");
            Socket client = server.accept();
            System.out.println("A client connected. Going to auth...");
            // To contact client-server we will use json messages. The format will be { "type": 1, "data": {} }.
            // First job is receiving the auth message containing the password. Auth is packet type 1, and data will contain {"password": the password}


            while (true) {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        Server server = new Server(this.port);
        ServerConnector connector = new ServerConnector(server);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Configure specific websocket behavior
        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) ->
        {
            // Configure default max size
            wsContainer.setMaxTextMessageSize(65535);

            // Add websockets
            wsContainer.addMapping("/", new WSEndpointCreator());
        });

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
