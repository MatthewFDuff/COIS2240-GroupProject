package productivityplanner.ui.main;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    public void generateCalendar(){
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Calendar.initializeView();

    }
}
