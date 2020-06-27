package Controllers;

import Main.Main;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


/**
 * Class responsible for controlling options menu.
 */
public class OptionsController implements Initializable {

    @FXML
    private ToggleButton MusicToggle;

    @FXML
    private ToggleButton SoundEffectsToggle;

    @FXML
    private ToggleButton fpsCounterToggle;

    @FXML
    private ToggleButton fallingBlocksToggle;

    /**
     * Function that toggles FPS counter visibility. Saves toggle buttons state to the register,
     * so that the setting doesn't reset on every app launch.
     */
    @FXML
    void toggleFPS(){
        Preferences preferences = Preferences.userRoot().node("/Main");
        SoundController.playMenuSound();
        if(fpsCounterToggle.isSelected()) {
            fpsCounterToggle.setStyle("-fx-base: lightgreen");
            fpsCounterToggle.setText("ON");
            preferences.putBoolean("fpsEnabled",true);
        }
        else if(!fpsCounterToggle.isSelected()){
            fpsCounterToggle.setStyle("-fx-base: salmon");
            fpsCounterToggle.setText("OFF");
            preferences.putBoolean("fpsEnabled",false);
        }
    }

    /**
     * Function that updates FPS toggle button graphics.
     */
    void updateFPSToggleButton(){
        if(fpsCounterToggle.isSelected()) {
            fpsCounterToggle.setStyle("-fx-base: lightgreen");
            fpsCounterToggle.setText("ON");
        }
        else if(!fpsCounterToggle.isSelected()){
            fpsCounterToggle.setStyle("-fx-base: salmon");
            fpsCounterToggle.setText("OFF");
        }
    }



    /**
     * Function that toggles music. Saves toggle buttons state to the register,
     * so that the setting doesn't reset on every app launch.
     */
    @FXML
    void toggleMusic() {
        Preferences preferences = Preferences.userRoot().node("/Main");
        SoundController.playMenuSound();
        if(MusicToggle.isSelected()){
            MusicToggle.setStyle("-fx-base: lightgreen");
            MusicToggle.setText("ON");
            preferences.putBoolean("musicEnabled",true);
        }else if(!MusicToggle.isSelected()){
            MusicToggle.setStyle("-fx-base: salmon");
            MusicToggle.setText("OFF");
            preferences.putBoolean("musicEnabled",false);
        }
    }

    /**
     * Function that updates music toggle button graphics.
     */
    void updateMusicToggleButton() {
        if(MusicToggle.isSelected()){
            MusicToggle.setStyle("-fx-base: lightgreen");
            MusicToggle.setText("ON");
        }else if(!MusicToggle.isSelected()){
            MusicToggle.setStyle("-fx-base: salmon");
            MusicToggle.setText("OFF");
        }
    }


    /**
     * Function that toggles sound effects. Saves toggle buttons state to the register,
     * so that the setting doesn't reset on every app launch.
     */
    @FXML
    void toggleSoundEffects() {
        Preferences preferences = Preferences.userRoot().node("/Main");
        SoundController.playMenuSound();
        if(SoundEffectsToggle.isSelected()) {
            SoundEffectsToggle.setStyle("-fx-base: lightgreen");
            SoundEffectsToggle.setText("ON");
            preferences.putBoolean("soundEffectsEnabled",true);
        }else if(!SoundEffectsToggle.isSelected()){
            SoundEffectsToggle.setText("OFF");
            SoundEffectsToggle.setStyle("-fx-base: salmon");
            preferences.putBoolean("soundEffectsEnabled",false);
        }
    }

    /**
     * Function that updates sound effects toggle button graphics.
     */
    void updateSoundEffectsToggleButton() {
        if(SoundEffectsToggle.isSelected()) {
            SoundEffectsToggle.setStyle("-fx-base: lightgreen");
            SoundEffectsToggle.setText("ON");
        }else if(!SoundEffectsToggle.isSelected()){
            SoundEffectsToggle.setText("OFF");
            SoundEffectsToggle.setStyle("-fx-base: salmon");
        }
    }

    /**
     * Function that toggles blocks falling option. Saves toggle buttons state to the register,
     * so that the setting doesn't reset on every app launch.
     */
    @FXML
    void toggleBlocksFalling(){
        Preferences preferences = Preferences.userRoot().node("/Main");
        SoundController.playMenuSound();
        if(fallingBlocksToggle.isSelected()) {
            fallingBlocksToggle.setStyle("-fx-base: lightgreen");
            fallingBlocksToggle.setText("ON");
            preferences.putBoolean("blocksFallingEnabled",true);
        }
        else if(!fallingBlocksToggle.isSelected()){
            fallingBlocksToggle.setStyle("-fx-base: salmon");
            fallingBlocksToggle.setText("OFF");
            preferences.putBoolean("blocksFallingEnabled",false);
        }
    }

    /**
     * Function that updates sound effects toggle button graphics.
     */
    void updateBlocksFallingToggleButton() {
        if(fallingBlocksToggle.isSelected()) {
            fallingBlocksToggle.setStyle("-fx-base: lightgreen");
            fallingBlocksToggle.setText("ON");
        }else if(!fallingBlocksToggle.isSelected()){
            fallingBlocksToggle.setText("OFF");
            fallingBlocksToggle.setStyle("-fx-base: salmon");
        }
    }


    /**
     * Function triggered when "Go back to menu" button is clicked. Odpowiada za obsługę powrotu do menu głównego gry, podczas gdy znajdujemy się w menu opcji gry.
     * Responsible for handling going back to main menu.
     * @param event Button click event.
     * @throws IOException File input/output exception.
     */
    @FXML
    void goBackToMenu(ActionEvent event) throws IOException {
        SoundController.playMenuSound();
        Parent menuParent = FXMLLoader.load((getClass().getResource("../FXML/Menu.fxml")));
        Scene menuScene = new Scene(menuParent,Main.WIDTH, Main.HEIGHT);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
        window.show();
    }

    /**
     *  Function that is triggered when options menu is loading. Sets up settings.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences preferences = Preferences.userRoot().node("/Main");
        setupSettings(preferences);
    }

    /**
     * Function responsible for setting up toggle button's states and update their graphics.
     * @param preferences Preferences object (reference to app settings stored in register).
     */
    private void setupSettings(Preferences preferences){
        MusicToggle.setSelected(preferences.getBoolean("musicEnabled",true));
        SoundEffectsToggle.setSelected(preferences.getBoolean("soundEffectsEnabled",true));
        fpsCounterToggle.setSelected(preferences.getBoolean("fpsEnabled",false));
        fallingBlocksToggle.setSelected(preferences.getBoolean("blocksFallingEnabled",false));

        updateMusicToggleButton();
        updateSoundEffectsToggleButton();
        updateFPSToggleButton();
        updateBlocksFallingToggleButton();
    }
}
