package controller;

import DB.DBConnection;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import java.sql.*;


public class addTaskFormController {

    public DatePicker txtDate;
    public Label lblTaskID;
    public TextArea txtDescription;
    public TextField txtTitle;
    public JFXButton btn;

    public void initialize() throws SQLException {
        try {
            setId();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error initializing task ID").show();
        }
    }

    public void btnAddTaskOnAction(ActionEvent actionEvent) throws SQLException {
        setId();

        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement pst = connection.prepareStatement("insert into addTask (taskTitle,taskDescription,taskDate)values(?,?,?)");

        pst.setString(1, txtTitle.getText());
        pst.setString(2, txtDescription.getText());
        pst.setString(3, txtDate.getValue() != null ? txtDate.getValue().toString() : null);


        if (pst.executeUpdate() > 0) {
            new Alert(Alert.AlertType.CONFIRMATION, "Added!").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "NOT Added!").show();
        }
    }
    public void setId() throws SQLException {
        Connection connection2 = DBConnection.getInstance().getConnection();

        Statement stm = connection2.createStatement();
        ResultSet rst = stm.executeQuery("select MAX(taskID) FROM addTask");

        int nextId = 1;
        if (rst.next()) {
            nextId = rst.getInt(1) + 1;
        }

        lblTaskID.setText(String.format("%02d", nextId));
    }
}
