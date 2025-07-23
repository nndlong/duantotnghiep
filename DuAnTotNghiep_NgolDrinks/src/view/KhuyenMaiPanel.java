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
/**
 *
 * @author Admin
 */
public class KhuyenMaiPanel extends javax.swing.JPanel {

    /**
     * Creates new form KhuyenMaiPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMagiamgia, txtTileGiam;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());

        // 🟦 Table
        model = new DefaultTableModel(new String[]{"Mã giảm giá", "Tỉ lệ giảm (%)", "Ngày tạo"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟦 Form input
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin khuyến mãi"));

        form.add(new JLabel("Mã giảm giá (ID):"));
        txtMagiamgia = new JTextField();
        txtMagiamgia.setEnabled(false); // ❌ Không cho nhập
        form.add(txtMagiamgia);

        form.add(new JLabel("Tỉ lệ giảm (%):"));
        txtTileGiam = new JTextField();
        form.add(txtTileGiam);

        // 🟦 Buttons
        JPanel buttons = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnTaiLai = new JButton("Tải lại");

        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnTaiLai);

        add(scrollPane, BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);

        // 🟦 Load data khi mở panel
        loadData();

        // 🟦 Sự kiện các nút
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnTaiLai.addActionListener(e -> loadData());

        // 🟦 Khi click table → set dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtMagiamgia.setText(table.getValueAt(row, 0).toString());
                txtTileGiam.setText(table.getValueAt(row, 1).toString());
            }
        });
    }

    // 🟦 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Giamgia";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Magiamgia"),
                    rs.getInt("Tilegiam"),
                    rs.getTimestamp("Ngaytao")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟦 Validate tỉ lệ giảm
    private boolean validateTileGiam() {
        try {
            int tile = Integer.parseInt(txtTileGiam.getText());
            if (tile < 1 || tile > 99) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ giảm phải từ 1% đến 99%!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tỉ lệ giảm phải là số!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    // 🟦 Thêm
    private void them() {
        if (!validateTileGiam()) return; // ✅ Validate trước khi thêm

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO Giamgia (Tilegiam) VALUES (?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtTileGiam.getText()));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm giảm giá thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm giảm giá: " + e.getMessage());
        }
    }

    // 🟦 Sửa (hỏi xác nhận)
    private void sua() {
        if (txtMagiamgia.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để sửa!");
            return;
        }

        if (!validateTileGiam()) return; // ✅ Validate trước khi sửa

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA giảm giá này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(txtMagiamgia.getText());

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE Giamgia SET Tilegiam=? WHERE Magiamgia=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtTileGiam.getText()));
            pst.setInt(2, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật giảm giá thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa giảm giá: " + e.getMessage());
        }
    }

    // 🟦 Xóa (hỏi xác nhận)
    private void xoa() {
        if (txtMagiamgia.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA giảm giá này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = Integer.parseInt(txtMagiamgia.getText());

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM Giamgia WHERE Magiamgia=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa giảm giá thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa giảm giá: " + e.getMessage());
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
