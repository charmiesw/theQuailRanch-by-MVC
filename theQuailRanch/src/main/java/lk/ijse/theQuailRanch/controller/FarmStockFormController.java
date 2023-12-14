package lk.ijse.theQuailRanch.controller;

import com.jfoenix.controls.JFXComboBox;
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
import lk.ijse.theQuailRanch.dto.FarmStockDto;
import lk.ijse.theQuailRanch.dto.SupplierDto;
import lk.ijse.theQuailRanch.dto.tm.FarmStockTm;
import lk.ijse.theQuailRanch.model.FarmStockModel;
import lk.ijse.theQuailRanch.model.SupplierModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class FarmStockFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colSupId;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<FarmStockTm> tblFarmStock;

    @FXML
    private TextField txtFarmId;

    @FXML
    private JFXComboBox<String> cmbSupId;

    @FXML
    private Label lblSupName;

    @FXML
    private TextField txtCategory;

    @FXML
    private TextField txtQuantity;

    private FarmStockModel farmStockModel = new FarmStockModel();

    private SupplierModel supplierModel = new SupplierModel();

    public void initialize() {
        txtCategory.requestFocus();
        setCellValueFactory();
        loadAllFarmStocks();
        loadAllSuppliers();
        generateNextFarmStockId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colSupId.setCellValueFactory(new PropertyValueFactory<>("SupId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
    }

    private void loadAllFarmStocks() {
        ObservableList<FarmStockTm> obList = FXCollections.observableArrayList();

        try {
            List<FarmStockDto> dtoList = farmStockModel.getAllFarmStocks();

            for(FarmStockDto dto : dtoList) {
                obList.add(
                        new FarmStockTm (
                                dto.getId(),
                                dto.getSupId(),
                                dto.getCategory(),
                                dto.getQuantity()
                        )
                );
            }

            tblFarmStock.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAllSuppliers() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<SupplierDto> list = supplierModel.loadAllSuppliers();

            for (SupplierDto dto : list) {
                obList.add(dto.getId());
            }

            cmbSupId.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextFarmStockId() {
        try {
            String id = farmStockModel.generateNextFarmStockId();
            txtFarmId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateFarmStock(String id, String category) {
        boolean isFarmStockIdValidated = Pattern.matches("[F][0-9]{3,}", id);
        if (!isFarmStockIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Farm-stock Id!!").show();
            return false;
        }

        boolean isFarmStockCategoryValidated = Pattern.matches("[A-Za-z]{3,}", category);
        if (!isFarmStockCategoryValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Category!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String farmId = txtFarmId.getText();
        String supId = cmbSupId.getValue();
        String category = txtCategory.getText();
        String quantity = txtQuantity.getText();

        boolean isFarmStockValidated = validateFarmStock(farmId, category);

        if (isFarmStockValidated) {
            try {
                boolean isSaved = farmStockModel.saveFarmStock(new FarmStockDto(farmId, supId, category, quantity));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Farm-stock Added Successfully!").show();
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
        String farmId = txtFarmId.getText();
        String supId = cmbSupId.getValue();
        String category = txtCategory.getText();
        String quantity = txtQuantity.getText();

        boolean isFarmStockValidated = validateFarmStock(farmId, category);

        if (isFarmStockValidated) {
            try {
                boolean isUpdated = farmStockModel.updateFarmStock(new FarmStockDto(farmId, supId, category, quantity));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Farm-stock Details Updated!").show();
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
        String id = txtFarmId.getText();

        try {
            boolean isDeleted = farmStockModel.deleteFarmStock(id);

            if(isDeleted) {
                tblFarmStock.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Farm-stock Deleted!").show();
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
        txtFarmId.setText("");
        cmbSupId.setValue("");
        lblSupName.setText("");
        txtCategory.setText("");
        txtQuantity.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        txtCategory.requestFocus();
        String id = txtFarmId.getText();

        try {
            FarmStockDto dto = farmStockModel.searchFarmStock(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Farm-stock Details Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(FarmStockDto dto) {
        txtFarmId.setText(dto.getId());
        cmbSupId.setValue(dto.getSupId());
        txtCategory.setText(dto.getCategory());
        txtQuantity.setText(dto.getQuantity());
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/stockWindow.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Stock Management");
        stage.centerOnScreen();
    }

    @FXML
    void cmbSupIdOnAction(ActionEvent actionEvent) throws SQLException {
        String supId = cmbSupId.getValue();
        SupplierDto dto = supplierModel.searchSupplier(supId);

        lblSupName.setText(dto.getName());
        txtCategory.requestFocus();
    }

    @FXML
    void txtCategoryOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        txtQuantity.requestFocus();
    }

    @FXML
    void txtQuantityOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        btnSaveOnAction(actionEvent);
    }
}
