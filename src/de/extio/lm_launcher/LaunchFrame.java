package de.extio.lm_launcher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class LaunchFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPane;
	
	JPanel panel;
	
	JPanel panel_1;
	
	JButton btnAdd;
	
	JButton btnEdit;
	
	JSplitPane splitPane;
	
	JScrollPane scrollPane;
	
	JScrollPane scrollPane_1;
	
	JList list;
	
	JList list_1;
	
	JButton btnDelete;
	
	JButton btnClone;
	
	JPanel panel_2;
	
	JButton btnAdd_1;
	
	JButton btnEdit_1;
	
	JButton btnDelete_1;
	
	JButton btnClone_1;
	
	JButton btnRun_1;
	
	DefaultListModel<String> listModel;
	
	DefaultListModel<String> list1Model;
	
	Component horizontalStrut;
	
	JSplitPane splitPane_1;
	
	JScrollPane scrollPane_2;
	
	JTable table;
	
	DefaultTableModel appArgumentsTableModel = new DefaultTableModel() {
		
		@Override
		public boolean isCellEditable(final int row, final int column) {
			return ((AppArgument) this.getValueAt(row, column)).optional();
		};
		
	};
	
	JSplitPane splitPane_2;
	
	JPanel panel_3;
	
	JSlider slider_context;
	
	JLabel lblContextLength;
	
	Component verticalStrut;
	
	JLabel lblThreads;
	
	JSlider slider_threads;
	
	JLabel lblGpuLayers;
	
	JSlider slider_gpu;
	
	JLabel lblPromptTemplate;
	
	JLabel lblPromptTemplate_1;
	
	/**
	 * Create the frame.
	 */
	public LaunchFrame() {
		this.setTitle("LM Launcher");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 400, 400);
		this.setPreferredSize(new Dimension(650, 650));
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new BorderLayout(0, 0));
		{
			this.panel = new JPanel();
			this.contentPane.add(this.panel, BorderLayout.SOUTH);
			{
				this.btnAdd = new JButton("Add");
				this.btnAdd.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final SelectModelDialog smDialog = new SelectModelDialog(LaunchFrame.this, path -> {
							final ModelPropertiesDialog mpDialog = new ModelPropertiesDialog(LaunchFrame.this, path, null, model -> {
								Data.modelData.models().add(model);
								Data.modelData.models().sort((o1, o2) -> {
									return o1.path().compareTo(o2.path());
								});
								try {
									Data.saveModels();
								}
								finally {
									LaunchFrame.this.refresh();
								}
							});
							mpDialog.setLocationRelativeTo(LaunchFrame.this);
							mpDialog.setVisible(true);
						});
						smDialog.setLocationRelativeTo(LaunchFrame.this);
						smDialog.setVisible(true);
					}
				});
				this.panel.add(this.btnAdd);
			}
			{
				this.btnEdit = new JButton("Edit");
				this.btnEdit.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list_1.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select a model first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						final Model editModel = Data.modelData.models().get(index);
						final ModelPropertiesDialog mpDialog = new ModelPropertiesDialog(LaunchFrame.this, editModel.path(), editModel, model -> {
							Data.modelData.models().set(index, model);
							try {
								Data.saveModels();
							}
							finally {
								LaunchFrame.this.refresh();
							}
						});
						mpDialog.setLocationRelativeTo(LaunchFrame.this);
						mpDialog.setVisible(true);
					}
				});
				this.panel.add(this.btnEdit);
			}
			{
				this.btnDelete = new JButton("Delete");
				this.btnDelete.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list_1.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select a model first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						Data.modelData.models().remove(index);
						try {
							Data.saveModels();
						}
						finally {
							LaunchFrame.this.refresh();
						}
					}
				});
				this.panel.add(this.btnDelete);
			}
			{
				this.btnClone = new JButton("Clone");
				this.btnClone.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list_1.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select a model first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						final Model model = Data.modelData.models().get(index);
						Data.modelData.models().add(index, new Model(model.path(), model.contextSize(), model.maxContextSize(), model.gpuLayers(), model.threads(), model.promptTemplate()));
						try {
							Data.saveModels();
						}
						finally {
							LaunchFrame.this.refresh();
						}
					}
				});
				this.panel.add(this.btnClone);
			}
		}
		{
			this.panel_1 = new JPanel();
			this.contentPane.add(this.panel_1, BorderLayout.CENTER);
			this.panel_1.setLayout(new BorderLayout(0, 0));
			{
				this.splitPane = new JSplitPane();
				this.splitPane.setResizeWeight(0.5);
				this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				this.panel_1.add(this.splitPane);
				{
					this.splitPane_1 = new JSplitPane();
					this.splitPane_1.setResizeWeight(0.75);
					this.splitPane.setLeftComponent(this.splitPane_1);
					this.scrollPane = new JScrollPane();
					this.splitPane_1.setLeftComponent(this.scrollPane);
					
					this.listModel = new DefaultListModel<>();
					this.list = new JList(this.listModel);
					this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					this.list.getSelectionModel().addListSelectionListener(e -> {
						if (!e.getValueIsAdjusting()) {
							if (this.table.getCellEditor() != null) {
								this.table.getCellEditor().stopCellEditing();
							}
							this.appArgumentsTableModel.setRowCount(0);
							final DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
							if (selectionModel.getMinSelectionIndex() > -1) {
								final App app = Data.appData.apps().get(selectionModel.getMinSelectionIndex());
								app.appArguments().forEach(arg -> this.appArgumentsTableModel.addRow(new Object[] { arg }));
							}
						}
					});
					this.scrollPane.setViewportView(this.list);
					{
						this.scrollPane_2 = new JScrollPane();
						this.splitPane_1.setRightComponent(this.scrollPane_2);
						{
							this.appArgumentsTableModel.addColumn("Arguments");
							
							this.table = new JTable(this.appArgumentsTableModel);
							this.table.setRowSelectionAllowed(false);
							this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
							this.table.getColumnModel().getColumn(0).setCellRenderer(new AppArgumentsJCheckBoxRenderer(this.appArgumentsTableModel));
							this.table.getColumnModel().getColumn(0).setCellEditor(new AppArgumentsJCheckBoxEditor());
							
							this.scrollPane_2.setViewportView(this.table);
						}
					}
				}
				{
					this.splitPane_2 = new JSplitPane();
					this.splitPane_2.setResizeWeight(0.75);
					this.splitPane.setRightComponent(this.splitPane_2);
					
					this.scrollPane_1 = new JScrollPane();
					this.splitPane_2.setLeftComponent(this.scrollPane_1);
					this.list1Model = new DefaultListModel<>();
					this.list_1 = new JList(this.list1Model);
					this.list_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					this.list_1.getSelectionModel().addListSelectionListener(e -> {
						if (!e.getValueIsAdjusting()) {
							final DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
							if (selectionModel.getMinSelectionIndex() > -1) {
								final Model model = Data.modelData.models().get(selectionModel.getMinSelectionIndex());
								
								this.slider_context.setMaximum(model.maxContextSize());
								this.slider_context.setMajorTickSpacing(model.maxContextSize() / 4);
								this.slider_context.setMinorTickSpacing(model.maxContextSize() / 8);
								this.slider_context.setValue(model.contextSize());
								this.slider_context.setLabelTable(this.slider_context.createStandardLabels(model.maxContextSize() / 4));
								this.slider_context.setSnapToTicks(true);
								this.slider_context.repaint();
								
								this.slider_threads.setValue(model.threads());
								
								this.slider_gpu.setValue(model.gpuLayers());
								
								this.lblPromptTemplate_1.setText(model.promptTemplate());
							}
						}
					});
					this.scrollPane_1.setViewportView(this.list_1);
					{
						this.panel_3 = new JPanel();
						this.splitPane_2.setRightComponent(this.panel_3);
						this.panel_3.setLayout(new GridLayout(0, 1, 0, 0));
						{
							this.lblContextLength = new JLabel("Context Length");
							this.lblContextLength.setHorizontalAlignment(SwingConstants.CENTER);
							this.panel_3.add(this.lblContextLength);
						}
						{
							this.slider_context = new JSlider();
							this.slider_context.setValue(0);
							this.slider_context.setMaximum(131072);
							this.slider_context.setMajorTickSpacing(65536);
							this.slider_context.setMinorTickSpacing(8192);
							this.slider_context.setSnapToTicks(true);
							this.slider_context.setPaintLabels(true);
							this.slider_context.setPaintTicks(true);
							this.panel_3.add(this.slider_context);
						}
						{
							this.lblThreads = new JLabel("Threads");
							this.lblThreads.setHorizontalAlignment(SwingConstants.CENTER);
							this.panel_3.add(this.lblThreads);
						}
						{
							this.slider_threads = new JSlider();
							this.slider_threads.setSnapToTicks(true);
							this.slider_threads.setValue(0);
							this.slider_threads.setPaintLabels(true);
							this.slider_threads.setPaintTicks(true);
							this.slider_threads.setMajorTickSpacing(8);
							this.slider_threads.setMinorTickSpacing(2);
							this.slider_threads.setMaximum(24);
							this.panel_3.add(this.slider_threads);
						}
						{
							this.lblGpuLayers = new JLabel("GPU Layers");
							this.lblGpuLayers.setHorizontalAlignment(SwingConstants.CENTER);
							this.panel_3.add(this.lblGpuLayers);
						}
						{
							this.slider_gpu = new JSlider();
							this.slider_gpu.setPaintLabels(true);
							this.slider_gpu.setMajorTickSpacing(16);
							this.slider_gpu.setMinorTickSpacing(4);
							this.slider_gpu.setValue(100);
							this.slider_gpu.setPaintTicks(true);
							this.panel_3.add(this.slider_gpu);
						}
						{
							this.lblPromptTemplate = new JLabel("Prompt Template");
							this.panel_3.add(this.lblPromptTemplate);
						}
						{
							this.lblPromptTemplate_1 = new JLabel("-");
							this.lblPromptTemplate_1.setHorizontalAlignment(SwingConstants.CENTER);
							this.panel_3.add(this.lblPromptTemplate_1);
						}
						{
							this.verticalStrut = Box.createVerticalStrut(20);
							this.panel_3.add(this.verticalStrut);
						}
					}
				}
			}
		}
		{
			this.panel_2 = new JPanel();
			this.contentPane.add(this.panel_2, BorderLayout.NORTH);
			{
				this.btnAdd_1 = new JButton("Add");
				this.btnAdd_1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setDialogTitle("Select executable");
						fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
						if (fileChooser.showOpenDialog(LaunchFrame.this) == JFileChooser.APPROVE_OPTION) {
							final Path executable = fileChooser.getSelectedFile().toPath();
							
							Path interpreter = null;
							fileChooser = new JFileChooser();
							fileChooser.setDialogTitle("Select interpreter or hit cancel if you don't need it");
							fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
							if (fileChooser.showOpenDialog(LaunchFrame.this) == JFileChooser.APPROVE_OPTION) {
								interpreter = fileChooser.getSelectedFile().toPath();
							}
							
							final App app = new App(executable, interpreter, new ArrayList<>(), new ArrayList<>());
							
							final AppPropertiesDialog appPropertiesDialog = new AppPropertiesDialog(LaunchFrame.this, app, () -> {
								Data.saveApps();
								LaunchFrame.this.refresh();
							});
							appPropertiesDialog.setLocationRelativeTo(LaunchFrame.this);
							appPropertiesDialog.setVisible(true);
						}
					}
				});
				this.panel_2.add(this.btnAdd_1);
			}
			{
				this.btnEdit_1 = new JButton("Edit");
				this.btnEdit_1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select an app first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						final App app = Data.appData.apps().get(index);
						
						final AppPropertiesDialog appPropertiesDialog = new AppPropertiesDialog(LaunchFrame.this, app, () -> {
							Data.saveApps();
							LaunchFrame.this.refresh();
						});
						appPropertiesDialog.setLocationRelativeTo(LaunchFrame.this);
						appPropertiesDialog.setVisible(true);
					}
				});
				this.panel_2.add(this.btnEdit_1);
			}
			{
				this.btnDelete_1 = new JButton("Delete");
				this.btnDelete_1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select an app first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						Data.appData.apps().remove(index);
						Data.saveApps();
						LaunchFrame.this.refresh();
					}
				});
				this.panel_2.add(this.btnDelete_1);
			}
			{
				this.btnClone_1 = new JButton("Clone");
				this.btnClone_1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int index = LaunchFrame.this.list.getSelectedIndex();
						if (index == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select an app first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						final App app = Data.appData.apps().get(index);
						Data.appData.apps().add(index, new App(app.path(), app.interpreter(), new ArrayList<>(app.arguments()), new ArrayList<>(app.appArguments())));
						
						Data.saveApps();
						LaunchFrame.this.refresh();
					}
				});
				this.panel_2.add(this.btnClone_1);
			}
			{
				this.horizontalStrut = Box.createHorizontalStrut(20);
				this.panel_2.add(this.horizontalStrut);
			}
			{
				this.btnRun_1 = new JButton("Run");
				this.btnRun_1.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(final ActionEvent e) {
						final int AppIndex = LaunchFrame.this.list.getSelectedIndex();
						if (AppIndex == -1) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Select an app first", "Error Message", JOptionPane.ERROR_MESSAGE);
							return;
						}
						final App app = Data.appData.apps().get(AppIndex);
						
						Model model = null;
						final int modelIndex = LaunchFrame.this.list_1.getSelectedIndex();
						if (modelIndex > -1) {
							final Model template = Data.modelData.models().get(modelIndex);
							model = new Model(
									template.path(),
									LaunchFrame.this.slider_context.getValue(),
									template.maxContextSize(),
									LaunchFrame.this.slider_gpu.getValue(),
									LaunchFrame.this.slider_threads.getValue(),
									template.promptTemplate());
						}
						
						final List<AppArgument> argumentOverrides = new ArrayList<>();
						if (LaunchFrame.this.table.getCellEditor() != null) {
							LaunchFrame.this.table.getCellEditor().stopCellEditing();
						}
						for (int i = 0; i < LaunchFrame.this.appArgumentsTableModel.getRowCount(); i++) {
							final AppArgument appArgument = (AppArgument) LaunchFrame.this.appArgumentsTableModel.getValueAt(i, 0);
							if (appArgument.optional()) {
								argumentOverrides.add(appArgument);
							}
						}
						
						try {
							app.run(model, argumentOverrides);
						}
						catch (final IllegalArgumentException iae) {
							JOptionPane.showMessageDialog(LaunchFrame.this, iae.getMessage(), "Error Message", JOptionPane.WARNING_MESSAGE);
						}
						catch (final IOException ioa) {
							JOptionPane.showMessageDialog(LaunchFrame.this, "Error: Could not execute program\n" + ioa.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
				this.panel_2.add(this.btnRun_1);
			}
		}
		
		this.pack();
		this.repaint();
		this.splitPane.setDividerLocation(0.33);
		this.splitPane_1.setDividerLocation(0.66);
		this.splitPane_2.setDividerLocation(0.66);
		
		this.refresh();
	}
	
	private void refresh() {
		this.listModel.clear();
		Data.appData.apps()
				.stream()
				.map(app -> app.path().getFileName() + " " + app.path().getParent() + " " + app.interpreter())
				.forEach(this.listModel::addElement);
		
		this.list1Model.clear();
		Data.modelData.models()
				.stream()
				.map(model -> Path.of(model.path()).getFileName() + "; ctx " + model.contextSize() + "/" + model.maxContextSize())
				.forEach(this.list1Model::addElement);
		
		if (this.table.getCellEditor() != null) {
			this.table.getCellEditor().stopCellEditing();
		}
		this.appArgumentsTableModel.setRowCount(0);
		
		this.repaint();
	}
	
	static class AppArgumentsJCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
		
		private final DefaultTableModel model;
		
		public AppArgumentsJCheckBoxRenderer(final DefaultTableModel model) {
			super();
			this.model = model;
		}
		
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
			this.setText(((AppArgument) value).argument());
			this.setSelected(((AppArgument) value).optional() ? ((AppArgument) value).default_() : true);
			this.setEnabled(this.model.isCellEditable(row, column));
			return this;
		}
	}
	
	static class AppArgumentsJCheckBoxEditor extends AbstractCellEditor implements TableCellEditor {
		
		private final JCheckBox checkBox = new JCheckBox();
		
		private AppArgument appArgument;
		
		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
			this.appArgument = (AppArgument) value;
			this.checkBox.setText(this.appArgument.argument());
			this.checkBox.setSelected(this.appArgument.optional() ? this.appArgument.default_() : true);
			return this.checkBox;
		}
		
		@Override
		public Object getCellEditorValue() {
			return new AppArgument(this.appArgument.argument(), this.appArgument.optional(), this.checkBox.isSelected());
		}
		
	}
}
