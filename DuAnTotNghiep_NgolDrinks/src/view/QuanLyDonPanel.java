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
public class QuanLyDonPanel extends javax.swing.JPanel {

    /**
     * Creates new form QuanLyDonPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMaBan, txtMaKhachHang, txtTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai;

    public QuanLyDonPanel() {
        setLayout(new BorderLayout());

        // 🟢 Table
        model = new DefaultTableModel(new String[]{"Mã đơn", "Mã bàn", "Mã khách hàng", "Trạng thái"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟢 Form input
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin đơn"));

        form.add(new JLabel("Mã bàn:"));
        txtMaBan = new JTextField();
        form.add(txtMaBan);

        form.add(new JLabel("Mã khách hàng:"));
        txtMaKhachHang = new JTextField();
        form.add(txtMaKhachHang);

        form.add(new JLabel("Trạng thái:"));
        txtTrangThai = new JTextField();
        form.add(txtTrangThai);

        // 🟢 Buttons
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

        // 🟢 Load data khi mở panel
        loadData();

        // 🟢 Sự kiện các nút
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> sua());
        btnXoa.addActionListener(e -> xoa());
        btnTaiLai.addActionListener(e -> loadData());

        // 🟢 Khi click table → set dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtMaBan.setText(table.getValueAt(row, 1).toString());
                txtMaKhachHang.setText(table.getValueAt(row, 2).toString());
                txtTrangThai.setText(table.getValueAt(row, 3).toString());
            }
        });
    }

    // 🟢 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM QuanLyDon";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Mandon"),
                    rs.getInt("Maban"),
                    rs.getInt("Makhachhang"),
                    rs.getString("Trangthai")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟢 Thêm
    private void them() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO QuanLyDon (Maban, Makhachhang, Trangthai) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtMaBan.getText()));
            pst.setInt(2, Integer.parseInt(txtMaKhachHang.getText()));
            pst.setString(3, txtTrangThai.getText());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm đơn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm đơn: " + e.getMessage());
        }
    }

    // 🟢 Sửa (hỏi xác nhận)
    private void sua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn để sửa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA đơn này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE QuanLyDon SET Maban=?, Makhachhang=?, Trangthai=? WHERE Mandon=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtMaBan.getText()));
            pst.setInt(2, Integer.parseInt(txtMaKhachHang.getText()));
            pst.setString(3, txtTrangThai.getText());
            pst.setInt(4, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật đơn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa đơn: " + e.getMessage());
        }
    }

    // 🟢 Xóa (hỏi xác nhận)
    private void xoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA đơn này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM QuanLyDon WHERE Mandon=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa đơn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa đơn: " + e.getMessage());
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
