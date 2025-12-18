import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<Card> playerHand;
    public ArrayList<Card> dealerHand;
    public int playerPPBet;
    public int playerAnteBet;
    public int roundEarnings;
    public int ppEarnings;
    public int playerTotal;
    public boolean pushAnte;
    public boolean won;
    public String message;

    public PokerInfo() {
        // Initially, every game will start with a balance of 0, the Ante will not be pushed to the
        // next round and the game obviously started
        this.playerTotal = 0;
        this.playerPPBet = 5;
        this.playerAnteBet = 5;
        this.pushAnte = false;
        this.won = false;
        this.message = "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPlayerTotal(int playerTotal) {
        this.playerTotal = playerTotal;
    }

    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = dealerHand;
    }
}
