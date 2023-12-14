package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.MaintenanceDto;

import java.sql.*;

public class MaintenanceModel {
    public boolean saveTT(MaintenanceDto maintenanceDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Cleaning_tt VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, maintenanceDto.getTtId());
        pstm.setString(2, maintenanceDto.getEmpId());
        pstm.setString(3, maintenanceDto.getNestId());
        pstm.setDate(4, maintenanceDto.getDate());

        return pstm.executeUpdate() > 0;
    }

    public MaintenanceDto searchTT(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Cleaning_tt WHERE tt_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        MaintenanceDto dto = null;

        if(resultSet.next()) {
            String tt_id = resultSet.getString(1);
            String emp_id = resultSet.getString(2);
            String nest_id = resultSet.getString(3);
            Date date = resultSet.getDate(4);

            dto = new MaintenanceDto(tt_id, emp_id, nest_id, date);
        }

        return dto;
    }

    public boolean updateTT(MaintenanceDto maintenanceDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Cleaning_tt SET emp_id = ?, nest_id = ?, date = ? WHERE tt_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, maintenanceDto.getEmpId());
        pstm.setString(2, maintenanceDto.getNestId());
        pstm.setDate(3, maintenanceDto.getDate());
        pstm.setString(4, maintenanceDto.getTtId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteTT(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Cleaning_tt WHERE tt_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextTtId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT tt_id FROM Cleaning_tt ORDER BY tt_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentTtId = null;

        if(resultSet.next()) {
            currentTtId = resultSet.getString(1);
            return splitTtId(currentTtId);
        }
        return splitTtId(null);
    }

    private String splitTtId(String currentTtId) {
        if(currentTtId != null) {
            String[] split = currentTtId.split("T");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "T00" + id;
        }
        return "T001";
    }
}
