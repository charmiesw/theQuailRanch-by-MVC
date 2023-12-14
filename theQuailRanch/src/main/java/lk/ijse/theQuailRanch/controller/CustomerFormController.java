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
import lk.ijse.theQuailRanch.dto.CustomerDto;
import lk.ijse.theQuailRanch.dto.tm.CustomerTm;
import lk.ijse.theQuailRanch.model.CustomerModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CustomerFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colTel;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtTel;

    private CustomerModel customerModel = new CustomerModel();

    public void initialize() {
        txtName.requestFocus();
        setCellValueFactory();
        loadAllCustomers();
        generateNextCustomerId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("Tel"));
    }

    private void loadAllCustomers() {
        ObservableList<CustomerTm> obList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerModel.getAllCustomers();

            for(CustomerDto dto : dtoList) {
                obList.add(
                        new CustomerTm (
                                dto.getId(),
                                dto.getName(),
                                dto.getTel()
                        )
                );
            }

            tblCustomer.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextCustomerId() {
        try {
            String id = customerModel.generateNextCustomerId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateCustomer(String id, String name, String tel) {
        boolean isCustomerIdValidated = Pattern.matches("[C][0-9]{3,}", id);
        if (!isCustomerIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Customer Id!!").show();
            return false;
        }

        boolean isCustomerNameValidated = Pattern.matches("[A-Za-z]{3,}", name);
        if (!isCustomerNameValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name!!").show();
            return false;
        }

        boolean isCustomerTelValidated = Pattern.matches("[0-9]{10}", tel);
        if (!isCustomerTelValidated) {
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

        boolean isCustomerValidated = validateCustomer(id, name,  tel);

        if (isCustomerValidated) {
            try {
                boolean isSaved = customerModel.saveCustomer(new CustomerDto(id, name, tel));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Customer Registered Successfully!").show();
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

        boolean isCustomerValidated = validateCustomer(id, name, tel);

        if (isCustomerValidated) {
            try {
                boolean isUpdated = customerModel.updateCustomer(new CustomerDto(id, name, tel));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Customer Details Updated!").show();
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
            boolean isDeleted = customerModel.deleteCustomer(id);

            if(isDeleted) {
                tblCustomer.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Customer Deleted!").show();
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
            CustomerDto dto = customerModel.searchCustomer(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Customer Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(CustomerDto dto) {
        txtId.setText(dto.getId());
        txtName.setText(dto.getName());
        txtTel.setText(dto.getTel());
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/registrationWindow.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Registrations");
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
