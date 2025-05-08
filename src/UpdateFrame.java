package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class UpdateFrame extends JFrame {

    private JTextField nameField, fnameField, rollField, dobField, addressField,
            phoneField, emailField, sicField, xiiField, aadharField, courseField, departmentField;
    private JButton updateBtn, cancelBtn;
    private DisplayFrame parentFrame;

    public UpdateFrame(Vector data, DisplayFrame parentFrame) {
        this.parentFrame = parentFrame;

        setTitle("Update Student Record");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(14, 2, 15, 15));

        add(new JLabel("Name:"));
        nameField = new JTextField((String) data.get(0));
        add(nameField);

        add(new JLabel("Father's Name:"));
        fnameField = new JTextField((String) data.get(1));
        add(fnameField);

        add(new JLabel("Roll No:"));
        rollField = new JTextField((String) data.get(2));
        rollField.setEditable(false); // Keep Roll No non-editable
        add(rollField);

        add(new JLabel("DOB:"));
        dobField = new JTextField((String) data.get(3));
        add(dobField);

        add(new JLabel("Address:"));
        addressField = new JTextField((String) data.get(4));
        add(addressField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField((String) data.get(5));
        add(phoneField);

        add(new JLabel("Email:"));
        emailField = new JTextField((String) data.get(6));
        add(emailField);

        add(new JLabel("SIC:"));
        sicField = new JTextField((String) data.get(7));
        add(sicField);

        add(new JLabel("Class XII (%):"));
        xiiField = new JTextField((String) data.get(8));
        add(xiiField);

        add(new JLabel("Aadhar:"));
        aadharField = new JTextField((String) data.get(9));
        add(aadharField);

        add(new JLabel("Course:"));
        courseField = new JTextField((String) data.get(10));
        add(courseField);

        add(new JLabel("Branch:"));
        departmentField = new JTextField((String) data.get(11));
        add(departmentField);

        updateBtn = new JButton("Update");
        cancelBtn = new JButton("Cancel");

        // Set default button styles
        updateBtn.setBackground(Color.LIGHT_GRAY);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        updateBtn.setOpaque(true);
        cancelBtn.setOpaque(true);
        updateBtn.setBorderPainted(true);
        cancelBtn.setBorderPainted(true);

        // Hover effects for Update button (Blue)
        updateBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                updateBtn.setBackground(Color.BLUE);
                updateBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                updateBtn.setBackground(Color.WHITE);
                updateBtn.setForeground(Color.BLACK);
            }
        });

        // Hover effects for Cancel button (Red)
        cancelBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(Color.RED);
                cancelBtn.setForeground(Color.WHITE);
            }

            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(Color.WHITE);
                cancelBtn.setForeground(Color.BLACK);
            }
        });

        add(updateBtn);
        add(cancelBtn);

        updateBtn.addActionListener(e -> updateRecord());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void updateRecord() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");

            String sql = "UPDATE student_data1 SET name=?, fname=?, dob=?, address=?, phone=?, email=?, sic=?, xii=?, aadhar=?, course=?, department=? WHERE rollno=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, nameField.getText());
            pst.setString(2, fnameField.getText());
            pst.setString(3, dobField.getText());
            pst.setString(4, addressField.getText());
            pst.setString(5, phoneField.getText());
            pst.setString(6, emailField.getText());
            pst.setString(7, sicField.getText());
            pst.setString(8, xiiField.getText());
            pst.setString(9, aadharField.getText());
            pst.setString(10, courseField.getText());
            pst.setString(11, departmentField.getText());
            pst.setString(12, rollField.getText());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
                parentFrame.loadData(); // Refresh main table
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update Failed.");
            }

            pst.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
