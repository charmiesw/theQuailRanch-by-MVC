package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.SellStockDto;
import lk.ijse.theQuailRanch.dto.tm.PlaceOrderCartTm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SellStockModel {
    public int getSellStockCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(sell_stock_id) FROM Sell_stock";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentSellStockCount = 0;
        int newSellStockCount = 0;

        if(resultSet.next()) {
            currentSellStockCount = resultSet.getInt(1);
            newSellStockCount = currentSellStockCount;
        }
        return newSellStockCount;
    }

    public List<SellStockDto> getAllSellStocks() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Sell_stock";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<SellStockDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String sell_stock_id = resultSet.getString(1);
            String category = resultSet.getString(2);
            int quantity = Integer.parseInt(resultSet.getString(3));
            double unit_price = Double.parseDouble(resultSet.getString(4));

            var dto = new SellStockDto(sell_stock_id, category, quantity, unit_price);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveSellStock(SellStockDto sellStockDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Sell_stock VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, sellStockDto.getId());
        pstm.setString(2, sellStockDto.getCategory());
        pstm.setInt(3, sellStockDto.getQuantity());
        pstm.setDouble(4, sellStockDto.getUnitPrice());

        return pstm.executeUpdate() > 0;
    }

    public SellStockDto searchSellStock(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Sell_stock WHERE sell_stock_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        SellStockDto dto = null;

        if(resultSet.next()) {
            String sell_stock_id = resultSet.getString(1);
            String category = resultSet.getString(2);
            int quantity = Integer.parseInt(resultSet.getString(3));
            double unit_price = Double.parseDouble(resultSet.getString(4));

            dto = new SellStockDto(sell_stock_id, category, quantity, unit_price);
        }

        return dto;
    }

    public boolean updateSellStock(SellStockDto sellStockDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Sell_stock SET category = ?, quantity = ?, unit_price = ? WHERE sell_stock_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, sellStockDto.getCategory());
        pstm.setInt(2, sellStockDto.getQuantity());
        pstm.setDouble(3,sellStockDto.getUnitPrice());
        pstm.setString(4, sellStockDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteSellStock(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Sell_stock WHERE sell_stock_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextSellStockId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT sell_stock_id FROM Sell_stock ORDER BY sell_stock_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentSellStockId = null;

        if(resultSet.next()) {
            currentSellStockId = resultSet.getString(1);
            return splitSellStockId(currentSellStockId);
        }
        return splitSellStockId(null);
    }

    private String splitSellStockId(String currentSellStockId) {
        if(currentSellStockId != null) {
            String[] split = currentSellStockId.split("Q");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "Q00" + id;
        }
        return "Q001";
    }

    public boolean updateSellStock(List<PlaceOrderCartTm> tmList) throws SQLException {
        for (PlaceOrderCartTm cartTm : tmList) {
            if(!updateQty(cartTm)) {
                return false;
            }
        }
        return true;
    }

    private boolean updateQty(PlaceOrderCartTm cartTm) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Sell_stock SET quantity = quantity - ? WHERE sell_stock_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setInt(1, cartTm.getQuantity());
        pstm.setString(2, cartTm.getSellStockId());

        return pstm.executeUpdate() > 0;
    }

    public static List<SellStockDto> loadAllSellStocks() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Sell_stock";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        List<SellStockDto> ssList = new ArrayList<>();

        while (resultSet.next()) {
            ssList.add(new SellStockDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getDouble(4)
            ));
        }
        return ssList;
    }
}
