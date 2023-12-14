package lk.ijse.theQuailRanch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.model.UserModel;

import java.io.IOException;
import java.sql.SQLException;

public class LoginFormController {

    @FXML
    private AnchorPane root;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;
    
    private UserModel userModel = new UserModel();
    public static String userId;

    @FXML
    void btnLoginOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            boolean isIncluded = userModel.searchUser(username, password);

            if (!isIncluded) {
                new Alert(Alert.AlertType.WARNING,"Invalid Username or Password!").show();
            } else {
               userId = username;
               navigateToMainForm();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void navigateToMainForm() throws SQLException, IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/dashboard.fxml"));
        Scene scene = new Scene(rootNode);

        root.getChildren().clear();
        Stage primaryStage = (Stage) root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("The Quail Ranch");
    }

    @FXML
    void hyperForgotPasswordOnAction(ActionEvent actionEvent) {
    }

    @FXML
    void hyperCreateNewAccountOnAction(ActionEvent actionEvent) throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/signup.fxml"));

        Scene scene = new Scene(rootNode);

        root.getChildren().clear();
        Stage primaryStage = (Stage) root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Signup Page");
    }

    @FXML
    void txtUsernameOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        txtPassword.requestFocus();
    }

    @FXML
    void txtPasswordOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        btnLoginOnAction(actionEvent);
    }
}
