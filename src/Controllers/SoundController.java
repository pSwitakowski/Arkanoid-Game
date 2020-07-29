package Controllers;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.prefs.Preferences;



/**
 * Class responsible for controlling sound in application. Contains only static methods and variables.
 */
public class SoundController {

    private static Preferences preferences = Preferences.userRoot().node("/Main");
    public static boolean soundEffectsEnabled=preferences.getBoolean("soundEffectsEnabled",false);



    private static String musicFile = "src/soundEffects/music.mp3";
    private static Media sound = new Media(new File(musicFile).toURI().toString());
    private static MediaPlayer musicPlayer = new MediaPlayer(sound);



    private static AudioClip wallBounceSound = new AudioClip(new File("src/soundEffects/wallBounce.mp3").toURI().toString());
    private static AudioClip platformBounceSound = new AudioClip(new File("src/soundEffects/platformBounce.mp3").toURI().toString());
    private static AudioClip failSound = new AudioClip(new File("src/soundEffects/died.wav").toURI().toString());
    private static AudioClip menuButtonSound = new AudioClip(new File("src/soundEffects/click.wav").toURI().toString());
    private static AudioClip levelFinishedSound = new AudioClip(new File("src/soundEffects/levelFinished.wav").toURI().toString());
    private static AudioClip gameOverSound = new AudioClip(new File("src/soundEffects/gameOver.wav").toURI().toString());

    /**
     * Function responsible for toggling music.
     * @param arg An argument that controls music state.
     */
    public static void toggleMusic(boolean arg) {
        musicPlayer.setVolume(0.075);
        if(arg) musicPlayer.play();
        else musicPlayer.pause();

        musicPlayer.setOnEndOfMedia(() -> {
            musicPlayer.seek(Duration.ZERO);
            musicPlayer.play();
        });

        System.out.println("Toggling music...");
    }

    /**
     * Function responsible for playing wall/brick bounce sound.
     */
    public static void playWallBounceSound(){
        if(soundEffectsEnabled) {
            wallBounceSound.play();
        }
    }

    /**
     * Function responsible for playing platform bounce sound.
     */
    public static void playPlatformBounceSound(){
        if(soundEffectsEnabled) {
            platformBounceSound.play();
        }
    }

    /**
     * Function responsible for playing level fail sound.
     */
    public static void playFailSound(){
        if(soundEffectsEnabled) {
            failSound.play();
        }
    }

    /**
     * Function responsible for playing menu button click sound.
     */
    public static void playMenuSound(){
        if(soundEffectsEnabled) {
            menuButtonSound.play();
            System.out.println("Menu button sound");
        }
        System.out.println(soundEffectsEnabled);
    }

    /**
     * Function responsible for playing level finish sound.
     */
    public static void playLevelFinishedSound(){
        if(soundEffectsEnabled) {
            levelFinishedSound.play();
        }
    }

    /**
     * Function responsible for playing game over sound.
     */
    public static void playGameOverSound(){
        if(soundEffectsEnabled) {
            gameOverSound.play();
        }
    }

}
