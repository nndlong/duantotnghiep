package view;

import Connect.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class DonHangPanel extends JPanel {

    private JTable tableDonHang;
    private JTable tableDoUong;
    private DefaultTableModel modelDonHang;
    private DefaultTableModel modelDoUong;
    private JButton btnThem, btnHoanTat;

    public DonHangPanel() {
 setLayout(new BorderLayout());

        // Bảng danh sách đồ uống từ CSDL
        modelDoUong = new DefaultTableModel(new Object[]{
            "Mã đồ uống", "Tên đồ uống", "Mã loại", "Giá bán", "Ảnh"
        }, 0);
        tableDoUong = new JTable(modelDoUong);
        JScrollPane scrollDoUong = new JScrollPane(tableDoUong);
        scrollDoUong.setBorder(BorderFactory.createTitledBorder("Danh sách đồ uống"));

        // Bảng đơn hàng tạm thời
        modelDonHang = new DefaultTableModel(new Object[]{
            "Mã đồ uống", "Tên đồ uống", "Mã loại", "Giá bán", "Ảnh"
        }, 0);
        tableDonHang = new JTable(modelDonHang);
        JScrollPane scrollDonHang = new JScrollPane(tableDonHang);
        scrollDonHang.setBorder(BorderFactory.createTitledBorder("Đơn hàng đang làm"));

        // Panel trung tâm chứa 2 bảng
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(scrollDoUong);
        centerPanel.add(scrollDonHang);

        // Nút Thêm và Hoàn tất
        JPanel buttonPanel = new JPanel();
        btnThem = new JButton("Thêm");
        btnHoanTat = new JButton("Hoàn tất");
        buttonPanel.add(btnThem);
        buttonPanel.add(btnHoanTat);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadDoUong();

        btnThem.addActionListener(e -> themVaoDonHang());
        btnHoanTat.addActionListener(e -> hoanTatDonHang());
    }

    private void loadDoUong() {
        modelDoUong.setRowCount(0);
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT Madouong, Tendouong, Maloaidouong, Giaban, Anhdouong FROM Douong";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                modelDoUong.addRow(new Object[]{
                    rs.getInt("Madouong"),
                    rs.getString("Tendouong"),
                    rs.getString("Maloaidouong"),
                    rs.getDouble("Giaban"),
                    rs.getString("Anhdouong")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi load dữ liệu đồ uống: " + e.getMessage());
        }
    }

    private void themVaoDonHang() {
        int row = tableDoUong.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để thêm vào đơn hàng.");
            return;
        }
        Vector<Object> rowData = new Vector<>();
        for (int i = 0; i < tableDoUong.getColumnCount(); i++) {
            rowData.add(tableDoUong.getValueAt(row, i));
        }
        modelDonHang.addRow(rowData);
    }

   private void hoanTatDonHang() {
    if (modelDonHang.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Đơn hàng đang trống, không thể hoàn tất.");
        return;
    }

    try (Connection conn = DBConnection.getConnect()) {
        conn.setAutoCommit(false); // Bắt đầu transaction

        // 1. Tạo đơn hàng mới
        String insertDon = "INSERT INTO DonHangCho (Thoigiantao) OUTPUT INSERTED.MadonhangCho VALUES (GETDATE())";
        PreparedStatement pstDon = conn.prepareStatement(insertDon);
        ResultSet rs = pstDon.executeQuery();

        int madonhangMoi = -1;
        if (rs.next()) {
            madonhangMoi = rs.getInt("MadonhangCho");
        }
        rs.close();
        pstDon.close();

        if (madonhangMoi == -1) {
            JOptionPane.showMessageDialog(this, "Không thể tạo đơn hàng mới.");
            conn.rollback();
            return;
        }

        // 2. Thêm chi tiết đơn hàng
        String insertChiTiet = "INSERT INTO ChitietdonhangCho (MadonhangCho, Madouong, Soluong, Giaban) VALUES (?, ?, ?, ?)";
        PreparedStatement pstChiTiet = conn.prepareStatement(insertChiTiet);

        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            int madouong = (int) modelDonHang.getValueAt(i, 0);
            double giaban = (double) modelDonHang.getValueAt(i, 3);

            pstChiTiet.setInt(1, madonhangMoi);
            pstChiTiet.setInt(2, madouong);
            pstChiTiet.setInt(3, 1); // Số lượng mặc định là 1
            pstChiTiet.setDouble(4, giaban);
            pstChiTiet.addBatch();
        }

        pstChiTiet.executeBatch();
        conn.commit(); // Kết thúc transaction

        JOptionPane.showMessageDialog(this, "Đã lưu đơn hàng chờ thành công!");
        modelDonHang.setRowCount(0);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Lỗi lưu đơn hàng: " + e.getMessage());
        e.printStackTrace();
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
