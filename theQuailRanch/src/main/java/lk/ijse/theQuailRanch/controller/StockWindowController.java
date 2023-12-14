package lk.ijse.theQuailRanch.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.model.FarmStockModel;
import lk.ijse.theQuailRanch.model.SellStockModel;

import java.io.IOException;
import java.sql.SQLException;

public class StockWindowController {
    @FXML
    private Label lblSellStockCount;
    
    @FXML
    private Label lblFarmStockCount;
    
    @FXML
    private AnchorPane root;
    
    public void initialize() {
        setCounts();
    }

    private void setCounts() {
        FarmStockModel farmStockModel = new FarmStockModel();
        SellStockModel sellStockModel = new SellStockModel();

        try {
            int currentFarmStockCount = farmStockModel.getFarmStockCount();
            int currentSellStockCount = sellStockModel.getSellStockCount();

            lblFarmStockCount.setText(String.valueOf(currentFarmStockCount));
            lblSellStockCount.setText(String.valueOf(currentSellStockCount));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnRegisterOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/registrationWindow.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Registrations");
        stage.centerOnScreen();
    }

    @FXML
    void btnStockOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/stockWindow.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Stock Management");
        stage.centerOnScreen();
    }

    @FXML
    void btnOrderOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/orderWindow.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Order Management");
        stage.centerOnScreen();
    }

    @FXML
    void btnWSOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/workspaceWindow.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.centerOnScreen();
    }

    @FXML
    void btnUserOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/userWindow.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("User Management");
        stage.centerOnScreen();
    }

    @FXML
    void btnBackOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("The Quail Ranch");
        stage.centerOnScreen();
    }

    @FXML
    void btnFarmStockOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/farmStockForm.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Farm Stock Management");
        stage.centerOnScreen();
    }

    @FXML
    void btnSellStockOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/sellStockForm.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Sell Stock Management");
        stage.centerOnScreen();
    }
}
