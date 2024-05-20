package at.ac.tuwien;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class MancalaBoard {

    int player1Loft;

    int player2Loft;

    int[] player1Pits;

    int[] player2Pits;

    MancalaBoard() {
        this(6, 4);
    }

    MancalaBoard(int pits, int seeds) {
        this.player1Pits = new int[pits];
        Arrays.fill(this.player1Pits, seeds);

        this.player2Pits = new int[pits];
        Arrays.fill(this.player2Pits, seeds);
    }

    MancalaBoard(int[] player1Pits, int player1Loft, int[] player2Pits, int player2Loft) {
        this.player1Loft = player1Loft;
        this.player2Loft = player2Loft;
        this.player1Pits = player1Pits;
        this.player2Pits = player2Pits;
    }

    public boolean isOneOfThePitsEmpty() {
        boolean empty = true;
        for (int i = 0; i < player1Pits.length; i++) {
            if (player1Pits[i] != 0) {
                empty = false;
            }
        }
        if(empty){
            return empty;
        }

        for (int i = 0; i < player2Pits.length; i++) {
            if (player2Pits[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public int[] getPlayerPits(CurrentPlayer player) {
        if (player == CurrentPlayer.PLAYER_1) {
            return player1Pits;
        } else {
            return player2Pits;
        }
    }

    public int getPlayerLoft(CurrentPlayer player) {
        if (player == CurrentPlayer.PLAYER_1) {
            return player1Loft;
        } else {
            return player2Loft;
        }
    }

}