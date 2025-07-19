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
public class DoUongPanel extends javax.swing.JPanel {

    /**
     * Creates new form DoUongPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTenDouong, txtGiaBan, txtMaLoai, txtAnhDouong;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai, btnQuanLyLoai;

    public DoUongPanel() {
        setLayout(new BorderLayout());

        // 🟢 Table
        model = new DefaultTableModel(new String[]{"Mã đồ uống", "Tên đồ uống", "Giá bán", "Mã loại", "Ảnh"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟢 Form input
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin đồ uống"));

        form.add(new JLabel("Tên đồ uống:"));
        txtTenDouong = new JTextField();
        form.add(txtTenDouong);

        form.add(new JLabel("Giá bán:"));
        txtGiaBan = new JTextField();
        form.add(txtGiaBan);

        form.add(new JLabel("Mã loại đồ uống:"));
        txtMaLoai = new JTextField();
        form.add(txtMaLoai);

        form.add(new JLabel("Ảnh (tên file):"));
        txtAnhDouong = new JTextField();
        form.add(txtAnhDouong);

        // 🟢 Buttons
        JPanel buttons = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnTaiLai = new JButton("Tải lại");
        btnQuanLyLoai = new JButton("Quản lý loại đồ uống"); // 🔥 Nút mở panel phụ

        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnTaiLai);
        buttons.add(btnQuanLyLoai);

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

        // 🔥 Khi bấm "Quản lý loại đồ uống"
        btnQuanLyLoai.addActionListener(e -> {
            Container parent = this.getParent();
            parent.removeAll();
            parent.add(new LoaiDoUongPanel());
            parent.revalidate();
            parent.repaint();
        });

        // 🟢 Khi click table → set dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtTenDouong.setText(table.getValueAt(row, 1).toString());
                txtGiaBan.setText(table.getValueAt(row, 2).toString());
                txtMaLoai.setText(table.getValueAt(row, 3).toString());
                txtAnhDouong.setText(table.getValueAt(row, 4).toString());
            }
        });
    }

    // 🟢 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Douong";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Madouong"),
                    rs.getString("Tendouong"),
                    rs.getInt("Giaban"),
                    rs.getInt("Maloaidouong"),
                    rs.getString("Anhdouong")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟢 Thêm
    private void them() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO Douong (Tendouong, Giaban, Maloaidouong, Anhdouong) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenDouong.getText());
            pst.setInt(2, Integer.parseInt(txtGiaBan.getText()));
            pst.setInt(3, Integer.parseInt(txtMaLoai.getText()));
            pst.setString(4, txtAnhDouong.getText());
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm đồ uống: " + e.getMessage());
        }
    }

    // 🟢 Sửa (hỏi xác nhận)
    private void sua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để sửa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA đồ uống này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE Douong SET Tendouong=?, Giaban=?, Maloaidouong=?, Anhdouong=? WHERE Madouong=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtTenDouong.getText());
            pst.setInt(2, Integer.parseInt(txtGiaBan.getText()));
            pst.setInt(3, Integer.parseInt(txtMaLoai.getText()));
            pst.setString(4, txtAnhDouong.getText());
            pst.setInt(5, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa đồ uống: " + e.getMessage());
        }
    }

    // 🟢 Xóa (hỏi xác nhận)
    private void xoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA đồ uống này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM Douong WHERE Madouong=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa đồ uống thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa đồ uống: " + e.getMessage());
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
