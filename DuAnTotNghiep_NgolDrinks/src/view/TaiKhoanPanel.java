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
public class TaiKhoanPanel extends javax.swing.JPanel {

    /**
     * Creates new form TaiKhoanPanel
     */
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtEmail, txtMatKhau, txtMaNguoiDung;
    private JButton btnThem, btnSua, btnXoa, btnTaiLai, btnQuanLyNguoiDung;

    public TaiKhoanPanel() {
        setLayout(new BorderLayout());

        // 🟢 Table
        model = new DefaultTableModel(new String[]{"Mã tài khoản", "Email", "Mật khẩu", "Mã người dùng"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🟢 Form input
        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));

        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JTextField();
        form.add(txtMatKhau);

        form.add(new JLabel("Mã người dùng:"));
        txtMaNguoiDung = new JTextField();
        form.add(txtMaNguoiDung);

        // 🟢 Buttons
        JPanel buttons = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnTaiLai = new JButton("Tải lại");
        btnQuanLyNguoiDung = new JButton("Quản lý người dùng"); // 🔥 Nút mở panel phụ

        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnTaiLai);
        buttons.add(btnQuanLyNguoiDung);

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

        // 🔥 Khi bấm "Quản lý người dùng"
        btnQuanLyNguoiDung.addActionListener(e -> {
            Container parent = this.getParent();
            parent.removeAll();
            parent.add(new NguoiDungPanel());
            parent.revalidate();
            parent.repaint();
        });

        // 🟢 Khi click table → set dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtEmail.setText(table.getValueAt(row, 1).toString());
                txtMatKhau.setText(table.getValueAt(row, 2).toString());
                txtMaNguoiDung.setText(table.getValueAt(row, 3).toString());
            }
        });
    }

    // 🟢 Load data
    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT * FROM Taikhoan";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Mataikhoan"),
                    rs.getString("Email"),
                    rs.getString("Matkhau"),
                    rs.getInt("Manguoidung")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // 🟢 Thêm
    private void them() {
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "INSERT INTO Taikhoan (Email, Matkhau, Manguoidung) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtEmail.getText());
            pst.setString(2, txtMatKhau.getText());
            pst.setInt(3, Integer.parseInt(txtMaNguoiDung.getText()));
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm tài khoản: " + e.getMessage());
        }
    }

    // 🟢 Sửa (hỏi xác nhận)
    private void sua() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để sửa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn SỬA tài khoản này không?",
            "Xác nhận sửa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0); // Lấy Mataikhoan

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "UPDATE Taikhoan SET Email=?, Matkhau=?, Manguoidung=? WHERE Mataikhoan=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtEmail.getText());
            pst.setString(2, txtMatKhau.getText());
            pst.setInt(3, Integer.parseInt(txtMaNguoiDung.getText()));
            pst.setInt(4, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa tài khoản: " + e.getMessage());
        }
    }

    // 🟢 Xóa (hỏi xác nhận)
    private void xoa() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn XÓA tài khoản này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        int id = (int) table.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnect()) {
            String sql = "DELETE FROM Taikhoan WHERE Mataikhoan=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa tài khoản: " + e.getMessage());
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
