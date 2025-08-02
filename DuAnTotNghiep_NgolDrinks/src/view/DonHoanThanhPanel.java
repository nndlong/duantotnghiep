/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import Connect.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DonHoanThanhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JButton btnThanhToan;
    private JComboBox<String> cboTrangThai;

    public DonHoanThanhPanel() {
        setLayout(new BorderLayout());

        // Bảng dữ liệu
        model = new DefaultTableModel();
        model.addColumn("Mã Đơn Hàng");
        model.addColumn("Ngày đặt hàng");
        model.addColumn("Mã Đơn Hàng Chờ");
        model.addColumn("Trạng thái");
        model.addColumn("Tổng tiền");

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Nút + Combobox lọc
        btnThanhToan = new JButton("Thanh toán");
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Chờ", "Đã Thanh Toán"});
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Lọc theo trạng thái:"));
        bottomPanel.add(cboTrangThai);
        bottomPanel.add(btnThanhToan);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load dữ liệu lần đầu
        loadData("Tất cả");

        // Sự kiện
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
        cboTrangThai.addActionListener(e -> loadData((String) cboTrangThai.getSelectedItem()));

        // Xem chi tiết đơn hàng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int madon = (int) model.getValueAt(table.getSelectedRow(), 0);
                    hienThiChiTietDon(madon);
                }
            }
        });
    }

    private void loadData(String trangThaiLoc) {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT d.Madonhang, d.Ngaydathang, d.MadonhangCho, d.Trangthai, " +
                         "(SELECT SUM(CT.Soluong * CT.Giaban) FROM Chitietdonhang CT WHERE CT.Madonhang = d.Madonhang) AS TongTien " +
                         "FROM Donhang d";

            if (!"Tất cả".equalsIgnoreCase(trangThaiLoc)) {
                sql += " WHERE Trangthai = ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!"Tất cả".equalsIgnoreCase(trangThaiLoc)) {
                stmt.setString(1, trangThaiLoc);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int madon = rs.getInt("Madonhang");
                Date ngay = rs.getDate("Ngaydathang");
                int madoncho = rs.getObject("MadonhangCho") != null ? rs.getInt("MadonhangCho") : 0;
                String trangthai = rs.getString("Trangthai");
                double tong = rs.getObject("TongTien") != null ? rs.getDouble("TongTien") : 0;

                model.addRow(new Object[]{
                        madon,
                        ngay,
                        madoncho == 0 ? "Không có" : madoncho,
                        trangthai,
                        String.format("%,.0f", tong) + " đ"
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi load dữ liệu đơn hàng.");
        }
    }

    private void xuLyThanhToan() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng để thanh toán.");
            return;
        }

        String trangthai = (String) model.getValueAt(selectedRow, 3);
        if ("Đã Thanh Toán".equalsIgnoreCase(trangthai)) {
            JOptionPane.showMessageDialog(this, "Đơn hàng này đã thanh toán rồi.");
            return;
        }

        int madon = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn thanh toán đơn hàng này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnect()) {
                String sql = "UPDATE Donhang SET Trangthai = N'Đã Thanh Toán' WHERE Madonhang = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, madon);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                    loadData((String) cboTrangThai.getSelectedItem());
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật trạng thái.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật đơn hàng.");
            }
        }
    }

    private void hienThiChiTietDon(int madonhang) {
        DefaultTableModel detailModel = new DefaultTableModel(new Object[]{
                "Tên đồ uống", "Số lượng", "Giá bán", "Thành tiền"
        }, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT d.Tendouong, c.Soluong, c.Giaban, (c.Soluong * c.Giaban) as Thanhtien " +
                         "FROM Chitietdonhang c JOIN Douong d ON c.Madouong = d.Madouong " +
                         "WHERE c.Madonhang = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, madonhang);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                detailModel.addRow(new Object[]{
                        rs.getString("Tendouong"),
                        rs.getInt("Soluong"),
                        rs.getDouble("Giaban"),
                        rs.getDouble("Thanhtien")
                });
            }

            JTable tbl = new JTable(detailModel);
            JScrollPane scroll = new JScrollPane(tbl);
            scroll.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(this, scroll, "Chi tiết đơn hàng #" + madonhang, JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hiển thị chi tiết đơn hàng.");
        }
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
