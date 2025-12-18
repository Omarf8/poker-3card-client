import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.io.Serializable;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client extends Thread {
    int port;
    String ip;
    Socket socketClient;
    ObjectOutputStream out;
    ObjectInputStream in;
    boolean connected;
    public PokerInfo info;
    public int gameNum;
    public String style = "/CLIENT_52CARDS/"; // We will use the default style initially
    public ObservableList<String> log; // We will use this to keep a log that lasts as we move from scene to scene

    private Consumer<Serializable> callback;
    private CountDownLatch latch = new CountDownLatch(1);

    Client(int portInput, String ipInput, Consumer<Serializable> call) {
        port = portInput;
        ip = ipInput;
        callback = call;
        connected = false;
        info = new PokerInfo();
        gameNum = 1;
        log = FXCollections.observableArrayList();
    }

    public void run() {
        try {
            socketClient = new Socket(ip, port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
            this.connected = true;
        }
        catch(Exception e) {
            this.connected = false;
            stopClientThread(); // Shut down the client thread
        }
        finally {
            latch.countDown();
        }

        while(this.connected) {
            try {
                PokerInfo inPI = (PokerInfo) in.readObject();
                if(inPI.message.equals("DEALDONE")) {
                    info.setPlayerHand(inPI.playerHand);
                    info.setDealerHand(inPI.dealerHand);
                    info.pushAnte = inPI.pushAnte;
                    info.won = inPI.won;
                    info.message = "DEALDONE";
                }
                else if(inPI.message.equals("PLAYDONE")) {
                    info.playerTotal = inPI.playerTotal;
                    info.roundEarnings = inPI.roundEarnings;
                    info.ppEarnings = inPI.ppEarnings;
                    info.message = "PLAYDONE";
                }
                else if(inPI.message.equals("FOLDDONE")) {
                    info.playerTotal = inPI.playerTotal;
                    info.roundEarnings = inPI.roundEarnings;
                    info.ppEarnings = inPI.ppEarnings;
                    info.message = "FOLDDONE";
                }
                else if(inPI.message.equals("PUSHPLAYDONE")) {
                    info.setPlayerHand(inPI.playerHand);
                    info.setDealerHand(inPI.dealerHand);
                    info.playerTotal = inPI.playerTotal;
                    info.ppEarnings = inPI.ppEarnings;
                    info.pushAnte = inPI.pushAnte;
                    info.won = inPI.won;
                    info.message = "PUSHPLAYDONE";
                }
            }
            catch(Exception e) {
                clientCallback("Something went wrong... Shutting down...");
                stopClientThread(); // Shut down the client thread
                break;
            }
        }

    }

    public boolean waitForConnection() {
        try {
            latch.await();
            return connected;
        }
        catch(InterruptedException e) {
            return false;
        }
    }

    public void send(String message) {
        try {
            PokerInfo outPI = new PokerInfo();
            outPI.playerTotal = info.playerTotal; // Send our current total
            outPI.setMessage(message); // Send the message
            if(message.equals("PLAY") || message.equals("FOLD") || message.equals("PUSHPLAY")) { outPI.playerAnteBet = info.playerAnteBet; outPI.playerPPBet = info.playerPPBet; }
            out.writeObject(outPI);
        }
        catch (SocketException e) {
            callback.accept("Error... Server is down...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetInfo() {
        info = new PokerInfo();
        log = FXCollections.observableArrayList();
        gameNum = 1;
    }

    public void clientCallback(String message) {
        Platform.runLater(() -> {
            callback.accept(message);
            log.add(message); // This helps keep a log across scenes
        });
    }

    public int getGameNum() {
        return gameNum++; // Increment the number when it gets called
    }

    public ObservableList<String> getLog() {
        return log;
    }

    public void stopClientThread() {
        try {
            if(in != null) {
                in.close();
            }
        }
        catch(Exception e) {}

        try {
            if(out != null) {
                out.close();
            }
        }
        catch(Exception e) {}

        try {
            if(socketClient != null && !socketClient.isClosed()) {
                socketClient.close();
            }
        }
        catch(Exception e) {}
    }
}
