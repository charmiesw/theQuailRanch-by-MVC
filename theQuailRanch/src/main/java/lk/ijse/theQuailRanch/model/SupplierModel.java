package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.SupplierDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierModel {

    public int getSupplierCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(sup_id) FROM Supplier";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentSupplierCount = 0;
        int newSupplierCount = 0;

        if(resultSet.next()) {
            currentSupplierCount = resultSet.getInt(1);
            newSupplierCount = currentSupplierCount;
        }
        return newSupplierCount;
    }

    public List<SupplierDto> getAllSuppliers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Supplier";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<SupplierDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String sup_id = resultSet.getString(1);
            String sup_name = resultSet.getString(2);

            var dto = new SupplierDto(sup_id, sup_name);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveSupplier(SupplierDto supplierDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO Supplier VALUES(?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, supplierDto.getId());
        pstm.setString(2, supplierDto.getName());

        return pstm.executeUpdate() > 0;
    }

    public SupplierDto searchSupplier(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Supplier WHERE sup_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        SupplierDto dto = null;

        if(resultSet.next()) {
            String sup_id = resultSet.getString(1);
            String sup_name = resultSet.getString(2);

            dto = new SupplierDto(sup_id, sup_name);
        }

        return dto;
    }

    public boolean updateSupplier(SupplierDto supplierDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE Supplier SET name = ? WHERE sup_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, supplierDto.getName());
        pstm.setString(2, supplierDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteSupplier(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM Supplier WHERE sup_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public String generateNextSupplierId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT sup_id FROM Supplier ORDER BY sup_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentSupplierId = null;

        if(resultSet.next()) {
            currentSupplierId = resultSet.getString(1);
            return splitSupplierId(currentSupplierId);
        }
        return splitSupplierId(null);
    }

    private String splitSupplierId(String currentSupplierId) {
        if(currentSupplierId != null) {
            String[] split = currentSupplierId.split("S");
            int id = Integer.parseInt(split[1]);
            id ++;
            return "S00" + id;
        }
        return "S001";
    }

    public List<SupplierDto> loadAllSuppliers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM Supplier";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        List<SupplierDto> supList = new ArrayList<>();

        while (resultSet.next()) {
            supList.add(new SupplierDto(
                    resultSet.getString(1),
                    resultSet.getString(2)
            ));
        }
        return supList;
    }
}
