/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import Connect.DBConnection;
import model.TaiKhoan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author Admin
 */
public class TaiKhoanDAO {
    // Hàm kiểm tra đăng nhập
    public static TaiKhoan kiemTraDangNhap(String email, String matKhau) {
        TaiKhoan tk = null;
        String sql = "SELECT * FROM Taikhoan WHERE Email = ? AND Matkhau = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, email);
            pst.setString(2, matKhau);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                tk = new TaiKhoan();
                tk.setEmail(rs.getString("Email"));
                tk.setMatKhau(rs.getString("Matkhau"));
                tk.setMaNguoiDung(rs.getInt("Manguoidung"));
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi kiểm tra đăng nhập: " + e.getMessage());
        }

        return tk;
    }

    // Hàm cập nhật mật khẩu
    public static boolean capNhatMatKhau(String email, String matKhauMoi) {
        String sql = "UPDATE Taikhoan SET Matkhau = ? WHERE Email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, matKhauMoi);
            pst.setString(2, email);

            int rows = pst.executeUpdate();
            return rows > 0; // true nếu có dòng được cập nhật

        } catch (Exception e) {
            System.out.println("Lỗi khi đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }
}
