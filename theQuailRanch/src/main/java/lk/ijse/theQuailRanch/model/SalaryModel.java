package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.SalaryDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryModel {
    public List<SalaryDto> getAllSalaries() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Salary";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<SalaryDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String sal_id = resultSet.getString(1);
            String emp_id = resultSet.getString(2);
            String amount = resultSet.getString(3);
            Date paidDate = resultSet.getDate(4);

            var dto = new SalaryDto(sal_id, emp_id, amount, paidDate);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveSalary(SalaryDto salaryDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Salary VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, salaryDto.getSalId());
        pstm.setString(2, salaryDto.getEmpId());
        pstm.setString(3, salaryDto.getAmount());
        pstm.setDate(4, salaryDto.getPaidDate());

        return pstm.executeUpdate() > 0;
    }

    public SalaryDto searchSalary(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Salary WHERE sal_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        SalaryDto dto = null;

        if(resultSet.next()) {
            String sal_id = resultSet.getString(1);
            String emp_id = resultSet.getString(2);
            String amount = resultSet.getString(3);
            Date paidDate = resultSet.getDate(4);

            dto = new SalaryDto(sal_id, emp_id, amount, paidDate);
        }

        return dto;
    }

    public boolean updateSalary(SalaryDto salaryDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Salary SET emp_id = ?, amount = ?, paid_date = ? WHERE sal_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, salaryDto.getEmpId());
        pstm.setString(2, salaryDto.getAmount());
        pstm.setDate(3, salaryDto.getPaidDate());
        pstm.setString(4, salaryDto.getSalId());


        return pstm.executeUpdate() > 0;
    }

    public boolean deleteSalaryBySalId(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Salary WHERE sal_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextSalaryId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT sal_id FROM Salary ORDER BY sal_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentSalaryId = null;

        if(resultSet.next()) {
            currentSalaryId = resultSet.getString(1);
            return splitSalaryId(currentSalaryId);
        }
        return splitSalaryId(null);
    }

    private String splitSalaryId(String currentSalaryId) {
        if(currentSalaryId != null) {
            String[] split = currentSalaryId.split("Z");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "Z00" + id;
        }
        return "Z001";
    }
}
