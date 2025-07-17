package view;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import Connect.DBConnection;
import view.FoodItem;
import view.FoodItem;
import javax.swing.table.TableCellRenderer;

public class dondanglam extends javax.swing.JPanel {

    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JComboBox<String> categoryComboBox;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private ArrayList<FoodItem> foodItems;
    private static final int ITEMS_PER_PAGE = 6; // Giới hạn số món hiển thị mỗi lần
    private ArrayList<FoodItem> selectedItems = new ArrayList<>();
    private int currentOrderId = 1;
    private JLabel label_doncho;
    private JTextField orderInputField;

    public dondanglam() {
        menuisekai();

    }

    private void menuisekai() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(40, 40, 40));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] categories = {"All"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        categoryComboBox.setBackground(Color.BLACK);
        categoryComboBox.setForeground(Color.WHITE);
        categoryComboBox.setPreferredSize(new Dimension(150, 30));
        categoryComboBox.addActionListener(e -> categoryComboBoxActionPerformed(e));
        loadCategoriesFromDB();

        JLabel pendingLabel = new JLabel("Đang chờ xử lý");
        pendingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pendingLabel.setForeground(Color.WHITE);

        label_doncho = new JLabel();
        label_doncho.setFont(new Font("Arial", Font.BOLD, 18));
        label_doncho.setForeground(Color.WHITE);

        int maxId = getMaxDonChoID();
        if (maxId != -1) {
            currentOrderId = maxId;
            label_doncho.setText("Đơn chờ số: " + currentOrderId);
        } else {
            label_doncho.setText("Không có đơn chờ");
        }

        JButton addToTableButton = new JButton("Thêm vào bảng");
        addToTableButton.setFont(new Font("Arial", Font.BOLD, 14));
        addToTableButton.setBackground(Color.BLACK);
        addToTableButton.setForeground(Color.WHITE);
        addToTableButton.addActionListener(e -> addToTable());

        JButton newOrderButton = new JButton("Tạo đơn hàng mới");
        newOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        newOrderButton.setBackground(Color.RED);
        newOrderButton.setForeground(Color.WHITE);
        newOrderButton.addActionListener(e -> taoDonHangChoMoi());

        JButton deleteOrderButton = new JButton("Xóa đơn hàng");
        deleteOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteOrderButton.setBackground(new Color(0, 100, 0));
        deleteOrderButton.setForeground(Color.WHITE);
        deleteOrderButton.addActionListener(e -> deleteSelectedOrder());

        JButton prevOrderButton = new JButton("<----");
        prevOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevOrderButton.setBackground(Color.GRAY);
        prevOrderButton.setForeground(Color.WHITE);
        prevOrderButton.addActionListener(e -> moveToPreviousOrder());

        JButton nextOrderButton = new JButton("---->");
        nextOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextOrderButton.setBackground(Color.GRAY);
        nextOrderButton.setForeground(Color.WHITE);
        nextOrderButton.addActionListener(e -> moveToNextOrder());

        // ✅ TextField và nút Tới đơn
        orderInputField = new JTextField(5);
        orderInputField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton goToOrderButton = new JButton("Tới đơn");
        goToOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        goToOrderButton.setBackground(Color.GRAY);
        goToOrderButton.setForeground(Color.WHITE);
        goToOrderButton.addActionListener(e -> goToSpecificOrder());

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        categoryPanel.setBackground(new Color(40, 40, 40));
        categoryPanel.add(new JLabel("Category: ") {
            {
                setFont(new Font("Arial", Font.BOLD, 14));
                setForeground(Color.WHITE);
            }
        });
        categoryPanel.add(categoryComboBox);
        categoryPanel.add(addToTableButton);
        categoryPanel.add(newOrderButton);
        categoryPanel.add(deleteOrderButton);
        categoryPanel.add(label_doncho);
        categoryPanel.add(prevOrderButton);
        categoryPanel.add(nextOrderButton);
        categoryPanel.add(orderInputField);
        categoryPanel.add(goToOrderButton);

        topPanel.add(categoryPanel, BorderLayout.WEST);
        topPanel.add(pendingLabel, BorderLayout.EAST);

        contentPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        contentPanel.setBackground(new Color(40, 40, 40));

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(900, 400));

        foodItems = new ArrayList<>();
        loadFoodItemsFromDB();

        tableModel = new DefaultTableModel(new String[]{"Tên món", "Số lượng", "Giá tiền", "Mã đơn xử lý"}, 0);
        orderTable = new JTable(tableModel);
        orderTable.setDefaultEditor(Object.class, null);
        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.add(tableScrollPane, BorderLayout.CENTER);

        JButton completeOrderButton = new JButton("Hoàn tất đơn");
        completeOrderButton.setFont(new Font("Arial", Font.BOLD, 14));
        completeOrderButton.setBackground(Color.BLACK);
        completeOrderButton.setForeground(Color.WHITE);
        completeOrderButton.addActionListener(e -> completeOrder());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(40, 40, 40));
        bottomPanel.add(completeOrderButton);
        orderPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(orderPanel, BorderLayout.EAST);

        loadDonDangCho();
    }

    private void loadFoodItemsFromDB() {
        foodItems.clear();
        String query = "SELECT Tenmonan, Giaban, Anhmonan FROM Monan";

        try {
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, new ArrayList<>());

            while (rs.next()) {
                String tenMon = rs.getString("Tenmonan");
                int gia = rs.getInt("Giaban");
                String anh = rs.getString("Anhmonan");

                // Tạo FoodItem và thêm vào danh sách
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
                foodItems.add(new FoodItem(createFoodItem(tenMon, gia, anh), tenMon, gia, anh, quantitySpinner));
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshMenu();
    }

    private JPanel createFoodItem(String name, int price, String imagePath) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
        itemPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        itemPanel.setBackground(new Color(60, 60, 60));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        JLabel priceLabel = new JLabel(String.format("%,d VND", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(Color.WHITE);

        ImageIcon imageIcon = null;
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource("anhicon/" + imagePath);
            if (imgURL != null) {
                imageIcon = new ImageIcon(imgURL);
            } else {
                imageIcon = new ImageIcon(); // fallback nếu không tìm thấy ảnh
                System.err.println("Không tìm thấy ảnh: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Image img = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));

        // Tạo JSpinner để chọn số lượng
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setFont(new Font("Arial", Font.BOLD, 14));

        itemPanel.addMouseListener(new MouseAdapter() {
            private boolean selected = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                selected = !selected;
                if (selected) {
                    itemPanel.setBackground(new Color(100, 100, 100));
                    JPanel itemPanelCopy = createFoodItem(name, price, imagePath);
                    selectedItems.add(new FoodItem(itemPanelCopy, name, price, imagePath, quantitySpinner));
                } else {
                    itemPanel.setBackground(new Color(60, 60, 60));
                    selectedItems.removeIf(item -> item.getTenMon().equals(name));
                }
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(new Color(60, 60, 60));
        controlPanel.add(priceLabel);
        controlPanel.add(quantitySpinner);

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(imageLabel, BorderLayout.CENTER);
        itemPanel.add(controlPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    // ✅ Phương thức xử lý khi nhấn nút "Tới đơn"
    private void goToSpecificOrder() {
        try {
            int inputId = Integer.parseInt(orderInputField.getText().trim());
            if (inputId < 1) {
                JOptionPane.showMessageDialog(this, "Mã đơn phải lớn hơn hoặc bằng 1.");
                return;
            }

            Connection conn = DBConnection.getConnect();
            String sql = "SELECT 1 FROM DonhangCho WHERE MadonhangCho = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, inputId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentOrderId = inputId;
                label_doncho.setText("Đơn chờ số: " + currentOrderId);
                loadDonDangCho();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đơn có mã: " + inputId);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToPreviousOrder() {
        int minOrderId = getMinDonChoID();
        int maxOrderId = getMaxDonChoID();
        if (currentOrderId > minOrderId) {
            currentOrderId--;
        } else {
            currentOrderId = maxOrderId; // quay vòng về đơn lớn nhất
        }
        label_doncho.setText("Đơn chờ số: " + currentOrderId);
        loadDonDangCho(); // load lại dữ liệu đơn chờ
    }

    private void moveToNextOrder() {
        int minOrderId = getMinDonChoID();
        int maxOrderId = getMaxDonChoID();
        if (currentOrderId < maxOrderId) {
            currentOrderId++;
        } else {
            currentOrderId = minOrderId; // quay vòng về đơn nhỏ nhất
        }
        label_doncho.setText("Đơn chờ số: " + currentOrderId);
        loadDonDangCho(); // load lại dữ liệu đơn chờ
    }

    private int getMinDonChoID() {
        int minID = -1;
        try {
            Connection conn = DBConnection.getConnect();
            String sql = "SELECT MIN(MadonhangCho) AS MinID FROM DonhangCho";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                minID = rs.getInt("MinID");
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minID;
    }

    private void completeOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int madonhangCho = (int) tableModel.getValueAt(selectedRow, 3); // Mã đơn chờ
        String tenMon = (String) tableModel.getValueAt(selectedRow, 0);
        int soLuong = (int) tableModel.getValueAt(selectedRow, 1);
        int giaBan = (int) tableModel.getValueAt(selectedRow, 2);

        // 🔍 Tìm mã món ăn
        List<Object> paramFind = new ArrayList<>();
        paramFind.add(tenMon);
        ResultSet rs = (ResultSet) DBConnection.executeQuery("SELECT Mamonan FROM Monan WHERE TenMonAn = ?", paramFind);

        int mamonan = -1;
        try {
            if (rs.next()) {
                mamonan = rs.getInt("Mamonan");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mamonan == -1) {
            return;
        }

        // ✅ Tạo đơn hàng nếu chưa có
        List<Object> paramNewOrder = new ArrayList<>();
        paramNewOrder.add(madonhangCho);              // Madonhang
        paramNewOrder.add(madonhangCho);              // MadonhangCho
        paramNewOrder.add("Chưa thanh toán");         // Trangthai

        try {
            DBConnection.executeQuery(
                    "INSERT INTO Donhang (Madonhang, Ngaydathang, MadonhangCho, Trangthai) VALUES (?, GETDATE(), ?, ?)",
                    paramNewOrder
            );
        } catch (Exception e) {
            System.out.println("Đơn đã tồn tại hoặc lỗi khác: " + e.getMessage());
        }

        // 🔍 Lấy mã đơn hàng chính
        List<Object> paramFindOrder = new ArrayList<>();
        paramFindOrder.add(madonhangCho);
        ResultSet rsNewOrder = (ResultSet) DBConnection.executeQuery(
                "SELECT Madonhang FROM Donhang WHERE MadonhangCho = ?", paramFindOrder
        );
        int madonhang = -1;
        try {
            if (rsNewOrder.next()) {
                madonhang = rsNewOrder.getInt("Madonhang");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (madonhang == -1) {
            return;
        }

        // 🔍 Kiểm tra món đã có trong đơn chưa
        List<Object> checkExistParams = new ArrayList<>();
        checkExistParams.add(madonhang);
        checkExistParams.add(mamonan);
        ResultSet rsExist = (ResultSet) DBConnection.executeQuery(
                "SELECT Soluong, Tonggiatri FROM Chitietdonhang WHERE Madonhang = ? AND Mamonan = ?",
                checkExistParams
        );

        try {
            if (rsExist.next()) {
                // ✅ Món đã có → Cập nhật số lượng & tổng giá trị
                int soLuongCu = rsExist.getInt("Soluong");
                int tongGiaCu = rsExist.getInt("Tonggiatri");

                int soLuongMoi = soLuongCu + soLuong;
                int tongGiaMoi = tongGiaCu + (soLuong * giaBan);

                List<Object> updateParams = new ArrayList<>();
                updateParams.add(soLuongMoi);
                updateParams.add(giaBan); // có thể update lại giá nếu muốn
                updateParams.add(tongGiaMoi);
                updateParams.add(madonhang);
                updateParams.add(mamonan);

                DBConnection.executeQuery(
                        "UPDATE Chitietdonhang SET Soluong = ?, Giaban = ?, Tonggiatri = ? WHERE Madonhang = ? AND Mamonan = ?",
                        updateParams
                );
            } else {
                // ❌ Món chưa có → Thêm mới
                List<Object> insertParams = new ArrayList<>();
                insertParams.add(madonhang);
                insertParams.add(mamonan);
                insertParams.add(soLuong);
                insertParams.add(giaBan);
                insertParams.add(soLuong * giaBan);
                DBConnection.executeQuery(
                        "INSERT INTO Chitietdonhang (Madonhang, Mamonan, Soluong, Giaban, Tonggiatri) VALUES (?, ?, ?, ?, ?)",
                        insertParams
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ❌ Xoá món đó khỏi chi tiết đơn chờ
        List<Object> paramDeleteCT = new ArrayList<>();
        paramDeleteCT.add(madonhangCho);
        paramDeleteCT.add(mamonan);
        DBConnection.executeQuery(
                "DELETE FROM ChitietdonhangCho WHERE MadonhangCho = ? AND Mamonan = ?", paramDeleteCT
        );

        // ✅ Cập nhật giao diện
        tableModel.removeRow(selectedRow);
    }

    // ✅ HÀM RIÊNG TẠO ĐƠN CHỜ
    public void taoDonHangChoMoi() {
        try {
            DBConnection.executeQuery("INSERT INTO DonhangCho DEFAULT VALUES;", new ArrayList<>());
            ResultSet rs = (ResultSet) DBConnection.executeQuery("SELECT IDENT_CURRENT('DonhangCho') AS OrderID;", new ArrayList<>());
            if (rs.next()) {
                currentOrderId = rs.getInt("OrderID");
                label_doncho.setText("Đơn chờ số: " + currentOrderId);
                System.out.println("Đã tạo đơn hàng mới với ID: " + currentOrderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getMaxDonChoID() {
        int maxID = -1;
        try {
            Connection conn = DBConnection.getConnect();
            String sql = "SELECT MAX(MadonhangCho) AS MaxID FROM DonhangCho";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                maxID = rs.getInt("MaxID");
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxID;
    }

    // ✅ CHỈ THÊM MÓN VÀO ĐƠN ĐÃ TẠO
    private void addToTable() {
        if (currentOrderId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo đơn mới trước khi thêm món.");
            return;
        }

        try {
            for (FoodItem item : selectedItems) {
                int mamonan = getMamonan(item.getTenMon());
                int gia = item.getGia();
                int soluong = (int) item.getQuantitySpinner().getValue();

                List<Object> checkParams = List.of(currentOrderId, mamonan);
                ResultSet rsCheck = (ResultSet) DBConnection.executeQuery("SELECT Soluong FROM ChitietdonhangCho WHERE MadonhangCho = ? AND Mamonan = ?", checkParams);

                if (rsCheck.next()) {
                    List<Object> updateParams = List.of(soluong, currentOrderId, mamonan);
                    DBConnection.executeQuery("UPDATE ChitietdonhangCho SET Soluong = Soluong + ? WHERE MadonhangCho = ? AND Mamonan = ?", updateParams);
                } else {
                    List<Object> insertParams = List.of(currentOrderId, mamonan, soluong, gia);
                    DBConnection.executeQuery("INSERT INTO ChitietdonhangCho (MadonhangCho, Mamonan, Soluong, Giaban) VALUES (?, ?, ?, ?);", insertParams);
                }

                boolean found = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(item.getTenMon())) {
                        int currentQty = (int) tableModel.getValueAt(i, 1);
                        tableModel.setValueAt(currentQty + soluong, i, 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    tableModel.addRow(new Object[]{item.getTenMon(), soluong, gia, currentOrderId});
                }
            }
            selectedItems.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDonDangCho() {
        tableModel.setRowCount(0);
        String query = """
        SELECT m.Tenmonan, cdc.Soluong, cdc.Giaban, dhc.MadonhangCho
        FROM ChitietdonhangCho cdc
        JOIN Monan m ON cdc.Mamonan = m.Mamonan
        JOIN DonhangCho dhc ON cdc.MadonhangCho = dhc.MadonhangCho
        """;
        try {
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, new ArrayList<>());
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("Tenmonan"),
                    rs.getInt("Soluong"),
                    rs.getInt("Giaban"),
                    rs.getInt("MadonhangCho")
                });
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Hàm lấy mã đơn chờ tiếp theo
    private void deleteSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            return; // Nếu không chọn dòng nào, không làm gì cả
        }
        // Lấy mã đơn hàng chờ và tên món từ bảng hiển thị
        int orderId = (int) tableModel.getValueAt(selectedRow, 3);
        String tenMon = (String) tableModel.getValueAt(selectedRow, 0);

        // Lấy mã món ăn từ tên món
        int mamonan = getMamonan(tenMon);
        if (mamonan == -1) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã món cho: " + tenMon);
            return;
        }

        // Xoá chỉ dòng chi tiết đơn có mã đơn chờ và mã món tương ứng
        List<Object> params = List.of(orderId, mamonan);
        DBConnection.executeQuery(
                "DELETE FROM ChitietdonhangCho WHERE MadonhangCho = ? AND Mamonan = ?",
                params
        );

        // Xoá dòng hiển thị tương ứng trên giao diện
        tableModel.removeRow(selectedRow);
    }

    //sê
    private void loadCategoriesFromDB() {
        String query = "SELECT Tenloai FROM Loaimonan";  // Truy vấn lấy tất cả loại món ăn từ bảng Loaimonan
        try {
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, new ArrayList<>());
            List<String> categories = new ArrayList<>();
            categories.add("All");  // Thêm lựa chọn "All" cho tất cả loại món ăn

            while (rs.next()) {
                String category = rs.getString("Tenloai");
                categories.add(category);
            }

            // Load danh sách loại món ăn vào ComboBox
            categoryComboBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void categoryComboBoxActionPerformed(ActionEvent e) {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        loadFoodItemsByCategory(selectedCategory);
    }

    private void loadFoodItemsByCategory(String category) {
        foodItems.clear();  // Xóa danh sách món ăn hiện tại

        String query;
        List<Object> params = new ArrayList<>();
        if ("All".equals(category)) {
            query = "SELECT Tenmonan, Giaban, Anhmonan FROM Monan";  // Lấy tất cả món ăn
        } else {
            query = "SELECT m.Tenmonan, m.Giaban, m.Anhmonan FROM Monan m "
                    + "JOIN Loaimonan l ON m.Maloaimonan = l.Maloaimonan "
                    + "WHERE l.Tenloai = ?";  // Lọc món ăn theo loại
            params.add(category);  // Thêm category vào tham số
        }

        try {
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, params);

            while (rs.next()) {
                String tenMon = rs.getString("Tenmonan");
                int gia = rs.getInt("Giaban");
                String anh = rs.getString("Anhmonan");

                // Tạo FoodItem và thêm vào danh sách
                JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
                foodItems.add(new FoodItem(createFoodItem(tenMon, gia, anh), tenMon, gia, anh, quantitySpinner));
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cập nhật giao diện menu
        refreshMenu();
    }

//chưc năng 
    private void refreshMenu() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        JPanel foodContainer = new JPanel();
        foodContainer.setLayout(new GridLayout(0, 2, 15, 15));
        foodContainer.setBackground(new Color(40, 40, 40));

        for (FoodItem item : foodItems) {
            foodContainer.add(item.getPanel());
        }

        int itemHeight = 150;
        int visibleRows = 3;
        int panelHeight = visibleRows * itemHeight;

        foodContainer.setPreferredSize(new Dimension(600, foodItems.size() / 2 * itemHeight));
        scrollPane.setPreferredSize(new Dimension(600, panelHeight));

        contentPanel.add(foodContainer, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private int getMamonan(String tenMon) {
        String query = "SELECT Mamonan FROM Monan WHERE Tenmonan = ?";
        try {
            List<Object> params = List.of(tenMon);
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, params);
            if (rs.next()) {
                return rs.getInt("Mamonan");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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
