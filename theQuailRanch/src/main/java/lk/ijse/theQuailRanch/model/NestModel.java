package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.NestDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NestModel {

    public int getNestCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(nest_id) FROM Nests";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentNestCount = 0;
        int newNestCount = 0;

        if(resultSet.next()) {
            currentNestCount = resultSet.getInt(1);
            newNestCount = currentNestCount;
        }
        return newNestCount;
    }

    public List<NestDto> getAllNests() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Nests";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<NestDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String nest_id = resultSet.getString(1);
            String category = resultSet.getString(2);
            String amount_of_birds = resultSet.getString(3);

            var dto = new NestDto(nest_id, category, amount_of_birds);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveNest(NestDto nestDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Nests VALUES(?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, nestDto.getId());
        pstm.setString(2, nestDto.getCategory());
        pstm.setString(3, nestDto.getAmountOfBirds());

        return pstm.executeUpdate() > 0;
    }

    public NestDto searchNest(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Nests WHERE nest_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        NestDto dto = null;

        if(resultSet.next()) {
            String nest_id = resultSet.getString(1);
            String category = resultSet.getString(2);
            String amount_of_birds = resultSet.getString(3);

            dto = new NestDto(nest_id, category, amount_of_birds);
        }

        return dto;
    }

    public boolean updateNest(NestDto nestDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Nests SET category = ?, amount_of_birds = ? WHERE nest_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, nestDto.getCategory());
        pstm.setString(2, nestDto.getAmountOfBirds());
        pstm.setString(3, nestDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteNest(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Nests WHERE nest_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextNestId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT nest_id FROM Nests ORDER BY nest_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentNestId = null;

        if(resultSet.next()) {
            currentNestId = resultSet.getString(1);
            return splitNestId(currentNestId);
        }
        return splitNestId(null);
    }

    private String splitNestId(String currentNestId) {
        if(currentNestId != null) {
            String[] split = currentNestId.split("N");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "N00" + id;
        }
        return "N001";
    }

    public List<NestDto> loadAllNests() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Nests";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        List<NestDto> nestList = new ArrayList<>();

        while (resultSet.next()) {
            nestList.add(new NestDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            ));
        }
        return nestList;
    }
}
