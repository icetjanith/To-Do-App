package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import model.Task;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

public class AddTaskWindowController implements Initializable {

    public JFXTextField title;
    public TextArea description;
    public DatePicker completionDate;
    public JFXComboBox<String> catagory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadComboBox();
    }

    private void loadComboBox() {
        ObservableList<String> categoryList = FXCollections.observableArrayList();
        categoryList.add("Work");
        categoryList.add("Personal");
        categoryList.add("Urgent");
        catagory.setItems(categoryList);
    }

    public void addTaskOnAction(ActionEvent actionEvent) throws RuntimeException {
        try {
            Task task=new Task(
                    title.getText(),
                    description.getText(),
                    completionDate.getValue(),
                    catagory.getValue()
            );

            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO ActiveTasks (task_title, task_description,task_date) VALUES (?, ?, ?)");
            preparedStatement.setString(1,task.getTitle());
            preparedStatement.setString(2, task.getDescription());
            preparedStatement.setString(3, String.valueOf(task.getCompletionDate()));
            boolean executed = preparedStatement.execute();
            if (executed){
                new Alert(Alert.AlertType.ERROR,"Customer Not Added !").show();
            }else{
                new Alert(Alert.AlertType.CONFIRMATION,"Customer Added !").show();
            }

            preparedStatement.close();
            connection.close();



        }catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
