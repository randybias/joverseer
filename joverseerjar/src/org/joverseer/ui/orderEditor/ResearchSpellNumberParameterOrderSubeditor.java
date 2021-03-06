package org.joverseer.ui.orderEditor;

import java.util.ArrayList;

import javax.swing.JComboBox;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.Messages;

/**
 * Subeditor for the Research Spell order
 * 
 * @author Marios Skounakis
 */
public class ResearchSpellNumberParameterOrderSubeditor extends SpellNumberParameterOrderSubeditor {

    public ResearchSpellNumberParameterOrderSubeditor(OrderEditor oe,String paramName, Order o, int orderNo) {
        super(oe,paramName, o, orderNo);
    }

    @Override
	protected void loadSpellCombo(JComboBox com) {
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        Character c = getOrder().getCharacter();
        com.addItem(""); //$NON-NLS-1$
        for (SpellInfo si : (ArrayList<SpellInfo>) gm.getSpells().getItems()) {
            boolean found = false;
            for (SpellProficiency sp : c.getSpells()) {
                if (sp.getSpellId() == si.getNumber()) {
                    found = true;
                }
            }
            com.addItem(si.getNumber() + " - " + si.getName() + (found ? Messages.getString("ResearchSpellNumberParameterOrderSubeditor.2") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

}
