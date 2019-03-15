package productivityplanner.ui.taskcell;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import productivityplanner.data.Task;

public class TaskCellFactory implements Callback<ListView<Task>, ListCell<Task>> {
    @Override
    public ListCell<Task> call(ListView<Task> param) {
        return new TaskCellController();
    }
}
