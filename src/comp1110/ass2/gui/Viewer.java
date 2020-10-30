package comp1110.ass2.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * A very simple viewer for piece placements in the IQ-Focus game.
 * <p>
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various piece
 * placements.
 * @author Shiyu
 */
public class Viewer extends Application {

    /* board layout */
    private static final int BOARD_WIDTH = 270;
    private static final int SQUARE_SIZE = 40;
    private static final int UPPER_MARGIN = 30;
    private static final int BOARD_UPPER_MARGIN=50;
    private static final int BOARD_LEFT_MARGIN = 230;
    private static final int VIEWER_WIDTH = 900;
    private static final int VIEWER_HEIGHT = 500;
    private static final int BOARD_MARGINX = 30;
    private static final int BOARD_MARGINY = 52;
    private static final int INITIAL_PIECE_MARGIN = 10;
    private static final int SECOND_PIECE_MARGIN = 700;
    private static final int THIRD_PIECE_MARGIN = 300;

    private static final String URI_BASE = "assets/";

    private final Group root = new Group();
    private final Group controls = new Group();
    private final Group pieces = new Group();
    private final Group board = new Group();
    private TextField textField;
    public ImageView pieceImage[] = new ImageView[10];

    /**
     * show piece on the board
     */
    public void initiatePiece(){
        for (char i='a'; i<'k'; i++){
            int index = i-'a';
            double x;
            double y=0;
            Image newImage = new Image(Board.class.getResource(URI_BASE + i + "-0.png").toString());
            ImageView newImageView = new ImageView(newImage);
            pieceImage[index]= newImageView;
            pieceImage[index].setFitHeight(setFit(i));
            pieceImage[index].setPreserveRatio(true);
            if (index<4) {
                pieceImage[index].setLayoutX(INITIAL_PIECE_MARGIN);
                pieceImage[index].setLayoutY(UPPER_MARGIN + index * SQUARE_SIZE * 3);
            } else if (index<8){
                pieceImage[index].setLayoutX(SECOND_PIECE_MARGIN);
                pieceImage[index].setLayoutY(UPPER_MARGIN +(index-4) * SQUARE_SIZE * 3);
            } else {
                pieceImage[index].setLayoutX(THIRD_PIECE_MARGIN+(index-8)*SQUARE_SIZE*4);
                pieceImage[index].setLayoutY(UPPER_MARGIN + 300);
            }
            pieces.getChildren().add(pieceImage[index]);
        }
    }

    /**
     * Set the size of piece given pieceID
     *
     * @param pieceID a character describe the type of piece
     * @return the size of piece
     */
    public double setFit(char pieceID){
        if (pieceID == 'h') {
            return SQUARE_SIZE * 3;
        } else {
            if (pieceID == 'f') {
                return SQUARE_SIZE;
            } else {
                return SQUARE_SIZE * 2;
            }
        }
    }

    /**
     * break placement into four by four piece placement
     *
     * @param placement A string describing a placement
     * @return broken string
     */
    public static String[] breakString(String placement) {
        placement = placement + "n";
        String newString[] = new String[placement.length()/4];
        for (int i=newString.length-1; i>=0; i--){
            newString[i]=placement.substring(i*4,(i+1)*4);
        }
        return newString;
    }

    /**
     * Draw a placement in the window, removing any previously drawn one
     *
     * @param placement A valid placement string
     */
    void makePlacement(String placement) {
        String[] brokenString = breakString(placement);
        for (int i=brokenString.length-1; i>=0; i--){
            int getPieceID = brokenString[i].charAt(0)-'a';
            double layoutX = BOARD_MARGINX+BOARD_LEFT_MARGIN+SQUARE_SIZE*Integer.parseInt(String.valueOf(brokenString[i].charAt(1)));
            double layoutY = BOARD_MARGINY+BOARD_UPPER_MARGIN+SQUARE_SIZE*Integer.parseInt(String.valueOf(brokenString[i].charAt(2)));
            pieceImage[getPieceID].setLayoutX(layoutX);
            pieceImage[getPieceID].setLayoutY(layoutY);

            int orientation = Integer.parseInt(String.valueOf(brokenString[i].charAt(3)));
            pieceImage[getPieceID].setRotate(90*orientation);
            if (orientation==1||orientation==3) {
                switch (brokenString[i].charAt(0)) {
                    case 'a':
                    case 'd':
                    case 'e':
                    case 'g':
                        pieceImage[getPieceID].setLayoutX(pieceImage[getPieceID].getLayoutX()-SQUARE_SIZE/2);
                        pieceImage[getPieceID].setLayoutY(pieceImage[getPieceID].getLayoutY()+SQUARE_SIZE/2);
                        break;
                    case 'b':
                    case 'c':
                    case 'j':
                    case 'f':
                        pieceImage[getPieceID].setLayoutX(pieceImage[getPieceID].getLayoutX()-SQUARE_SIZE);
                        pieceImage[getPieceID].setLayoutY(pieceImage[getPieceID].getLayoutY()+SQUARE_SIZE);
                        break;
                    default:
                        break;
                }
            }
            pieceImage[getPieceID].toFront();
        }
        // FIXME Task 4: implement the simple placement viewer
    }

    /**
     * show board in the window
     */
    void makeBoard(){
        Image newImage = new Image(Board.class.getResource(URI_BASE + "board.png").toString());
        ImageView newImageView = new ImageView(newImage);
        newImageView.setFitHeight(BOARD_WIDTH);
        newImageView.setPreserveRatio(true);
        newImageView.setLayoutX(BOARD_LEFT_MARGIN);
        newImageView.setLayoutY(BOARD_UPPER_MARGIN);
        board.getChildren().add(newImageView);
    }

    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label label1 = new Label("Placement:");
        textField = new TextField();
        textField.setPrefWidth(300);
        Button button = new Button("Refresh");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                makePlacement(textField.getText());
                textField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button);
        hb.setSpacing(10);
        hb.setLayoutX(130);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("FocusGame Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        initiatePiece();
        makeControls();
        makeBoard();
        root.getChildren().add(board);
        root.getChildren().add(controls);
        root.getChildren().add(pieces);


        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
