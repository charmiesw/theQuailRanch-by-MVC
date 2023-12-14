package lk.ijse.theQuailRanch.model;

import lk.ijse.theQuailRanch.db.DbConnection;
import lk.ijse.theQuailRanch.dto.UserDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserModel {

    public int getUserCount() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT COUNT(user_id) FROM User";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        int currentUserCount = 0;
        int newUserCount = 0;

        if(resultSet.next()) {
            currentUserCount = resultSet.getInt(1);
            newUserCount = currentUserCount;
        }
        return newUserCount;
    }

    public List<UserDto> getAllUsers() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM User";
        PreparedStatement pstm = connection.prepareStatement(sql);

        List<UserDto> dtoList = new ArrayList<>();

        ResultSet resultSet = pstm.executeQuery();

        while (resultSet.next()) {
            String user_id = resultSet.getString(1);
            String username = resultSet.getString(2);
            String pw = resultSet.getString(3);
            String tel = resultSet.getString(4);


            var dto = new UserDto(user_id, username, pw, tel);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public boolean saveUser(UserDto userDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "INSERT INTO User VALUES(?, ?, ?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, userDto.getId());
        pstm.setString(2, userDto.getUsername());
        pstm.setString(3, userDto.getPassword());
        pstm.setString(4, userDto.getTel());

        return pstm.executeUpdate() > 0;
    }

    public boolean searchUser(String username, String password) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM User WHERE name = ? AND pw = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, username);
        pstm.setString(2, password);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public String generateNextUserId() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT user_id FROM User ORDER BY user_id DESC LIMIT 1";
        ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

        String currentUserId = null;

        if (resultSet.next()) {
            currentUserId = resultSet.getString(1);
            return splitUserId(currentUserId);
        }
        return splitUserId(null);
    }

    private String splitUserId(String currentUserId) {
        if (currentUserId != null) {
            String[] split = currentUserId.split("U");
            int id = Integer.parseInt(split[1]);
            id++;
            return "U00" + id;
        }
        return "U001";
    }

    public boolean updateUser(UserDto userDto) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "UPDATE User SET name = ?, tel = ?, pw = ? WHERE user_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, userDto.getUsername());
        pstm.setString(2, userDto.getTel());
        pstm.setString(3, userDto.getPassword());
        pstm.setString(4, userDto.getId());

        return pstm.executeUpdate() > 0;
    }

    public boolean deleteUser(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "DELETE FROM User WHERE user_id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        return pstm.executeUpdate() > 0;
    }

    public UserDto searchUserById(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();

        String sql = "SELECT * FROM User WHERE user_id = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, id);

        ResultSet resultSet = pstm.executeQuery();

        UserDto dto = null;

        if (resultSet.next()) {
            String user_id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String pw = resultSet.getString(3);
            String tel = resultSet.getString(4);

            dto = new UserDto(user_id, name, pw, tel);
        }
        return dto;
    }
}