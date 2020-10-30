package comp1110.ass2;

import static comp1110.ass2.States.*;

public class PieceType {

    public static void main(String[] args) {
        System.out.println(stateFromOffset('c', 1, 2, 1));
    }

    public static States stateFromOffset(char pieceType, int xoff, int yoff, int orientation) {
        int pieceID = pieceType-'a';
        switch (pieceType) {
            case 'a': case 'd': case 'e': case 'g':
                /* 0 1 2   3 0   5 4 3   2 5
                   3 4 5   4 1   2 1 0   1 4
                           5 2           0 3
                 */
                if (xoff < 0 || yoff < 0 || xoff > 2 || yoff > 2) return null;
                switch (orientation) {
                    case 0:
                        return statemap[pieceID][(yoff * 3) + xoff];
                    case 1:
                        return statemap[pieceID][(1 - xoff) * 3 + yoff];
                    case 2:
                        return statemap[pieceID][(1 - yoff) * 3 + (2 - xoff)];
                    case 3:
                        return statemap[pieceID][xoff * 3 + (2 - yoff)];
                    default:
                        return null;
                }
            case 'b': case 'c': case 'j':
                /*  0 1 2 3    4 0   7 6 5 4   3 7
                    4 5 6 7    5 1   3 2 1 0   2 6
                               6 2             1 5
                               7 3             0 4
                 */
                if (xoff < 0 || yoff < 0 || xoff > 3 || yoff > 3) return null;
                switch (orientation) {
                    case 0:
                        return statemap[pieceID][(yoff * 4) + xoff];
                    case 1:
                        return statemap[pieceID][(1 - xoff) * 4 + yoff];
                    case 2:
                        return statemap[pieceID][(1 - yoff) * 4 + (3 - xoff)];
                    case 3:
                        return statemap[pieceID][xoff * 4 + (3 - yoff)];
                    default:
                        return null;
                }
            case 'f':
                /* 0 1 2    0    2 1 0   2
                            1            1
                            2            0
                 */
                if (xoff < 0 || yoff < 0 || xoff > 2 || yoff > 2) return null;
                switch (orientation) {
                    case 0:
                        return statemap[pieceID][xoff];
                    case 1:
                        return statemap[pieceID][yoff];
                    case 2:
                        return statemap[pieceID][(2 - xoff)];
                    case 3:
                        return statemap[pieceID][(2 - yoff)];
                    default:
                        return null;
                }
            case 'h':
                /* 0 1 2    6 3 0   8 7 6   2 5 8
                   3 4 5    7 4 1   5 4 3   1 4 7
                   6 7 8    8 5 3   2 1 0   0 3 6
                 */
                if (xoff < 0 || yoff < 0 || xoff > 2 || yoff > 2) return null;
                switch (orientation) {
                    case 0:
                        return statemap[pieceID][(yoff * 3) + xoff];
                    case 1:
                        return statemap[pieceID][(2 - xoff) * 3 + yoff];
                    case 2:
                        return statemap[pieceID][(2 - yoff) * 3 + (2 - xoff)];
                    case 3:
                        return statemap[pieceID][xoff * 3 + (2 - yoff)];
                    default:
                        return null;
                }
            case 'i':
                /*   0 1    2 0     3 2     1 3
                     2 3    3 1     1 0     0 2
                 */
                if (xoff < 0 || yoff < 0 || xoff > 1 || yoff > 1) return null;
                switch (orientation) {
                    case 0:
                        return statemap[pieceID][(yoff * 2) + xoff];
                    case 1:
                        return statemap[pieceID][(1 - xoff) * 2 + yoff];
                    case 2:
                        return statemap[pieceID][(1 - yoff) * 2 + (1 - xoff)];
                    case 3:
                        return statemap[pieceID][xoff * 2 + (1 - yoff)];
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public static int getLength(char pieceType, int orientation) {
        int pieceID = pieceType-'a';
        switch (pieceType) {
            case 'a': case 'b': case 'c': case 'd': case 'e': case 'g': case 'i': case 'j':
                switch (orientation) {
                    case 0: case 2:
                        return statemap[pieceID].length/2;
                    case 1: case 3:
                        return 2;
                    default:
                        return -1;
                }
            case 'f':
                switch (orientation) {
                    case 0: case 2:
                        return statemap[pieceID].length;
                    case 1: case 3:
                        return 1;
                    default:
                        return -1;
                }
            case 'h':
                switch (orientation) {
                    case 0: case 2:
                        return statemap[pieceID].length/3;
                    case 1: case 3:
                        return 3;
                    default:
                        return -1;
                }
            default:
                return -1;
        }
    }

    public static int getHeight(char pieceType, int orientation){
        return statemap[pieceType-'a'].length/getLength(pieceType,orientation);
    }


    public static States[][] statemap = { //A, D, E, G
            {GREEN, WHITE, RED,
                    EMPTY, RED, EMPTY},
            {EMPTY, BLUE, GREEN, GREEN,
                    WHITE, WHITE, EMPTY, EMPTY},
            {EMPTY, EMPTY, GREEN, EMPTY,
                    RED, RED, WHITE, BLUE},
            {RED, RED, RED,
                    EMPTY, EMPTY, BLUE},//D
            {BLUE, BLUE, BLUE,
                    RED, RED, EMPTY},//E
            {WHITE, WHITE, WHITE},
            {WHITE, BLUE, EMPTY,
                    EMPTY, BLUE, WHITE},//G
            {RED, GREEN, GREEN,
                    WHITE, EMPTY, EMPTY,
                    WHITE, EMPTY, EMPTY},
            {BLUE, BLUE,
                    EMPTY, WHITE},
            {GREEN, GREEN, WHITE, RED,
                    GREEN, EMPTY, EMPTY, EMPTY}
    };
}
