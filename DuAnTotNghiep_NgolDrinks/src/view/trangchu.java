package view;

import view.DangNhap;
import view.Account;
import view.dondahoantat;
import view.banan;
import view.dondanglam;
import view.Home;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;

public class trangchu extends javax.swing.JFrame {

    private JPanel contentPanel;
    private String userEmail;

    public trangchu(String email) {
        this.userEmail = email;
        initUI();
    }

    private void initUI() {
        setTitle("Hệ Thống Đặt Đồ Ăn Isekai");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Màu sắc
        Color backgroundColor = new Color(40, 40, 40);
        Color sidebarColor = new Color(30, 30, 30);
        Color textColor = Color.WHITE;

        // Sidebar
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(100, getHeight()));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo
        JLabel logo = createImageLabel("/images/uB7AJOL3Qu2gu6cay6I75g-removebg-preview.png", 80, 80);
        gbc.gridy = 0;
        sidebar.add(logo, gbc);

        // Sidebar Buttons
        String[] buttonIcons = {"🏠", "🍽", "📦", "🍛", "👤", "🚪"};
        String[] buttonLabels = {"Trang Chủ", "Tạo đơn", "Đơn đã làm", "Bàn ăn", "Tài Khoản", "Đăng Xuất"};
        for (int i = 0; i < buttonIcons.length; i++) {
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setBackground(sidebarColor);
            buttonPanel.setPreferredSize(new Dimension(80, 80));

            JLabel iconLabel = new JLabel(buttonIcons[i], SwingConstants.CENTER);
            iconLabel.setFont(new Font("Poppins", Font.PLAIN, 24));
            iconLabel.setForeground(textColor);

            JLabel textLabel = new JLabel(buttonLabels[i], SwingConstants.CENTER);
            textLabel.setFont(new Font("Poppins", Font.PLAIN, 12));
            textLabel.setForeground(textColor);

            buttonPanel.add(iconLabel, BorderLayout.CENTER);
            buttonPanel.add(textLabel, BorderLayout.SOUTH);
            buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            gbc.gridy++;
            sidebar.add(buttonPanel, gbc);

            // Thêm sự kiện click cho nút "Trang Chủ"
            if (i == 0) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showHomePanel();
                    }
                });
            } else if (buttonLabels[i].equals("Tài Khoản")) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showtaikhoan();
                    }
                });
            } else if (buttonLabels[i].equals("Tạo đơn")) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showdondanglamPanel();
                    }
                });
            } else if (buttonLabels[i].equals("Đơn đã làm")) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showdondahoanthanhPanel();
                    }
                });
            } else if (buttonLabels[i].equals("Bàn ăn")) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showbanan();
                    }
                });
            } else if (buttonLabels[i].equals("Đăng Xuất")) {
                buttonPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        logout();
                    }
                });
            }

        }

        // Content Panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị Home panel ngay từ đầu
        showHomePanel();

        setVisible(true);
    }

    private void showHomePanel() {
        contentPanel.removeAll();
        contentPanel.add(new Home(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showdondanglamPanel() {
        contentPanel.removeAll();
        contentPanel.add(new dondanglam(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showdondahoanthanhPanel() {
        contentPanel.removeAll();
        contentPanel.add(new dondahoantat(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showbanan() {
        contentPanel.removeAll();
        contentPanel.add(new banan(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JLabel createImageLabel(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage();
        double aspectRatio = (double) img.getWidth(null) / img.getHeight(null);
        if (aspectRatio > 1) {
            height = (int) (width / aspectRatio);
        } else {
            width = (int) (height * aspectRatio);
        }
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    private void showtaikhoan() {
        contentPanel.removeAll();
        contentPanel.add(new Account(userEmail), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose(); // Đóng cửa sổ hiện tại
            new DangNhap().setVisible(true); // Mở lại màn hình đăng nhập
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(trangchu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(trangchu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(trangchu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(trangchu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        String email = "user@gmail.com";
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new trangchu(email).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
