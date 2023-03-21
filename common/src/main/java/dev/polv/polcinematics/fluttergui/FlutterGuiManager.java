package dev.polv.polcinematics.fluttergui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FlutterGuiManager {

    public static final FlutterGuiManager INSTANCE = new FlutterGuiManager();

    private boolean running = false;

    private static Process guiProcess = null;
    private Thread wsThread = null;

    private final int port = 5555;
    public final ConcurrentHashMap<String, UUID> playerPasswords = new ConcurrentHashMap<>();

    private FlutterGuiManager() {

    }

    @Environment(EnvType.CLIENT)
    public static void executeProgram(String uri, String password) {
        switch (System.getProperty("os.name").toLowerCase().split(" ")[0]) {
            case "windows" -> {
                try {
                    executeWindows(uri, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            default -> {
                System.out.println("OS not supported");
            }
        }
    }

    private static void executeWindows(String uri, String password) throws URISyntaxException, IOException {
        // Copy gui/windows to folder. note that gui/windows is in the resources folder
        URL resourceUrl = FlutterGuiManager.class.getResource("gui/windows/");
        Path sourcePath = Paths.get(resourceUrl.toURI());
        Path destinationFolderPath = Paths.get("cinematics/gui");

        if (!Files.exists(destinationFolderPath)) {
            Files.createDirectories(destinationFolderPath);
        }

        Files.walk(sourcePath).forEach(sourceFile -> {
            Path destinationFile = destinationFolderPath.resolve(sourcePath.relativize(sourceFile));

            try {
                Files.copy(sourceFile, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Run the program
        File program = new File("cinematics/gui/polcinematicsgui.exe");
        ProcessBuilder builder = new ProcessBuilder(program.getAbsolutePath(), "--password=" + password, "--host=" + uri);
        builder.redirectErrorStream(true);
        guiProcess = builder.start();
        new Thread(() -> {
            try {
                guiProcess.waitFor();
                // Exited editor
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startServer() {
        if (this.running) return;
        this.running = true;
        wsThread = new WebsocketServer(port);
        wsThread.start();
        // TODO: Check if socket started successfully
    }

    public void stopServer() {
        if (!this.running) return;
        this.running = false;
        // TODO: Stop server gracefully
        if (this.wsThread != null) {
            this.wsThread.interrupt();
        }
        this.playerPasswords.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public void test() {
        var t = new WebsocketServer(5555);
        t.start();
    }

}
