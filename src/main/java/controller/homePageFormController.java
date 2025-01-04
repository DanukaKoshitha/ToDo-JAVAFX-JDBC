package controller;

import DB.DBConnection;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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

    public void setTasks() throws SQLException {
        ArrayList<TaskObject> arrayList = new ArrayList<>();
        Connection connection =DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("select * from addTask");


        while (rst.next()){
            arrayList.add(
                    new TaskObject(
                            rst.getString(2),
                            rst.getString(3),
                            rst.getString(4)
                    )
            );
        }

        VBox vbox = new VBox();
        for (TaskObject task : arrayList) {
            CheckBox checkBox = new CheckBox(task.getTaskName().get());
            checkBox.selectedProperty().bindBidirectional(task.completedProperty());
//            Label description = new Label(task.getDescription().get());
//            Label dueDate = new Label("" + task.getDueDate());

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            vbox.getChildren().remove(checkBox);
                            completed.getChildren().add(checkBox);
                            pane.setContent(vbox);
                            scrollComplete.setContent(completed);

                            try {

                                setCompleteTask(task);
                                deleteTask(task);

                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            vbox.getChildren().add(checkBox);

        }
        pane.setContent(vbox);
    }

    public void setCompleteTask(TaskObject task) throws SQLException {
        Connection con = DBConnection.getInstance().getConnection();
        PreparedStatement pst = con.prepareStatement("insert into completeTask(taskTitle,taskDescription,taskDate) values(?,?,?)");
        pst.setString(1, task.getTaskName().get());
        pst.setString(2, task.getDescription().get());
        pst.setString(3, task.getDueDate().get());

        if (pst.executeUpdate() > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Task completed!").show();
        }
    }

//    public void undoSetCompleteTask(TaskObject task) throws SQLException {
//        Connection connection = DBConnection.getInstance().getConnection();
//        PreparedStatement pst = connection.prepareStatement("DELETE FROM completeTask WHERE taskTitle = ? AND taskDescription = ? AND taskDate = ?");
//        pst.setString(1, task.getTaskName().get());
//        pst.setString(2, task.getDescription().get());
//        pst.setString(3, task.getDueDate().get());
//        pst.executeUpdate();
//    }

    public void deleteTask(TaskObject task) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement("DELETE FROM addTask WHERE taskTitle = ? AND taskDescription = ? AND taskDate = ?");
        pst.setString(1, task.getTaskName().get());
        pst.setString(2, task.getDescription().get());
        pst.setString(3, task.getDueDate().get());
        pst.executeUpdate();
    }

//    public void reStoreToDoTask(TaskObject task) throws SQLException {
//        Connection con = DBConnection.getInstance().getConnection();
//        PreparedStatement pst = con.prepareStatement("insert into addTask(taskTitle,taskDescription,taskDate) values(?,?,?)");
//        pst.setString(1, task.getTaskName().get());
//        pst.setString(2, task.getDescription().get());
//        pst.setString(3, task.getDueDate().get());
//
//        if (pst.executeUpdate() > 0) {
//            new Alert(Alert.AlertType.CONFIRMATION, "Task Added!").show();
//        }
//    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setTasks();
            setCompletTaskToHome();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCompletTaskToHome() throws SQLException {

        ArrayList<TaskObject> completeArrayList = new ArrayList<>();
        Connection connection =DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("select * from completeTask");


        while (rst.next()){
            completeArrayList.add(
                    new TaskObject(
                            rst.getString(2),
                            rst.getString(3),
                            rst.getString(4)
                    )
            );
        }

        VBox vbox = new VBox();
        for (TaskObject task : completeArrayList) {
            CheckBox checkBox = new CheckBox(task.getTaskName().get());
            checkBox.selectedProperty().bindBidirectional(task.completedProperty());
//            Label description = new Label(task.getDescription().get());
//            Label dueDate = new Label("" + task.getDueDate());

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    vbox.getChildren().remove(checkBox);
                    vBoxToDoTask.getChildren().add(checkBox);
                    scrollComplete.setContent(vbox);
                    pane.setContent(vBoxToDoTask);

                    try {

                        undoSetCompleteTask(task);
                        reStoreToDoTask(task);

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            vbox.getChildren().add(checkBox);

        }
        scrollComplete.setContent(vbox);
    }


    public void btnAddTaskOnAction(ActionEvent actionEvent) throws IOException, SQLException {

        setTasks();
        setCompletTaskToHome();

        Stage stage =new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/todo_app.fxml"))));
        stage.show();
    }

}
