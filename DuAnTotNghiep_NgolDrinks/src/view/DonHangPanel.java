package view;

import Connect.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DonHangPanel extends JPanel {

    private JTable tableDonHang, tableDoUong;
    private DefaultTableModel modelDonHang, modelDoUong;
    private JButton btnThem, btnXoa, btnHoanTat;
    private JComboBox<String> cboGiamGia;
    private JLabel lblTongTien;

    public DonHangPanel() {
        setLayout(new BorderLayout());

        // Bảng danh sách đồ uống từ CSDL
        modelDoUong = new DefaultTableModel(new Object[]{
                "Mã đồ uống", "Tên đồ uống", "Mã loại", "Giá bán", "Ảnh"
        }, 0);
        tableDoUong = new JTable(modelDoUong);
        JScrollPane scrollDoUong = new JScrollPane(tableDoUong);
        scrollDoUong.setBorder(BorderFactory.createTitledBorder("Danh sách đồ uống"));

        // Bảng đơn hàng tạm thời (không có ảnh)
        modelDonHang = new DefaultTableModel(new Object[]{
                "Mã đồ uống", "Tên đồ uống", "Mã loại", "Giá bán", "Số lượng", "Thành tiền"
        }, 0);
        tableDonHang = new JTable(modelDonHang);
        JScrollPane scrollDonHang = new JScrollPane(tableDonHang);
        scrollDonHang.setBorder(BorderFactory.createTitledBorder("Đơn hàng đang làm"));

        // Panel trung tâm chứa 2 bảng
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(scrollDoUong);
        centerPanel.add(scrollDonHang);

        // Panel dưới chứa nút, combo giảm giá, tổng tiền
        JPanel bottomPanel = new JPanel();
        btnThem = new JButton("Thêm");
        btnXoa = new JButton("Xóa");
        btnHoanTat = new JButton("Hoàn tất");
        cboGiamGia = new JComboBox<>();
        lblTongTien = new JLabel("Tổng tiền: 0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));

        bottomPanel.add(btnThem);
        bottomPanel.add(btnXoa);
        bottomPanel.add(new JLabel("Mã giảm giá:"));
        bottomPanel.add(cboGiamGia);
        bottomPanel.add(lblTongTien);
        bottomPanel.add(btnHoanTat);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadDoUong();
        loadGiamGia();

        // Sự kiện
        btnThem.addActionListener(e -> themVaoDonHang());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnHoanTat.addActionListener(e -> hoanTatDonHang());
        cboGiamGia.addActionListener(e -> capNhatTongTien());
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

    private void loadGiamGia() {
        cboGiamGia.removeAllItems();
        cboGiamGia.addItem("Không áp dụng");
        try (Connection conn = DBConnection.getConnect()) {
            String sql = "SELECT Magiamgia, Tilegiam FROM Giamgia";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int ma = rs.getInt("Magiamgia");
                int tile = rs.getInt("Tilegiam");
                cboGiamGia.addItem(ma + " - " + tile + "%");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi load mã giảm giá: " + e.getMessage());
        }
    }

    private void themVaoDonHang() {
        int row = tableDoUong.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đồ uống để thêm vào đơn hàng.");
            return;
        }

        int madouong = (int) modelDoUong.getValueAt(row, 0);
        String tendouong = (String) modelDoUong.getValueAt(row, 1);
        String maloaidouong = (String) modelDoUong.getValueAt(row, 2);
        double giaban = (double) modelDoUong.getValueAt(row, 3);

        boolean daCo = false;
        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            int maDaCo = (int) modelDonHang.getValueAt(i, 0);
            if (madouong == maDaCo) {
                int soLuongCu = (int) modelDonHang.getValueAt(i, 4);
                int soLuongMoi = soLuongCu + 1;
                modelDonHang.setValueAt(soLuongMoi, i, 4);
                modelDonHang.setValueAt(soLuongMoi * giaban, i, 5);
                daCo = true;
                break;
            }
        }

        if (!daCo) {
            modelDonHang.addRow(new Object[]{
                    madouong, tendouong, maloaidouong, giaban, 1, giaban
            });
        }

        capNhatTongTien();
    }

    private void xoaSanPham() {
        int row = tableDonHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm này khỏi đơn?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        modelDonHang.removeRow(row);
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        double tongTienGoc = 0;
        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            tongTienGoc += (double) modelDonHang.getValueAt(i, 5);
        }

        double tileGiam = 0;
        String selected = (String) cboGiamGia.getSelectedItem();
        if (selected != null && !selected.equals("Không áp dụng")) {
            try {
                String[] parts = selected.split("-");
                tileGiam = Double.parseDouble(parts[1].replace("%", "").trim());
            } catch (Exception ignored) {}
        }

        double tongTienSauGiam = tongTienGoc * (100 - tileGiam) / 100.0;
        lblTongTien.setText(String.format("Tổng tiền: %,.0f đ", tongTienSauGiam));
    }

    private void hoanTatDonHang() {
        if (modelDonHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Đơn hàng đang trống, không thể hoàn tất.");
            return;
        }

        try (Connection conn = DBConnection.getConnect()) {
            conn.setAutoCommit(false); // transaction

            // Tính tổng tiền
            double tongTienGoc = 0;
            for (int i = 0; i < modelDonHang.getRowCount(); i++) {
                tongTienGoc += (double) modelDonHang.getValueAt(i, 5);
            }

            // Lấy mã giảm giá
            Integer magiam = null;
            double tileGiam = 0;
            String selected = (String) cboGiamGia.getSelectedItem();
            if (selected != null && !selected.equals("Không áp dụng")) {
                try {
                    String[] parts = selected.split("-");
                    magiam = Integer.parseInt(parts[0].trim());
                    tileGiam = Double.parseDouble(parts[1].replace("%", "").trim());
                } catch (Exception ignored) {}
            }

            double tongTienSauGiam = tongTienGoc * (100 - tileGiam) / 100.0;

            // 1. Tạo đơn hàng chờ
            String sqlDon = "INSERT INTO DonHangCho (Thoigiantao, Magiamgia, Tongtien) OUTPUT INSERTED.MadonhangCho VALUES (GETDATE(), ?, ?)";
            PreparedStatement pstDon = conn.prepareStatement(sqlDon);
            if (magiam == null) pstDon.setNull(1, Types.INTEGER); else pstDon.setInt(1, magiam);
            pstDon.setDouble(2, tongTienSauGiam);
            ResultSet rs = pstDon.executeQuery();

            int madonhangMoi = -1;
            if (rs.next()) {
                madonhangMoi = rs.getInt("MadonhangCho");
            }
            rs.close();
            pstDon.close();

            if (madonhangMoi == -1) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Không thể tạo đơn hàng mới.");
                return;
            }

            // 2. Thêm chi tiết đơn hàng
            String sqlCT = "INSERT INTO ChitietdonhangCho (MadonhangCho, Madouong, Soluong, Giaban) VALUES (?, ?, ?, ?)";
            PreparedStatement pstCT = conn.prepareStatement(sqlCT);

            for (int i = 0; i < modelDonHang.getRowCount(); i++) {
                int madouong = (int) modelDonHang.getValueAt(i, 0);
                double giaban = (double) modelDonHang.getValueAt(i, 3);
                int soluong = (int) modelDonHang.getValueAt(i, 4);

                pstCT.setInt(1, madonhangMoi);
                pstCT.setInt(2, madouong);
                pstCT.setInt(3, soluong);
                pstCT.setDouble(4, giaban);
                pstCT.addBatch();
            }

            pstCT.executeBatch();
            conn.commit();

            JOptionPane.showMessageDialog(this, "Đã lưu đơn hàng chờ thành công!");

            // Reset đơn mới
            modelDonHang.setRowCount(0);
            lblTongTien.setText("Tổng tiền: 0 đ");
            cboGiamGia.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu đơn hàng: " + e.getMessage());
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
