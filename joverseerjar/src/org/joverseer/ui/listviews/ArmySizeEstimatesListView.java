package org.joverseer.ui.listviews;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimator;

/**
 * List view that shows ArmySizeEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmySizeEstimatesListView extends BaseItemListView {
	public ArmySizeEstimatesListView() {
		super(ArmySizeEstimatesTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 64, 64, 64, 64, 64, 64 };
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent comp = super.createControlImpl();
		this.table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
				try {
					if (value == null || (Integer) value <= 0) {
						lbl.setText("");
					}
					lbl.setHorizontalAlignment(SwingConstants.RIGHT);
				} catch (Exception exc) {
				}
				;
				return lbl;
			}

		});
		return comp;
	}

	@Override
	protected void setItems() {
		ArmySizeEstimator ase = new ArmySizeEstimator();
		ArrayList<ArmySizeEstimate> items = ase.estimateArmySizes();
		// find navy index
		int idx = -1;
		for (int i = 0; i < items.size(); i++) {
			ArmySizeEstimate aseo = items.get(i);
			if (aseo.getType().equals(ArmySizeEstimate.NAVY_TYPE)) {
				idx = i;
				break;
			}
		}
		if (idx > -1) {
			items.add(idx, new ArmySizeEstimate("", null));
		}
		this.tableModel.setRows(items);
		this.tableModel.fireTableDataChanged();
	}
	
	
}
