package comp1110.ass2;

public enum States {
    EMPTY, RED, GREEN, BLUE, WHITE;

    char toChar(){
        return toString().charAt(0);
    }
}
