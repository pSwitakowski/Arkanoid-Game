package Controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.fxml.FXML;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.prefs.Preferences;


public class GameController implements Initializable {

    //GAME LOOP SETTINGS
    private AnimationTimer timer;
    private static long lastUpdate = 0;
    private static int index = 0;
    private static double[] frameRates = new double[100];
    private static String averageFPS;

    private boolean ingameSettingsActive = false;

    private boolean gameLoopStarted=false;
    private boolean levelStarted=false;
    private boolean goLeft;
    private boolean goRight;
    private final float platformMoveSpeed=7f;
    private double deltaX = 0.0;
    private double deltaY = -6.0;
    //MAX BOUNCE OFF ANGLE 75 DEGREES (5*180degrees/12)
    private final static double MAXBOUNCEANGLE = 5 * Math.PI / 12;
    private int livesLeft=3;
    private int score=0;

    private boolean beforeHittingPlatform=true;
    private boolean isGameOver=false;

    private int currentLevel;
    private boolean levelFinished=false;
    private boolean blocksFalling=true;
    double brickFallSpeed=2;




    /**
     * Function that returns value of the current level.
     * @return Current level value.
     */
    public int getCurrentLevel(){
        return this.currentLevel;
    }

    /**
     * Function that sets the current level to this given in the function parameter.
     * @param level Set game level.
     */
    public void setCurrentLevel(int level){
        this.currentLevel = level;
    }

    /**
     * List that contains all current level blocks.
     */
    private ArrayList<Rectangle> levelBlocks = new ArrayList<Rectangle>();


    @FXML
    private Rectangle gamePlatform;

    @FXML
    private Label levelLabel;

    @FXML
    private Pane gamePane;

    @FXML
    private Circle Ball;

    @FXML
    private Label pressSpaceLabel;

    @FXML
    private Label currentFPSLabel;

    @FXML
    private Label gameOverLabel;

    @FXML
    private Label pressSpaceLabel2;

    @FXML
    private Label congratulationsLabel;

    @FXML
    private Label startNextLevelLabel;

    @FXML
    private Label gameFinishedLabel;

    @FXML
    private Label goBackToMenuLabel;

    @FXML
    private ImageView heart1;

    @FXML
    private ImageView heart2;

    @FXML
    private ImageView heart3;

    @FXML
    private Label scoreLabel;




    /**
     * Function that handles resetting level. Used when initializing levels, going to another levels
     * and after restarting current level (that option is only available after losing a level).
     */
    private void resetLevel(){
        gamePlatform.setLayoutX(500);
        Ball.setLayoutX(600);
        Ball.setLayoutY(636);

        levelLabel.setVisible(true);
        pressSpaceLabel.setVisible(true);
        gameOverLabel.setVisible(false);
        pressSpaceLabel2.setVisible(false);
        gameLoopStarted=false;
        beforeHittingPlatform=true;
        isGameOver=false;
        levelFinished=false;
        levelStarted=false;

        clearFallingBlocks();

        deltaX = 0.0;
        deltaY = -6.0;

        if(timer!= null)
            timer.stop();
    }

    /**
     * Function that resets score to 0.
     */
    private void resetScore() {
        score=0;
        scoreLabel.setText("0");
    }

    /**
     * Function responsible for handling start of a level keyboard input and entering in-game options menu.
     * @param event Button click event.
     * @throws IOException File input/output exception.
     */
    @FXML
    void gameOnKeyPressed(KeyEvent event) throws IOException {
        switch (event.getCode()) {
            case SPACE:
                if(!gameLoopStarted && !isGameOver) {
                    gameLoopStarted = true;
                    levelStarted=true;
                    levelLabel.setVisible(false);
                    pressSpaceLabel.setVisible(false);
                    gameLoop();
                }
                break;
            case ESCAPE:
                toggleInGameSettings(event);
                break;
        }
    }


    /**
     * Function responsible for controlling the platform movement in game (1/2).
     * Controlling the platform is handled by left arrow and right arrow on a keyboard.
     * @param event Button click event.
     */
    @FXML
    void platformMovementOnKeyPressed(KeyEvent event) {
            switch (event.getCode()) {
                case LEFT:
                    goLeft = true;
                    break;
                case RIGHT:
                    goRight = true;
                    break;
            }
    }

    /**
     * Function responsible for controlling the platform movement in game (2/2).
     * On key release, the platform stops.
     * @param event Button click event.
     */
    @FXML
    void platformMovementOnKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:  goLeft  = false; break;
            case RIGHT: goRight  = false; break;
        }
    }

    /**
     * Main function responsible for setting up the game loop handling every frame of the timer. Handles platform movement, ball movement and
     * also every possible in-game scenario, such as losing a life, finishing the level, finishing the game.
     */
    private void gameLoop() {
    timer = new AnimationTimer() {

        final Bounds levelBounds = gamePane.getBoundsInLocal();
        final double ballSpeed = 2.5;
        double platformMiddleX;
        double ballCenterX;
        double relativeIntersectX;
        double normalizedRelativeIntersectionX;
        double bounceAngle;

        @Override
        public void handle(long now) {

            //FRAME RATE SETTINGS (Frame rate is set to around 150)
            if (lastUpdate > 0) {
                long nanosElapsed = now - lastUpdate;
                double frameRate = 500000000.0 / nanosElapsed;
                index %= frameRates.length;
                frameRates[index++] = frameRate;
            }
            lastUpdate = now;

            //SHOW FPS INFO
            averageFPS = Float.toString(getAverageFPS());
            currentFPSLabel.setText("FPS: " + averageFPS);


            //Platform Movement

            final boolean PlatformAtRightBorder = gamePlatform.getLayoutX() >= (levelBounds.getMaxX() - gamePlatform.getWidth());
            final boolean PlatformAtLeftBorder = gamePlatform.getLayoutX() <= (levelBounds.getMinX());

            //PLATFORM MOVEMENT
            handlePlatformMovement(PlatformAtRightBorder, PlatformAtLeftBorder);

            rotateBall();

            if (gameLoopStarted && beforeHittingPlatform) {
                Ball.setLayoutX(Ball.getLayoutX() + deltaX);
                Ball.setLayoutY(Ball.getLayoutY() + deltaY);
            }else{
                Ball.setLayoutX(Ball.getLayoutX() + deltaX * ballSpeed);
                Ball.setLayoutY(Ball.getLayoutY() + deltaY * ballSpeed);
            }

            final boolean BallAtRightBorder = Ball.getLayoutX() >= (levelBounds.getMaxX() - Ball.getRadius());
            final boolean BallAtLeftBorder = Ball.getLayoutX() <= (levelBounds.getMinX() + Ball.getRadius());
            final boolean BallAtBottomBorder = Ball.getLayoutY() >= (levelBounds.getMaxY() - Ball.getRadius());
            final boolean BallAtTopBorder = Ball.getLayoutY() <= (levelBounds.getMinY());
            final boolean BallAtPlatformBorder = Ball.getBoundsInParent().intersects(gamePlatform.getBoundsInParent());

            handleBallBounceOffBrick();

            handleBricksFall();

            if (BallAtRightBorder || BallAtLeftBorder) {
                SoundController.playWallBounceSound();
                deltaX = -deltaX;
            }
            if (BallAtTopBorder) {
                SoundController.playWallBounceSound();
                deltaY = -deltaY;
            }
            if (BallAtBottomBorder) {
                SoundController.playFailSound();

                if(livesLeft>1) {
                    decreaseLives();
                    resetLevel();
                }else{
                    handleGameOver();
                }
            }
            if (BallAtPlatformBorder) {
                handleBallBounceOffPlatform();
            }

            System.out.println("bricks left: " + levelBlocks.size());
        }

        /**
         * Function responsible for handling ball platform bounce.
         * Bounce off angle is calculated based on which part of the platform did the ball hit.
         */
        private void handleBallBounceOffPlatform() {

            SoundController.playPlatformBounceSound();

            if(beforeHittingPlatform) beforeHittingPlatform=false;

            platformMiddleX = gamePlatform.getLayoutX() + gamePlatform.getWidth() / 2;
            ballCenterX = Ball.getLayoutX();

            //BALL BOUNCE OFF PLATFORM PHYSICS
            relativeIntersectX = ballCenterX - platformMiddleX;
            normalizedRelativeIntersectionX = (relativeIntersectX / (gamePlatform.getWidth() / 2));
            bounceAngle = normalizedRelativeIntersectionX * MAXBOUNCEANGLE;

            deltaX = ballSpeed * (Math.sin(bounceAngle));
            deltaY = ballSpeed * (-Math.cos(bounceAngle));
        }

        /**
         * Function responsible for handling ball bounce off bricks. The ball movement is based on which part of a brick did the ball hit.
         * Works similar to function that controls ball bounce off level boundaries.
         * On hit, the brick that is hit disappears, is removed from the bricks list and the score is increased.
         */
        private void handleBallBounceOffBrick(){

            for(Rectangle rect : levelBlocks){
                if(!(rect.getOpacity()==0.4)) {
                    if (Ball.getBoundsInParent().intersects(rect.getBoundsInParent())) {
                        SoundController.playWallBounceSound();

                        final boolean BallAtRightBorder = (Ball.getLayoutX() >= (rect.getX() + rect.getWidth()));
                        final boolean BallAtLeftBorder = Ball.getLayoutX() <= (rect.getX());
                        final boolean BallAtBottomBorder = Ball.getLayoutY() >= (rect.getY() + rect.getHeight());
                        final boolean BallAtTopBorder = Ball.getLayoutY() <= (rect.getY());

                        if (BallAtRightBorder || BallAtLeftBorder) deltaX = -deltaX;
                        if (BallAtTopBorder || BallAtBottomBorder) deltaY = -deltaY;

                        score += 10;
                        scoreLabel.setText(String.valueOf(score));


                        if (blocksFalling) {  //brick falling mode
                            rect.setFill(Color.GREY);
                            rect.setOpacity(0.4);
                            brickFall(rect);
                        } else {  //only destroy brick
                            gamePane.getChildren().remove(rect);
                            levelBlocks.remove(rect);
                        }

                        checkLevelFinished();

                        break;
                    }
                }
            }
        }


    };
    timer.start();
}

    /**
     * Function responsible for handling end of the game event (when player has no lives left). Shows proper info and waits for
     * pressing R key, which will reset the current level.
     */
    private void handleGameOver() {
        SoundController.playGameOverSound();
        decreaseLives();
        toggleGameLoop();
        isGameOver=true;
        pressSpaceLabel2.setVisible(true);
        gameOverLabel.setVisible(true);

        Ball.getScene().setOnKeyPressed(e->{
            if(isGameOver) {
                if (e.getCode() == KeyCode.R) {
                    pressSpaceLabel2.setVisible(false);
                    gameOverLabel.setVisible(false);
                    clearLevelBlocks();
                    resetLevel();
                    generateLevel(currentLevel);
                }
            }
        });
    }

    /**
     * Function responsible for handling event of winning current level. Depending on if the currently finished level was the last one,
     * proper action is executed. If currently finished level was not the last one, game shows proper info and waits for confirmation from player
     * to load the next level. If it was the last level, end of the game information appears and the game waits for pressing button Space, which
     * will take the player back to main menu.
     */
    private void checkLevelFinished() {
        if(levelBlocks.isEmpty()){
            levelFinished=true;
            if(gameLoopStarted){
                gameLoopStarted =false;
                timer.stop();
            }

            if(levelFinished) {
                SoundController.playLevelFinishedSound();

                        if (currentLevel <= 2) {

                            congratulationsLabel.setVisible(true);
                            startNextLevelLabel.setVisible(true);

                            EventHandler<KeyEvent> nextLevelHandler = new EventHandler<>() {
                                @Override
                                public void handle(KeyEvent e) {
                                    if (e.getCode() == KeyCode.SPACE) {
                                        congratulationsLabel.setVisible(false);
                                        startNextLevelLabel.setVisible(false);
                                        startNextLevelLabel.setVisible(false);
                                        currentLevel++;
                                        resetLevel();
                                        generateLevel(getCurrentLevel());
                                        levelFinished = false;
                                        Ball.getScene().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                                    }
                                }
                            };
                            Ball.getScene().addEventHandler(KeyEvent.KEY_PRESSED,nextLevelHandler);

                        }else { //last level
                            gameFinishedLabel.setVisible(true);
                            goBackToMenuLabel.setVisible(true);
                            isGameOver=true;

                            EventHandler<KeyEvent> gameOverHandler = new EventHandler<>() {
                                @Override
                                public void handle(KeyEvent e) {
                                    if (e.getCode() == KeyCode.SPACE) {
                                        Parent root = null;
                                        try {
                                            root = FXMLLoader.load(getClass().getResource("../FXML/Menu.fxml"));
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                        Stage currentStage = (Stage) gameFinishedLabel.getScene().getWindow();
                                        Scene menuScene = new Scene(root);
                                        currentStage.setScene(menuScene);
                                        Ball.getScene().removeEventHandler(KeyEvent.KEY_PRESSED,this);
                                    }
                                }
                            };
                            Ball.getScene().addEventHandler(KeyEvent.KEY_PRESSED,gameOverHandler);
                        }
                    }
            }
    }

    /**
     * Current ball angle.
     */
    int angle=0;

    /**
     * Function responsible for rotating the ball.
     */
    private void rotateBall() {
        Ball.setRotate(angle);
        angle = (angle+3) % 360;
    }

    /**
     * Function responsible for not letting the platform exit level boundaries.
     * @param platformAtRightBorder Boolean. Contains information if the platform is at the right edge of the level boundaries.
     * @param platformAtLeftBorder Boolean. Contains information if the platform is at the right edge of the level boundaries.
     */
    private void handlePlatformMovement(boolean platformAtRightBorder, boolean platformAtLeftBorder) {
        if (gameLoopStarted) {
            if (!platformAtLeftBorder) {
                if (goLeft) gamePlatform.setLayoutX((gamePlatform.getLayoutX() - platformMoveSpeed));
            }
            if (!platformAtRightBorder) {
                if (goRight) gamePlatform.setLayoutX(gamePlatform.getLayoutX() + platformMoveSpeed);
            }
        }
    }


    /**
     * Function that returns average frames per second (FPS) of the last 100 frames.
     * @return the average fps
     */
    public static float getAverageFPS()
    {
        float total = 0.0f;

        for (int i = 0; i < frameRates.length; i++)
        {
            total += frameRates[i];
        }

        return total / frameRates.length;
    }

    /**
     * Function executed at loading of game level. Sets the visibility of FPS Counter.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences preferences = Preferences.userRoot().node("/Main");
        updateFPSVisibility(preferences);
        updateBlocksFallingMode(preferences);

    }

    /**
     * Function responsible for updating the FPS counter visibility.
     * @param preferences Preferences object (settings stored in the register).
     */
    void updateFPSVisibility(Preferences preferences){
        currentFPSLabel.setVisible(preferences.getBoolean("fpsEnabled",false));
    }

    void updateBlocksFallingMode(Preferences preferences){
        blocksFalling = preferences.getBoolean("blocksFallingEnabled",false);
    }

    /**
     * Function responsible for toggling game loop
     */
    private void toggleGameLoop() {
        if(gameLoopStarted) {
            if(timer != null) {
                timer.stop();
                gameLoopStarted = false;
            }
        }else {
            if(timer != null) {
                timer.start();
                gameLoopStarted = true;
            }
        }
    }


    /**
     * Function responsible for toggling in-game options menu. Controlls state of the game-loop so that the game stops when
     * in-game settings menu is visible. Also handles going back to game.
     * @param event Button click event.
     * @throws IOException File Input/Output exception.
     */
    void toggleInGameSettings(KeyEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/ingameOptions.fxml"));
        Parent root = loader.load();

        Scene ingameSettingsScene = new Scene(root);
        Stage currentStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene currentScene = ((Node)event.getSource()).getScene();

        if(!ingameSettingsActive) {
            if(!levelFinished && !isGameOver && levelStarted) toggleGameLoop();
            currentStage.setScene(ingameSettingsScene);
            ingameSettingsActive = true;
        }
        ingameSettingsScene.setOnKeyPressed(e -> {
            if(e.getCode()== KeyCode.ESCAPE){
                currentStage.setScene(currentScene);
                ingameSettingsActive=false;

                if(!levelFinished && !isGameOver && levelStarted) toggleGameLoop();

                Preferences preferences = Preferences.userRoot().node("/Main");
                updateFPSVisibility(preferences);
            }
            Preferences preferences = Preferences.userRoot().node("/Main");
            updateFPSVisibility(preferences);
            SoundController.toggleMusic(preferences.getBoolean("musicEnabled",false));
        });

        IngameOptionsController ingameOptionsController = loader.getController();
        ingameOptionsController.resumeButton.setOnAction(e -> {

                currentStage.setScene(currentScene);
                ingameSettingsActive=false;

            if(!levelFinished && !isGameOver && levelStarted) toggleGameLoop();

                Preferences preferences = Preferences.userRoot().node("/Main");
                updateFPSVisibility(preferences);
                SoundController.toggleMusic(preferences.getBoolean("musicEnabled",false));
        });
    }


    /**
     * Function responsible for executing set level generating function. Also resets the score and left lives amount.
     * @param level Integer value of set level.
     */
    public void generateLevel(int level){
        resetScore();
        resetLives();
        if(level==1) generateLevel1();
        else if(level==2) generateLevel2();
        else if(level==3) generateLevel3();
    }

    /**
     * Function that generates level 1 blocks.
     */
    public void generateLevel1(){
        levelLabel.setText("Level 1");

        levelBlocks.clear();
        resetLevel();

        Rectangle[] objects = new Rectangle[2];
        int rectLine=0;
        int rectColumn=0;
        int spaceBetweenRects = 10;
        int rectXOffset=20;
        int rectYOffset=20;
        int rectNextLineOffset=40;
        int rectWidth=80;
        int rectHeight=30;
        for(int i=0;i<objects.length;i++){
            objects[i] = new Rectangle();

            if(i%13==0) {
                rectColumn=0;
                rectLine++;
            }
            objects[i].setX(rectXOffset + rectColumn*(rectWidth + spaceBetweenRects));
            objects[i].setY(rectYOffset + rectLine*(rectNextLineOffset));
            objects[i].setWidth(rectWidth);
            objects[i].setHeight(rectHeight);
            objects[i].setFill(new Color(ThreadLocalRandom.current().nextFloat(),ThreadLocalRandom.current().nextFloat(),ThreadLocalRandom.current().nextFloat(),1));

            gamePane.getChildren().add(objects[i]);
            levelBlocks.add(objects[i]);

            rectColumn++;
        }
    }

    /**
     * Function that generates level 2 blocks.
     */
    public void generateLevel2(){
        levelLabel.setText("Level 2");

        levelBlocks.clear();
        resetLevel();

        int blocksAmount = ThreadLocalRandom.current().nextInt(5,35);
        Rectangle[] objects = new Rectangle[blocksAmount];
        int rectWidth=40;
        int rectHeight=40;

        for(int i=0;i<objects.length;i++){
            objects[i] = new Rectangle();

            objects[i].setX(ThreadLocalRandom.current().nextInt(40,1120));
            objects[i].setY(ThreadLocalRandom.current().nextInt(50,350));

            objects[i].setWidth(rectWidth);
            objects[i].setHeight(rectHeight);
            objects[i].setStyle("-fx-fill: url('file:src/images/star.png')");


            for(int j=0;j<levelBlocks.size();j++){
                if(objects[i].intersects(levelBlocks.get(j).getBoundsInParent())){
                    objects[i].setX(ThreadLocalRandom.current().nextInt(40,700));
                    objects[i].setY(ThreadLocalRandom.current().nextInt(50,350));
                    j=0;
                }
                else if(j==levelBlocks.size()-1) break;
            }

            if(!gamePane.getChildren().contains(objects[i])) {
                gamePane.getChildren().add(objects[i]);
                levelBlocks.add(objects[i]);
            }
        }
    }

    /**
     * Function that generates level 3 blocks.
     */
    public void generateLevel3(){
        levelLabel.setText("Level 3");

        levelBlocks.clear();
        resetLevel();

        Rectangle[] objects = new Rectangle[32];

        for(int i=0;i<objects.length;i++){
            objects[i] = new Rectangle();
            objects[i].setStroke(Color.BLACK);
            objects[i].setStrokeWidth(1.0);
            objects[i].setStrokeType(StrokeType.INSIDE);
            if(i>=0 && i<=26){
                objects[i].setWidth(30);
                objects[i].setHeight(80);
                objects[i].setFill(new Color(0.109,0.180,0.0078,1));
                if(i==0){
                    objects[i].setX(91);
                    objects[i].setY(70);
                }else if(i==1){
                    objects[i].setX(121);
                    objects[i].setY(136);
                }else if(i==2){
                    objects[i].setX(151);
                    objects[i].setY(194);
                }else if(i==3){
                    objects[i].setX(181);
                    objects[i].setY(254);
                }else if(i==4){
                    objects[i].setX(211);
                    objects[i].setY(301);
                }else if(i==5){
                    objects[i].setX(241);
                    objects[i].setY(254);
                }else if(i==6){
                    objects[i].setX(271);
                    objects[i].setY(200);
                }else if(i==7){
                    objects[i].setX(301);
                    objects[i].setY(254);
                }else if(i==8){
                    objects[i].setX(331);
                    objects[i].setY(311);
                }else if(i==9){
                    objects[i].setX(361);
                    objects[i].setY(254);
                }else if(i==10){
                    objects[i].setX(391);
                    objects[i].setY(194);
                }else if(i==11){
                    objects[i].setX(421);
                    objects[i].setY(136);
                }else if(i==12){
                    objects[i].setX(451);
                    objects[i].setY(70);
                }else if(i==13){
                    objects[i].setX(481);
                    objects[i].setY(320);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==14){
                    objects[i].setX(511);
                    objects[i].setY(254);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==15){
                    objects[i].setX(541);
                    objects[i].setY(194);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==16){
                    objects[i].setX(571);
                    objects[i].setY(128);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==17){
                    objects[i].setX(601);
                    objects[i].setY(63);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==18){
                    objects[i].setX(631);
                    objects[i].setY(63);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==19){
                    objects[i].setX(661);
                    objects[i].setY(128);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==20){
                    objects[i].setX(691);
                    objects[i].setY(194);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==21){
                    objects[i].setX(721);
                    objects[i].setY(261);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==22){
                    objects[i].setX(751);
                    objects[i].setY(320);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==23){
                    objects[i].setX(913);
                    objects[i].setY(320);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==24){
                    objects[i].setX(913);
                    objects[i].setY(240);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==25){
                    objects[i].setX(913);
                    objects[i].setY(160);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==26){
                    objects[i].setX(913);
                    objects[i].setY(80);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }
            }else if(i>=27){
                objects[i].setWidth(105);
                objects[i].setHeight(27);
                if(i==27){
                    objects[i].setX(579);
                    objects[i].setY(248);
                    objects[i].setFill(new Color(0.274,0.368,0.133,1));
                }else if(i==28){
                    objects[i].setX(721);
                    objects[i].setY(53);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==29){
                    objects[i].setX(823);
                    objects[i].setY(53);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==30){
                    objects[i].setX(928);
                    objects[i].setY(53);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }else if(i==31){
                    objects[i].setX(1033);
                    objects[i].setY(53);
                    objects[i].setFill(new Color(0.491,0.6,0.325,1));
                }
            }
            gamePane.getChildren().add(objects[i]);
            levelBlocks.add(objects[i]);
        }


    }

    /**
     * Function that removes blocks on the current level. Useful when resetting a level after losing all lives.
     */
    private void clearLevelBlocks() {
        for (Iterator<Rectangle> it = levelBlocks.iterator(); it.hasNext();) {
            Rectangle next = it.next();
                gamePane.getChildren().remove(next);
                it.remove();
        }
    }

    /**
     * Function that decreases amount of lives left and updates lives left information.
     */
    private void decreaseLives(){
        this.livesLeft--;
        if(heart3.isVisible()) heart3.setVisible(false);
        else if(heart2.isVisible()) heart2.setVisible(false);
        else if(heart1.isVisible()) heart1.setVisible(false);
    }

    /**
     * Function that resets lives left amount. Updates graphics. Useful when resetting a level and when going to the next level.
     */
    private void resetLives(){
        this.livesLeft=3;
        if(!heart3.isVisible()) heart3.setVisible(true);
        if(!heart2.isVisible()) heart2.setVisible(true);
        if(!heart1.isVisible()) heart1.setVisible(true);
    }

    private void brickFall(Rectangle brick){
        brick.setLayoutY(brick.getLayoutY() + brickFallSpeed);

        final boolean BrickAtBottomBorder = brick.getLayoutY() + brick.getHeight() >= (640);
        if(brick.getBoundsInParent().intersects(gamePlatform.getBoundsInParent())){
            SoundController.playFailSound();

            Platform.runLater(()-> {
                levelBlocks.remove(brick);
                gamePane.getChildren().remove(brick);
                checkLevelFinished();
            });

            if(livesLeft>1) {
                decreaseLives();
                resetLevel();
            }else{
                handleGameOver();
            }
        }

        if(BrickAtBottomBorder) {
            Platform.runLater(()-> {
                levelBlocks.remove(brick);
                gamePane.getChildren().remove(brick);
                checkLevelFinished();
            });
        }
    }


    private void handleBricksFall(){
        for (Iterator<Rectangle> it = levelBlocks.iterator(); it.hasNext();) {
            Rectangle next = it.next();
            if(next.getOpacity()==0.4)
                brickFall(next);
        }
    }

    private void clearFallingBlocks(){
        for (Iterator<Rectangle> it = levelBlocks.iterator(); it.hasNext();) {
            Rectangle next = it.next();
            if(next.getOpacity()==0.4)
                Platform.runLater(()-> {
                    levelBlocks.remove(next);
                    gamePane.getChildren().remove(next);
                });
        }
    }
}