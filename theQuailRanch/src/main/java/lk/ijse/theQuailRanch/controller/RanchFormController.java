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
import lk.ijse.theQuailRanch.dto.RanchDto;
import lk.ijse.theQuailRanch.dto.tm.RanchTm;
import lk.ijse.theQuailRanch.model.RanchModel;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class RanchFormController {
    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colCategory;

    @FXML
    private TableColumn<?, ?> colAmountOfBirds;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<RanchTm> tblRanch;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtCategory;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextField txtAmountOfBirds;

    private RanchModel ranchModel = new RanchModel();

    public void initialize() {
        txtCategory.requestFocus();
        setCellValueFactory();
        loadAllRanches();
        generateNextRanchId();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("Date"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("Category"));
        colAmountOfBirds.setCellValueFactory(new PropertyValueFactory<>("AmountOfBirds"));
    }

    private void loadAllRanches() {
        var model = new RanchModel();

        ObservableList<RanchTm> obList = FXCollections.observableArrayList();

        try {
            List<RanchDto> dtoList = model.getAllRanches();

            for(RanchDto dto : dtoList) {
                obList.add(
                        new RanchTm (
                                dto.getId(),
                                dto.getDate(),
                                dto.getCategory(),
                                dto.getAmountOfBirds()
                        )
                );
            }

            tblRanch.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextRanchId() {
        try {
            String id = ranchModel.generateNextRanchId();
            txtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateRanch(String id) {
        boolean isEmployeeIdValidated = Pattern.matches("[R][0-9]{3,}", id);
        if (!isEmployeeIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Ranch Id!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String id = txtId.getText();
        Date date = Date.valueOf(txtDate.getValue());
        String category = txtCategory.getText();
        String amountOfBirds = txtAmountOfBirds.getText();

        boolean isRanchValidated = validateRanch(id);

        if (isRanchValidated) {
            try {
                boolean isSaved = ranchModel.saveRanch(new RanchDto(id, date, category, amountOfBirds));

                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION,"Ranch Added Successfully!").show();
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
        Date date = Date.valueOf(txtDate.getValue());
        String category = txtCategory.getText();
        String amountOfBirds = txtAmountOfBirds.getText();

        boolean isRanchValidated = validateRanch(id);

        if (isRanchValidated) {
            try {
                boolean isUpdated = ranchModel.updateRanch(new RanchDto(id, date, category, amountOfBirds));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Ranch Updated!").show();
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
        txtDate.requestFocus();
        String id = txtId.getText();

        var model = new RanchModel();

        try {
            RanchDto dto = model.searchRanch(id);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Ranch Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(RanchDto dto) {
        txtId.setText(dto.getId());
        txtDate.setValue(dto.getDate().toLocalDate());
        txtCategory.setText(dto.getCategory());
        txtAmountOfBirds.setText(dto.getAmountOfBirds());
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        var ranchModel = new RanchModel();

        try {
            boolean isDeleted = ranchModel.deleteRanch(id);

            if(isDeleted) {
                tblRanch.refresh();
                new Alert(Alert.AlertType.CONFIRMATION, "Ranch Deleted!").show();
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
        txtDate.setValue(null);
        txtCategory.setText("");
        txtAmountOfBirds.setText("");
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
    public void txtAmountOfBirdsOnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }

    @FXML
    void txtCategoryOnAction(ActionEvent actionEvent) {
        txtAmountOfBirds.requestFocus();
    }

    @FXML
    void txtDateOnAction(ActionEvent actionEvent) {
        txtCategory.requestFocus();
    }
}
