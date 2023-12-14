package lk.ijse.theQuailRanch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.dto.UserDto;
import lk.ijse.theQuailRanch.model.UserModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SignupFormController {

    @FXML
    private Label lblUserId;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtTel;
    @FXML
    private TextField txtUsername;
    private UserModel userModel = new UserModel();

    public void initialize() {
        txtUsername.requestFocus();
        generateNextUserId();
    }

    private void generateNextUserId() {
        try {
            String id = userModel.generateNextUserId();
            lblUserId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSignupOnAction(ActionEvent actionEvent) {
        String id = lblUserId.getText();
        String name = txtUsername.getText();
        String tel = txtTel.getText();
        String password = txtPassword.getText();

        boolean isUserValidated = validateUser(name, tel);

        if (isUserValidated) {
            try {
                boolean isSaved = userModel.saveUser(new UserDto(id, name, password, tel));

                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION,"Signup Successfully!").show();
                    clearFields();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    private boolean validateUser(String username, String tel) {
        boolean isUsernameValidated = Pattern.matches("[A-Za-z]{3,}", username);
        if (!isUsernameValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid User Name!!").show();
            return false;
        }

        boolean isUserTelValidated = Pattern.matches("[0-9]{10}", tel);
        if (!isUserTelValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Number!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void hyperLoginOnAction(ActionEvent actionEvent) throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/loginForm.fxml"));

        Scene scene = new Scene(rootNode);

        root.getChildren().clear();
        Stage primaryStage = (Stage) root.getScene().getWindow();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
    }

    void clearFields() {
        lblUserId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtTel.setText("");
    }

    @FXML
    void txtPasswordOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        btnSignupOnAction(actionEvent);
    }

    @FXML
    void txtUsernameOnAction(ActionEvent actionEvent) {
        txtTel.requestFocus();
    }

    @FXML
    void txtTelOnAction(ActionEvent actionEvent) {
        txtPassword.requestFocus();
    }
}
