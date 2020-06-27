package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Main class. The Application starts here.
 */
public class Main extends Application {

    /**
     * Constant value that stores window HEIGHT;
     */
    public static final int WIDTH=1200;
    /**
     * Constant value that stores window WIDTH;
     */
    public static final int HEIGHT=800;


    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("../FXML/Menu.fxml"));

        primaryStage.setTitle("Arkanoid Game");
        primaryStage.setResizable(false);

        Scene menuScene = new Scene(root,WIDTH,HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.show();
        primaryStage.requestFocus();
    }

    /**
     * main() method. Start point of the application.
     *
     * @param args main() launch parameters
     */
    public static void main(String[] args) {
        launch(args);
    }
}
