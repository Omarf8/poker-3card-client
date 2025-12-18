import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public char suit;
    public int rank;
    public String pathName;

    public Card(char suit, int rank, String pathName) {
        this.suit = suit;
        this.rank = rank;
        this.pathName = pathName;
    }

    public char getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public String getPathName() {
        return pathName;
    }

    public void setSuit(char suit) {
        this.suit = suit;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
