/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Connect.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author Admin
 */
public class NguoiDungDAO {
    // Hàm kiểm tra thông tin người dùng khi quên mật khẩu
    public static boolean kiemTraThongTin(String email, String hoTen, String sdt) {
        String sql = "SELECT * FROM Nguoidung n JOIN Taikhoan t ON n.Manguoidung = t.Manguoidung " +
                     "WHERE t.Email = ? AND n.Hoten = ? AND n.SDT = ?";

        try (Connection conn = DBConnection.getConnect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, hoTen);
            pst.setString(3, sdt);

            ResultSet rs = pst.executeQuery();
            return rs.next(); // true nếu tìm thấy thông tin khớp

        } catch (Exception e) {
            System.out.println("Lỗi khi kiểm tra thông tin người dùng: " + e.getMessage());
            return false;
        }
    }
}
