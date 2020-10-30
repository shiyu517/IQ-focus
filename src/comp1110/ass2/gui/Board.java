package comp1110.ass2.gui;

import comp1110.ass2.FocusGame;
import comp1110.ass2.States;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static comp1110.ass2.FocusGame.*;
import static comp1110.ass2.gui.Viewer.breakString;

/**
 * This class is the main board of the game
 * <p>
 * The game is based directly on Smart Games' IQ-Focus game
 * (https://www.smartgames.eu/uk/one-player-games/iq-focus)
 * @author Shiyu
 */
public class Board extends Application {
    private static final int BOARD_WIDTH = 270;
    private static final int SQUARE_SIZE = 40;
    private static final int UPPER_MARGIN = 30;
    private static final int BOARD_UPPER_MARGIN = 50;
    private static final int BOARD_LEFT_MARGIN = 230;
    private static final int VIEWER_WIDTH = 900;
    private static final int VIEWER_HEIGHT = 500;
    private static final int BOARD_MARGINX = 30;
    private static final int BOARD_MARGINY = 52;
    private static final int INITIAL_PIECE_MARGIN = 10;
    private static final int SECOND_PIECE_MARGIN = 700;
    private static final int THIRD_PIECE_MARGIN = 300;
    private static final int ROTATION_THRESHOLD = 50;
    private static final String URI_BASE = "assets/";
    private static final int LEFT_PLAY_SPACE = BOARD_LEFT_MARGIN + BOARD_MARGINX;
    private static final int UPPER_PLAY_SPACE = BOARD_UPPER_MARGIN + BOARD_MARGINY;

    public String solution;
    FocusGame focusGame;
    private final Slider difficulty = new Slider();

    long lastRotationTime = System.currentTimeMillis();
    int boardMatrixX[] = new int[43];
    int boardMatrixY[] = new int[43];
    String placement = "";
    String[] challengeGroup = {"RRRBWBBRB", "RWWRRRWWW", "BGGWGGRWB", "WRRWRRGWW", "GWRGWWGGG", "GRWGRWWWW",
            "RGGRGGRRB", "GGGRGRBBB", "RGGGGRBGG", "BBBWRWGGG",};

    Random random = new Random();
    int index = random.nextInt(10);

    public String getChallengeString=challengeGroup[4];
    String challengeString = getChallengeString.toLowerCase();

    Image newImage = new Image(Board.class.getResource(URI_BASE + "board.png").toString());

    private final Group root = new Group();
    private final Group pieces = new Group();
    private final Group board = new Group();
    private final Group challenge = new Group();
    private final Group controls = new Group();
    private final Text completionText = new Text("Well done!");

    class DraggablePiece extends ImageView {
        int pieceID;
        char piece;
        double mouseX, mouseY;      // the last known mouse positions (used when dragging)
        int orientation;    // 0=North... 3=West

        Image[] images = new Image[4];

        private void snapToHome() {
            setLayoutX(getInitialPiecePosition(pieceID)[0]);
            setLayoutY(getInitialPiecePosition(pieceID)[1]);
            setFitHeight(2 * SQUARE_SIZE);
            setFitWidth(SQUARE_SIZE);
            setImage(images[0]);
            orientation = 0;
        }

        void setImages() {
            while (orientation < 4) {
                images[orientation] = new Image(Board.class.getResource(URI_BASE + piece + "-" + orientation + ".png").toString());
                orientation++;
            }
            orientation = 0;
        }

        int[] getInitialPiecePosition(int pieceID) {
            int output[] = new int[2];
            if (pieceID < 4) {
                output[0] = (INITIAL_PIECE_MARGIN);
                output[1] = (UPPER_MARGIN + pieceID * SQUARE_SIZE * 3);
            } else if (pieceID < 8) {
                output[0] = (SECOND_PIECE_MARGIN);
                output[1] = (UPPER_MARGIN + (pieceID - 4) * SQUARE_SIZE * 3);
            } else {
                output[0] = (THIRD_PIECE_MARGIN + (pieceID - 8) * SQUARE_SIZE * 4);
                output[1] = (UPPER_MARGIN + 300);
            }
            return output;
        }

        /**
         * create a piece that can be drag
         *
         * @param piece the type of piece
         */
        DraggablePiece(char piece) {
            this.pieceID = piece - 'a';
            this.piece = piece;

            setImages();
            setImage(images[0]);
            setPreserveRatio(true);
            setFitHeight(setFit(piece));
            setFitWidth(getFitHeight() / images[0].getHeight() * images[0].getWidth());
            setLayoutX(getInitialPiecePosition(pieceID)[0]);
            setLayoutY(getInitialPiecePosition(pieceID)[1]);

            /* event handlers */
            setOnScroll(event -> {            // scroll to change orientation
                if (System.currentTimeMillis() - lastRotationTime > ROTATION_THRESHOLD) {
                    lastRotationTime = System.currentTimeMillis();
                    orientation = (orientation + 1) % 4;
                    setImage(images[orientation]);
                    if (orientation == 1 || orientation == 3) {
                        setFitWidth(setFit(piece));
                        setFitHeight(getFitWidth() / images[0].getHeight() * images[0].getWidth());
                    } else {
                        setFitHeight(setFit(piece));
                        setFitWidth(getFitHeight() / images[0].getHeight() * images[0].getWidth());
                    }
                }
            });

            setOnMousePressed(event -> {      // mouse press indicates begin of drag
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            });
            setOnMouseDragged(event -> {      // mouse is being dragged
                // hideCompletion();
                toFront();
                double movementX = event.getSceneX() - mouseX;
                double movementY = event.getSceneY() - mouseY;
                setLayoutX(getLayoutX() + movementX);
                setLayoutY(getLayoutY() + movementY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                event.consume();
            });
            setOnMouseReleased(event -> {     // drag is complete
                int x = (int) Math.round((getLayoutX() - LEFT_PLAY_SPACE) / SQUARE_SIZE);
                int y = (int) Math.round((getLayoutY() - UPPER_PLAY_SPACE) / SQUARE_SIZE);
                if (piece=='f'||piece=='g'){
                    if (orientation==3) orientation=1;
                    if (orientation==2) orientation=0;
                }
                String singlePlacement = String.valueOf(piece) + x + y + orientation;
                String newPlacement = placement;
                int getIndex = placement.indexOf(piece);
                if (getIndex == -1) {
                    newPlacement = newPlacement + singlePlacement;
                } else {
                    if (getIndex != placement.length() - 5) {
                        newPlacement = placement.substring(0, getIndex) + placement.substring(getIndex + 4) + singlePlacement;
                    } else {
                        newPlacement = placement.substring(0, getIndex) + singlePlacement;
                    }
                }
                if (!isPlacementStringValid(newPlacement) || !isPlacenetMatchChallenge(getChallengeString.toUpperCase())) {
                    setLayoutX(getInitialPiecePosition(pieceID)[0]);
                    setLayoutY(getInitialPiecePosition(pieceID)[1]);
                    removePiecePlacementString(singlePlacement);
                } else {
                    findNearestPosition();
                    placement = newPlacement;
                    checkCompletion();
                    makePlacement(placement);
                }
            });
        }

        /**
         * when an invalid singlePlacement is created, remove it from board
         *
         * @param singlePlacement a string describe a single piece placement
         */
        void removePiecePlacementString(String singlePlacement) {
            int getIndex = placement.indexOf(piece);
            if (getIndex != -1 && placement != "") {
                if (placement.length() == 4) {
                    placement = "";
                } else if (getIndex != placement.length() - 5) {
                    placement = placement.substring(0, getIndex) + placement.substring(getIndex + 4);
                } else if (getIndex == placement.length() - 5) {
                    placement = placement.substring(0, getIndex);
                }
            }
        }

        /**
         * when mouse release a piece, move piece to the nearest position
         */
        public void findNearestPosition() {
            double nearestDistance = 10000;
            int nearestPositionID = -1;
            setBoardMatrix();
            double getX = getLayoutX();
            double getY = getLayoutY();
            for (int i = 0; i < 43; i++) {
                double distance = Math.sqrt((getX - boardMatrixX[i]) * (getX - boardMatrixX[i]) + (getY - boardMatrixY[i]) * (getY - boardMatrixY[i]));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPositionID = i;
                }
            }
            setLayoutX(boardMatrixX[nearestPositionID]);
            setLayoutY(boardMatrixY[nearestPositionID]);
        }
    }

    /**
     * set the index of the board
     * 00--01--02--03--04--05--06--07--08
     * |   |   |   |   |   |   |   |   |
     * 09--10--11--12--13--14--15--16--17
     * |   |   |   |   |   |   |   |   |
     * 18--19--20--21--22--23--24--25--26
     * |   |   |   |   |   |   |   |   |
     * 27--28--29--30--31--32--33--34--35
     * |   |   |   |   |   |   |
     * 36--37--38--39--40--41--42
     **/
    void setBoardMatrix() {
        for (int i = 0; i < 43; i++) {
            if (i < 36) {
                boardMatrixX[i] = LEFT_PLAY_SPACE + SQUARE_SIZE * (i % 9);
                boardMatrixY[i] = (int) (UPPER_PLAY_SPACE + SQUARE_SIZE * Math.floor(i / 9));
            } else {
                boardMatrixX[i] = LEFT_PLAY_SPACE + SQUARE_SIZE * (i - 35);
                boardMatrixY[i] = UPPER_PLAY_SPACE + SQUARE_SIZE * 4;
            }
        }
    }

    /**
     * Set the size of piece given pieceID
     *
     * @param piece a character describe the type of piece
     * @return the size of piece
     */
    public double setFit(char piece) {
        if (piece == 'h') {
            return SQUARE_SIZE * 3;
        } else {
            if (piece == 'f') {
                return SQUARE_SIZE;
            } else {
                return SQUARE_SIZE * 2;
            }
        }
    }

    /**
     * create piece on the window
     */
    public void makePiece() {
        pieces.getChildren().clear();
        for (char m = 'a'; m <= 'j'; m++) {
            pieces.getChildren().add(new DraggablePiece(m));
        }
    }

    /**
     * creat board on the window
     */
    public void makeBoard() {
        ImageView newImageView = new ImageView(newImage);
        newImageView.setFitHeight(BOARD_WIDTH);
        newImageView.setPreserveRatio(true);
        newImageView.setLayoutX(BOARD_LEFT_MARGIN);
        newImageView.setLayoutY(BOARD_UPPER_MARGIN);
        board.getChildren().add(newImageView);
    }

    // FIXME Task 7: Implement a basic playable Focus Game in JavaFX that only allows pieces to be placed in valid places

    /**
     * create challenge piece on board and make sure challenge works
     */
    public void makeChallenge(String getChallengeString) {
        challenge.getChildren().clear();
        root.getChildren().remove(challenge);
        challengeString=getChallengeString.toLowerCase();
        ImageView[] colorImage = new ImageView[9];
        for (int i = 0; i < 9; i++) {
            Image newColor = new Image(Board.class.getResource(URI_BASE + "sq-" + challengeString.charAt(i) + ".png").toString());
            colorImage[i] = new ImageView(newColor);
            colorImage[i].setFitHeight(SQUARE_SIZE);
            colorImage[i].setFitWidth(SQUARE_SIZE);
            colorImage[i].setLayoutX(LEFT_PLAY_SPACE + SQUARE_SIZE * 3 + i % 3 * SQUARE_SIZE);
            colorImage[i].setLayoutY(UPPER_PLAY_SPACE + SQUARE_SIZE * 1 + Math.floor(i / 3) * SQUARE_SIZE);
            colorImage[i].setOpacity(0.5);
            challenge.getChildren().add(colorImage[i]);
            solution = getSolution(challengeString.toUpperCase());
        }
    }
    // FIXME Task 8: Implement challenges (you may use challenges and assets provided for you in comp1110.ass2.gui.assets: sq-b.png, sq-g.png, sq-r.png & sq-w.png)

    // FIXME Task 10: Implement hints
    /**
     * Set up event handlers for the main game
     * By press slash, a semi-transparent piece will occur
     * If the current placement already violate the solution
     * the give hint of moving one of the current piece to the suitable place
     * @param scene The Scene used by the game.
     */
    private void setUpHandlers(Scene scene) {
        setBoardMatrix();
        ImageView hintImageView = new ImageView();
        root.getChildren().add(hintImageView);
        makePlacement(placement);

        /* create handlers for key press and release events */
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SLASH) {
                char pieceType = 'z';
                int x = -1;
                int y = -1;
                int orientation = -1;
                if (placement.length() == 40) {
                } else if (placement == "") {
                    pieceType = solution.charAt(0);
                    x = Integer.valueOf(String.valueOf(solution.charAt(1)));
                    y = Integer.valueOf(String.valueOf(solution.charAt(2)));
                    orientation = Integer.valueOf(String.valueOf(solution.charAt(3)));
                } else {
                    for (int i = placement.length() - 4; i >= 0; i = i - 4) {
                        pieceType = placement.charAt(i);
                        int pieceTypeIndex = solution.indexOf(pieceType);
                        if (solution.charAt(pieceTypeIndex) == placement.charAt(i) &&
                                solution.charAt(pieceTypeIndex + 1) == placement.charAt(i + 1) &&
                                solution.charAt(pieceTypeIndex + 2) == placement.charAt(i + 2) &&
                                solution.charAt(pieceTypeIndex + 3) == placement.charAt(i + 3)) {
                            pieceType = 'z';
                            continue;
                        }
                        x = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 1)));
                        y = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 2)));
                        orientation = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 3)));
                        break;
                    }
                }
                if (pieceType == 'z') {
                    for (char hintPiece = 'a'; hintPiece < 'k'; hintPiece++) {
                        if (placement.indexOf(hintPiece) == -1) {
                            pieceType = hintPiece;
                            int pieceTypeIndex = solution.indexOf(hintPiece);
                            x = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 1)));
                            y = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 2)));
                            orientation = Integer.valueOf(String.valueOf(solution.charAt(pieceTypeIndex + 3)));
                        }
                    }
                }
                if (pieceType!='z') {
                    Image hintImage = new Image(Board.class.getResource(URI_BASE + pieceType + "-" + orientation + ".png").toString());
                    hintImageView.setImage(hintImage);
                    if (orientation == 1 || orientation == 3) {
                        hintImageView.setFitWidth(setFit(pieceType));
                        hintImageView.setFitHeight(hintImageView.getFitWidth() / hintImage.getWidth() * hintImage.getHeight());
                    } else {
                        hintImageView.setFitHeight(setFit(pieceType));
                        hintImageView.setFitWidth(hintImageView.getFitHeight() / hintImage.getHeight() * hintImage.getWidth());
                    }
                    hintImageView.setOpacity(0.5);
                    int getPositionIndex = y * 9 + x;
                    hintImageView.setLayoutX(boardMatrixX[getPositionIndex]);
                    hintImageView.setLayoutY(boardMatrixY[getPositionIndex]);
                    hintImageView.toFront();
                }
                event.consume();
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SLASH) {
                hintImageView.setOpacity(0);
                hintImageView.setImage(null);
                event.consume();
            }
        });
    }

    // FIXME Task 11: Generate interesting challenges (each challenge may have just one solution)@Override

    private void makeControls() {
        Button button = new Button("Restart");
        button.setLayoutX(240);
        button.setLayoutY(10);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                newGame();
            }
        });
        controls.getChildren().add(button);

        difficulty.setMin(1);
        difficulty.setMax(3);
        difficulty.setValue(0);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(1);
        difficulty.setSnapToTicks(true);

        difficulty.setLayoutX(450);
        difficulty.setLayoutY(10);
        controls.getChildren().add(difficulty);

        final Label difficultyCaption = new Label("Difficulty:");
        difficultyCaption.setTextFill(Color.GREY);
        difficultyCaption.setLayoutX(350);
        difficultyCaption.setLayoutY(10);
        controls.getChildren().add(difficultyCaption);
    }

    /**
     * Check game completion and update status
     */
    private void checkCompletion() {
        Set<String> placementSet=new HashSet<>();
        Set<String> solutionSet=new HashSet<>();
        if (placement.length()==40){
            String[] placements = breakString(placement);
            String[] solutions = breakString(solution);
            System.out.println(placements[0]);
            System.out.println(solutions[0]);
            for (int i=0; i<10; i++){
                System.out.println(i);;
                placementSet.add(placements[i]);
                solutionSet.add(solutions[i]);
            }
            if (placementSet.equals(solutionSet)) showCompletion();
        }
    }

    /**
     * Create the message to be displayed when the player completes the puzzle.
     */
    private void makeCompletion() {
        completionText.setFill(Color.BLACK);
        completionText.setCache(true);
        completionText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        completionText.setLayoutX(20);
        completionText.setLayoutY(375);
        completionText.setTextAlignment(TextAlignment.CENTER);
        root.getChildren().add(completionText);
        completionText.toFront();
    }

    /**
     * Hide the completion message
     */
    private void hideCompletion() {
        completionText.toBack();
        completionText.setOpacity(0);
    }

    /**
     * Show the completion message
     */
    private void showCompletion() {
        completionText.toFront();
        completionText.setOpacity(1);
    }

    /**
     * Start a new game, resetting everything as necessary
     */
    private void newGame() {
        try {
            hideCompletion();
            focusGame = new FocusGame((int) difficulty.getValue()-1);
            System.out.println(focusGame.getObjective());
            getChallengeString= focusGame.getObjective();
            System.out.println(getChallengeString);
            makePiece();
            challenge.getChildren().clear();
            makeChallenge(getChallengeString);
            root.getChildren().add(challenge);
            pieces.toFront();
        } catch (IllegalArgumentException e) {
            System.err.println("Uh oh. " + e);
            e.printStackTrace();
            Platform.exit();
        }
    }


    public void start(Stage primaryStage) {
        primaryStage.setTitle("FocusGame Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        makePiece();
        makeBoard();
        makeChallenge(challengeString);
        setUpHandlers(scene);
        makeControls();
        makeCompletion();
        root.getChildren().add(board);
        root.getChildren().add(pieces);
        root.getChildren().add(challenge);
        root.getChildren().add(controls);
        pieces.toFront();
        hideCompletion();



        primaryStage.setScene(scene);
        primaryStage.show();


    }
}