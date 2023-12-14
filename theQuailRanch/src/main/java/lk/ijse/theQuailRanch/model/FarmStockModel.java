package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.FarmStockDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FarmStockModel {
    public int getFarmStockCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(farm_stock_id) FROM Farm_stock";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentFarmStockCount = 0;
        int newFarmStockCount = 0;

        if(resultSet.next()) {
            currentFarmStockCount = resultSet.getInt(1);
            newFarmStockCount = currentFarmStockCount;
        }
        return newFarmStockCount;
    }

    public List<FarmStockDto> getAllFarmStocks() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Farm_stock";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<FarmStockDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String farm_stock_id = resultSet.getString(1);
            String sup_id = resultSet.getString(2);
            String category = resultSet.getString(3);
            String quantity = resultSet.getString(4);

            var dto = new FarmStockDto(farm_stock_id, sup_id, category, quantity);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveFarmStock(FarmStockDto farmStockDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Farm_stock VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, farmStockDto.getId());
        pstm.setString(2, farmStockDto.getSupId());
        pstm.setString(3, farmStockDto.getCategory());
        pstm.setString(4, farmStockDto.getQuantity());

        return pstm.executeUpdate() > 0;
    }

    public FarmStockDto searchFarmStock(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Farm_stock WHERE farm_stock_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        FarmStockDto dto = null;

        if(resultSet.next()) {
            String farm_stock_id = resultSet.getString(1);
            String sup_id = resultSet.getString(2);
            String category = resultSet.getString(3);
            String quantity = resultSet.getString(4);

            dto = new FarmStockDto(farm_stock_id, sup_id, category, quantity);
        }

        return dto;
    }

    public boolean updateFarmStock(FarmStockDto farmStockDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Farm_stock SET sup_id = ?, category = ?, quantity = ? WHERE farm_stock_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, farmStockDto.getSupId());
        pstm.setString(2, farmStockDto.getCategory());
        pstm.setString(3, farmStockDto.getQuantity());
        pstm.setString(4, farmStockDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteFarmStock(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Farm_stock WHERE farm_stock_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextFarmStockId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT farm_stock_id FROM Farm_stock ORDER BY farm_stock_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentFarmStockId = null;

        if(resultSet.next()) {
            currentFarmStockId = resultSet.getString(1);
            return splitFarmStockId(currentFarmStockId);
        }
        return splitFarmStockId(null);
    }

    private String splitFarmStockId(String currentFarmStockId) {
        if(currentFarmStockId != null) {
            String[] split = currentFarmStockId.split("F");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "F00" + id;
        }
        return "F001";
    }
}
