package comp1110.ass2;

import javax.swing.plaf.basic.BasicBorders;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static comp1110.ass2.PieceType.*;
import static comp1110.ass2.States.*;
import static comp1110.ass2.gui.Viewer.breakString;
import static javafx.application.Application.setUserAgentStylesheet;

/**
 * This class provides the text interface for the IQ Focus Game
 * <p>
 * The game is based directly on Smart Games' IQ-Focus game
 * (https://www.smartgames.eu/uk/one-player-games/iq-focus)
 */
public class FocusGame {
    public static final States[][] INITIALSTATE = {
            {States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY},
            {States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY},
            {States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY},
            {States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY},
            {States.BLUE, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.EMPTY, States.BLUE}
    };
    public static States[][] boardstate = new States[5][9];
    private static States[][] challengestate = new States[5][9];

    private Objective objective;

    public FocusGame(Objective objective) {
        this.objective = objective;
    }

    public FocusGame(int difficulty) {
        this(Objective.newObjective(difficulty));
    }

    public String getObjective() {
        return objective.getChallenge();
    }

    private static void setInitialstate() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                boardstate[i][j] = INITIALSTATE[i][j];
            }
        }
    }

    public static void printBoardstate() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(boardstate[i][j] + "  ");
            }
            System.out.println("");
        }
    }


    public static void printChallenge(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(challengestate[i][j] + "  ");
            }
            System.out.println("");
        }
    }


    /**
     * Determine whether a piece placement is well-formed according to the
     * following criteria:
     * - it consists of exactly four characters
     * - the first character is in the range a .. j (shape)
     * - the second character is in the range 0 .. 8 (column)
     * - the third character is in the range 0 .. 4 (row)
     * - the fourth character is in the range 0 .. 3 (orientation)
     *
     * @param piecePlacement A string describing a piece placement
     * @return True if the piece placement is well-formed
     *
     * @author Shiyu-All
     */
    public static boolean isPiecePlacementWellFormed(String piecePlacement) {
        if (piecePlacement.length() == 4) {
            if (piecePlacement.charAt(0) >= 97 & piecePlacement.charAt(0) <= 106 &
                    piecePlacement.charAt(1) >= 48 & piecePlacement.charAt(1) <= 56 &
                    piecePlacement.charAt(2) >= 48 & piecePlacement.charAt(2) <= 52 &
                    piecePlacement.charAt(3) >= 48 & piecePlacement.charAt(3) <= 51) {
                return true;
            } else return false;
        } else return false;
        // FIXME Task 2: determine whether a piece placement is well-formed
    }

    /**
     * Determine whether a placement string is well-formed:
     * - it consists of exactly N four-character piece placements (where N = 1 .. 10);
     * - each piece placement is well-formed
     * - no shape appears more than once in the placement
     *
     * @param placement A string describing a placement of one or more pieces
     * @return True if the placement is well-formed
     *
     * @author Ujjwal-All
     */
    public static boolean isPlacementStringWellFormed(String placement) {
        // FIXME Task 3: determine whether a placement is well-formed
        boolean multiples = (placement.length() % 4 == 0 && placement.length() / 4 > 0); // checks if there is a correct number of
        // Check if all pieces are well formed
        boolean pieceCheck = true;
        char[] piece = new char[4];
        String newPiece;
        for (int i = 0; i <= (placement.length() - 4); i = i + 4) {
            piece[0] = placement.charAt(i);
            piece[1] = placement.charAt(i + 1);
            piece[2] = placement.charAt(i + 2);
            piece[3] = placement.charAt(i + 3);
            newPiece = new String(piece);
            pieceCheck = pieceCheck && isPiecePlacementWellFormed(newPiece);
        }
        // Check if a shape is repeated
        String shapes = "abcdefghij";
        for (int i = 0; i < shapes.length(); i++) {
            int n = 0;
            char current = shapes.charAt(i);
            for (int j = 0; j < placement.length(); j++) {
                if (placement.charAt(j) == current) n++;
                if (n > 1) return false;
            }
        }
        return (multiples && pieceCheck);
    }

    /**
     * Determine whether a placement string is valid.
     * <p>
     * To be valid, the placement string must be:
     * - well-formed, and
     * - each piece placement must be a valid placement according to the
     * rules of the game:
     * - pieces must be entirely on the board
     * - pieces must not overlap each other
     *
     * @param placement A placement string
     * @return True if the placement sequence is valid
     *
     * @author Ujjwal-Structured
     * @author Group-implemented
     */
    public static boolean isPlacementStringValid(String placement) {
        setInitialstate();
        if (!isPlacementStringWellFormed(placement)) return false;
        for (int i = placement.length()-4; i >=0; i = i - 4) {
            char pieceType = placement.charAt(i);
            int col = Integer.parseInt(String.valueOf(placement.charAt(i + 1)));
            int row = Integer.parseInt(String.valueOf(placement.charAt(i + 2)));
            int orientation = Integer.parseInt(String.valueOf(placement.charAt(i + 3)));
            for (int yoff = getHeight(pieceType, orientation)-1; yoff >=0; yoff--) {
                for (int xoff = getLength(pieceType, orientation)-1; xoff >=0; xoff--) {
                    if (row + yoff > 4 || col + xoff > 8) return false;
                    if (boardstate[row + yoff][col + xoff] != EMPTY && stateFromOffset(pieceType, xoff, yoff, orientation) != EMPTY) return false;
                    if (boardstate[row + yoff][col + xoff]!=EMPTY && stateFromOffset(pieceType, xoff, yoff, orientation)==EMPTY) continue;
                    boardstate[row + yoff][col + xoff] = stateFromOffset(pieceType, xoff, yoff, orientation);
                }
            }
        }
        // FIXME Task 5: determine whether a placement string is valid
        return true;
    }

    /**
     * Given a string describing a placement of pieces and a string describing
     * a challenge, return a set of all possible next viable piece placements
     * which cover a specific board location.
     * <p>
     * For a piece placement to be viable
     * - it must be valid
     * - it must be consistent with the challenge
     *
     * @param placement A viable placement string
     * @param challenge The game's challenge is represented as a 9-character string
     *                  which represents the color of the 3*3 central board area
     *                  squares indexed as follows:
     *                  [0] [1] [2]
     *                  [3] [4] [5]
     *                  [6] [7] [8]
     *                  each character may be any of
     *                  - 'R' = RED square
     *                  - 'B' = Blue square
     *                  - 'G' = Green square
     *                  - 'W' = White square
     * @param col       The location's column.
     * @param row       The location's row.
     * @return A set of viable piece placements, or null if there are none.
     * @auther Ujjwal-All:
     */
    public static Set<String> getViablePiecePlacements(String placement, String challenge, int col, int row) {
        // FIXME Task 6: determine the set of all viable piece placements given existing placements and a challenge
        Set<String> rtn = new HashSet<>();
        setInitialstate();
        if (placement!="" && !isPlacementStringValid(placement)) return rtn; //test and make placement
        if (boardstate[row][col] != EMPTY) return rtn; // if position itself is not empty
        if (!isPlacenetMatchChallenge(challenge)) return rtn;
        for (char pieceType='a'; pieceType<'k'; pieceType++){
            if (placement.indexOf(pieceType)==-1) {
                for (int orientation=0; orientation<4; orientation++){
                    int leftestPotentialCol = col-getLength(pieceType, orientation)+1;
                    int highestPotentialRow = row-getHeight(pieceType, orientation)+1;
                    if (leftestPotentialCol<0) leftestPotentialCol=0;
                    if (highestPotentialRow<0) highestPotentialRow=0;
                    for (int pieceY=highestPotentialRow; pieceY<=row; pieceY++){
                        for (int pieceX = leftestPotentialCol; pieceX<=col; pieceX++){
                            String thisPiecePlacement = pieceType+String.valueOf(pieceX)+pieceY+orientation;
                            String thisPlacement = placement+thisPiecePlacement;
                            if (!isPlacementStringValid(thisPlacement) || !isPlacenetMatchChallenge(challenge) || boardstate[row][col]==EMPTY) {
                                isPlacementStringValid(placement);
                                continue;
                            }
                            isPlacementStringValid(placement);
                            rtn.add(thisPiecePlacement);
                        }
                    }
                }
            }
        }
        if (rtn.isEmpty()) rtn=null;
        return rtn;
    }


    public static boolean isPlacenetMatchChallenge(String challenge){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                challengestate[i][j]=INITIALSTATE[i][j];
            }
        }
        for (int m = 0; m < 9; m++) {
            States thisChallengePieceState = EMPTY;
            if (challenge.charAt(m)=='R') thisChallengePieceState=RED;
            if (challenge.charAt(m)=='G') thisChallengePieceState=GREEN;
            if (challenge.charAt(m)=='B') thisChallengePieceState=BLUE;
            if (challenge.charAt(m)=='W') thisChallengePieceState=WHITE;
            challengestate[1 + (int) Math.floor(m / 3)][3 + m % 3]=thisChallengePieceState;
        }
        for (int y=1; y<4; y++){
            for (int x=3; x<6; x++){
                if (boardstate[y][x]!=EMPTY && boardstate[y][x]!=challengestate[y][x]) return false;
            }
        }
        return true;
    }

    /**
     * Return the canonical encoding of the solution to a particular challenge.
     * <p>
     * A given challenge can only solved with a single placement of pieces.
     * <p>
     * Since some piece placements can be described two ways (due to symmetry),
     * you need to use a canonical encoding of the placement, which means you
     * must:
     * - Order the placement sequence by piece IDs
     * - If a piece exhibits rotational symmetry, only return the lowest
     * orientation value (0 or 1)
     *
     * @param challenge A challenge string.
     * @return A placement string describing a canonical encoding of the solution to
     * the challenge.
     * 0   1   2
     * 3   4   5
     * 6   7   8
     * @author group-not done yet
     */
    public static String getSolution(String challenge) {
        Set<String> initialPlacement = null;
        String initialPlacements=null;
        return addNextPiecePlacement(initialPlacements, challenge);
        //String[] output= addNextPiecePlacement(initialPlacement, challenge).toArray(new String[0]);
        //return output[0];
        // FIXME Task 9: determine the solution to the game, given a particular challenge
    }

    public static String addNextPiecePlacement(String initialPlacementSet, String challenge) {
        Set<String> output;
        int nextEmptyX = -1;
        int nextEmptyY = -1;
        if (initialPlacementSet == null) {
            output = getViablePiecePlacements("", challenge, 4, 2);
            //System.out.println(output);
            String[] initialPiecePlacementArray = output.toArray(new String[0]);
            for (String initialPiecePlacement : initialPiecePlacementArray) {
                //System.out.println(initialPiecePlacement);
                if (!isSymmetryExcluded(initialPiecePlacement)) continue;
                initialPlacementSet = initialPiecePlacement;
                makePlacement(initialPlacementSet);
                for (int i=1; i<4; i++) {
                    for (int j = 3; j < 6; j++) {
                        if (boardstate[i][j]==EMPTY){
                            if (getViablePiecePlacements(initialPiecePlacement, challenge, j, i)==null) continue;
                            String outputReal = addNextPiecePlacement(initialPlacementSet, challenge);
                            if (outputReal==null) continue;
                            return outputReal;
                        }
                    }
                }
            }
        }
        makePlacement(initialPlacementSet);
        boolean test=true;
        for (int i=1; i<4; i++) {
            for (int j = 3; j < 6; j++) {
                if (boardstate[i][j]==EMPTY){
                    nextEmptyX = j;
                    nextEmptyY = i;
                }
            }
        }
        if (nextEmptyX==-1) {
            nextEmptyX = getNextEmptyPosition()[0];
            nextEmptyY = getNextEmptyPosition()[1];
        }
        Set<String> viablePiecePlacement = getViablePiecePlacements(initialPlacementSet, challenge, nextEmptyX, nextEmptyY);
        //System.out.println("kk");
        if (viablePiecePlacement==null) return null;
        String[] viablePiecePlacementArray = viablePiecePlacement.toArray(new String[0]);
        for (String nextPiecePlacement : viablePiecePlacementArray) {
            if (!isSymmetryExcluded(nextPiecePlacement)) continue;
            String thisPlacement = initialPlacementSet + nextPiecePlacement;
            if (thisPlacement.length()==40) return thisPlacement;
            makePlacement(thisPlacement);
            nextEmptyX = getNextEmptyPosition()[0];
            nextEmptyY = getNextEmptyPosition()[1];
            if (getViablePiecePlacements(thisPlacement, challenge,nextEmptyX, nextEmptyY)==null) continue;
            String outputReal = addNextPiecePlacement(thisPlacement, challenge);
            //System.out.println(thisPlacement);
            if (outputReal==null) continue;
            return outputReal;
        }
        //System.out.println("mm");
        return null;
    }

    public static boolean isSymmetryExcluded(String placement){
        if (placement.indexOf('f') != -1) {
            char f = placement.charAt(placement.indexOf('f') + 3);
            if (f == '2' || f == '3')
                return false;
        }

        if (placement.indexOf('g') != -1) {
            char g = placement.charAt(placement.indexOf('g') + 3);
            if (g == '2' || g == '3')
                return false;
        }
        return true;


    }

    public static int[] getNextEmptyPosition(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                if (boardstate[i][j]==EMPTY) {
                    int[] output = new int[] {j, i};
                    return output;
                }
            }
        }
        return null;
    }

    public static void makePlacement(String placement) {
        setInitialstate();
        for (int i = placement.length() - 4; i >= 0; i = i - 4) {
            char pieceType = placement.charAt(i);
            int col = Integer.parseInt(String.valueOf(placement.charAt(i + 1)));
            int row = Integer.parseInt(String.valueOf(placement.charAt(i + 2)));
            int orientation = Integer.parseInt(String.valueOf(placement.charAt(i + 3)));
            for (int yoff = getHeight(pieceType, orientation) - 1; yoff >= 0; yoff--) {
                for (int xoff = getLength(pieceType, orientation) - 1; xoff >= 0; xoff--) {
                    if (boardstate[row + yoff][col + xoff]!=EMPTY) continue;
                    boardstate[row + yoff][col + xoff] = stateFromOffset(pieceType, xoff, yoff, orientation);
                }
            }
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        String[] challengeGroup = {"RRRBWBBRB", "RWWRRRWWW", "BGGWGGRWB", "WRRWRRGWW", "GWRGWWGGG", "GRWGRWWWW",
                "RGGRGGRRB", "GGGRGRBBB", "RGGGGRBGG", "BBBWRWGGG",};
        for (String i: challengeGroup){
            startTime = System.currentTimeMillis();
            getSolution(i);
            endTime = System.currentTimeMillis();
            System.out.println("运行时间:" + (endTime - startTime) + "ms");
        }
    }
}
