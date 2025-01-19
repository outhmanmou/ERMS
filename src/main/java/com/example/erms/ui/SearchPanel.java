package com.example.erms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.example.erms.dao.Employee;
import com.example.erms.service.EmployeeService;

import net.miginfocom.swing.MigLayout;

public class SearchPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final EmployeeService employeeService;
    private final JTextField searchField;
    private final JComboBox<String> searchCriteria;
    private final JComboBox<String> departmentFilter;
    private final JComboBox<String> statusFilter;
    private final JTable resultsTable;
    private final DefaultTableModel tableModel;

    public SearchPanel(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setLayout(new MigLayout("fillx", "[grow]", "[]10[]10[]"));

        // Initialize components
        searchField = new JTextField(30);
        searchCriteria = new JComboBox<>(new String[]{
            "All Fields", "Name", "Job Title", "Email", "Department"
        });
        departmentFilter = new JComboBox<>(new String[]{
            "All Departments", "HR", "IT", "Finance", "Marketing", "Operations"
        });
        statusFilter = new JComboBox<>(new String[]{
            "All Statuses", "Active", "Inactive", "On Leave"
        });

        // Initialize table
        String[] columns = {
            "ID", "Full Name", "Department", "Job Title", 
            "Status", "Email", "Phone", "Hire Date"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        
        initComponents();
    }

    private void initComponents() {
        // Search Panel
        JPanel searchControls = new JPanel(new MigLayout("fillx", "[][grow][]", "[][]"));
        searchControls.setBorder(BorderFactory.createTitledBorder("Search Employees"));

        // Add search components
        searchControls.add(new JLabel("Search:"));
        searchControls.add(searchField, "growx");
        searchControls.add(searchCriteria, "wrap");

        // Add filters
        searchControls.add(new JLabel("Department:"));
        searchControls.add(departmentFilter);
        searchControls.add(new JLabel("Status:"));
        searchControls.add(statusFilter, "wrap");

        // Add buttons
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton exportButton = new JButton("Export Results");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);
        searchControls.add(buttonPanel, "span 4, align right, wrap");

        // Results Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Search Results"));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add record count label
        JLabel recordCountLabel = new JLabel("Records found: 0");
        tablePanel.add(recordCountLabel, BorderLayout.SOUTH);

        // Add components to main panel
        add(searchControls, "growx, wrap");
        add(tablePanel, "grow, push, wrap");

        // Add action listeners
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        exportButton.addActionListener(e -> exportResults());
        
        // Add key listener for enter key in search field
        searchField.addActionListener(e -> performSearch());

        // Update record count after search
        addPropertyChangeListener("tableUpdated", evt -> 
            recordCountLabel.setText("Records found: " + tableModel.getRowCount())
        );
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();
        String department = departmentFilter.getSelectedItem().toString();
        String status = statusFilter.getSelectedItem().toString();
        String searchField = searchCriteria.getSelectedItem().toString();

        try {
            List<Employee> results;
            
            // If department or status filters are active, use findEmployees
            if (!department.equals("All Departments") || !status.equals("All Statuses")) {
                results = employeeService.findEmployees(
                    department.equals("All Departments") ? null : department,
                    status.equals("All Statuses") ? null : status
                );
            } else {
                // If search text is present, use searchEmployees
                results = employeeService.searchEmployees(
                    searchText,
                    searchField.equals("All Fields") ? null : searchField.toLowerCase()
                );
            }

            updateTable(results);
            firePropertyChange("tableUpdated", false, true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error performing search: " + ex.getMessage(),
                "Search Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTable(List<Employee> employees) {
        // Clear existing rows
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        // Add new rows
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getEmploymentStatus(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getHireDate()
            });
        }
    }

    private void clearSearch() {
        searchField.setText("");
        searchCriteria.setSelectedIndex(0);
        departmentFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        updateTable(employeeService.findEmployees(null, null));
        firePropertyChange("tableUpdated", false, true);
    }

    private void exportResults() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No results to export.",
                "Export",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Results");
        fileChooser.setSelectedFile(new File("employee_search_results.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                exportToCSV(file);
                JOptionPane.showMessageDialog(
                    this,
                    "Results exported successfully to: " + file.getAbsolutePath(),
                    "Export Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error exporting results: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void exportToCSV(File file) throws Exception {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write headers
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                header.append(tableModel.getColumnName(i)).append(",");
            }
            writer.println(header.substring(0, header.length() - 1));

            // Write data
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                StringBuilder line = new StringBuilder();
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    line.append(tableModel.getValueAt(row, col)).append(",");
                }
                writer.println(line.substring(0, line.length() - 1));
            }
        }
    }
}