package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.sql.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import Connect.DBConnection;
import java.util.ArrayList;
import java.util.List;
public class banan extends javax.swing.JPanel {

   private JTextField searchField, orderIdField;
    private JButton searchButton, moveButton, resetButton, orderButton;
    private JComboBox<String> filterComboBox;
    private JTable emptyTable, occupiedTable;
    private JLabel totalTablesLabel, emptyTablesLabel, occupiedTablesLabel;


    public banan() {
        setLayout(new BorderLayout());
        setBackground(new Color(40, 40, 40));

        // Khởi tạo filterComboBox
        filterComboBox = new JComboBox<>();

        // Tạo thanh tìm kiếm và chức năng
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(40, 40, 40));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Poppins", Font.PLAIN, 14));
        searchButton = new JButton("🔍 Tìm");
        customizeButton(searchButton, new Dimension(150, 40));

        orderIdField = new JTextField(15);  // Trường nhập mã đơn
        orderIdField.setFont(new Font("Poppins", Font.PLAIN, 14));
        orderIdField.setPreferredSize(new Dimension(200, 40));
        orderIdField.setToolTipText("Nhập mã đơn chờ");

        moveButton = new JButton("⇄ Chuyển Bàn");
        customizeButton(moveButton, new Dimension(150, 40));
        resetButton = new JButton("🔄 Khởi động lại");
        customizeButton(resetButton, new Dimension(150, 40));

        searchPanel.add(new JLabel("🔍 Nhập mã bàn: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("🔍 Lọc theo loại bàn: "));
        searchPanel.add(filterComboBox);
        searchPanel.add(orderIdField);  // Thêm trường mã đơn
        searchPanel.add(moveButton);    // Nút chuyển bàn
        searchPanel.add(resetButton);

        add(searchPanel, BorderLayout.NORTH);

        // Tạo bảng
        JPanel tablePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablePanel.setBackground(new Color(40, 40, 40));
        String[] emptyColumnNames = {"Mã bàn", "Tên bàn", "Tầng", "Ghi chú", "Trạng thái"};
        emptyTable = new JTable(new DefaultTableModel(emptyColumnNames, 0));
        customizeTable(emptyTable);

        String[] occupiedColumnNames = {"Mã bàn", "Tên bàn", "Tầng", "Ghi chú", "Trạng thái", "Mã đơn chờ"};
        occupiedTable = new JTable(new DefaultTableModel(occupiedColumnNames, 0));
        customizeTable(occupiedTable);

        JScrollPane emptyScrollPane = new JScrollPane(emptyTable);
        emptyScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "🍽️ Bàn trống", 0, 0,
                new Font("Poppins", Font.BOLD, 16), Color.WHITE));
        tablePanel.add(emptyScrollPane);

        JScrollPane occupiedScrollPane = new JScrollPane(occupiedTable);
        occupiedScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                "🍽️ Bàn có người", 0, 0,
                new Font("Poppins", Font.BOLD, 16), Color.WHITE));
        tablePanel.add(occupiedScrollPane);

        add(tablePanel, BorderLayout.CENTER);

        // Tổng kết
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        summaryPanel.setBackground(new Color(40, 40, 40));
        totalTablesLabel = createSummaryLabel("Tổng số bàn: 0");
        emptyTablesLabel = createSummaryLabel("Bàn trống: 0");
        occupiedTablesLabel = createSummaryLabel("Bàn có người: 0");

        summaryPanel.add(totalTablesLabel);
        summaryPanel.add(emptyTablesLabel);
        summaryPanel.add(occupiedTablesLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        loadTablesWithFilter("toàn bộ");

        moveButton.addActionListener(e -> moveTableStatus());
        filterComboBox.addActionListener(e -> {
            String filter = (String) filterComboBox.getSelectedItem();
            loadTablesWithFilter(filter);
        });

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (!keyword.isEmpty()) {
                searchTableByName();
            }
        });

        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn khởi động lại toàn bộ bàn về trạng thái trống không?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                resetAllTables();
            }
        });

        loadComboBoxWithDescriptions();
    }

 private void loadComboBoxWithDescriptions() {
        try {
            String sql = "SELECT DISTINCT MoTa FROM BanAn";
            ResultSet rs = (ResultSet) DBConnection.executeQuery(sql, new ArrayList<>());

            filterComboBox.removeAllItems();
            filterComboBox.addItem("toàn bộ");

            while (rs.next()) {
                String moTa = rs.getString("MoTa");
                filterComboBox.addItem(moTa);
            }

            filterComboBox.setSelectedItem("toàn bộ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
private void moveTableStatus() {
    try {
        int emptyRow = emptyTable.getSelectedRow();
        int occupiedRow = occupiedTable.getSelectedRow();
        String orderIdText = orderIdField.getText().trim();

        if (emptyRow != -1) {
            // Chuyển từ Trống → Có người
            if (orderIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đơn.");
                return;
            }

            int maDon;
            try {
                maDon = Integer.parseInt(orderIdText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã đơn phải là số.");
                return;
            }

            if (maDon < 1) {
                JOptionPane.showMessageDialog(this, "Mã đơn phải từ 1 trở lên.");
                return;
            }

            // Kiểm tra mã đơn có tồn tại trong DonhangCho
            String checkSql = "SELECT COUNT(*) FROM DonhangCho WHERE MadonhangCho = ?";
            try (Connection conn = DBConnection.getConnect();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, maDon);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Mã đơn không tồn tại trong danh sách đơn chờ.");
                    return;
                }
            }

            int maBan = Integer.parseInt(emptyTable.getValueAt(emptyRow, 0).toString());
            String sql = "UPDATE BanAn SET TrangThai = N'Có người', MadonhangCho = ? WHERE MaBan = ?";
            List<Object> params = new ArrayList<>();
            params.add(maDon);
            params.add(maBan);
            DBConnection.executeQuery(sql, params);

            JOptionPane.showMessageDialog(this, "Chuyển bàn thành công!");
            loadTablesWithFilter("toàn bộ");

        } else if (occupiedRow != -1) {
            // Chuyển từ Có người → Trống
            int maBan = Integer.parseInt(occupiedTable.getValueAt(occupiedRow, 0).toString());

            String sql = "UPDATE BanAn SET TrangThai = N'Trống', MadonhangCho = 0 WHERE MaBan = ?";
            List<Object> params = new ArrayList<>();
            params.add(maBan);
            DBConnection.executeQuery(sql, params);

            JOptionPane.showMessageDialog(this, "Chuyển bàn về trạng thái trống!");
            loadTablesWithFilter("toàn bộ");

        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần chuyển và điền mã đơn nếu là bàn trống.");
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi chuyển bàn.");
    }
}

    private void resetAllTables() {
        try {
            String sql = "UPDATE BanAn SET TrangThai = N'Trống', MadonhangCho = 0";
            DBConnection.executeQuery(sql, new ArrayList<>());

            JOptionPane.showMessageDialog(this, "Tất cả bàn đã được khởi động lại về trạng thái trống.");
            loadTablesWithFilter("toàn bộ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchTableByName() {
        String searchName = searchField.getText().trim();
        if (searchName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên bàn để tìm kiếm.");
            return;
        }

        DefaultTableModel emptyModel = (DefaultTableModel) emptyTable.getModel();
        DefaultTableModel occupiedModel = (DefaultTableModel) occupiedTable.getModel();
        emptyModel.setRowCount(0);
        occupiedModel.setRowCount(0);

        try {
            String sql = "SELECT * FROM BanAn WHERE TenBan LIKE ?";
            List<Object> params = new ArrayList<>();
            params.add("%" + searchName + "%");

            ResultSet rs = (ResultSet) DBConnection.executeQuery(sql, params);

            while (rs.next()) {
                int maBan = rs.getInt("MaBan");
                String tenBan = rs.getString("TenBan");
                int tang = rs.getInt("Tang");
                String moTa = rs.getString("MoTa");
                String trangThai = rs.getString("TrangThai");
                int maDon = rs.getInt("MadonhangCho");

                if (trangThai.equalsIgnoreCase("Trống")) {
                    emptyModel.addRow(new Object[]{maBan, tenBan, tang, moTa, trangThai});
                } else {
                    occupiedModel.addRow(new Object[]{maBan, tenBan, tang, moTa, trangThai, maDon});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTablesWithFilter(String filter) {
        DefaultTableModel emptyModel = (DefaultTableModel) emptyTable.getModel();
        DefaultTableModel occupiedModel = (DefaultTableModel) occupiedTable.getModel();
        emptyModel.setRowCount(0);
        occupiedModel.setRowCount(0);

        try {
            String sql = "SELECT * FROM BanAn";
            List<Object> params = new ArrayList<>();

            if (!filter.equals("toàn bộ")) {
                sql += " WHERE MoTa = ?";
                params.add(filter);
            }

            ResultSet rs = (ResultSet) DBConnection.executeQuery(sql, params);

            int total = 0, empty = 0, occupied = 0;

            while (rs.next()) {
                int maBan = rs.getInt("MaBan");
                String tenBan = rs.getString("TenBan");
                int tang = rs.getInt("Tang");
                String moTa = rs.getString("MoTa");
                String trangThai = rs.getString("TrangThai");
                int maDon = rs.getInt("MadonhangCho");

                total++;
                if (trangThai.equalsIgnoreCase("Trống")) {
                    emptyModel.addRow(new Object[]{maBan, tenBan, tang, moTa, trangThai});
                    empty++;
                } else {
                    occupiedModel.addRow(new Object[]{maBan, tenBan, tang, moTa, trangThai, maDon});
                    occupied++;
                }
            }

            totalTablesLabel.setText("Tổng số bàn: " + total);
            emptyTablesLabel.setText("Bàn trống: " + empty);
            occupiedTablesLabel.setText("Bàn có người: " + occupied);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Poppins", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        return label;
    }

 private void customizeButton(JButton button, Dimension size) {
        button.setFont(new Font("Poppins", Font.PLAIN, 14));
        button.setBackground(new Color(255, 102, 102));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(size);
    }

    private void customizeTable(JTable table) {
        table.setFont(new Font("Poppins", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(255, 102, 102));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(Color.LIGHT_GRAY);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Poppins", Font.BOLD, 14));
        header.setBackground(Color.DARK_GRAY);
        header.setForeground(Color.WHITE);
    }

    // Phương thức tải bảng (hiện tại không sử dụng)
    
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
