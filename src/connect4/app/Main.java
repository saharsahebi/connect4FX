package connect4.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("StartPage.fxml"));
		primaryStage.setTitle("Connect4");
		Image image=new Image(getClass().getResourceAsStream( "Icon/logo.png"));
		primaryStage.getIcons().add(image);//Icon
		primaryStage.setScene(new Scene(root, 680, 550));
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
