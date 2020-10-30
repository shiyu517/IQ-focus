package comp1110.ass2;

import comp1110.ass2.gui.Board;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class OurTests {
    @Test
    public void SquareToStringTest() {
        Square sq = new Square(3,4, States.RED);
        assertTrue(sq.toString().equals("34R"));
    }
    Square s1 = new Square(0,0, States.WHITE);
    Square s2 = new Square(1,0, States.WHITE);
    Square s3 = new Square(2,0, States.WHITE);
    Square[] squares = {s1,s2,s3};



    @Test
    public void placementWellFormedTest(){
        assertTrue("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("a000"));
        assertTrue("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("b233"));
        assertTrue("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("f512"));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed(""));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("a0"));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("a01"));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("ahuy"));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("sygboj"));
        assertFalse("isPiecePlacementWellFormed Function not valid", FocusGame.isPiecePlacementWellFormed("a000000"));
        System.out.println("'isPiecePlacementWellFormed' valid");
    }

}
