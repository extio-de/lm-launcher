package de.extio.lm_launcher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class AppPropertiesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	
	JTextField textField;
	
	JScrollPane scrollPane;
	
	JTable table;
	
	DefaultTableModel model = new DefaultTableModel();
	
	JPanel panel;
	
	JLabel lblNewLabel;
	
	JButton btnUpdate;
	
	static App app;
	
	private final Runnable onClose;
	
	/**
	 * Create the dialog.
	 */
	public AppPropertiesDialog(final Frame parent, final App app, final Runnable onClose) {
		super(parent);
		
		AppPropertiesDialog.app = app;
		this.onClose = onClose;
		
		this.setBounds(100, 100, 681, 350);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new BorderLayout(0, 0));
		{
			this.scrollPane = new JScrollPane();
			this.contentPanel.add(this.scrollPane, BorderLayout.CENTER);
			{
				this.model.addColumn("Parameter");
				this.model.addColumn("Optional");
				this.model.addColumn("Default");
				
				this.table = new JTable(this.model);
				this.table.setRowSelectionAllowed(false);
				this.table.setFillsViewportHeight(true);
				this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				
				JCheckBoxRenderer renderer = new JCheckBoxRenderer();
				JCheckBoxEditor editor = new JCheckBoxEditor();
				this.table.getColumnModel().getColumn(1).setCellRenderer(renderer);
				this.table.getColumnModel().getColumn(1).setCellEditor(editor);
				renderer = new JCheckBoxRenderer();
				editor = new JCheckBoxEditor();
				this.table.getColumnModel().getColumn(2).setCellRenderer(renderer);
				this.table.getColumnModel().getColumn(2).setCellEditor(editor);
				
				this.scrollPane.setViewportView(this.table);
			}
		}
		{
			this.panel = new JPanel();
			this.contentPanel.add(this.panel, BorderLayout.NORTH);
			this.panel.setLayout(new BorderLayout(0, 0));
			{
				this.textField = new JTextField();
				this.panel.add(this.textField);
				this.textField.setColumns(10);
			}
			{
				this.lblNewLabel = new JLabel("Enter the arguments. Placeholders: " + String.join(", ", App.PLACEHOLDERS.keySet()));
				this.panel.add(this.lblNewLabel, BorderLayout.NORTH);
			}
			{
				this.btnUpdate = new JButton("Update");
				this.btnUpdate.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						AppPropertiesDialog.this.refreshTable();
					}
				});
				this.panel.add(this.btnUpdate, BorderLayout.EAST);
			}
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						AppPropertiesDialog.this.setVisible(false);
						AppPropertiesDialog.this.onClose.run();
						AppPropertiesDialog.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				this.getRootPane().setDefaultButton(okButton);
			}
		}
		
		this.textField.setText(app.argumentsToString(true));
		
		this.refreshTable();
	}
	
	private void refreshTable() {
		this.model.setRowCount(0);
		
		final List<String> arguments = Arrays.asList(this.textField.getText().split("\\s+"));
		final List<AppArgument> appArgs = new ArrayList<>();
		for (int i = 0; i < arguments.size(); i++) {
			final String argument = arguments.get(i);
			if (argument.isBlank()) {
				continue;
			}
			
			final long existingRecords = AppPropertiesDialog.app.appArguments().stream().filter(arg -> arg.argument().equals(argument)).count() - 1;
			final long newRecordIndex = appArgs.stream().filter(arg -> arg.argument().equals(argument)).count();
			
			final AppArgument appArgument = AppPropertiesDialog.app.appArguments()
					.stream()
					.filter(arg -> arg.argument().equals(argument))
					.skip(Math.max(0, Math.min(newRecordIndex, existingRecords)))
					.findFirst()
					.orElseGet(() -> new AppArgument(argument, false, true));
			appArgs.add(appArgument);
			
			this.model.addRow(new Object[] { appArgument.argument(), appArgument.optional(), appArgument.default_() });
		}
		
		AppPropertiesDialog.app.appArguments().clear();
		AppPropertiesDialog.app.appArguments().addAll(appArgs);
		
		this.table.revalidate();
		this.table.repaint();
	}
	
	static class JCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
		
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			this.setSelected((boolean) value);
			return this;
		}
	}
	
	static class JCheckBoxEditor extends AbstractCellEditor implements TableCellEditor {
		
		private final JCheckBox checkBox = new JCheckBox() {
			
			{
				this.addActionListener(e -> {
					final AppArgument existing = AppPropertiesDialog.app.appArguments().get(JCheckBoxEditor.this.row);
					if (JCheckBoxEditor.this.column == 1) {
						AppPropertiesDialog.app.appArguments().set(JCheckBoxEditor.this.row, new AppArgument(existing.argument(), this.isSelected(), existing.default_()));
					}
					else {
						AppPropertiesDialog.app.appArguments().set(JCheckBoxEditor.this.row, new AppArgument(existing.argument(), existing.optional(), this.isSelected()));
					}
				});
			}
		};
		
		private int row;
		
		private int column;
		
		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
			this.row = row;
			this.column = column;
			this.checkBox.setSelected((Boolean) value);
			return this.checkBox;
		}
		
		@Override
		public Object getCellEditorValue() {
			return this.checkBox.isSelected();
		}
		
		@Override
		public boolean isCellEditable(final EventObject e) {
			return true;
		}
	}
}
