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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author Admin
 */
public class NguoiDungPanel extends javax.swing.JPanel {

    /**
     * Creates new form NguoiDungPanel
     */
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtHoTen, txtSDT, txtDiaChi;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai;

    public NguoiDungPanel() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(new String[]{"Mã người dùng", "Họ tên", "SĐT", "Địa chỉ"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Form input
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin người dùng"));

        formPanel.add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        formPanel.add(txtHoTen);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        formPanel.add(txtDiaChi);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnTaiLai = new JButton("Tải lại");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnTaiLai);

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadData();

        // Events
        btnThem.addActionListener(e -> themNguoiDung());
        btnSua.addActionListener(e -> suaNguoiDung());
        btnXoa.addActionListener(e -> xoaNguoiDung());
        btnTaiLai.addActionListener(e -> loadData());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtHoTen.setText(table.getValueAt(row, 1).toString());
                txtSDT.setText(table.getValueAt(row, 2).toString());
                txtDiaChi.setText(table.getValueAt(row, 3).toString());
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Nguoidung";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("Manguoidung"),
                    rs.getString("Hoten"),
                    rs.getString("SDT"),
                    rs.getString("Diachi")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void themNguoiDung() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO Nguoidung (Hoten, SDT, Diachi) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtHoTen.getText());
            pst.setString(2, txtSDT.getText());
            pst.setString(3, txtDiaChi.getText());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm người dùng: " + e.getMessage());
        }
    }

    private void suaNguoiDung() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng để sửa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE Nguoidung SET Hoten=?, SDT=?, Diachi=? WHERE Manguoidung=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtHoTen.getText());
            pst.setString(2, txtSDT.getText());
            pst.setString(3, txtDiaChi.getText());
            pst.setInt(4, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
        }
    }

    private void xoaNguoiDung() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng để xóa!");
            return;
        }
        int id = (int) table.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM Nguoidung WHERE Manguoidung=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage());
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
