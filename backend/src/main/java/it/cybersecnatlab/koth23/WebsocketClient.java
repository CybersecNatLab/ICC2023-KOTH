package it.cybersecnatlab.koth23;

import jakarta.websocket.*;
import org.glassfish.tyrus.client.ClientManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

@ClientEndpoint
public final class WebsocketClient {
    private static final Logger LOGGER = Logger.getLogger(WebsocketClient.class.getSimpleName());
    private static final ClientManager CLIENT_MANAGER = ClientManager.createClient();
    private final BlockingQueue<String> messages = new ArrayBlockingQueue<>(128);
    private Session session;

    public static WebsocketClient connect(URI uri) throws DeploymentException, IOException, InterruptedException, TimeoutException {
        var client = new WebsocketClient();
        Future<Session> connection = CLIENT_MANAGER.asyncConnectToServer(client, uri);

        try {
            connection.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof DeploymentException) throw (DeploymentException) ex.getCause();
            else throw new RuntimeException(ex.getCause());
        } catch (TimeoutException ex) {
            connection.cancel(true);
            throw ex;
        }

        return client;
    }

    @OnOpen
    public synchronized void onOpen(Session session) {
        this.session = session;
        LOGGER.info("Connected to %s".formatted(session.getId()));
    }

    @OnClose
    public synchronized void onClose(Session session, CloseReason closeReason) {
        this.session = null;
        LOGGER.info("Closing %s because %s".formatted(session.getId(), closeReason.getReasonPhrase()));
    }

    @OnMessage
    public synchronized void onMessage(String content) {
        this.messages.add(content);
    }

    public synchronized void sendMessage(String message) throws IOException {
        if (message == null || session == null) {
            LOGGER.info("Not sending data to client.");
            return;
        }

        // TODO: check if the compression is actually needed
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(message.getBytes());
        }

        session.getBasicRemote().sendText(Base64.getEncoder().encodeToString(out.toByteArray()));
    }

    public synchronized boolean isConnected() {
        return session != null && session.isOpen();
    }

    public String nextMessage() {
        if (!this.isConnected()) {
            return null;
        }

        try {
            return this.messages.poll(Config.MAX_WAIT_FOR_MOVE_IN_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            return null;
        }
    }
}
