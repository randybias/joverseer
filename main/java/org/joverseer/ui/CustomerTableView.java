package org.joverseer.ui;

import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.context.MessageSource;
import org.springframework.binding.form.FormModel;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.events.SelectedHexChangedListener;
import org.joverseer.ui.events.SelectedHexChangedEvent;
import org.joverseer.ui.viewers.PopulationCenterViewer;
import org.joverseer.ui.viewers.CharacterViewer;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 2:30:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTableView extends AbstractForm implements ActionListener, SelectedHexChangedListener {
    FormModel model;
    BeanTableModel tableModel;
    JTable table;
    int[] _selectedRows ;
    MapPanel mapPanel;
    JComponent p;
    JPanel pcViewerHolder;
    PopulationCenterViewer pcViewer;
    CharacterViewer charViewer;

    public CustomerTableView(FormModel model) {
        super(model, "TableClass");
        this.model = model;
    }

    protected JComponent createFormControl() {
        // fetch the messageSource instance from the application context
        MessageSource messageSource = (MessageSource) getApplicationContext()
                .getBean("messageSource");

        // create the CustomerTableModel
        tableModel = new CustomerTableModel(messageSource);

        tableModel.setRows((ArrayList) model.getFormObject());

        // create the JTable instance
        table = TableUtils.createStandardSortableTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        JScrollPane sp = new JScrollPane(table);

        TableLayoutBuilder b = new TableLayoutBuilder();
        b.row();
        b.cell(table);
        b.row();

        JButton btn = new JButton("test");
        btn.addActionListener(this);

        b.cell(btn);
        b.row();
        JScrollPane scp;

//        b.cell(scp = new JScrollPane(mapPanel = new MapPanel()));
//        scp.setPreferredSize(new Dimension(500, 500));
//        mapPanel.setPreferredSize(new Dimension(2000, 2000));
//        mapPanel.setSelectedHex(new Point(3,1));
//        mapPanel.addSelectedHexChangedEventListener(this);
//        b.row();
//        b.cell(scp = new JScrollPane(pcViewerHolder = new JPanel()));
//        scp.setPreferredSize(new Dimension(200, 100));
        //pcViewerHolder.setPreferredSize(new Dimension(100,50));
        //pcViewerHolder.setAutoscrolls(true);

        //Point p = mapPanel.getSelectedHex();
//        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
//        Turn t = g.getTurn();
//        Container c = t.getContainer(TurnElementsEnum.PopulationCenter);
//        PopulationCenter pc = (PopulationCenter)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{22, 27});
//        if (pc == null) {
//            //pcViewerHolder.removeAll();
//        } else {
//            FormModel formModel = FormModelHelper.createFormModel(pc);
//            pcViewer = new PopulationCenterViewer(formModel);
//            pcViewer.getFormModel().setValidating(false);
//            pcViewerHolder.add(pcViewer.getControl());
//        }
//
//        c = t.getContainer(TurnElementsEnum.Character);
//        Character ch = (Character)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{22, 27});
//        if (ch == null) {
//            //pcViewerHolder.removeAll();
//        } else {
//            FormModel formModel = FormModelHelper.createFormModel(ch);
//            charViewer = new CharacterViewer(formModel);
//            charViewer.getFormModel().setValidating(false);
//            pcViewerHolder.add(charViewer.getControl());
//        }
        return (p = b.getPanel());
    }

    public void actionPerformed(ActionEvent e) {
        PopulationCenter pc = null;
        Point p = mapPanel.getSelectedHex();
        if (p != null) {
            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Turn turn = game.getTurn();
            pc = (PopulationCenter)turn.
                    getContainer(TurnElementsEnum.PopulationCenter).
                    findFirstByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
            if (pc != null) {
                final FormModel formModel = FormModelHelper.createFormModel(pc);
                EditPopulationCenter form = new EditPopulationCenter(formModel);
                FormBackedDialogPage page = new FormBackedDialogPage(form);
                TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page, getActiveWindow().getControl()) {
                    protected void onAboutShow() {
                    };
                    protected boolean onFinish() {
                        formModel.commit();
                        mapPanel.invalidate();
                        mapPanel.repaint();
                        return true;
                    }
                };
                dialog.showDialog();
            }

        }


    }

    public void eventOccured(SelectedHexChangedEvent ev) {
//        Point p = mapPanel.getSelectedHex();
//        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
//        Turn t = g.getTurn();
//        Container c = t.getContainer(TurnElementsEnum.PopulationCenter);
//        PopulationCenter pc = (PopulationCenter)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
//        if (pc != null) {
//            pcViewer.setFormObject(pc);
//            pcViewer.getControl().setVisible(true);
//            //pcViewerHolder.setVisible(true);
//        } else {
//            pcViewer.getControl().setVisible(false);
//            //pcViewerHolder.setVisible(false);
//            //pcViewerHolder.add(pcViewer.getControl());
//        }
//
//        c = t.getContainer(TurnElementsEnum.Character);
//        ArrayList chars = c.findAllByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
//        //ArrayList chars = c.getItems();
//        pcViewerHolder.removeAll();
////        for (Character ch : (ArrayList<Character>)chars) {
////            FormModel formModel = FormModelHelper.createFormModel(ch);
////            charViewer = new CharacterViewer(formModel);
////            charViewer.getFormModel().setValidating(false);
////            pcViewerHolder.add(charViewer.getControl());
////        }
////        pcViewerHolder.setAutoscrolls(true);
////        pcViewerHolder.updateUI();
    }
}
