package at.ac.tuwien;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Mancala implements Game<Integer, MancalaBoard> {

    private MancalaBoard mancalaBoard;

    private CurrentPlayer currentPlayer = CurrentPlayer.PLAYER_1;

    private final List<ActionRecord<Integer>> actionRecords;



    private boolean canonical = true;


    public Mancala() {
        this.mancalaBoard = new MancalaBoard();
        this.actionRecords = new ArrayList<>();
    }

    public Mancala(int numberOfPits, int numberOfSeeds) {
        this.mancalaBoard = new MancalaBoard(numberOfPits, numberOfSeeds);
        this.actionRecords = new ArrayList<>();

    }

    public Mancala(MancalaBoard mancalaBoard) {
        this.mancalaBoard = mancalaBoard;
        this.actionRecords = new ArrayList<>();
    }

    public Mancala(String board, int numberOfPlayers) {
        this(6, 4);
        if (numberOfPlayers != 2) {
            throw new IllegalArgumentException("2 player game");
        }
    }


    public Mancala(int currentPlayer, boolean canonical, List<ActionRecord<Integer>> actionRecords,
                   MancalaBoard board) {
        if (currentPlayer == 0) {
            this.currentPlayer = CurrentPlayer.PLAYER_1;
        } else {
            this.currentPlayer = CurrentPlayer.PLAYER_2;
        }
        this.actionRecords = actionRecords;
        this.mancalaBoard = board;
        this.canonical = canonical;
    }

    public Mancala(Mancala mancala) {
        this.mancalaBoard = mancala.mancalaBoard;
        this.actionRecords = mancala.actionRecords;
    }

    @Override
    public int getMinimumNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getMaximumNumberOfPlayers() {
        return 2;
    }

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }

    @Override
    public double getUtilityValue(int i) {
        if (i == 0) {
            return mancalaBoard.player1Loft;
        } else {
            return mancalaBoard.player2Loft;
        }
    }


    @Override
    public Set<Integer> getPossibleActions() {
        if(isGameOver()){
            return Collections.emptySet();
        }

        var getCurrentPlayerPits = mancalaBoard.getPlayerPits(currentPlayer);

        Set<Integer> possibleActions = new TreeSet<>();

        for (int i = 0; i < getCurrentPlayerPits.length; i++) {
            if (getCurrentPlayerPits[i] > 0) {
                possibleActions.add(i);
            }
        }

        return possibleActions;
    }

    @Override
    public MancalaBoard getBoard() {
        return mancalaBoard;
    }


    @Override
    public Game<Integer, MancalaBoard> doAction(Integer mancalaAction) {

        Mancala next = new Mancala(this);


        int[] currentPlayerPits = mancalaBoard.getPlayerPits(currentPlayer);
        int currentPlayerLoft = mancalaBoard.getPlayerLoft(currentPlayer);
        int[] oppositePlayerPits = mancalaBoard.getPlayerPits(currentPlayer.nextPlayer());
        int oppositePlayerLoft = mancalaBoard.getPlayerLoft(currentPlayer.nextPlayer());
        int pitPointer = mancalaAction;
        int pitLimit = currentPlayerPits.length;
        int seeds = currentPlayerPits[pitPointer];

        currentPlayerPits[pitPointer] = 0;
        pitPointer++;

        boolean takeExtraTurn = false;

        while (seeds > 0) {
            // ADD TO CURRENT PITS
            for (int i = 0; i < pitLimit - pitPointer; i++) {
                if (seeds > 0) {
                    currentPlayerPits[i + pitPointer] += 1;
                    seeds--;
                }

                if(seeds == 0 && currentPlayerPits[i + pitPointer] == 1){
                    System.out.println("stealing");
                   currentPlayerLoft += oppositePlayerPits[(oppositePlayerPits.length - 1) - (i + pitPointer)];
                   oppositePlayerPits[(oppositePlayerPits.length - 1) - (i + pitPointer)] = 0;
                }
            }

            // ADD TO CURRENT LOFT
            if (seeds > 0) {
                currentPlayerLoft += 1;
                seeds--;
                if(seeds == 0){
                    takeExtraTurn = true;
                }
            }

            // ADD TO ENEMY PITS
            for (int i = 0; i < pitLimit; i++) {
                if (seeds > 0) {
                    oppositePlayerPits[i] += 1;
                    seeds--;
                }
            }
        }
        MancalaBoard newBoardState;
        if (this.currentPlayer == CurrentPlayer.PLAYER_1) {
            newBoardState = new MancalaBoard(currentPlayerPits, currentPlayerLoft, oppositePlayerPits, oppositePlayerLoft);
            next.actionRecords.add(new ActionRecord<>(0, mancalaAction));

        } else {
            newBoardState = new MancalaBoard(oppositePlayerPits, oppositePlayerLoft, currentPlayerPits, currentPlayerLoft);
            next.actionRecords.add(new ActionRecord<>(1, mancalaAction));

        }

        next.mancalaBoard = newBoardState;

        if(takeExtraTurn){
            next.currentPlayer = this.currentPlayer;
        } else {
            next.currentPlayer = this.currentPlayer.nextPlayer();
        }

        if (isGameOver()) {
            for (int currentPlayerPit : currentPlayerPits) {
                currentPlayerLoft += currentPlayerPit;
            }
            for (int oppositePlayerPit : oppositePlayerPits) {
                oppositePlayerLoft += oppositePlayerPit;
            }
            if (this.currentPlayer == CurrentPlayer.PLAYER_1) {
                newBoardState = new MancalaBoard(currentPlayerPits, currentPlayerLoft, oppositePlayerPits, oppositePlayerLoft);
                next.actionRecords.add(new ActionRecord<>(0, mancalaAction));

            } else {
                newBoardState = new MancalaBoard(oppositePlayerPits, oppositePlayerLoft, currentPlayerPits, currentPlayerLoft);
                next.actionRecords.add(new ActionRecord<>(1, mancalaAction));

            }
            next.mancalaBoard = newBoardState;
            System.out.println("GAME OVER");
        }

        int[] temp = next.mancalaBoard.player1Pits;

        System.out.println("BOARD");
        System.out.println("["+  next.mancalaBoard.player1Loft+  "] | " + Arrays.toString(reverseArray(temp))+  "  ");
        reverseArray(temp);
        System.out.println("      "+Arrays.toString(next.mancalaBoard.player2Pits)+  " | [" + next.mancalaBoard.player2Loft + "]");

        return next;
    }

    public int[] reverseArray(int[] array) {
        int start = 0;
        int end = array.length - 1;

        while (start < end) {
            // Swap elements at start and end
            int temp = array[start];
            array[start] = array[end];
            array[end] = temp;

            // Move pointers
            start++;
            end--;
        }
        return array;
    }

    @Override
    public int getCurrentPlayer() {
        if (this.currentPlayer == CurrentPlayer.PLAYER_1) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Integer determineNextAction() {
        return null;
    }

    @Override
    public List<ActionRecord<Integer>> getActionRecords() {
        return Collections.unmodifiableList(actionRecords);
    }

    @Override
    public boolean isCanonical() {
        return this.canonical;
    }

    @Override
    public Game<Integer, MancalaBoard> getGame(int i) {
        return this;
    }


    @Override
    public boolean isGameOver() {
        return mancalaBoard.isOneOfThePitsEmpty();
    }

    public void nextPlayer() {
        // BU BU ŞEKİLDE DEĞİŞTİRİYOR MU PLAYER IDYİ YOKSA DİREK VARİABLEYE ASSİGNLAMAN MI LAZIM BAKMAYI UNUTMA.
        this.currentPlayer.nextPlayer();
    }
}