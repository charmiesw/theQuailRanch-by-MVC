package lk.ijse.theQuailRanch.controller;


import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.EmployeeDto;
import lk.ijse.theQuailRanch.dto.SalaryDto;
import lk.ijse.theQuailRanch.model.EmployeeModel;
import lk.ijse.theQuailRanch.model.SalaryModel;
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

public class SalaryFormController {
    @FXML
    private Label lblEmployeeName;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtSalId;

    @FXML
    private JFXComboBox<String> cmbEmployeeId;

    @FXML
    private TextField txtAmount;

    @FXML
    private DatePicker txtDate;

    private SalaryModel salaryModel = new SalaryModel();

    private EmployeeModel employeeModel = new EmployeeModel();

    public void initialize() {
        txtAmount.requestFocus();
        loadAllEmployees();
        generateNextSalaryId();
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

    private void generateNextSalaryId() {
        try {
            String id = salaryModel.generateNextSalaryId();
            txtSalId.setText(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateSalary(String id) {
        boolean isSalaryIdValidated = Pattern.matches("[Z][0-9]{3,}", id);
            if (!isSalaryIdValidated) {
            new Alert(Alert.AlertType.ERROR, "Invalid Salary Id!!").show();
            return false;
        }
            return true;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String salId = txtSalId.getText();
        String empId = cmbEmployeeId.getValue();
        String amount = txtAmount.getText();
        Date date = Date.valueOf(txtDate.getValue());

        boolean isSalaryValidated = validateSalary(salId);

        if (isSalaryValidated) {
            try {
                boolean isSaved = salaryModel.saveSalary(new SalaryDto(salId, empId, amount, date));
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Salary Details Added Successfully!").show();
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
        String salId = txtSalId.getText();
        String empId = cmbEmployeeId.getValue();
        String amount = txtAmount.getText();
        Date date = Date.valueOf(txtDate.getValue());

        boolean isSalaryValidated = validateSalary(salId);

        if (isSalaryValidated) {
            try {
                boolean isUpdated = salaryModel.updateSalary(new SalaryDto(salId, empId, amount, date));
                if(isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Salary Details Updated!").show();
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
        String salId = txtSalId.getText();

        try {
            boolean isDeleted = salaryModel.deleteSalaryBySalId(salId);

            if(isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "Salary Details Deleted!").show();
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
        txtSalId.setText("");
        txtAmount.setText("");
        cmbEmployeeId.setValue("");
        txtDate.setValue(null);
        lblEmployeeName.setText("");
    }

    @FXML
    void txtSearchOnAction(ActionEvent event) {
        cmbEmployeeId.requestFocus();
        String salId = txtSalId.getText();

        try {
            SalaryDto dto = salaryModel.searchSalary(salId);

            if(dto != null) {
                fillFields(dto);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Salary Details Not Found!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private void fillFields(SalaryDto dto) {
        txtSalId.setText(dto.getSalId());
        cmbEmployeeId.setValue(dto.getEmpId());
        txtAmount.setText(dto.getAmount());
        txtDate.setValue(dto.getPaidDate().toLocalDate());
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
    void btnReportOnAction(ActionEvent actionEvent) throws SQLException {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load("/home/charmie/Desktop/AviaryPulse/CODE/LAYERED/The Quail Ranch/theQuailRanch/src/main/resources/report/TQR_SalaryReport.jrxml");
            JRDesignQuery query = new JRDesignQuery();
            query.setText("SELECT s.sal_id, s.amount, s.paid_date, e.emp_id, e.name FROM Salary s JOIN Employee e ON s.emp_id = e.emp_id");
            jasperDesign.setQuery(query);

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DbConnection.getInstance().getConnection());

            JFrame frame = new JFrame("Salary Details Reports");
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
    public void txtDateOnAction(ActionEvent actionEvent) {
        txtAmount.requestFocus();
    }

    @FXML
    public void txtAmountOnAction(ActionEvent actionEvent) {
        btnSaveOnAction(actionEvent);
    }
}
