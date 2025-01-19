package com.example.erms.ui;

import com.example.erms.dao.Employee;
import com.example.erms.service.EmployeeService;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class EmployeePanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final EmployeeService employeeService;
    private final JTextField nameField;
    private final JTextField jobTitleField;
    private final JComboBox<String> departmentCombo;
    private final JXDatePicker hireDatePicker;
    private final JComboBox<String> statusCombo;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextArea addressArea;
    private final JTable employeeTable;
    private final DefaultTableModel tableModel;
    private Employee currentEmployee;

    public EmployeePanel(EmployeeService employeeService) {
        this.employeeService = employeeService;
        setLayout(new MigLayout("fill", "[grow]", "[grow][grow]"));

        // Initialize components
        nameField = new JTextField(20);
        jobTitleField = new JTextField(20);
        departmentCombo = new JComboBox<>(new String[]{"HR", "IT", "Finance", "Marketing", "Operations"});
        hireDatePicker = new JXDatePicker();
        hireDatePicker.setDate(java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(4, 20);

        // Initialize table
        String[] columns = {"ID", "Name", "Department", "Job Title", "Status", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);

        initComponents();
        loadEmployees(); // Load initial data
    }

    private void initComponents() {
        // Form Panel
        JPanel formPanel = new JPanel(new MigLayout("fillx", "[][grow]", "[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        // Add form components
        formPanel.add(new JLabel("Full Name:*"));
        formPanel.add(nameField, "growx, wrap");

        formPanel.add(new JLabel("Job Title:*"));
        formPanel.add(jobTitleField, "growx, wrap");

        formPanel.add(new JLabel("Department:*"));
        formPanel.add(departmentCombo, "growx, wrap");

        formPanel.add(new JLabel("Hire Date:*"));
        formPanel.add(hireDatePicker, "growx, wrap");

        formPanel.add(new JLabel("Status:*"));
        formPanel.add(statusCombo, "growx, wrap");

        formPanel.add(new JLabel("Email:*"));
        formPanel.add(emailField, "growx, wrap");

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField, "growx, wrap");

        formPanel.add(new JLabel("Address:*"));
        formPanel.add(new JScrollPane(addressArea), "growx, wrap");

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formPanel.add(new JLabel("* Required fields"), "split 2");
        formPanel.add(buttonPanel, "growx, wrap");

        // Table Panel
        JPanel tablePanel = new JPanel(new MigLayout("fill", "[grow]", "[grow]"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Employee List"));
        tablePanel.add(new JScrollPane(employeeTable), "grow");

        // Add panels to main panel
        add(formPanel, "grow, wrap");
        add(tablePanel, "grow");

        // Add action listeners
        saveButton.addActionListener(e -> saveEmployee());
        newButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Long employeeId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    loadEmployee(employeeId);
                }
            }
        });
    }

    private void loadEmployees() {
        // Clear existing rows
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        // Load employees from service
        List<Employee> employees = employeeService.findEmployees(null, null);
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getEmploymentStatus(),
                employee.getEmail()
            });
        }
    }

    private void loadEmployee(Long employeeId) {
        try {
            currentEmployee = employeeService.findByEmployeeId(employeeId);
            if (currentEmployee != null) {
                nameField.setText(currentEmployee.getFullName());
                jobTitleField.setText(currentEmployee.getJobTitle());
                departmentCombo.setSelectedItem(currentEmployee.getDepartment());
                hireDatePicker.setDate(java.util.Date.from(
                    currentEmployee.getHireDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                statusCombo.setSelectedItem(currentEmployee.getEmploymentStatus());
                emailField.setText(currentEmployee.getEmail());
                phoneField.setText(currentEmployee.getPhone());
                addressArea.setText(currentEmployee.getAddress());
            }
        } catch (Exception ex) {
            showError("Error loading employee: " + ex.getMessage());
        }
    }

    private void saveEmployee() {
        try {
            Employee employee = new Employee();
            if (currentEmployee != null) {
                employee.setEmployeeId(currentEmployee.getEmployeeId());
            }
            employee.setFullName(nameField.getText().trim());
            employee.setJobTitle(jobTitleField.getText().trim());
            employee.setDepartment(departmentCombo.getSelectedItem().toString());
            employee.setHireDate(hireDatePicker.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
            employee.setEmploymentStatus(statusCombo.getSelectedItem().toString());
            employee.setEmail(emailField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setAddress(addressArea.getText().trim());

            if (currentEmployee == null) {
                // Create new employee
                employeeService.createEmployee(employee);
            } else {
                // Update existing employee
                employeeService.updateEmployee(employee.getEmployeeId(),employee);
            }

            showSuccess("Employee saved successfully!");
            loadEmployees(); // Refresh table
            clearForm();
        } catch (Exception ex) {
        	ex.printStackTrace();
            showError("Error saving employee: " + ex.getMessage());
        }
    }

    private void deleteEmployee() {
        if (currentEmployee == null) {
            showError("Please select an employee to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this employee?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                employeeService.deleteEmployee(currentEmployee.getEmployeeId());
                showSuccess("Employee deleted successfully!");
                loadEmployees();
                clearForm();
            } catch (Exception ex) {
            	ex.printStackTrace();
                showError("Error deleting employee: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        currentEmployee = null;
        nameField.setText("");
        jobTitleField.setText("");
        departmentCombo.setSelectedIndex(0);
        hireDatePicker.setDate(new java.util.Date());
        statusCombo.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        employeeTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}