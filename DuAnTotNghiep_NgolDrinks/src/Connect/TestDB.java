/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Connect;

import java.sql.Connection;
/**
 *
 * @author Admin
 */
public class TestDB {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Đã kết nối SQL Server thành công!");
        } else {
            System.out.println("❌ Kết nối SQL Server thất bại!");
        }
    }
}
