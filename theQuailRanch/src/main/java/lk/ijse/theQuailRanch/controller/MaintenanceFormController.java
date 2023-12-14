package lk.ijse.theQuailRanch.controller;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.EmployeeDto;
import lk.ijse.theQuailRanch.dto.MaintenanceDto;
import lk.ijse.theQuailRanch.dto.NestDto;
import lk.ijse.theQuailRanch.model.EmployeeModel;
import lk.ijse.theQuailRanch.model.MaintenanceModel;
import lk.ijse.theQuailRanch.model.NestModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class MaintenanceFormController {
    @FXML
    private TextField txtTtId;

    @FXML
    private Label lblEmployeeName;

    @FXML
    private DatePicker txtDate;

    @FXML
    private JFXComboBox<String> cmbNestId;

    @FXML
    private JFXComboBox<String> cmbEmployeeId;

    @FXML
    private AnchorPane root;

    private MaintenanceModel maintenanceModel = new MaintenanceModel();

    private EmployeeModel employeeModel = new EmployeeModel();

    private NestModel nestModel = new NestModel();

    public void initialize() {
        loadAllEmployees();
        loadAllNests();
        generateNextTtId();
    }

    private void loadAllNests() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<NestDto> list = nestModel.loadAllNests();

            for (NestDto dto : list) {
                obList.add(dto.getId());
            }

            cmbNestId.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAllEmployees() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<EmployeeDto> list = employeeModel.loadAllEmployees();

            for (EmployeeDto dto : list) {
                obList.add(dto.getId());
            }

            cmbEmployeeId.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateNextTtId() {
        try {
            String id = maintenanceModel.generateNextTtId();
            txtTtId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateTT(String id) {
        boolean isTtValidated = Pattern.matches("[T][0-9]{3,}", id);
        if (!isTtValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Timetable Id!!").show();
            return false;
        }
        return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String ttId = txtTtId.getText();
        String empId = cmbEmployeeId.getValue();
        String nestId = cmbNestId.getValue();
        Date date = Date.valueOf(txtDate.getValue());

        boolean isTTValidated = validateTT(ttId);

        if (isTTValidated) {
            try {
                boolean isSaved = maintenanceModel.saveTT(new MaintenanceDto(ttId, empId, nestId, date));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Maintenance Details Added Successfully!").show();
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
        String ttId = txtTtId.getText();
        String nestId = cmbNestId.getValue();
        String empId = cmbEmployeeId.getValue();
        Date date = Date.valueOf(txtDate.getValue());

        try {
            boolean isUpdated = maintenanceModel.updateTT(new MaintenanceDto(ttId, empId, nestId, date));
            if(isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "Maintenance Details Updated!").show();
                clearFields();
                initialize();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String ttId = txtTtId.getText();

        try {
            boolean isDeleted = maintenanceModel.deleteTT(ttId);

            if(isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "Maintenance Details Deleted!").show();
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
        txtTtId.setText("");
        cmbNestId.setValue("");
        cmbEmployeeId.setValue("");
        txtDate.setValue(null);
        lblEmployeeName.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        cmbNestId.requestFocus();
        String ttId = txtTtId.getText();

        try {
            MaintenanceDto dto = maintenanceModel.searchTT(ttId);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Maintenance Details Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(MaintenanceDto dto) {
        txtTtId.setText(dto.getTtId());
        cmbNestId.setValue(dto.getNestId());
        cmbEmployeeId.setValue(dto.getEmpId());
        txtDate.setValue(dto.getDate().toLocalDate());
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
    void btnReportOnAction(ActionEvent actionEvent) throws SQLException  {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("/home/charmie/IdeaProjects/theQuailRanch/src/main/resources/report/TQR_MaintenanceReport.jrxml");
            JRDesignQuery query = new JRDesignQuery();

            query.setText("SELECT c.tt_id, n.nest_id, e.emp_id, e.name, c.date FROM Nests n JOIN Cleaning_tt c ON n.nest_id = c.nest_id JOIN Employee e ON e.emp_id = c.emp_id");
            jasperDesign.setQuery(query);

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DbConnection.getInstance().getConnection());

            JFrame frame = new JFrame("Maintenance Details Reports");
            JRViewer viewer = new JRViewer(jasperPrint);

            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(viewer);
            frame.setSize(new Dimension(1440, 1024));
            frame.setVisible(true);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cmbEmployeeIdOnAction(ActionEvent actionEvent) throws SQLException {
        String empId = cmbEmployeeId.getValue();
        EmployeeDto dto = employeeModel.searchEmployee(empId);

        lblEmployeeName.setText(dto.getName());
        txtDate.requestFocus();
    }

    @FXML
    void txtDateOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        //btnSaveOnAction(actionEvent);
    }

    @FXML
    void txtNestIdOnAction(ActionEvent actionEvent) {
        cmbEmployeeId.requestFocus();
    }
}
