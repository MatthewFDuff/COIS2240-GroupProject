package productivityplanner.ui.main;
import javafx.fxml.FXMLLoader;
import org.junit.Test;


// Tests each FXML file to see if it loads properly
public class ControllerTester {

    @Test
    public void checkMainWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));

    }

    @Test
    public void checkAddWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/productivityplanner/ui/addtask/AddNewTask.fxml"));

    }

    @Test
    public void checkEditWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/productivityplanner/ui/edittask/EditTask.fxml"));

    }

    @Test
    public void checkDeleteWindow(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/productivityplanner/ui/deletetask/DeleteTask.fxml"));

    }

}
