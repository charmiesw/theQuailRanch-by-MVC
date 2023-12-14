package lk.ijse.theQuailRanch.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.dto.UserDto;
import lk.ijse.theQuailRanch.dto.tm.UserTm;
import lk.ijse.theQuailRanch.model.UserModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class UserFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colUsername;

    @FXML
    private TableColumn<?, ?> colPassword;

    @FXML
    private TableColumn<?, ?> colTel;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<UserTm> tblUser;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtTel;

    private UserModel userModel = new UserModel();

    public void initialize() {
        txtUsername.requestFocus();
        setCellValueFactory();
        loadAllUsers();
        generateNextUserId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("Username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("Password"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("Tel"));
    }

    private void loadAllUsers() {
        ObservableList<UserTm> obList = FXCollections.observableArrayList();

        try {
            List<UserDto> dtoList = userModel.getAllUsers();

            for(UserDto dto : dtoList) {
                obList.add(
                        new UserTm (
                                dto.getId(),
                                dto.getUsername(),
                                dto.getPassword(),
                                dto.getTel()
                        )
                );
            }

            tblUser.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextUserId() {
        try {
            String id = userModel.generateNextUserId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateUser(String id, String username, String tel) {
        boolean isUserIdValidated = Pattern.matches("[U][0-9]{3,}", id);
        if (!isUserIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid User Id!!").show();
            return false;
        }

        boolean isUserNameValidated = Pattern.matches("[A-Za-z]{3,}", username);
        if (!isUserNameValidated) {
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
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtUsername.getText();
        String tel = txtTel.getText();
        String password = txtPassword.getText();

        boolean isUserValidated = validateUser(id, name, tel);

        if (isUserValidated) {
            try {
                boolean isSaved = userModel.saveUser(new UserDto(id, name, password, tel));

                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION,"User Registered Successfully!").show();
                    clearFields();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtUsername.getText();
        String pw = txtPassword.getText();
        String tel = txtTel.getText();

        boolean isUserValidated = validateUser(id, name, tel);

        if (isUserValidated) {
            try {
                boolean isUpdated = userModel.updateUser(new UserDto(id, name, pw, tel));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "User Details Updated!").show();
                    clearFields();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        try {
            boolean isDeleted = userModel.deleteUser(id);

            if(isDeleted) {
                tblUser.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "User Deleted!").show();
                initialize();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        initialize();
    }

    void clearFields() {
        txtId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtTel.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        txtUsername.requestFocus();
        String id = txtId.getText();

        try {
            UserDto dto = userModel.searchUserById(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "User Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(UserDto dto) {
        txtId.setText(dto.getId());
        txtUsername.setText(dto.getUsername());
        txtPassword.setText(dto.getPassword());
        txtTel.setText(dto.getTel());
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/userWindow.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("User Management");
        stage.centerOnScreen();
    }

    @FXML
    public void txtTelOnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }

    @FXML
    void txtUsernameOnAction(ActionEvent actionEvent) {
        txtPassword.requestFocus();
    }

    @FXML
    void txtPasswordOnAction(ActionEvent actionEvent) {
        txtTel.requestFocus();
    }
}
