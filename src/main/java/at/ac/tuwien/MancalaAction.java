package at.ac.tuwien;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
public class MancalaAction {

    int targetPit;

    public MancalaAction(int targetPit) {
        System.out.println("mancalaAc");

        this.targetPit = targetPit;
    }

}
