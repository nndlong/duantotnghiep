
package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Connect.DBConnection;
import java.awt.geom.RoundRectangle2D;

public class Account extends javax.swing.JPanel {
    private JTextField emailField, passwordField, phoneField, addressField, birthField;
    private JComboBox<String> genderComboBox;
    private JLabel nameLabel, avatarLabel;
    private JButton saveButton;
    private List<JTextField> textFields = new ArrayList<>();
    private String userEmail;
    
  public Account(String userEmail) {
        this.userEmail = userEmail;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 30, 20, 20));
        setBackground(new Color(40, 40, 40)); // Background color 40, 40, 40

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(40, 40, 40)); // Main panel background 40, 40, 40

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 40, 40)); // Header panel background 40, 40, 40
        headerPanel.setPreferredSize(new Dimension(900, 60));
        JLabel headerLabel = new JLabel("Thông tin tài khoản");
        headerLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE); // White text color
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel);

        // Profile Panel
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setBackground(new Color(40, 40, 40)); // Profile panel background 40, 40, 40
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        nameLabel = new JLabel("Tên");
        nameLabel.setFont(new Font("Poppins", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE); // White text color

        JButton editNameButton = new JButton("✏️"); // Sử dụng emoji làm icon
        editNameButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); // Font hỗ trợ emoji
        editNameButton.setBackground(new Color(40, 40, 40)); // Button background 40, 40, 40
        editNameButton.setForeground(Color.WHITE); // White text color
        editNameButton.setBorderPainted(false); // Loại bỏ viền xung quanh button
        editNameButton.setFocusPainted(false); // Loại bỏ hiệu ứng focus
        editNameButton.setContentAreaFilled(false); // Loại bỏ nền trong của button
        editNameButton.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog("Hãy nhập tên mới:");
            if (newName != null && !newName.trim().isEmpty()) {
                nameLabel.setText(newName);
            }
        });

        profilePanel.add(nameLabel);
        profilePanel.add(Box.createHorizontalStrut(20));
        profilePanel.add(editNameButton);
        mainPanel.add(profilePanel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setBackground(new Color(40, 40, 40)); // Form panel background 40, 40, 40

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Email:", "Mật khẩu:", "Sđt:", "Địa chỉ:", "Ngày sinh:", "Giới tính:"};
        JTextField[] textFieldsArr = {
            emailField = new JTextField(),
            passwordField = new JTextField(),
            phoneField = new JTextField(),
            addressField = new JTextField(),
            birthField = new JTextField()
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Poppins", Font.BOLD, 14));
            label.setForeground(Color.WHITE); // White text color
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            if (labels[i].equals("Giới tính:")) {
                genderComboBox = new JComboBox<>(new String[]{"Nam", "Nữ","Lgbt"});
                genderComboBox.setPreferredSize(new Dimension(400, 40));
                genderComboBox.setFont(new Font("Poppins", Font.PLAIN, 14));
                genderComboBox.setBackground(new Color(40, 40, 40)); // Dark background
                genderComboBox.setForeground(Color.WHITE); // White text color
                genderComboBox.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // White border
                formPanel.add(genderComboBox, gbc);
            } else {
                textFieldsArr[i].setEnabled(false);
                textFieldsArr[i].setPreferredSize(new Dimension(400, 40));
                textFieldsArr[i].setFont(new Font("Poppins", Font.PLAIN, 14));
                textFieldsArr[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE), // White border
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                textFieldsArr[i].setBackground(new Color(40, 40, 40)); // Dark background
                textFieldsArr[i].setForeground(Color.WHITE); // White text color
                textFields.add(textFieldsArr[i]);
                formPanel.add(textFieldsArr[i], gbc);
            }

            gbc.gridx = 2;
            JButton editButton = new JButton("✏️"); // Sử dụng emoji làm icon
            editButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); // Font hỗ trợ emoji
            editButton.setBackground(new Color(40, 40, 40)); // Button background 40, 40, 40
            editButton.setForeground(Color.WHITE); // White text color
            editButton.setBorderPainted(false); // Loại bỏ viền xung quanh button
            editButton.setFocusPainted(false); // Loại bỏ hiệu ứng focus
            editButton.setContentAreaFilled(false); // Loại bỏ nền trong của button
            int index = textFields.size() - 1;
            editButton.addActionListener(e -> textFields.get(index).setEnabled(true));
            formPanel.add(editButton, gbc);
        }
        mainPanel.add(formPanel);

        saveButton = new RoundedButton("Lưu thông tin", 20); // Sử dụng RoundedButton
        saveButton.setFont(new Font("Poppins", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(40, 40, 40));
        saveButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(150, 50));
        saveButton.addActionListener(e -> saveUserData());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(40, 40, 40)); // Nền 40, 40, 40
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        loadUserData(userEmail);
    }
    
    private void loadUserData(String userEmail) {
        try {
            String query = "SELECT N.Hoten, N.NgaySinh, N.Sdt, N.DiaChi, N.GioiTinh, T.Email, T.Matkhau " +
                           "FROM Taikhoan T " +
                           "JOIN Nguoidung N ON T.Manguoidung = N.Manguoidung " +
                           "WHERE T.Email = ?";
            List<Object> params = new ArrayList<>();
            params.add(userEmail);
            ResultSet rs = (ResultSet) DBConnection.executeQuery(query, params);

            if (rs.next()) {
                nameLabel.setText(rs.getString("Hoten"));
                emailField.setText(rs.getString("Email"));
                passwordField.setText(rs.getString("Matkhau"));
                phoneField.setText(rs.getString("Sdt"));
                addressField.setText(rs.getString("DiaChi"));
                birthField.setText(rs.getString("NgaySinh"));
                genderComboBox.setSelectedItem(rs.getString("GioiTinh"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveUserData() {
        try {
            String query = "UPDATE Nguoidung SET Hoten=?, NgaySinh=?, Sdt=?, DiaChi=?, GioiTinh=? WHERE Manguoidung=(SELECT Manguoidung FROM Taikhoan WHERE Email=?)";
            List<Object> params = new ArrayList<>();
            params.add(nameLabel.getText());
            params.add(birthField.getText());
            params.add(phoneField.getText());
            params.add(addressField.getText());
            params.add(genderComboBox.getSelectedItem());
            params.add(userEmail);
            DBConnection.executeQuery(query, params);

            query = "UPDATE Taikhoan SET Matkhau=? WHERE Email=?";
            params.clear();
            params.add(passwordField.getText());
            params.add(userEmail);
            DBConnection.executeQuery(query, params);

            JOptionPane.showMessageDialog(this, "Thông tin đã được cập nhật!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }
    
    
     class RoundedButton extends JButton {

        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false); // Loại bỏ nền mặc định của button
            setFocusPainted(false); // Loại bỏ viền focus
            setBorderPainted(false); // Loại bỏ viền border
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ nền bo góc
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // Vẽ viền bo góc
            g2.setColor(Color.WHITE); // Màu viền
            g2.setStroke(new BasicStroke(2)); // Độ dày viền
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            super.paintComponent(g2);
            g2.dispose();
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
