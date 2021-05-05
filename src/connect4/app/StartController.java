package connect4.app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {

    Controller controller;

    @FXML
    private JFXButton startBtn;

    @FXML
    void history(ActionEvent event) throws IOException {
        Stage stage=new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("HistoryPage.fxml"));
        stage.setTitle("Connect4");
        Image image=new Image(getClass().getResourceAsStream( "Icon/logo.png"));
        stage.getIcons().add(image);//Icon
        stage.setScene(new Scene(root, 680, 550));
        stage.show();
    }
    @FXML
    void learning(ActionEvent event) {
        aboutConnect4();

    }
    @FXML
    void newGame(ActionEvent event) throws IOException {
        Stage primaryStage=new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootGridPane);
        Image image=new Image(getClass().getResourceAsStream("Icon/logo.png"));
        primaryStage.getIcons().add(image);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void settings(ActionEvent event) {


    }



    private MenuBar createMenu() {

        Menu fileMenu = new Menu("فایل");
        fileMenu.setStyle("-fx-background-color: #006886;-fx-font-family: \"B Titr\";");

        MenuItem newGame = new MenuItem("بازی جدید");

        newGame.setOnAction(event -> controller.resetGame());

        MenuItem resetGame = new MenuItem("ریست ");

        resetGame.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("خروج");

        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        //Help Menu
        Menu helpMenu = new Menu("راهنمایی");
        helpMenu.setStyle("-fx-background-color: #006886;-fx-font-family: \"B Titr\";");

        MenuItem aboutGame = new MenuItem("  درباره بازی");

        aboutGame.setOnAction(event -> aboutConnect4());
        SeparatorMenuItem separatorHelpItem = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem(" درباره ما");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separatorHelpItem, aboutMe);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;

    }


    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogPane dialogPane= alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm());
        alert.setTitle("درباره ما");
        alert.setHeaderText("SHY");
        alert.setContentText("ما علاقه مند به ساخت بازی های سرگرم کننده از طریق کد نویسی هستیم. " +
                "connect 4  نیز یکی از این بازی ها است و میتواند شما را تا ساعت ها سرگرم کند" +
                " امیدواریم از بازی لذت ببرید. ");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogPane dialogPane= alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm());
        alert.setTitle("درباره بازی");
        alert.setHeaderText("چگونه بازی کنیم؟");
        alert.setContentText("اين بازي دو نفره روي يك صفحه ي ايستاده به عرض 7 و ارتفاع 6 صورت ميگيرد." +
                " هر بازيكن در هر نوبت يكي از مهره هايش را از بالا در " +
                "يكي از ستون هاي جدول مياندازد. مهره ي وارد شده تا جاي ممكن سقوط مي كند، " +
                "كه مي تواند كف صفحه ي بازي و يا مهره اي ديگر باشد.\n" +
                "هدف اين بازي ايجاد يك رديف چهارتايي " +
                " از مهره ها است كه مي تواند عمودي افقي و يا به شكل مورب ايجاد شده باشد. بنا براين هر بازيكن " +
                "علاوه بر تلاش براي رسيدن به اين هدف مانع رسيدن حريف به چهارتايي هم مي شود.  ");
        alert.show();
    }
    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

}
