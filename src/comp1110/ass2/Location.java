package comp1110.ass2;

public class Location {
    private int x;
    private int y;
    static final int out = -1;

    public  Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Location(){
        this.x = out;
        this.y = out;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
