package ca.ubc.cs304.database;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.MemberModel;

import java.sql.*;
import java.util.ArrayList;

public class MemberHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";

    private Connection connection = null;

    public MemberHandler(Connection connection) {
        this.connection = connection;
    }

    public void updateMemberEmail(int id, String email) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE member SET email = ? WHERE member_id = ?");
            ps.setString(1, email);
            ps.setInt(2, id);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Member " + id + " does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    private void rollbackConnection() {
        try  {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

//    division
    public MemberModel[] membersInAllBranches() {
        ArrayList<MemberModel> result = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM member m WHERE NOT EXISTS (SELECT b.branch_id FROM branch b WHERE NOT EXISTS ( SELECT r.branch_id FROM registersIN r WHERE r.branch_id = b.branch_id AND m.member_id = r.member_id");

            while(rs.next()) {
                MemberModel model = new MemberModel(rs.getInt("member_id"),
                        rs.getDate("member_since"),
                        rs.getDate("dob"),
                        rs.getString("membership_type"),
                        rs.getString("gender").charAt(0),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getInt("membership_fee"));
                result.add(model);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result.toArray(new MemberModel[result.size()]);
    }
}

