import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ClientController implements Initializable {
    // Welcome Scene FXML Elements
    @FXML
    public BorderPane welcomeBP;

    @FXML
    public TextField portTF;

    @FXML
    public TextField ipTF;

    @FXML
    public Button connect;

    // Play Scene Elements
    @FXML
    public BorderPane gameBP;

    @FXML
    public Text totalEarnings;

    @FXML
    public Text anteBet;
    @FXML
    public Text playBet;
    @FXML
    public Text pairBet;

    @FXML
    public Button anteInc;
    @FXML
    public Button anteDec;

    @FXML
    public Button pairInc;
    @FXML
    public Button pairDec;

    @FXML
    public ImageView dealerC1;
    @FXML
    public ImageView dealerC2;
    @FXML
    public ImageView dealerC3;

    @FXML
    public ImageView playerC1;
    @FXML
    public ImageView playerC2;
    @FXML
    public ImageView playerC3;

    @FXML
    public Button dealButton;
    @FXML
    public Button playButton;
    @FXML
    public Button foldButton;
    @FXML
    public Button nextButton;

    // End Scene Elements
    @FXML
    public BorderPane endBP;

    @FXML
    public Text statusText;

    @FXML
    public Text earningsText;

    @FXML
    public ImageView dealerEndC1;
    @FXML
    public ImageView dealerEndC2;
    @FXML
    public ImageView dealerEndC3;

    @FXML
    public ImageView playerEndC1;
    @FXML
    public ImageView playerEndC2;
    @FXML
    public ImageView playerEndC3;

    @FXML
    public ListView<String> listItems;

    // Error Scene Elements
    @FXML
    public BorderPane errorBP;

    public String sending = "";

    private Client client;
    public int anteVal = 5;
    public int pairVal = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void connectToServer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientDeal.fxml"));
            Parent root2 = loader.load(); // Load view into parent
            ClientController ctrl = loader.getController(); // Controller created by the FXML
            root2.getStylesheets().add("/styles/clientDealStyle.css"); // Set the style for the second scene

            ctrl.nextButton.setVisible(false); // The button to the next scene should be invisible
            ctrl.lockDealPlayFold(); // We should lock the ante and pair buttons before showing the new scene
            ctrl.unlockDeal(); // Unlock just the deal button

            Client client = new Client(Integer.parseInt(portTF.getText()), ipTF.getText(), data -> {
                Platform.runLater(()->{
                    ctrl.listItems.getItems().add(data.toString());
                });
            });
            client.start(); // Start the thread
            ctrl.setClient(client); // This gives us access to the Client object we just created in case we need it

            // This is necessary because starting the thread takes some times so we want to wait if a connection was
            // successful to change scenes otherwise we should stay in the same scene
            Platform.runLater(()->{
                if(client.waitForConnection()) {
                    welcomeBP.getScene().setRoot(root2); // Set the scene from the welcome screen to the new scene when server is on
                    ctrl.client.clientCallback("Game #" + client.getGameNum() + ": ");
                    client.send("started a game"); // We should inform the server that we started a game
                    client.send("DEAL"); // We want to prefetch the data needed so that the user program can immediately use the server data
                }
            });

            // We should have an else statement where if there wasn't a successful connection then we should
            // make sure that we stop the Client thread and free everything
        }
        catch (NumberFormatException e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setContentText("Please enter a valid port number");
            alert.showAndWait();
        }
        catch (Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }

    public void dealMethod() {
        if(sending.equals("RESET")) {
            Platform.runLater(()->{
                totalEarnings.setText("$" + client.info.playerTotal); // Update the earnings
            });

            client.send("earned $" + client.info.ppEarnings + " from the Pair Plus bet"); // Inform the server how much was won or lost

            if(client.info.ppEarnings > 0) {
                client.clientCallback("Player won $" + client.info.ppEarnings + " from the the Pair Plus");
            }
            else if(client.info.ppEarnings < 0) {
                client.clientCallback("Player lost $" + client.info.ppEarnings + " from the the Pair Plus");
            }

            unlockPair(); // Unlock the pair plus buttons to change pair plus bets
            // Clear the board
            playerC1.setImage(null);
            playerC2.setImage(null);
            playerC3.setImage(null);
            dealerC1.setImage(null);
            dealerC2.setImage(null);
            dealerC3.setImage(null);
            sending = ""; // Change this so we don't continue resetting
            client.info.setMessage("DEALDONE");
            return;
        }
        // Since we prefetch data once the client connects, we want to make sure the PokerInfo
        // object had its String message changed to DEALDONE to begin displaying the cards to the user
        if(client.info.message.equals("DEALDONE")) {
            sending = "DEAL";
            client.send("is dealing"); // Send status of the game
            lockAntePair(); // Prevent the player from changing the bets
            lockDealPlayFold(); // Lock all buttons
            unlockPlayFold(); // Unlock only the play and fold option

            // Set player card images and dealer images
            playerC1.setImage(new Image("/images" + client.style + client.info.playerHand.get(0).getPathName()));
            playerC2.setImage(new Image("/images" + client.style + client.info.playerHand.get(1).getPathName()));
            playerC3.setImage(new Image("/images" + client.style + client.info.playerHand.get(2).getPathName()));
            dealerC1.setImage(new Image("/images" + client.style + "DeckBack.png"));
            dealerC2.setImage(new Image("/images" + client.style + "DeckBack.png"));
            dealerC3.setImage(new Image("/images" + client.style + "DeckBack.png"));
        }
    }

    public void playMethod() {
        if(client.info.pushAnte) {
            client.send("set the Ante to $" + anteVal + " and has a Pair Plus bet of $" + pairVal); // Inform the server of the bets
            client.clientCallback("Dealer does not have at least a Queen High; Ante Wager is pushed");
            // Reveal the dealer's hand
            dealerC1.setImage(new Image("/images" + client.style + client.info.dealerHand.get(0).getPathName()));
            dealerC2.setImage(new Image("/images" + client.style + client.info.dealerHand.get(1).getPathName()));
            dealerC3.setImage(new Image("/images" + client.style + client.info.dealerHand.get(2).getPathName()));

            lockDealPlayFold(); // Lock everything
            unlockDeal(); // Only unlock the deal button

            sending = "RESET";
            client.send("PUSHPLAY");
            return;
        }

        if(!sending.equals("PLAY")) {
            sending = "PLAY";
            client.send("set the Ante to $" + anteVal + " and has a Pair Plus bet of $" + pairVal); // Inform the server of the bets
            client.send("PLAY"); // Give the server the next piece of info it needs
            lockFold();
        }

        if(client.info.message.equals("PLAYDONE")) {
            Platform.runLater(()->{
                totalEarnings.setText("$" + client.info.playerTotal); // Update the earnings
            });
            // Reveal the dealer's hand
            dealerC1.setImage(new Image("/images" + client.style + client.info.dealerHand.get(0).getPathName()));
            dealerC2.setImage(new Image("/images" + client.style + client.info.dealerHand.get(1).getPathName()));
            dealerC3.setImage(new Image("/images" + client.style + client.info.dealerHand.get(2).getPathName()));

            if(client.info.won) {
                client.send("beat the dealer and earned $" + client.info.roundEarnings);
                client.clientCallback("Player beats dealer");
            }
            else {
                client.send("lost against the dealer and lost $" + client.info.roundEarnings);
                client.clientCallback("Player lost to dealer");
            }

            if(client.info.ppEarnings > 0) {
                client.clientCallback("Player won $" + client.info.ppEarnings + " from the the Pair Plus");
            }
            else if(client.info.ppEarnings < 0) {
                client.clientCallback("Player lost $" + client.info.ppEarnings + " from the the Pair Plus");
            }

            // Remove the Buttons at the bottom from the screen
            dealButton.setVisible(false);
            playButton.setVisible(false);
            foldButton.setVisible(false);

            nextButton.setVisible(true);
        }
    }

    public void foldMethod() {
        if(!sending.equals("FOLD")) {
            sending = "FOLD";
            client.send("set the Ante to $" + anteVal + " and has a Pair Plus bet of $" + pairVal); // Inform the server of the bets
            client.send("FOLD"); // Give the server the next piece of info it needs
            lockPlay();
        }

        if(client.info.message.equals("FOLDDONE")) {
            client.info.won = false; // We might have won but should indicate that we lost because we folded

            Platform.runLater(()->{
                totalEarnings.setText("$" + client.info.playerTotal); // Update the earnings
            });

            // Reveal the dealer's hand
            dealerC1.setImage(new Image("/images" + client.style + client.info.dealerHand.get(0).getPathName()));
            dealerC2.setImage(new Image("/images" + client.style + client.info.dealerHand.get(1).getPathName()));
            dealerC3.setImage(new Image("/images" + client.style + client.info.dealerHand.get(2).getPathName()));

            client.send("lost against the dealer and lost $" + client.info.roundEarnings);
            client.clientCallback("Player lost to dealer");
            client.clientCallback("Player lost $" + client.info.ppEarnings + " from the the Pair Plus");

            Platform.runLater(()->{
                playBet.setText("$0");
            });

            // Remove the Buttons at the bottom from the screen
            dealButton.setVisible(false);
            playButton.setVisible(false);
            foldButton.setVisible(false);

            nextButton.setVisible(true);
        }
    }

    public void nextMethod() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientEnd.fxml"));
            Parent root2 = loader.load(); // Load view into parent
            ClientController ctrl = loader.getController(); // Controller created by the FXML
            root2.getStylesheets().add("/styles/clientEndStyle.css"); // Set the style for the last scene

            // Add the new look if it is on
            if(gameBP.getStyleClass().contains("new-look")) {
                root2.getStyleClass().add("new-look");
            }

            ctrl.setClient(client); // This gives us access to the Client object we created in case we need it

            // Set the image views
            ctrl.dealerEndC1.setImage(new Image("/images" + client.style + client.info.dealerHand.get(0).getPathName()));
            ctrl.dealerEndC2.setImage(new Image("/images" + client.style + client.info.dealerHand.get(1).getPathName()));
            ctrl.dealerEndC3.setImage(new Image("/images" + client.style + client.info.dealerHand.get(2).getPathName()));
            ctrl.playerEndC1.setImage(new Image("/images" + client.style + client.info.playerHand.get(0).getPathName()));
            ctrl.playerEndC2.setImage(new Image("/images" + client.style + client.info.playerHand.get(1).getPathName()));
            ctrl.playerEndC3.setImage(new Image("/images" + client.style + client.info.playerHand.get(2).getPathName()));

            Platform.runLater(()->{
                if(client.info.won) {
                    ctrl.statusText.setText("You Won!");
                }
                else {
                    ctrl.statusText.setText("You Lost!");
                }
                ctrl.earningsText.setText("$" + client.info.roundEarnings);
            });

            gameBP.getScene().setRoot(root2); // Set the scene from the welcome screen to the new scene when server is on
        }
        catch(Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }

    public void playAgainMethod() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientDeal.fxml"));
            Parent root2 = loader.load(); // Load view into parent
            ClientController ctrl = loader.getController(); // Controller created by the FXML
            root2.getStylesheets().add("/styles/clientDealStyle.css"); // Set the style for the second scene

            // Add the new look if it is on
            if(endBP != null && endBP.getStyleClass().contains("new-look")) {
                root2.getStyleClass().add("new-look");
            }

            ctrl.setClient(client); // This gives us access to the Client object we just created in case we need it

            ctrl.nextButton.setVisible(false); // The button to the next scene should be invisible
            ctrl.lockDealPlayFold(); // We should lock the ante and pair buttons before showing the new scene
            ctrl.unlockDeal(); // Unlock just the deal button

            client.clientCallback("Game #" + client.getGameNum() + ": ");
            ctrl.listItems.setItems(client.getLog()); // This displays the entire log that has been accumulating throughout the game

            ctrl.totalEarnings.setText("$" + client.info.playerTotal);
            // Reset the bets
            ctrl.client.info.playerAnteBet = 5;
            ctrl.client.info.playerPPBet = 5;

            client.send("is playing another game"); // Tell the server the client is playing again
            client.send("DEAL"); // We want to prefetch the data needed so that the user program can immediately use the server data
            endBP.getScene().setRoot(root2); // Set the scene from the end screen to the new scene
        }
        catch(Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }

    public void freshStartMethod() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientDeal.fxml"));
            Parent root2 = loader.load(); // Load view into parent
            ClientController ctrl = loader.getController(); // Controller created by the FXML
            root2.getStylesheets().add("/styles/clientDealStyle.css"); // Set the style for the second scene

            // Add the new look if it is on
            if(gameBP != null && gameBP.getStyleClass().contains("new-look")) {
                root2.getStyleClass().add("new-look");
            }
            else if(endBP != null && endBP.getStyleClass().contains("new-look")) {
                root2.getStyleClass().add("new-look");
            }

            ctrl.nextButton.setVisible(false); // The button to the next scene should be invisible
            ctrl.lockDealPlayFold(); // We should lock the ante and pair buttons before showing the new scene
            ctrl.unlockDeal(); // Unlock just the deal button

            ctrl.setClient(client); // This gives us access to the Client object we just created in case we need it
            ctrl.client.resetInfo(); // This makes a new Poker Info object which resets all values
            ctrl.client.send("FRESH"); // Refresh all content
            ctrl.client.send("is restarting");

            client.clientCallback("Game #" + client.getGameNum() + ": ");
            ctrl.listItems.setItems(client.getLog());

            if(gameBP != null) {
                gameBP.getScene().setRoot(root2);
            }
            else if(endBP != null) {
                endBP.getScene().setRoot(root2);
            }

            ctrl.client.send("DEAL"); // Tell the server to deal
        }
        catch(Exception e) {
            // Stay in the same scene and alert the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Something went wrong with loading the scene...");
            alert.showAndWait();
        }
    }

    public void newLookDealMethod() {
        // We need a try/catch because if the cards are not set then it causes errors
        try {
            // Get the path names for each card
            String pc1Path = playerC1.getImage().getUrl();
            String pc2Path = playerC2.getImage().getUrl();
            String pc3Path = playerC3.getImage().getUrl();
            String dc1Path = dealerC1.getImage().getUrl();
            String dc2Path = dealerC2.getImage().getUrl();
            String dc3Path = dealerC3.getImage().getUrl();

            // Default look
            if(gameBP.getStyleClass().contains("new-look")) {
                client.style = "/CLIENT_52CARDS/";
                gameBP.getStyleClass().remove("new-look");

                // Set player card images and dealer images
                playerC1.setImage(new Image(pc1Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                playerC2.setImage(new Image(pc2Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                playerC3.setImage(new Image(pc3Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerC1.setImage(new Image(dc1Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerC2.setImage(new Image(dc2Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerC3.setImage(new Image(dc3Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
            }
            // New look
            else {
                client.style = "/CLIENT_52CARDSGOLD/";
                gameBP.getStyleClass().add("new-look");

                // Set player card images and dealer images
                playerC1.setImage(new Image(pc1Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                playerC2.setImage(new Image(pc2Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                playerC3.setImage(new Image(pc3Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerC1.setImage(new Image(dc1Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerC2.setImage(new Image(dc2Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerC3.setImage(new Image(dc3Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
            }
        }
        catch(Exception e) {
            // We should still change the border pane color style
            // Default look
            if(gameBP.getStyleClass().contains("new-look")) {
                client.style = "/CLIENT_52CARDS/";
                gameBP.getStyleClass().remove("new-look");
            }
            // New look
            else {
                client.style = "/CLIENT_52CARDSGOLD/";
                gameBP.getStyleClass().add("new-look");
            }
        }
    }

    public void newLookEndMethod() {
        // We need a try/catch because if the cards are not set then it causes errors
        try {
            // Get the path names for each card
            String pc1Path = playerEndC1.getImage().getUrl();
            String pc2Path = playerEndC2.getImage().getUrl();
            String pc3Path = playerEndC3.getImage().getUrl();
            String dc1Path = dealerEndC1.getImage().getUrl();
            String dc2Path = dealerEndC2.getImage().getUrl();
            String dc3Path = dealerEndC3.getImage().getUrl();

            // Default look
            if(endBP.getStyleClass().contains("new-look")) {
                client.style = "/CLIENT_52CARDS/";
                endBP.getStyleClass().remove("new-look");

                // Set player card images and dealer images
                playerEndC1.setImage(new Image(pc1Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                playerEndC2.setImage(new Image(pc2Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                playerEndC3.setImage(new Image(pc3Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerEndC1.setImage(new Image(dc1Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerEndC2.setImage(new Image(dc2Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
                dealerEndC3.setImage(new Image(dc3Path.replace("/CLIENT_52CARDSGOLD/", "/CLIENT_52CARDS/")));
            }
            // New look
            else {
                client.style = "/CLIENT_52CARDSGOLD/";
                endBP.getStyleClass().add("new-look");

                // Set player card images and dealer images
                playerEndC1.setImage(new Image(pc1Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                playerEndC2.setImage(new Image(pc2Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                playerEndC3.setImage(new Image(pc3Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerEndC1.setImage(new Image(dc1Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerEndC2.setImage(new Image(dc2Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
                dealerEndC3.setImage(new Image(dc3Path.replace("/CLIENT_52CARDS/", "/CLIENT_52CARDSGOLD/")));
            }
        }
        catch(Exception e) {
            // We should still change the border pane color style
            // Default look
            if(endBP.getStyleClass().contains("new-look")) {
                client.style = "/CLIENT_52CARDS/";
                endBP.getStyleClass().remove("new-look");
            }
            // New look
            else {
                client.style = "/CLIENT_52CARDSGOLD/";
                endBP.getStyleClass().add("new-look");
            }
        }
    }

    public void exitMethod() {
        Platform.exit();
    }

    public void incAnteMethod() {
        if(anteVal < 25) {
            anteVal++;
            Platform.runLater(()->{
               anteBet.setText("$" + anteVal);
               playBet.setText("$" + anteVal);
            });
            client.info.playerAnteBet  = anteVal;
        }
    }

    public void decAnteMethod() {
        if(anteVal > 5) {
            anteVal--;
            Platform.runLater(()->{
                anteBet.setText("$" + anteVal);
                playBet.setText("$" + anteVal);
            });
            client.info.playerAnteBet = anteVal;
        }
    }

    public void incPairMethod() {
        // This executes if we want to bet on the pair plus
        if(pairVal == 0) {
            pairVal = 5;
            Platform.runLater(()->{
                pairBet.setText("$5");
            });
            client.info.playerPPBet = pairVal;
        }
        else if(pairVal < 25) {
            pairVal++;
            Platform.runLater(()->{
                pairBet.setText("$" + pairVal);
            });
            client.info.playerPPBet = pairVal;
        }
    }

    public void decPairMethod() {
        if(pairVal > 5) {
            pairVal--;
            Platform.runLater(()->{
                pairBet.setText("$" + pairVal);
            });
            client.info.playerPPBet = pairVal;
        }
        // We can go from 5 to 0 if we don't want to bet on a pair plus
        else if(pairVal == 5) {
            pairVal = 0;
            Platform.runLater(()->{
                pairBet.setText("$0");
            });
            client.info.playerPPBet = pairVal;
        }
    }

    public void lockDealPlayFold() {
        lockDeal();
        lockPlay();
        lockFold();
    }

    public void lockDeal() {
        dealButton.setDisable(true);
    }

    public void lockPlay() {
        playButton.setDisable(true);
    }

    public void lockFold() {
        foldButton.setDisable(true);
    }

    public void unlockDeal() {
        dealButton.setDisable(false);
    }

    public void unlockPlayFold() {
        playButton.setDisable(false);
        foldButton.setDisable(false);
    }

    public void lockAntePair() {
        anteInc.setDisable(true);
        anteDec.setDisable(true);
        pairInc.setDisable(true);
        pairDec.setDisable(true);
    }

    public void unlockPair() {
        pairInc.setDisable(false);
        pairDec.setDisable(false);
    }
}
