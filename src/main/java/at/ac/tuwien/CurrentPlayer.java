package at.ac.tuwien;

public enum CurrentPlayer {
    PLAYER_1 {
        @Override
        public CurrentPlayer nextPlayer() {
            return PLAYER_2;
        }
    },
    PLAYER_2 {
        @Override
        public CurrentPlayer nextPlayer() {
            return PLAYER_1;
        }
    };

    public abstract CurrentPlayer nextPlayer();
}
