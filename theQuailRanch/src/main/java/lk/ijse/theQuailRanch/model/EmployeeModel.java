package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.EmployeeDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeModel {

    public int getEmployeeCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(emp_id) FROM Employee";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentEmployeeCount = 0;
        int newEmployeeCount = 0;

        if(resultSet.next()) {
            currentEmployeeCount = resultSet.getInt(1);
            newEmployeeCount = currentEmployeeCount;
        }
        return newEmployeeCount;
    }

    public List<EmployeeDto> getAllEmployees() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Employee";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<EmployeeDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String emp_id = resultSet.getString(1);
            String emp_name = resultSet.getString(2);
            String emp_tel = resultSet.getString(3);

            var dto = new EmployeeDto(emp_id, emp_name, emp_tel);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveEmployee(EmployeeDto employeeDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Employee VALUES(?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, employeeDto.getId());
        pstm.setString(2, employeeDto.getName());
        pstm.setString(3, employeeDto.getTel());

        return pstm.executeUpdate() > 0;
    }

    public boolean updateEmployee(EmployeeDto employeeDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Employee SET name = ?, tel = ? WHERE emp_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, employeeDto.getName());
        pstm.setString(2, employeeDto.getTel());
        pstm.setString(3, employeeDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public EmployeeDto searchEmployee(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Employee WHERE emp_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        EmployeeDto dto = null;

        if(resultSet.next()) {
            String emp_id = resultSet.getString(1);
            String emp_name = resultSet.getString(2);
            String emp_tel = resultSet.getString(3);

            dto = new EmployeeDto(emp_id, emp_name, emp_tel);
        }

        return dto;
    }

    public boolean deleteEmployee(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Employee WHERE emp_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextEmployeeId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT emp_id FROM Employee ORDER BY emp_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentEmployeeId = null;

        if(resultSet.next()) {
            currentEmployeeId = resultSet.getString(1);
            return splitEmployeeId(currentEmployeeId);
        }
        return splitEmployeeId(null);
    }

    private String splitEmployeeId(String currentEmployeeId) {
        if(currentEmployeeId != null) {
            String[] split = currentEmployeeId.split("E");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "E00" + id;
        }
        return "E001";
    }

    public List<EmployeeDto> loadAllEmployees() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Employee";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        List<EmployeeDto> empList = new ArrayList<>();

        while (resultSet.next()) {
            empList.add(new EmployeeDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            ));
        }
        return empList;
    }
}
