package lk.ijse.theQuailRanch.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.dto.CustomerDto;
import lk.ijse.theQuailRanch.dto.PlaceOrderDto;
import lk.ijse.theQuailRanch.dto.SellStockDto;
import lk.ijse.theQuailRanch.dto.tm.PlaceOrderCartTm;
import lk.ijse.theQuailRanch.model.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PlaceOrderFormController {
    private final CustomerModel customerModel = new CustomerModel();
    private final SellStockModel sellStockModel = new SellStockModel();
    private final OrderModel orderModel = new OrderModel();
    private final ObservableList<PlaceOrderCartTm> obList = FXCollections.observableArrayList();

    @FXML
    private JFXComboBox<String> cmbCustomerId;
    @FXML
    private JFXComboBox<String> cmbSellStockId;
    @FXML
    private TableColumn<?, ?> colAction;
    @FXML
    private TableColumn<?, ?> colCategory;
    @FXML
    private TableColumn<?, ?> colSellStockId;
    @FXML
    private TableColumn<?, ?> colQuantity;
    @FXML
    private TableColumn<?, ?> colTotal;
    @FXML
    private TableColumn<?, ?> colUnitPrice;
    @FXML
    private Label lblCustomerName;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblOrderId;
    @FXML
    private Label lblQtyOnHand;
    @FXML
    private Label lblUnitPrice;
    @FXML
    private AnchorPane root;
    @FXML
    private TableView<PlaceOrderCartTm> tblPlaceOrderCart;
    @FXML
    private TextField txtQuantity;
    @FXML
    private Label lblTotal;

    private final OrderDetailModel orderDetailModel = new OrderDetailModel();

    private final PlaceOrderModel placeOrderModel = new PlaceOrderModel();

    public void initialize() {
        setCellValueFactory();
        generateNextOrderId();
        setDate();
        loadCustomerIds();
        loadSellStockIds();
    }

    private void setCellValueFactory() {
        colSellStockId.setCellValueFactory(new PropertyValueFactory<>("sellStockId"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }

    private void generateNextOrderId() {
        try {
            String orderId = orderModel.generateNextOrderId();
            lblOrderId.setText(orderId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSellStockIds() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<SellStockDto> sellStockList = SellStockModel.loadAllSellStocks();

            for (SellStockDto sellStockDto : sellStockList) {
                obList.add(sellStockDto.getId());
            }

            cmbSellStockId.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerIds() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<CustomerDto> cusList = customerModel.loadAllCustomers();

            for (CustomerDto dto : cusList) {
                obList.add(dto.getId());
            }
            cmbCustomerId.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDate() {
        String date = String.valueOf(LocalDate.now());
        lblDate.setText(date);
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        String id = cmbSellStockId.getValue();
        String category = lblCategory.getText();
        int qty = Integer.parseInt(txtQuantity.getText());
        double unitPrice = Double.parseDouble(lblUnitPrice.getText());
        double total = qty * unitPrice;
        Button btn = new Button("Remove");
        btn.setCursor(Cursor.HAND);

        btn.setOnAction((e) -> {
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

            if(type.orElse(no) == yes) {
                int selectedIndex = tblPlaceOrderCart.getSelectionModel().getSelectedIndex();
                String sellStockId = (String) colSellStockId.getCellData(selectedIndex);

                deleteOrderDetails(sellStockId);

                obList.remove(selectedIndex);
                tblPlaceOrderCart.refresh();
            }
        });

        for (int i = 0; i < tblPlaceOrderCart.getItems().size(); i++) {
            if (id.equals(colSellStockId.getCellData(i))) {
                qty += (int) colQuantity.getCellData(i);
                total = qty * unitPrice;

                obList.get(i).setQuantity(qty);
                obList.get(i).setTotal(total);

                tblPlaceOrderCart.refresh();
                calculateNetTotal();
                return;
            }
        }

        obList.add(new PlaceOrderCartTm(
                id,
                category,
                qty,
                unitPrice,
                total,
                btn
        ));

        tblPlaceOrderCart.setItems(obList);
        calculateNetTotal();
        txtQuantity.clear();
    }

    private void deleteOrderDetails(String sellStockId) {
        try {
            boolean isDeleted = orderDetailModel.deleteOrderDetails(sellStockId);
            if(isDeleted)
                new Alert(Alert.AlertType.CONFIRMATION, "Order Details Deleted!").show();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void calculateNetTotal() {
        double total = 0;
        for (int i = 0; i < tblPlaceOrderCart.getItems().size(); i++) {
            total += (double) colTotal.getCellData(i);
        }

        lblTotal.setText(String.valueOf(total));
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) throws JRException {
        String orderId = lblOrderId.getText();
        String cusId = cmbCustomerId.getValue();
        LocalDate date = LocalDate.parse(lblDate.getText());

        List<PlaceOrderCartTm> tmList = new ArrayList<>();

        for (PlaceOrderCartTm cartTm : obList) {
            tmList.add(cartTm);
        }

        var placeOrderDto = new PlaceOrderDto(
                orderId,
                cusId,
                date,
                tmList
        );

        try {
            boolean isSuccess = placeOrderModel.placeOrder(placeOrderDto);
            if(isSuccess) {
                new Alert(Alert.AlertType.CONFIRMATION, "Order Completed!").show();
                clearFields();
                initialize();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    void clearFields() {
        lblOrderId.setText("");
        lblDate.setText("");
        cmbCustomerId.setValue("");
        lblCustomerName.setText("");
        cmbSellStockId.setValue("");
        lblCategory.setText("");
        lblQtyOnHand.setText("");
        lblUnitPrice.setText("");
        txtQuantity.setText("");
        lblTotal.setText("");
        tblPlaceOrderCart.setItems(null);
    }


    @FXML
    void cmbSellStockIdOnAction(ActionEvent event) {
        String id = cmbSellStockId.getValue();

        txtQuantity.requestFocus();

        try {
            SellStockDto dto = sellStockModel.searchSellStock(id);

            lblCategory.setText(dto.getCategory());
            lblUnitPrice.setText(String.valueOf(dto.getUnitPrice()));
            lblQtyOnHand.setText(String.valueOf(dto.getQuantity()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void cmbCustomerIdOnAction(ActionEvent event) throws SQLException {
        String id = cmbCustomerId.getValue();
        CustomerDto dto = customerModel.searchCustomer(id);

        lblCustomerName.setText(dto.getName());
    }

    @FXML
    void txtQuantityOnAction(ActionEvent event) {
        btnAddToCartOnAction(event);
    }

    @FXML
    void btnAddNewCustomerOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/customerForm.fxml"));
        Scene scene = new Scene(anchorPane);

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Customer Management");
        stage.centerOnScreen();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        Parent anchorPane = FXMLLoader.load(getClass().getResource("/view/orderWindow.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Order Management");
        stage.centerOnScreen();
    }
}
