package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.jdesktop.swingx.autocomplete.Configurator;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortOrder;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.renderer.BooleanTableCellRenderer;

import com.jidesoft.grid.JideTable;

public class OrderEditorListView extends ItemListView {

    ActionCommand deleteOrderAction = new DeleteOrderAction();
    ActionCommand editOrderAction = new EditOrderAction();
    JComboBox combo;

    public OrderEditorListView() {
        super(TurnElementsEnum.Character, OrderEditorTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {
        		32, 
        		64, 
        		32, 
        		64, 
        		80,
        		
        		48,
        		48,
        		27,
        		27,
        		27,
        		
        		27,
        		27,
        		27,
        		27,
        		27,
        		
        		27,
        		27,
        		27,
        		27,
        		27,
        		
        		30, 120, 64};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        Container items = g.getTurn().getContainer(turnElementType);
        ArrayList orders = new ArrayList();
        OrderFilter f = (OrderFilter)combo.getSelectedItem();
        if (f != null) { 
            for (Character c : (ArrayList<Character>) items.getItems()) {
                if (!f.acceptCharacter(c)) continue;
                for (Order o : c.getOrders()) {
                    orders.add(o);
                }
            }
        }
        tableModel.setRows(orders);
    }

    private ArrayList<OrderFilter> createOrderFilterList() {
        ArrayList<OrderFilter> filterList = new ArrayList<OrderFilter>();
        OrderFilter f = new OrderFilter("All characters with orders") {
            public boolean acceptCharacter(Character c) {
                return c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0 && (!c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank());
            }
        };
        filterList.add(f);

        if (GameHolder.hasInitializedGame()) {
            Game g = GameHolder.instance().getGame();
            GameMetadata gm = g.getMetadata();
            for (int i = 1; i < 26; i++) {
                PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", i);
                if (pi == null) continue;
                f = new OrderFilter(gm.getNationByNum(i).getName()) {
                    public boolean acceptCharacter(Character c) {
                        Game g = GameHolder.instance().getGame();
                        GameMetadata gm = g.getMetadata();
                        return c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0 && gm.getNationByNum(c.getNationNo()).getName().equals(getDescription());
                    }
                };
                filterList.add(f);
            }
        }
        return filterList;
    }

    protected void setFilters() {        
       combo.removeAllItems();
       ArrayList<OrderFilter> filterList = createOrderFilterList();
       for (OrderFilter f : filterList) {
           combo.addItem(f);
       }
    }
    
    protected JTable createTable() {
    	JTable table = TableUtils.createStandardSortableTable(tableModel);
    	JTable newTable = new JideTable(table.getModel());
    	newTable.setColumnModel(table.getColumnModel());
    	newTable.setAutoResizeMode(table.getAutoResizeMode());
    	table = null;
    	return newTable;
    }
    
    

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(combo = new JComboBox(), "align=left");
        combo.setPreferredSize(new Dimension(200, 24));
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setItems();
            }
        });
        table.setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer() {
        	Color selectionBackground = (Color) UIManager.get("Table.selectionBackground");
            Color normalBackground = (Color) UIManager.get("Table.background");
            
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Order o = (Order)tableModel.getRow(((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row));
                JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                if (GraphicUtils.canRenderOrder(o)) {
                    JCheckBox b = new JCheckBox();
                    b.setSelected((Boolean)value);
                    b.setHorizontalAlignment(JCheckBox.CENTER);
                    System.out.println("row == table.getSelectedRow() = " + String.valueOf(row == table.getSelectedRow()));
                    System.out.println("isSelected = " + String.valueOf(isSelected));
                    b.setBackground(row == table.getSelectedRow() && isSelected ? selectionBackground : normalBackground);
                    return b;
                } else {
                    return lbl;
                }
            }
            
        });
        // specialized renderer for the icon returned by the orderResultType virtual field
        table.setDefaultRenderer(ImageIcon.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ImageIcon ico = (ImageIcon)value;
                JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                lbl.setIcon(ico);
                if (ico != null) {
                    OrderResultContainer container = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
                    int idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                    Object obj = tableModel.getRow(idx);
                    Order o = (Order)obj;
                    String txt = "";
                    for (OrderResult result : container.getResultsForOrder(o)) {
                        txt += (txt.equals("") ? "" : "") + "<li>" + result.getType().toString() + ": " + result.getMessage() + "</li>";
                    }
                    txt = "<html><body><lu>" + txt + "</lu></body></html>";
                    lbl.setToolTipText(txt);
                } 
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
            
        });
        tlb.row();
        tlb.cell(tableComp);
        tlb.row();
        return tlb.getPanel();
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                setFilters();
                TableColumn noAndCodeColumn = table.getColumnModel().getColumn(OrderEditorTableModel.iNoAndCode);
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                ListListModel orders = new ListListModel();
                orders.add(Order.NA);
                if (Game.isInitialized(g)) {
                    GameMetadata gm = g.getMetadata();
                    Container orderMetadata = gm.getOrders();
                    for (OrderMetadata om : (ArrayList<OrderMetadata>) orderMetadata.getItems()) {
                        orders.add(om.getNumber() + " " + om.getCode());
                    }
                }
                SortedListModel slm = new SortedListModel(orders);

//                JComboBox comboBox = new JComboBox(new ComboBoxListModelAdapter(slm));
//                JComboBox comboBox = new AutocompletionComboBox(new ComboBoxListModelAdapter(slm));
                JComboBox comboBox = new JComboBox(new ComboBoxListModelAdapter(slm));
                Configurator.enableAutoCompletion(comboBox);
                final ComboBoxCellEditor editor = new ComboBoxCellEditor(comboBox);
                noAndCodeColumn.setCellEditor(editor);
                editor.addCellEditorListener(new CellEditorListener() {

					public void editingCanceled(ChangeEvent e) {
						table.requestFocus();
					}

					public void editingStopped(ChangeEvent e) {
						table.requestFocus();
					}
                	
                });
                comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent arg0) {
						if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
							editor.cancelCellEditing();
						}
					}
                	
                });
                
//                
//                final DefaultCellEditor editor = new DefaultCellEditor(comboBox) {
//					public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//						JComboBox combo = (JComboBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
//						((JTextComponent)combo.getEditor().getEditorComponent()).setCaretPosition(0);
//						((JTextComponent)combo.getEditor().getEditorComponent()).selectAll();
//						return combo;
//					}
//                	
//					
//                };
                
//                noAndCodeColumn.setCellEditor(editor);
//                
//                comboBox.addActionListener(new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						editor.stopCellEditing();
//						table.requestFocus();
//					}
//                });
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshOrders.toString())) {
                setItems();
            }
        }
    }


    public JPopupMenu getPopupMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "orderCommandGroup", new Object[] {editOrderAction, deleteOrderAction});
        return group.createPopupMenu();
    }
    
    private class EditOrderAction extends ActionCommand {
        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Order order = (Order) obj;
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
                } catch (Exception exc) {

                }
            }
        }
    }

    private class DeleteOrderAction extends ActionCommand {

        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Order order = (Order) obj;
                    order.clear();
                    ((BeanTableModel) table.getModel()).fireTableDataChanged();
                } catch (Exception exc) {

                }
            }
        }

    }

    private abstract class OrderFilter {

        String description;

        public abstract boolean acceptCharacter(Character c);

        public OrderFilter(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String toString() {
            return getDescription();
        }
    }
    
    public ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{new ColumnToSort(0, 0, SortOrder.ASCENDING),
                new ColumnToSort(0, 1, SortOrder.ASCENDING)};
    }


}