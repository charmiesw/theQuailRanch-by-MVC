package lk.ijse.theQuailRanch.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.dto.EmployeeDto;
import lk.ijse.theQuailRanch.dto.tm.EmployeeTm;
import lk.ijse.theQuailRanch.model.EmployeeModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class EmployeeFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colTel;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<EmployeeTm> tblEmployee;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtTel;

    private EmployeeModel employeeModel = new EmployeeModel();

    public void initialize() {
        txtName.requestFocus();
        setCellValueFactory();
        loadAllEmployees();
        generateNextEmployeeId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("Tel"));
    }

    private void loadAllEmployees() {
        ObservableList<EmployeeTm> obList = FXCollections.observableArrayList();

        try {
            List<EmployeeDto> dtoList = employeeModel.getAllEmployees();

            for(EmployeeDto dto : dtoList) {
                obList.add(
                        new EmployeeTm (
                                dto.getId(),
                                dto.getName(),
                                dto.getTel()
                        )
                );
            }

            tblEmployee.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextEmployeeId() {
        try {
            String id = employeeModel.generateNextEmployeeId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateEmployee(String id, String name, String tel) {
        boolean isEmployeeIdValidated = Pattern.matches("[E][0-9]{3,}", id);
        if (!isEmployeeIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Employee Id!!").show();
            return false;
        }

        boolean isEmployeeNameValidated = Pattern.matches("[A-Za-z]{3,}", name);
        if (!isEmployeeNameValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name!!").show();
            return false;
        }

        boolean isEmployeeTelValidated = Pattern.matches("[0-9]{10}", tel);
        if (!isEmployeeTelValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Number!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();
        String tel = txtTel.getText();

        boolean isEmployeeValidated = validateEmployee(id, name, tel);

        if (isEmployeeValidated) {
            try {
                boolean isSaved = employeeModel.saveEmployee(new EmployeeDto(id, name, tel));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Employee Registered Successfully!").show();
                    clearFields();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();
        String tel = txtTel.getText();

        boolean isEmployeeValidated = validateEmployee(id, name, tel);

        if (isEmployeeValidated) {
            try {
                boolean isUpdated = employeeModel.updateEmployee(new EmployeeDto(id, name, tel));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Employee Details Updated!").show();
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
            boolean isDeleted = employeeModel.deleteEmployee(id);

            if(isDeleted) {
                tblEmployee.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Employee Deleted!").show();
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
        txtName.setText("");
        txtTel.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        txtName.requestFocus();
        String id = txtId.getText();

        try {
            EmployeeDto dto = employeeModel.searchEmployee(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Employee Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(EmployeeDto dto) {
        txtId.setText(dto.getId());
        txtName.setText(dto.getName());
        txtTel.setText(dto.getTel());
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/workspaceWindow.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Workspace");
        stage.centerOnScreen();
    }

    @FXML
    void txtNameOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        txtTel.requestFocus();
    }

    @FXML
    void txtTelOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        btnSaveOnAction(actionEvent);
    }
}
