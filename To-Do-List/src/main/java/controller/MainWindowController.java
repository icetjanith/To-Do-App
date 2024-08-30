package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    public ListView<String> todolist;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTaskList();

        // Set custom cell factory for the ListView
        todolist.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> listView) {
                return new CheckBoxListCell();
            }
        });
    }

    private void loadTaskList() {
        List<String> tasks = getTaskTitlesFromDatabase();
        todolist.getItems().addAll(tasks);
    }

    private List<String> getTaskTitlesFromDatabase() {
        List<String> tasks = new ArrayList<>();
        String query = "SELECT task_title FROM ActiveTasks";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String taskTitle = resultSet.getString("task_title");
                tasks.add(taskTitle);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void addNewTaskOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/addtaskwindow.fxml"))));
        stage.show();
    }

    public void addCompletedTasksOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/completedtaskwindow.fxml"))));
        stage.show();
    }


    public  class CheckBoxListCell extends ListCell<String> {
        private final CheckBox checkBox = new CheckBox();
        private final Label label = new Label();
        private final VBox content = new VBox(checkBox, label);

        public CheckBoxListCell() {
            super();

            checkBox.setOnAction(event -> {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Are you sure you want to proceed?");
                alert.setContentText("Click OK to proceed or Cancel to abort.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {

                    String a=getItem();
                    System.out.println(a);
                    System.out.println("User clicked OK");

                } else {

                    System.out.println("User clicked Cancel");
                    checkBox.setSelected(false);
                }

                System.out.println(getItem() + " is " + (checkBox.isSelected() ? "completed" : "not completed"));

            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                label.setText(item);
                setGraphic(content);
            }
        }
    }
}

