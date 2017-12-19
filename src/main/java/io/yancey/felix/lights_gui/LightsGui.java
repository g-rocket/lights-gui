package io.yancey.felix.lights_gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

public class LightsGui extends JPanel {
	JComboBox<String> mode;
	JPanel controlsPanel;
	JList<Color> colors;
	DefaultListModel<Color> colorModel;
	NameThatColor ntc = new NameThatColor();
	
	public static void main(String[] args) {
		JFrame window = new JFrame("Christmas Lights");
		LightsGui lg = new LightsGui();
		window.setContentPane(lg);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//toFullScreen(window);
		window.pack();
		window.setVisible(true);
	}
	
	public static void toFullScreen(JFrame window) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		window.setUndecorated(true);
		window.setResizable(false);
		window.setSize(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
		window.setLocation(0, 0);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//window.setAlwaysOnTop(true);
		window.setVisible(true);
		window.toFront();
	}
	
	public LightsGui() {
		mode = new JComboBox<String>(new String[] {"Off", "March", "Twinkle"});
		controlsPanel = new JPanel(new CardLayout());
		colorModel = new DefaultListModel<Color>();
		colors = new JList<Color>(colorModel);
	    colors.setTransferHandler(new ListItemTransferHandler<Color>());
	    colors.setDropMode(DropMode.INSERT);
		colors.setDragEnabled(true);
		colors.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete item");
		colors.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete item");
		colors.getActionMap().put("delete item", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedItems();
			}
		});
		colors.setCellRenderer(new ListCellRenderer<Color>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index,
			        boolean isSelected, boolean cellHasFocus) {
				JLabel label = new JLabel(ntc.colorToName(value));
				JPanel swatch = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.setColor(getBackground());
						g.fillRect(0, 0, getWidth(), getHeight());
						g.setColor(getForeground());
						g.drawRect(0, 0, getWidth(), getHeight());
					}
				};
				swatch.setMaximumSize(new Dimension(23, 11));
				Box swatchPanel = new Box(BoxLayout.LINE_AXIS);
				swatchPanel.add(swatch);
				swatchPanel.setPreferredSize(new Dimension(25, 13));
				JPanel listItem = new JPanel();
				listItem.setLayout(new BoxLayout(listItem, BoxLayout.LINE_AXIS));
				listItem.add(swatchPanel);
				listItem.add(Box.createHorizontalGlue());
				listItem.add(label);
				swatch.setBackground(value);
				label.setForeground(isSelected? list.getSelectionForeground(): list.getForeground());
				swatch.setForeground(isSelected? list.getSelectionForeground(): list.getForeground());
				listItem.setBackground(isSelected? list.getSelectionBackground(): list.getBackground());
				return listItem;
			}});
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(mode);
		this.add(controlsPanel);
		controlsPanel.add(new JPanel(), "Off");
		Box marchControlsPanel = new Box(BoxLayout.PAGE_AXIS);
		controlsPanel.add(marchControlsPanel, "March");
		controlsPanel.add(new JPanel(), "Twinkle");
		mode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				CardLayout cl = (CardLayout)controlsPanel.getLayout();
				cl.show(controlsPanel, (String)e.getItem());
			}
		});
		marchControlsPanel.add(colors);
		Box buttonsPanel = new Box(BoxLayout.LINE_AXIS);
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColor = pickColor(Color.BLACK);
				if(newColor != null) {
					colorModel.addElement(newColor);
					Frame window = JOptionPane.getFrameForComponent(LightsGui.this);
					window.pack();
				}
			}
		});
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedItems();
			}
		});
		buttonsPanel.add(deleteButton);
		buttonsPanel.add(addButton);
		marchControlsPanel.add(buttonsPanel);
	}
	
	private void deleteSelectedItems() {
		int removeNum = 0;
		for(int idx: colors.getSelectedIndices()) {
			colorModel.remove(idx - removeNum++);
		}
	}
	
	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}
	
	private class DialogCloseActionListener implements ActionListener {
		public JDialog db;
		public boolean triggered = false;

		@Override
		public void actionPerformed(ActionEvent e) {
			triggered = true;
			db.setVisible(false);
		}
	}
	
	public Color pickColor(Color initialColor) {
		JColorChooser cc = new JColorChooser(initialColor);
		
		cc.removeChooserPanel(cc.getChooserPanels()[0]);
		cc.removeChooserPanel(cc.getChooserPanels()[1]);
		cc.removeChooserPanel(cc.getChooserPanels()[2]);
		
		DialogCloseActionListener okListener = new DialogCloseActionListener();
		DialogCloseActionListener cancelListener = new DialogCloseActionListener();
		JDialog db = JColorChooser.createDialog(this, "Choose a Color", true, cc, okListener, cancelListener);
		okListener.db = db;
		cancelListener.db = db;
		
		db.setVisible(true);
		if(okListener.triggered) {
			return cc.getColor();
		} else {
			// canceled or closed
			return null;
		}
	}
}
