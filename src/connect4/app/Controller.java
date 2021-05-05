package connect4.app;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS=7;
	private static final int ROWS=6;
	private static final int CIRCLE_DIAMETER=80;
	private static final String discColor1="#24303E";
	private static final String discColor2="#4CAA88";


	private static long startTime;
	private static long endTime;

	private static String PLAYER_ONE="Player One";
	private static String PLAYER_TWO="Player Two";

	private boolean isPlayerOneTurn=true;
	
	private Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS];

	private boolean isAllowedToInsert=true;

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField playerOneTextField,playerTwoTextField;

	@FXML
	public Button setNamesButton;

	public void createPlayground(){
		Platform.runLater(() -> setNamesButton.requestFocus());
		Shape rectangleWithHoles=createGameStructureGrid();
		rootGridPane.add(rectangleWithHoles,0,1);
		setNamesButton.setOnAction(event -> {
			PLAYER_ONE=playerOneTextField.getText();
			PLAYER_TWO=playerTwoTextField.getText();
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
			playMusic();
			startTime=System.currentTimeMillis();
			List<Rectangle> rectangleList=createClickableColumns();
			for (Rectangle rectangle:rectangleList){
				rootGridPane.add(rectangle,0,1);
			}
		});
	}
	private Shape createGameStructureGrid(){
		Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
		for (int row=0;row<ROWS;row++){
			for (int col=0;col<COLUMNS;col++){
				Circle circle=new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);
				circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns(){
		List<Rectangle> rectangleList=new ArrayList<>();
		for (int col=0;col<COLUMNS;col++){
			Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee66")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column=col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert=false;
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}

		return rectangleList;
	}
	private void insertDisc(Disc disc,int column){

		int row=ROWS-1;
		while (row >= 0){

			if (getDiscIfPresent(row,column)==null)
				break;
			row--;
		}
		if (row<0)
			return;

		insertedDiscsArray[row][column]=disc;
		
		insertedDiscPane.getChildren().add(disc);

		disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

		int currRow=row;
		TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.4),disc);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);


		translateTransition.setOnFinished(event -> {
			 isAllowedToInsert=true;
			if(gameEnded(currRow,column)){
				try {
					gameOver();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
				return;
			}

			isPlayerOneTurn =! isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
		});

		translateTransition.play();
	}

	private boolean gameEnded(int row, int column){
		List<Point2D> verticalPointes=IntStream.rangeClosed(row-3,row+3)  //range of row values= 0,1,2,3,4,5
										.mapToObj(r-> new Point2D(r,column))  //0,3  1,3  2,3   3,3  4,3  5,3 ==> Point2D  x,y
										.collect(Collectors.toList());

		List<Point2D> horizontalPoints=IntStream.rangeClosed(column-3,column+3)
				.mapToObj(col-> new Point2D(row,col))
				.collect(Collectors.toList());

		Point2D startPoint1 =new Point2D(row-3,column+3);
		List<Point2D> digonal1Point=    IntStream.rangeClosed(0,6)
										.mapToObj(i-> startPoint1.add(i,-i))
										.collect(Collectors.toList());

		Point2D startPoint2 =new Point2D(row-3,column-3);
		List<Point2D> digonal2Point=    IntStream.rangeClosed(0,6)
										.mapToObj(i-> startPoint2.add(i,i))
										.collect(Collectors.toList());


		boolean isEnded=checkCombination(verticalPointes) || checkCombination(horizontalPoints)
				                                          || checkCombination(digonal1Point)
				                                          || checkCombination(digonal2Point);

		return isEnded;
	}

	private boolean checkCombination(List<Point2D> points) {
		int chain = 0;
		for (Point2D point: points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}
		}

		return false;
	}




	public Disc getDiscIfPresent(int row,int column){
		if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;
		return insertedDiscsArray[row][column];
	}


	private void gameOver() throws IOException {

		mediaPlayer.stop();
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is: " + winner);
         endTime=System.currentTimeMillis();
         long allSeconds=endTime-startTime;
         writeToFIle(winner,allSeconds);

		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		DialogPane dialogPane= alert.getDialogPane();
		dialogPane.getStylesheets().add(
				getClass().getResource("style.css").toExternalForm());
		alert.setTitle("Connect Four");
		alert.setHeaderText("برنده بازی: "+winner);
		alert.setContentText("آیا میخواهید دوباره بازی کنید؟");
		ButtonType yeBtn=new ButtonType("بله");
		ButtonType noBtn =new ButtonType("نه!");
		alert.getButtonTypes().setAll(yeBtn,noBtn);

		Platform.runLater(()->{

			Optional<ButtonType> btnClicked= alert.showAndWait();

			if (btnClicked.isPresent() && btnClicked.get() ==yeBtn){
				mediaPlayer.play();
				resetGame();
			}else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	//Hana
	public void resetGame() {
		insertedDiscPane.getChildren().clear();
		for (int row = 0; row <insertedDiscsArray.length ; row++) {
			for (int column = 0; column < insertedDiscsArray[row].length; column++) {
				insertedDiscsArray[row][column] = null;
			}
		}
		isPlayerOneTurn=true;
		playerNameLabel.setText(PLAYER_ONE);

		createPlayground();
	}



	//Yalda{
	private static class Disc extends Circle{
		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;

			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}



	static AudioClip mediaPlayer;
	public void playMusic(){
		String path = "src/connect4/app/Media/test.mp3";
		Media hit = new Media(Paths.get(path).toUri().toString());
		 mediaPlayer= new AudioClip(hit.getSource());
		 mediaPlayer.play();
	}



	public  void writeToFIle(String winner,long time) throws IOException {
		RandomAccessFile file=new RandomAccessFile("history.txt","rw");
		    file.seek(file.length());
			file.writeUTF(":"+PLAYER_ONE+":"+PLAYER_TWO+":"+winner+":"+time+"\n");
			file.close();
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
