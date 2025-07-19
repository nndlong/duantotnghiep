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
public class BanPanel extends javax.swing.JPanel {

    /**
     * Creates new form BanPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTenBan, txtTang, txtMoTa;
    private JComboBox<String> cboTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai;

    public BanPanel() {
        setLayout(new BorderLayout());

        // 🟢 Table
        model = new DefaultTableModel(new String[]{"Tên bàn", "Tầng", "Mô tả", "Trạng thái"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟢 Form input
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin bàn"));

        form.add(new JLabel("Tên bàn:"));
        txtTenBan = new JTextField();
        form.add(txtTenBan);

        form.add(new JLabel("Tầng:"));
        txtTang = new JTextField();
        form.add(txtTang);

        form.add(new JLabel("Mô tả:"));
        txtMoTa = new JTextField();
        form.add(txtMoTa);

        form.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Trống", "Có người"});
        form.add(cboTrangThai);

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
                txtTenBan.setText(table.getValueAt(row, 0).toString());
                txtTang.setText(table.getValueAt(row, 1).toString());
                txtMoTa.setText(table.getValueAt(row, 2).toString());
                cboTrangThai.setSelectedItem(table.getValueAt(row, 3).toString());
            }
        });
    }

    // 🟢 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT TenBan, Tang, MoTa, TrangThai FROM BanAn";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("TenBan"),
                    rs.getInt("Tang"),
                    rs.getString("MoTa"),
                    rs.getString("TrangThai")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟢 Thêm
    private void them() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO BanAn (TenBan, Tang, MoTa, TrangThai) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenBan.getText());
            pst.setInt(2, Integer.parseInt(txtTang.getText()));
            pst.setString(3, txtMoTa.getText());
            pst.setString(4, cboTrangThai.getSelectedItem().toString());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm bàn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm bàn: " + e.getMessage());
        }
    }

    // 🟢 Sửa (hỏi xác nhận)
    private void sua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để sửa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA bàn này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        String tenBan = table.getValueAt(row, 0).toString();

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE BanAn SET TenBan=?, Tang=?, MoTa=?, TrangThai=? WHERE TenBan=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenBan.getText());
            pst.setInt(2, Integer.parseInt(txtTang.getText()));
            pst.setString(3, txtMoTa.getText());
            pst.setString(4, cboTrangThai.getSelectedItem().toString());
            pst.setString(5, tenBan);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa bàn: " + e.getMessage());
        }
    }

    // 🟢 Xóa (hỏi xác nhận)
    private void xoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA bàn này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        String tenBan = table.getValueAt(row, 0).toString();

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM BanAn WHERE TenBan=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, tenBan);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa bàn thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa bàn: " + e.getMessage());
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
