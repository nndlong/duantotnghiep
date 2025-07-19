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
public class LoaiDoUongPanel extends javax.swing.JPanel {

    /**
     * Creates new form LoaiDoUongPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTenLoai;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai;

    public LoaiDoUongPanel() {
        setLayout(new BorderLayout());

        // 🟢 Table
        model = new DefaultTableModel(new String[]{"Mã loại đồ uống", "Tên loại"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟢 Form input
        JPanel form = new JPanel(new GridLayout(1, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin loại đồ uống"));

        form.add(new JLabel("Tên loại:"));
        txtTenLoai = new JTextField();
        form.add(txtTenLoai);

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
                txtTenLoai.setText(table.getValueAt(row, 1).toString());
            }
        });
    }

    // 🟢 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Loaidouong";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Maloaidouong"),
                    rs.getString("Tenloai")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟢 Thêm
    private void them() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO Loaidouong (Tenloai) VALUES (?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenLoai.getText());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm loại đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm loại đồ uống: " + e.getMessage());
        }
    }

    // 🟢 Sửa (hỏi xác nhận)
    private void sua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại đồ uống để sửa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA loại đồ uống này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE Loaidouong SET Tenloai=? WHERE Maloaidouong=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenLoai.getText());
            pst.setInt(2, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật loại đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa loại đồ uống: " + e.getMessage());
        }
    }

    // 🟢 Xóa (hỏi xác nhận)
    private void xoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại đồ uống để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA loại đồ uống này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM Loaidouong WHERE Maloaidouong=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa loại đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa loại đồ uống: " + e.getMessage());
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
