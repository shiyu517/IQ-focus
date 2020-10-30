package comp1110.ass2;

import java.util.Random;

/**
 * choose by the amount of time it takes to find the solution, that is the total trying amount needed in each challenge
 * the more trying amount needed, the more difficult
 *
 */

public class Objective {
    private int problemNumber;
    private String challenge;


    static Objective[] OBJECTIVES= {
            new Objective("GGGRGRBBB", //12ms
                    1),
            new Objective("GWRGWWGGG", //23ms
                    2),
            new Objective("RGGGGRBGG", //41ms
                    3),
            new Objective("BGGWGGRWB", // 78ms
                    4),
            new Objective("BBBWRWGGG", // 90ms
                    5),
            new Objective("RGGRGGRRB", // 97ms
                    6),
            new Objective("WRRWRRGWW", //114ms
                    7),
            new Objective("GRWGRWWWW", // 226ms
                    8),
            new Objective("RRRBWBBRB", // 229ms
                    9),
            new Objective("RWWRRRWWW", // 753ms
                    10)
    };

    public String getChallenge(){
        return challenge;
    }
    public Objective(String challenge, int problemNumber) {
        assert problemNumber >= 1 && problemNumber <= 10;
        this.challenge = challenge;
        this.problemNumber = problemNumber;
    }

    public static Objective newObjective(int difficulty) {
        assert difficulty >= 0 && difficulty <= 3;
        Random r = new Random();
        int range;
        int initialNum;
        switch (difficulty){
            case 0:
                range = 3;
                System.out.println("__"+r.nextInt(range));
                return OBJECTIVES[r.nextInt(range)];
            case 1:
                range=4;
                initialNum=3;
                return OBJECTIVES[r.nextInt(range)+initialNum];
            case 2:
                range = 3;
                initialNum=6;
                return OBJECTIVES[r.nextInt(range)+initialNum];
            default:
                return OBJECTIVES[0];
        }
    }

}
