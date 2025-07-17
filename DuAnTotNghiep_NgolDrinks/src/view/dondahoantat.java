package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class dondahoantat extends javax.swing.JPanel {

    private JTable orderTable;
    private JTextArea orderDetailsArea;
    private JButton viewDetailsButton, exportInvoiceButton;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> discountComboBox;

    public dondahoantat() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(new Color(40, 40, 40));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Poppins", Font.PLAIN, 14));

        searchButton = new JButton("🔍 Tìm");
        searchButton.setFont(new Font("Poppins", Font.BOLD, 14));
        searchButton.setBackground(new Color(40, 40, 40));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        searchButton.addActionListener(e -> searchOrder());

        discountComboBox = new JComboBox<>();
        discountComboBox.setFont(new Font("Poppins", Font.BOLD, 14));
        discountComboBox.setForeground(Color.WHITE);
        discountComboBox.setBackground(new Color(255, 192, 203));
        discountComboBox.setBorder(BorderFactory.createEmptyBorder());
        loadDiscounts();

        JLabel discountLabel = new JLabel("Giảm giá:");
        discountLabel.setFont(new Font("Poppins", Font.BOLD, 14));
        discountLabel.setForeground(Color.WHITE);

        searchPanel.add(new JLabel("🔍 Nhập mã đơn hàng: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(discountLabel);
        searchPanel.add(discountComboBox);
        add(searchPanel, BorderLayout.NORTH);

        // Thêm cột Trạng thái
        String[] columnNames = {"Mã đơn hàng", "Ngày tạo đơn", "Trạng thái"};
        model = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(model);
        loadOrders();

        orderTable.setFont(new Font("Poppins", Font.BOLD, 14));
        orderTable.setRowHeight(30);
        orderTable.setBackground(Color.WHITE);
        orderTable.setForeground(Color.BLACK);
        orderTable.setSelectionBackground(Color.GRAY);
        orderTable.setSelectionForeground(Color.WHITE);

        // Màu chữ trạng thái
        orderTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                if (status.equalsIgnoreCase("Chưa thanh toán")) {
                    c.setForeground(Color.RED);
                } else if (status.equalsIgnoreCase("Đã thanh toán")) {
                    c.setForeground(new Color(0, 153, 0)); // xanh lá
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "📦 Danh sách đơn đã hoàn tất", 0, 0,
                new Font("Poppins", Font.BOLD, 16),
                Color.WHITE
        ));
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        tableScrollPane.setBackground(new Color(40, 40, 40));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 20));
        bottomPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        bottomPanel.setBackground(new Color(40, 40, 40));

        orderDetailsArea = new JTextArea();
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setText("Thông tin chi tiết đơn hàng sẽ hiển thị tại đây.");
        orderDetailsArea.setFont(new Font("Poppins", Font.PLAIN, 14));
        orderDetailsArea.setForeground(Color.WHITE);
        orderDetailsArea.setBackground(new Color(60, 60, 60));

        JScrollPane detailsScrollPane = new JScrollPane(orderDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "📄 Chi tiết đơn hàng", 0, 0,
                new Font("Poppins", Font.BOLD, 16),
                Color.WHITE
        ));
        detailsScrollPane.setPreferredSize(new Dimension(800, 250));
        bottomPanel.add(detailsScrollPane, BorderLayout.CENTER);

        viewDetailsButton = new JButton("🔍 Xem chi tiết");
        viewDetailsButton.setFont(new Font("Poppins", Font.BOLD, 14));
        viewDetailsButton.setBackground(new Color(40, 40, 40));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        viewDetailsButton.setPreferredSize(new Dimension(150, 40));
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> showOrderDetails());

        exportInvoiceButton = new JButton("📄 Xuất hóa đơn");
        exportInvoiceButton.setFont(new Font("Poppins", Font.BOLD, 14));
        exportInvoiceButton.setBackground(new Color(40, 40, 40));
        exportInvoiceButton.setForeground(Color.WHITE);
        exportInvoiceButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        exportInvoiceButton.setPreferredSize(new Dimension(150, 40));
        exportInvoiceButton.setFocusPainted(false);
        exportInvoiceButton.addActionListener(e -> exportInvoice());

        JButton saveButton = new JButton("💾 Lưu");
        saveButton.setFont(new Font("Poppins", Font.BOLD, 14));
        saveButton.setBackground(new Color(40, 40, 40));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveOrder());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40));
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(exportInvoiceButton);
        buttonPanel.add(saveButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    
    private void loadOrders() {
        try (Connection conn = Connect.DBConnection.getConnect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT Madonhang, Ngaydathang, Trangthai FROM Donhang")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Madonhang"),
                    rs.getDate("Ngaydathang"),
                    rs.getString("Trangthai")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một đơn hàng để lưu.");
            return;
        }

        int orderId = (int) orderTable.getValueAt(selectedRow, 0);

        // Lấy tổng tiền thanh toán từ getOrderDetails
        String details = getOrderDetails(orderId);
        int startIndex = details.lastIndexOf("✅ Tổng tiền thanh toán: ") + 23;
        int endIndex = details.indexOf(" VND", startIndex);
        int totalAmount = Integer.parseInt(details.substring(startIndex, endIndex).trim());

        // Lấy giá trị giảm giá từ ComboBox
        int selectedDiscountIndex = discountComboBox.getSelectedIndex();
        int discountRate = 0;
        if (selectedDiscountIndex >= 0) {
            String selectedDiscount = (String) discountComboBox.getSelectedItem();
            discountRate = Integer.parseInt(selectedDiscount.replace("%", ""));
        }

        try (Connection conn = Connect.DBConnection.getConnect()) {
            conn.setAutoCommit(false);

            // Thêm vào bảng QuanLyDon
            String insertQuery = "INSERT INTO QuanLyDon (MaDonHang, NgayTao, GiamGia, TongTien) VALUES (?, GETDATE(), ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, orderId);
                insertStmt.setInt(2, discountRate);
                insertStmt.setInt(3, totalAmount);
                insertStmt.executeUpdate();
            }

            // Cập nhật trạng thái đơn hàng
            String updateQuery = "UPDATE Donhang SET TrangThai = N'Đã thanh toán' WHERE Madonhang = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, orderId);
                updateStmt.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this, "Đơn hàng đã được lưu và cập nhật trạng thái.");

            // Load lại bảng
            model.setRowCount(0); // Xóa dữ liệu cũ
            loadOrders();         // Tải lại dữ liệu mới
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đơn hàng.");
        }
    }

    private void loadDiscounts() {
        try (Connection conn =Connect.DBConnection.getConnect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT Tilegiam FROM Giamgia")) {
            while (rs.next()) {
                discountComboBox.addItem(rs.getInt("Tilegiam") + "%");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private void showOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int orderId = (int) orderTable.getValueAt(selectedRow, 0);
            String details = getOrderDetails(orderId);
            orderDetailsArea.setText(details);
        } else {
            JOptionPane.showMessageDialog(this, "Hãy chọn một đơn hàng để xem thông tin chi tiết.");
        }
    }

    private String getOrderDetails(int orderId) {
        
        StringBuilder details = new StringBuilder("📄 Chi tiết đơn hàng:\n");
        details.append("Mã đơn hàng: ").append(orderId).append("\n\n");

        int selectedDiscountIndex = discountComboBox.getSelectedIndex();
        int discountRate = 0; // Giảm giá mặc định là 0%
        if (selectedDiscountIndex >= 0) {
            String selectedDiscount = (String) discountComboBox.getSelectedItem();
            discountRate = Integer.parseInt(selectedDiscount.replace("%", "")); // Lấy số từ "20%"
        }

        try (Connection conn = Connect.DBConnection.getConnect(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT Monan.Tenmonan, Chitietdonhang.Soluong, Chitietdonhang.Giaban, Chitietdonhang.Tonggiatri "
                + "FROM Chitietdonhang "
                + "JOIN Monan ON Chitietdonhang.Mamonan = Monan.Mamonan "
                + "WHERE Madonhang = ?")) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            int totalAmount = 0;
            while (rs.next()) {
                String tenmonan = rs.getString("Tenmonan");
                int soluong = rs.getInt("Soluong");
                int giaban = rs.getInt("Giaban");
                int tonggiatri = rs.getInt("Tonggiatri");
                totalAmount += tonggiatri;

                details.append("- ").append(tenmonan)
                        .append(" | Số lượng: ").append(soluong)
                        .append(" | Giá: ").append(giaban)
                        .append(" | Thành tiền: ").append(tonggiatri)
                        .append("\n");
            }
            rs.close();

            // Tính toán giảm giá
            int discountAmount = (totalAmount * discountRate) / 100;
            int finalAmount = totalAmount - discountAmount;

            if (discountRate > 0) {
                details.append("\n💰 Tổng thành tiền (chưa giảm): ").append(totalAmount).append(" VND");
                details.append("\n🎁 Giảm giá: ").append(discountRate).append("% (-").append(discountAmount).append(" VND)");
            }

            details.append("\n✅ Tổng tiền thanh toán: ").append(finalAmount).append(" VND");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return details.toString();
    }

    private void exportInvoice() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một đơn hàng để xuất hóa đơn.");
            return;
        }

        int orderId = (int) orderTable.getValueAt(selectedRow, 0);
        String invoiceContent = getOrderDetails(orderId);

        // Đường dẫn lưu file hóa đơn trên ổ D:
        String filePath = "D:\\HoaDon_" + orderId + ".txt";

        try {
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            writer.write(invoiceContent);
            writer.close();

            // Mở file hóa đơn sau khi xuất
            Desktop.getDesktop().open(file);

            JOptionPane.showMessageDialog(this, "Hóa đơn đã được xuất: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất hóa đơn.");
        }
    }
    
private void searchOrder() {
    String searchText = searchField.getText().trim();

    // Nếu không nhập gì thì hiển thị toàn bộ đơn hàng
    if (searchText.isEmpty()) {
        model.setRowCount(0);
        try (Connection conn = Connect.DBConnection.getConnect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT Madonhang, Ngaydathang, Trangthai FROM Donhang");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("Madonhang"),
                    rs.getDate("Ngaydathang"),
                    rs.getString("Trangthai")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đơn hàng.");
        }
        return;
    }

    // Trường hợp có nhập dữ liệu
    try {
        int orderId = Integer.parseInt(searchText);

        if (orderId < 1) {
            JOptionPane.showMessageDialog(this, "Mã đơn hàng phải từ 1 trở lên.");
            return;
        }

        model.setRowCount(0); // xóa bảng cũ

        try (Connection conn = Connect.DBConnection.getConnect();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT Madonhang, Ngaydathang, Trangthai FROM Donhang WHERE Madonhang = ?")) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("Madonhang"),
                        rs.getDate("Ngaydathang"),
                        rs.getString("Trangthai")
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng.");
                }
            }
        }

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã đơn hàng phải là số.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tìm đơn hàng.");
    }
}


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
