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
import lk.ijse.theQuailRanch.dto.SellStockDto;
import lk.ijse.theQuailRanch.dto.tm.SellStockTm;
import lk.ijse.theQuailRanch.model.SellStockModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class SellStockFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colQuantity;

    @FXML
    private TableColumn<?, ?> colUnitPrice;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<SellStockTm> tblSellStock;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtCategory;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtUnitPrice;

    private SellStockModel sellStockModel = new SellStockModel();

    public void initialize() {
        txtCategory.requestFocus();
        setCellValueFactory();
        loadAllSellStocks();
        generateNextSellStockId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("UnitPrice"));
    }

    private void loadAllSellStocks() {
        ObservableList<SellStockTm> obList = FXCollections.observableArrayList();

        try {
            List<SellStockDto> dtoList = sellStockModel.getAllSellStocks();

            for(SellStockDto dto : dtoList) {
                obList.add(
                        new SellStockTm (
                                dto.getId(),
                                dto.getCategory(),
                                dto.getQuantity(),
                                dto.getUnitPrice()
                        )
                );
            }

            tblSellStock.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextSellStockId() {
        try {
            String id = sellStockModel.generateNextSellStockId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateSellStock(String id, String category) {
        boolean isSellStockIdValidated = Pattern.matches("[Q][0-9]{3,}", id);
        if (!isSellStockIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Sell-stock Id!!").show();
            return false;
        }

        boolean isSellStockCategoryValidated = Pattern.matches("[A-Za-z]{3,}", category);
        if (!isSellStockCategoryValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Category!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        String category = txtCategory.getText();
        int quantity = Integer.parseInt(txtQuantity.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());

        boolean isSellStockValidated = validateSellStock(id, category);

        if (isSellStockValidated) {
            try {
                boolean isSaved = sellStockModel.saveSellStock(new SellStockDto(id, category, quantity, unitPrice));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Sell-stock Added Successfully!").show();
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
        String category = txtCategory.getText();
        int quantity = Integer.parseInt(txtQuantity.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());

        boolean isSellStockValidated = validateSellStock(id, category);

        if (isSellStockValidated) {
            try {
                boolean isUpdated = sellStockModel.updateSellStock(new SellStockDto(id, category, quantity, unitPrice));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Sell-stock Details Updated!").show();
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
            boolean isDeleted = sellStockModel.deleteSellStock(id);

            if(isDeleted) {
                tblSellStock.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Sell-stock Deleted!").show();
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
        txtCategory.setText("");
        txtQuantity.setText("");
        txtUnitPrice.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        txtCategory.requestFocus();
        String id = txtId.getText();

        try {
            SellStockDto dto = sellStockModel.searchSellStock(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Sell-stock Details Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(SellStockDto dto) {
        txtId.setText(dto.getId());
        txtCategory.setText(dto.getCategory());
        txtQuantity.setText(String.valueOf(dto.getQuantity()));
        txtUnitPrice.setText(String.valueOf(dto.getUnitPrice()));
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
    void txtCategoryOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        txtQuantity.requestFocus();
    }

    @FXML
    void txtQuantityOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        txtUnitPrice.requestFocus();
    }

    @FXML
    void txtUnitPriceOnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }
}
