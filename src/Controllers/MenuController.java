package Controllers;

import Main.Main;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


/**
 * Class responsible for handling main menu and level choose menu.
 */
public class MenuController implements Initializable {

    /**
     * Function responsible for handling choosing level 1. Loads the game and generates level 1 objects.
     * @param event MouseEvent of clicking the mouse at level 1 area.
     * @throws IOException File input/output exception
     */
    @FXML
    void level1Action(MouseEvent event) throws IOException {
        SoundController.playMenuSound();
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("../FXML/Game.fxml"));
        Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();

        Parent gameParent = gameLoader.load();

        Scene gameScene = new Scene(gameParent);

        GameController gameController = gameLoader.getController();

        gameController.setCurrentLevel(1);

        currentStage.setScene(gameScene);

        gameController.generateLevel(gameController.getCurrentLevel());
    }

    /**
     * Function responsible for handling choosing level 2. Loads the game and generates level 2 objects.
     * @param event MouseEvent of clicking the mouse at level 2 area.
     * @throws IOException File input/output exception.
     */
    @FXML
    void level2Action(MouseEvent event) throws IOException {
        SoundController.playMenuSound();
        FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("../FXML/Game.fxml"));
        Stage currentStage = (Stage)((Node)event.getSource()).getScene().getWindow();

        Parent gameParent = gameLoader.load();

        Scene gameScene = new Scene(gameParent);

        GameController gameController = gameLoader.getController();

        gameController.setCurrentLevel(2);

        currentStage.setScene(gameScene);

        gameController.generateLevel(gameController.getCurrentLevel());
    }

    /**
     * Function responsible for handling choosing level 3. Loads the game and generates level 3 objects.
     * @param event MouseEvent of clicking the mouse at level 3 area.
     * @throws IOException File input/output exception.
     */
    @FXML
    void level3Action(MouseEvent event) throws IOException {
            SoundController.playMenuSound();
            FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("../FXML/Game.fxml"));
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Parent gameParent = gameLoader.load();

            Scene gameScene = new Scene(gameParent);

            GameController gameController = gameLoader.getController();

            gameController.setCurrentLevel(3);

            currentStage.setScene(gameScene);

            gameController.generateLevel(gameController.getCurrentLevel());
    }


    /**
     * Function responsible for handling game exit.
     * @param event Event of pressing the button, to which the function is assigned to.
     */
    @FXML
    void exitGame(ActionEvent event) {
        SoundController.playMenuSound();
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Function responsible for handling opening options menu.
     * @param event Event of pressing the button, to which the function is assigned to.
     * @throws IOException File input/output exception.
     */
    @FXML
    void openOptions(ActionEvent event) throws IOException {
        SoundController.playMenuSound();
        Parent optionsParent = FXMLLoader.load((getClass().getResource("../FXML/Options.fxml")));
        Scene optionsScene = new Scene(optionsParent,Main.WIDTH,Main.HEIGHT);

        Scene menuScene = ((Node)event.getSource()).getScene();
        Stage menuStage = (Stage)((Node)event.getSource()).getScene().getWindow();

        menuStage.setScene(optionsScene);

        optionsScene.setOnKeyPressed(e -> {
            if(e.getCode()== KeyCode.ESCAPE){
                menuStage.setScene(menuScene);
            }
        });
        menuStage.show();
        menuStage.requestFocus();
    }

    /**
     * Function responsible for handling opening level chooser menu, also handling going back and forth.
     * @param event Event of pressing the button, to which the function is assigned to.
     * @throws IOException File input/output exception.
     */
    @FXML
    void playGame(ActionEvent event) throws IOException {
        SoundController.playMenuSound();

        Parent loaderParent = new FXMLLoader().load(getClass().getResource("../FXML/LevelChooser.fxml"));
        Scene levelChooserScene = new Scene(loaderParent);

        Parent menuParent = new FXMLLoader().load(getClass().getResource("../FXML/Menu.fxml"));
        Scene menuScene = new Scene(menuParent);

        Stage currentStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        currentStage.setScene(levelChooserScene);

        levelChooserScene.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ESCAPE){
                currentStage.setScene(menuScene);
            }
        });

    }

    /**
     *  Function that is called before main menu loads. Sets up the settings according to registry values.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences preferences = Preferences.userRoot().node("/Main");
        boolean musicEnabled = preferences.getBoolean("musicEnabled",true);
        SoundController.toggleMusic(musicEnabled);
    }
}
