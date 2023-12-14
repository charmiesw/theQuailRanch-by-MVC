package lk.ijse.theQuailRanch.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.dto.SupplierDto;
import lk.ijse.theQuailRanch.dto.tm.SupplierTm;
import lk.ijse.theQuailRanch.model.SupplierModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class SupplierFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<SupplierTm> tblSupplier;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    private SupplierModel supplierModel = new SupplierModel();

    public void initialize() {
        txtName.requestFocus();
        setCellValueFactory();
        loadAllSuppliers();
        generateNextSupplierId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("Name"));
    }

    private void loadAllSuppliers() {
        ObservableList<SupplierTm> obList = FXCollections.observableArrayList();

        try {
            List<SupplierDto> dtoList = supplierModel.getAllSuppliers();

            for(SupplierDto dto : dtoList) {
                obList.add(
                        new SupplierTm (
                                dto.getId(),
                                dto.getName()
                        )
                );
            }

            tblSupplier.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextSupplierId() {
        try {
            String id = supplierModel.generateNextSupplierId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateSupplier(String id, String name) {
        boolean isSupplierIdValidated = Pattern.matches("[S][0-9]{3,}", id);
        if (!isSupplierIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Supplier Id!!").show();
            return false;
        }

        boolean isSupplierNameValidated = Pattern.matches("[A-Za-z]{3,}", name);
        if (!isSupplierNameValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Name!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String name = txtName.getText();

        boolean isSupplierValidated = validateSupplier(id, name);

        if (isSupplierValidated) {
            try {
                boolean isSaved = supplierModel.saveSupplier(new SupplierDto(id, name));

                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION,"Supplier Added Successfully!").show();
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
        String name = txtName.getText();

        boolean isSupplierValidated = validateSupplier(id, name);

        if (isSupplierValidated) {
            try {
                boolean isUpdated = supplierModel.updateSupplier(new SupplierDto(id, name));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Supplier Updated!").show();
                    clearFields();
                    initialize();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        txtName.requestFocus();
        String id = txtId.getText();

        try {
            SupplierDto dto = supplierModel.searchSupplier(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Supplier Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(SupplierDto dto) {
        txtId.setText(dto.getId());
        txtName.setText(dto.getName());
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        try {
            boolean isDeleted = supplierModel.deleteSupplier(id);

            if(isDeleted) {
                tblSupplier.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Supplier Deleted!").show();
                initialize();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    void clearFields() {
        txtId.setText("");
        txtName.setText("");
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
    public void txtNameOnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }
}
