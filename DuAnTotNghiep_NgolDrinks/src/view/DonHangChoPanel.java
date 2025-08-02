/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import Connect.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DonHangChoPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnChuyenThanhDonChinh, btnXoa;

    public DonHangChoPanel() {
        setLayout(new BorderLayout());

        // Bảng hiển thị
        model = new DefaultTableModel();
        model.addColumn("Mã đơn hàng chờ");
        model.addColumn("Thời gian tạo");
        model.addColumn("Tỉ lệ giảm (%)");
        model.addColumn("Tổng tiền");

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Danh sách đơn hàng đang chờ"));
        add(scroll, BorderLayout.CENTER);

        // Nút chức năng
        JPanel bottomPanel = new JPanel();
        btnChuyenThanhDonChinh = new JButton("Chuyển thành đơn đã tạo");
        btnXoa = new JButton("Xóa đơn hàng"); // ✅ Nút xóa

        bottomPanel.add(btnChuyenThanhDonChinh);
        bottomPanel.add(btnXoa);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load dữ liệu
        loadData();

        // Sự kiện nút
        btnChuyenThanhDonChinh.addActionListener(e -> chuyenThanhDonDaTao());
        btnXoa.addActionListener(e -> xoaDonHangCho());
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = """
                    SELECT d.MadonhangCho, d.Thoigiantao, 
                           ISNULL(g.Tilegiam, 0) AS Tilegiam, 
                           d.Tongtien
                    FROM DonHangCho d 
                    LEFT JOIN Giamgia g ON d.Magiamgia = g.Magiamgia
                    ORDER BY d.MadonhangCho DESC
                    """;
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int madon = rs.getInt("MadonhangCho");
                Timestamp thoigian = rs.getTimestamp("Thoigiantao");
                int tilegiam = rs.getInt("Tilegiam");
                double tongtien = rs.getDouble("Tongtien");

                model.addRow(new Object[]{
                        madon,
                        thoigian,
                        tilegiam + "%",
                        String.format("%,.0f đ", tongtien)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load dữ liệu đơn hàng chờ.");
        }
    }

    private void chuyenThanhDonDaTao() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 đơn để chuyển.");
            return;
        }

        int madonCho = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            conn.setAutoCommit(false);

            int newMaDon = taoMaDonHangTiepTheo(conn);

            // Lấy Magiamgia từ DonHangCho
            Integer magiam = null;
            String queryGG = "SELECT Magiamgia FROM DonHangCho WHERE MadonhangCho = ?";
            PreparedStatement pstGG = conn.prepareStatement(queryGG);
            pstGG.setInt(1, madonCho);
            ResultSet rsGG = pstGG.executeQuery();
            if (rsGG.next()) {
                int ma = rsGG.getInt("Magiamgia");
                if (!rsGG.wasNull()) magiam = ma;
            }
            rsGG.close();
            pstGG.close();

            // 1. Insert vào DonHang
            String insertDon = """
                    INSERT INTO DonHang (Madonhang, Ngaydathang, MadonhangCho, Trangthai, Magiamgia)
                    VALUES (?, GETDATE(), ?, N'Chờ', ?)
                    """;
            PreparedStatement pstDon = conn.prepareStatement(insertDon);
            pstDon.setInt(1, newMaDon);
            pstDon.setInt(2, madonCho);
            if (magiam == null) pstDon.setNull(3, Types.INTEGER);
            else pstDon.setInt(3, magiam);
            pstDon.executeUpdate();
            pstDon.close();

            // 2. Insert vào Chitietdonhang (không còn Tonggiatri)
            String insertCT = """
                    INSERT INTO Chitietdonhang (Madonhang, Madouong, Soluong, Giaban)
                    SELECT ?, Madouong, Soluong, Giaban
                    FROM ChitietdonhangCho WHERE MadonhangCho = ?
                    """;
            PreparedStatement pstCT = conn.prepareStatement(insertCT);
            pstCT.setInt(1, newMaDon);
            pstCT.setInt(2, madonCho);
            pstCT.executeUpdate();
            pstCT.close();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Chuyển đơn hàng thành công!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi chuyển đơn hàng: " + e.getMessage());
        }
    }

    private void xoaDonHangCho() { // ✅ Hàm xóa đơn hàng chờ
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 đơn để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa đơn hàng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int madonCho = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            conn.setAutoCommit(false);

            // 1. Xóa chi tiết đơn hàng chờ
            PreparedStatement pstCT = conn.prepareStatement(
                    "DELETE FROM ChitietdonhangCho WHERE MadonhangCho = ?");
            pstCT.setInt(1, madonCho);
            pstCT.executeUpdate();
            pstCT.close();

            // 2. Xóa đơn hàng chờ
            PreparedStatement pstDon = conn.prepareStatement(
                    "DELETE FROM DonHangCho WHERE MadonhangCho = ?");
            pstDon.setInt(1, madonCho);
            pstDon.executeUpdate();
            pstDon.close();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Xóa đơn hàng chờ thành công!");
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn hàng chờ: " + e.getMessage());
        }
    }

    private int taoMaDonHangTiepTheo(Connection conn) throws SQLException {
        String sql = "SELECT ISNULL(MAX(Madonhang), 0) + 1 AS nextMa FROM DonHang";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("nextMa");
        }
        return 1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
