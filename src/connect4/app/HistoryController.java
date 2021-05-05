package connect4.app;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HistoryController {

    final ObservableList<String> type = FXCollections.observableArrayList();

    @FXML
    private JFXListView<String> list;

    public void initialize() throws FileNotFoundException {

        list.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm());
        File file=new File("history.txt");
        Scanner scanner=new Scanner(file);
        while (scanner.hasNextLine()){
            String[] text=scanner.nextLine().split(":");
            System.out.println(text[1]);
            System.out.println(text[2]);
            System.out.println(text[3]);
            System.out.println(text[4]);
            String item="بازیکن اول  "+text[1]+"  بازیکن دوم    "+text[2]+"  برنده  "+text[3]+"  کل زمان بازی  "+text[4];
            type.add(item);
        }

      list.setItems(type);

    }
}
