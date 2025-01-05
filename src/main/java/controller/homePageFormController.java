package controller;

import DB.DBConnection;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TaskObject;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class homePageFormController implements Initializable {
    public JFXButton btn;
    public ScrollPane pane;
    public VBox completed;
    public ScrollPane scrollComplete;
    public VBox vBoxToDoTask;
    public JFXButton btnReload;

    private void refreshTasks() throws SQLException {
        setToDoTasks();
        setCompletedTasks();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            refreshTasks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setToDoTasks() throws SQLException {
        ArrayList<TaskObject> taskList = getTasksFromDB("addTask");
        VBox toDoVBox = new VBox();
        for (TaskObject task : taskList) {
            // Styled CheckBox
            CheckBox checkBox = new CheckBox(task.getTaskName().get());
            checkBox.getStyleClass().add("custom-checkbox");

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    try {
                        new Alert(Alert.AlertType.CONFIRMATION, "Task Completed!").show();
                        moveToCompleted(task);
                        refreshTasks();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Styled Labels
            Label description = new Label(task.getDescription().get());
            description.getStyleClass().add("custom-label");

            Label dueDate = new Label(task.getDueDate().get());
            dueDate.getStyleClass().add("custom-label");

            // Add checkboxes and labels to the VBox
            VBox taskContainer = new VBox(checkBox, description, dueDate);
            taskContainer.getStyleClass().add("task-container");
            toDoVBox.getChildren().add(taskContainer);



            toDoVBox.getChildren().addAll(checkBox, description, dueDate);
        }

        pane.setContent(toDoVBox);
    }

    private void setCompletedTasks() throws SQLException {
        ArrayList<TaskObject> completedList = getTasksFromDB("completeTask");
        VBox completedVBox = new VBox();

        for (TaskObject task : completedList) {
            CheckBox checkBox = new CheckBox(task.getTaskName().get());
            checkBox.getStyleClass().add("custom-checkbox");

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    try {
                        new Alert(Alert.AlertType.CONFIRMATION, "Not Completed!").show();
                        moveToToDo(task);
                        refreshTasks();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            // Styled Labels
            Label description = new Label(task.getDescription().get());
            description.getStyleClass().add("custom-label");

            Label dueDate = new Label(task.getDueDate().get());
            dueDate.getStyleClass().add("custom-label");

            // Add checkboxes and labels to the VBox
            VBox taskContainer = new VBox(checkBox, description, dueDate);
            taskContainer.getStyleClass().add("task-container");
            completedVBox.getChildren().add(taskContainer);


            completedVBox.getChildren().addAll(checkBox, description, dueDate);
        }

        scrollComplete.setContent(completedVBox);
    }

    private ArrayList<TaskObject> getTasksFromDB(String tableName) throws SQLException {
        ArrayList<TaskObject> taskList = new ArrayList<>();
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);

        while (resultSet.next()) {
            taskList.add(new TaskObject(
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            ));
        }

        return taskList;
    }

    private void moveToCompleted(TaskObject task) throws SQLException {
        addTaskToDB("completeTask", task);
        deleteTaskFromDB("addTask", task);
    }

    private void moveToToDo(TaskObject task) throws SQLException {
        addTaskToDB("addTask", task);
        deleteTaskFromDB("completeTask", task);
    }

    private void addTaskToDB(String tableName, TaskObject task) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO " + tableName + " (taskTitle, taskDescription, taskDate) VALUES (?, ?, ?)");
        preparedStatement.setString(1, task.getTaskName().get());
        preparedStatement.setString(2, task.getDescription().get());
        preparedStatement.setString(3, task.getDueDate().get());
        preparedStatement.executeUpdate();
    }

    private void deleteTaskFromDB(String tableName, TaskObject task) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM " + tableName + " WHERE taskTitle = ? AND taskDescription = ? AND taskDate = ?");
        preparedStatement.setString(1, task.getTaskName().get());
        preparedStatement.setString(2, task.getDescription().get());
        preparedStatement.setString(3, task.getDueDate().get());
        preparedStatement.executeUpdate();
    }
    public void btnAddTaskOnAction(ActionEvent actionEvent) throws IOException, SQLException {
        Stage stage =new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/todo_app.fxml"))));
        stage.show();
    }

    public void btnReloadOnAction(ActionEvent actionEvent) throws SQLException {
        refreshTasks();
    }
}
