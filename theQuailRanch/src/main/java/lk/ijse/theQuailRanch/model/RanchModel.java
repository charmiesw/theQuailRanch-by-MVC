package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.RanchDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RanchModel {

    public int getRanchCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(ranch_id) FROM Quails";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentRanchCount = 0;
        int newRanchCount = 0;

        if(resultSet.next()) {
            currentRanchCount = resultSet.getInt(1);
            newRanchCount = currentRanchCount;
        }
        return newRanchCount;
    }

    public List<RanchDto> getAllRanches() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Quails";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<RanchDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String ranch_id = resultSet.getString(1);
            Date date = resultSet.getDate(2);
            String category = resultSet.getString(3);
            String amount_of_birds = resultSet.getString(4);


            var dto = new RanchDto(ranch_id, date, category, amount_of_birds);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveRanch(RanchDto ranchDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Quails VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, ranchDto.getId());
        pstm.setDate(2, ranchDto.getDate());
        pstm.setString(3, ranchDto.getCategory());
        pstm.setString(4, ranchDto.getAmountOfBirds());

        return pstm.executeUpdate() > 0;
    }

    public RanchDto searchRanch(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Quails WHERE ranch_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        RanchDto dto = null;

        if(resultSet.next()) {
            String ranch_id = resultSet.getString(1);
            Date date = resultSet.getDate(2);
            String category = resultSet.getString(3);
            String amount_of_birds = resultSet.getString(4);

            dto = new RanchDto(ranch_id, date, category, amount_of_birds);
        }

        return dto;
    }

    public boolean updateRanch(RanchDto ranchDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Quails SET date = ?, category = ?, amount_of_birds = ? WHERE ranch_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(4, ranchDto.getId());
        pstm.setDate(1, ranchDto.getDate());
        pstm.setString(2, ranchDto.getCategory());
        pstm.setString(3, ranchDto.getAmountOfBirds());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteRanch(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Quails WHERE ranch_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextRanchId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT ranch_id FROM Quails ORDER BY ranch_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentRanchId = null;

        if(resultSet.next()) {
            currentRanchId = resultSet.getString(1);
            return splitRanchId(currentRanchId);
        }
        return splitRanchId(null);
    }

    private String splitRanchId(String currentRanchId) {
        if(currentRanchId != null) {
            String[] split = currentRanchId.split("R");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "R00" + id;
        }
        return "R001";
    }
}
