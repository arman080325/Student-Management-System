package src;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

class DisplayFrame extends JFrame implements ActionListener {

	JTable table;
	JButton addNewBtn, deleteBtn, clearBtn, updateBtn;
	DefaultTableModel model;
	JLabel backgroundLabel;
	JTextField searchField;
	JComboBox<String> filterBox;
	JButton searchButton, resetButton;

	public DisplayFrame() {
		setTitle("Student Details");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		try {
			BufferedImage originalImg = ImageIO.read(new File("icons/college2.jpg"));
			backgroundLabel = new JLabel(new ImageIcon(originalImg)) {
				@Override
				public void paintComponent(Graphics g) {
					Image scaled = originalImg.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
					BufferedImage buffered = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = buffered.createGraphics();
					g2.drawImage(scaled, 0, 0, null);
					g2.dispose();

					float[] blurKernel = new float[25];
					for (int i = 0; i < 25; i++) blurKernel[i] = 1f / 25f;
					ConvolveOp op = new ConvolveOp(new Kernel(5, 5, blurKernel), ConvolveOp.EDGE_NO_OP, null);
					BufferedImage blurredImage = op.filter(buffered, null);

					g.drawImage(blurredImage, 0, 0, null);
					super.paintComponent(g);
				}
			};
			backgroundLabel.setLayout(new BorderLayout());
			setContentPane(backgroundLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.setOpaque(false);

		searchField = new JTextField(20);
		filterBox = new JComboBox<>(new String[]{"Name", "Roll No", "Course", "Branch"});
		searchButton = new JButton("Search");
		resetButton = new JButton("Reset");

		searchButton.addActionListener(e -> performSearch());
		resetButton.addActionListener(e -> loadData());

		topPanel.add(new JLabel("Search:"));
		topPanel.add(searchField);
		topPanel.add(new JLabel("Filter by:"));
		topPanel.add(filterBox);
		topPanel.add(searchButton);
		topPanel.add(resetButton);

		backgroundLabel.add(topPanel, BorderLayout.NORTH);

		String[] columnNames = {
				"Name", "Father's Name", "Roll No", "DOB", "Address", "Phone", "Email",
				"SIC", "Class XII", "Aadhar", "Course", "Branch"
		};
		model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		table.setFont(new Font("Times New Roman", Font.BOLD, 14));
		table.setRowHeight(28);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setOpaque(true);
		renderer.setBackground(new Color(255, 255, 255, 220));
		renderer.setForeground(Color.BLACK);

		Font smallerFont = new Font("Times New Roman", Font.BOLD, 11);

		table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value,
														   boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setFont(smallerFont);
				return c;
			}
		});

		table.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value,
														   boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setFont(smallerFont);
				return c;
			}
		});

		for (int i = 0; i < table.getColumnCount(); i++) {
			if (i != 6 && i != 11)
				table.getColumnModel().getColumn(i).setCellRenderer(renderer);

			if (i == 0 || i == 1 || i == 4 || i == 6 || i == 9)
				table.getColumnModel().getColumn(i).setPreferredWidth(200);
			else if (i == 11)
				table.getColumnModel().getColumn(i).setPreferredWidth(220);
			else
				table.getColumnModel().getColumn(i).setPreferredWidth(100);
		}

		table.getTableHeader().setFont(new Font("Algerian", Font.BOLD, 16));
		table.getTableHeader().setOpaque(false);
		table.getTableHeader().setForeground(Color.BLACK);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		backgroundLabel.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);

		addNewBtn = new JButton("Add New Student");
		deleteBtn = new JButton("Delete Selected Record");
		clearBtn = new JButton("Clear All Records");
		updateBtn = new JButton("Update Selected Record");

		addNewBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		updateBtn.addActionListener(this);

		addHoverEffect(addNewBtn, new Color(0, 153, 0));  // Green
		addHoverEffect(deleteBtn, new Color(204, 0, 0));  // Red
		addHoverEffect(clearBtn, new Color(255, 0, 0));   // Red
		addHoverEffect(updateBtn, new Color(0, 102, 204)); // Blue

		bottomPanel.add(addNewBtn);
		bottomPanel.add(updateBtn);
		bottomPanel.add(deleteBtn);
		bottomPanel.add(clearBtn);

		backgroundLabel.add(bottomPanel, BorderLayout.SOUTH);

		loadData();
		setVisible(true);
	}

	private void addHoverEffect(JButton button, Color hoverColor) {
		Color original = button.getBackground();
		button.setContentAreaFilled(true);
		button.setOpaque(true);
		button.setBackground(original);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hoverColor);
				button.setForeground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(original);
				button.setForeground(Color.BLACK);
			}
		});
	}

	void loadData() {
		model.setRowCount(0);
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");

			String sql = "SELECT * FROM student_data1";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String[] row = {
						rs.getString("name"),
						rs.getString("fname"),
						rs.getString("rollno"),
						rs.getString("dob"),
						rs.getString("address"),
						rs.getString("phone"),
						rs.getString("email"),
						rs.getString("sic"),
						rs.getString("xii"),
						rs.getString("aadhar"),
						rs.getString("course"),
						rs.getString("department")
				};
				model.addRow(row);
			}

			rs.close();
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void performSearch() {
		String keyword = searchField.getText().trim();
		String filter = filterBox.getSelectedItem().toString().toLowerCase();
		model.setRowCount(0);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");

			String column = switch (filter) {
				case "name" -> "name";
				case "roll no" -> "rollno";
				case "course" -> "course";
				case "branch" -> "department";
				default -> "";
			};

			String sql = "SELECT * FROM student_data1 WHERE LOWER(" + column + ") LIKE ?";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, "%" + keyword.toLowerCase() + "%");

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				String[] row = {
						rs.getString("name"),
						rs.getString("fname"),
						rs.getString("rollno"),
						rs.getString("dob"),
						rs.getString("address"),
						rs.getString("phone"),
						rs.getString("email"),
						rs.getString("sic"),
						rs.getString("xii"),
						rs.getString("aadhar"),
						rs.getString("course"),
						rs.getString("department")
				};
				model.addRow(row);
			}

			rs.close();
			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void deleteSelectedRecord(String rollNo) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");

			String sql = "DELETE FROM student_data1 WHERE rollno = ?";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, rollNo);
			pst.executeUpdate();

			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void clearAllRecords() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "arman0803");

			String sql = "DELETE FROM student_data1";
			PreparedStatement pst = con.prepareStatement(sql);
			pst.executeUpdate();

			pst.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addNewBtn) {
			dispose();
			new InsertData1();
		} else if (e.getSource() == deleteBtn) {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				String rollNo = (String) model.getValueAt(selectedRow, 2);
				int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete Roll No: " + rollNo + "?", "Confirm", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.YES_OPTION) {
					deleteSelectedRecord(rollNo);
					loadData();
				}
			} else {
				JOptionPane.showMessageDialog(this, "Please select a row to delete.");
			}
		} else if (e.getSource() == clearBtn) {
			int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete ALL records?", "Confirm", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				clearAllRecords();
				loadData();
			}
		} else if (e.getSource() == updateBtn) {
			int selectedRow = table.getSelectedRow();
			if (selectedRow != -1) {
				Vector rowData = new Vector();
				for (int i = 0; i < table.getColumnCount(); i++) {
					rowData.add(model.getValueAt(selectedRow, i));
				}
				new UpdateFrame(rowData, this);  // pass 'this'
			} else {
				JOptionPane.showMessageDialog(this, "Please select a row to update.");
			}
		}

	}

	public static void main(String[] args) {
		new DisplayFrame();
	}
}



//"Arial"	new Font("Arial", Font.PLAIN, 14)	Clean sans-serif font
//"Times New Roman"	new Font("Times New Roman", Font.BOLD, 16)	Classic serif font
//"Courier New"	new Font("Courier New", Font.PLAIN, 12)	Monospaced (typewriter-style) font
//"Verdana"	new Font("Verdana", Font.ITALIC, 14)	Readable sans-serif font
//"Tahoma"	new Font("Tahoma", Font.BOLD, 13)	Crisp sans-serif, popular in Windows
//"Comic Sans MS"	new Font("Comic Sans MS", Font.PLAIN, 15)	Informal, playful look
//"Georgia"	new Font("Georgia", Font.PLAIN, 14)	Elegant serif font
//"Impact"	new Font("Impact", Font.BOLD, 18)	Bold, condensed display font
//"Algerian"	new Font("Algerian", Font.BOLD, 20)	Decorative display font (if installed)
