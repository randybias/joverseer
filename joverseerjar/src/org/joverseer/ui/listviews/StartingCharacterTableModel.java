package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;

/**
 * Table model for starting character objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class StartingCharacterTableModel extends ItemTableModel {

	public StartingCharacterTableModel(MessageSource messageSource) {
		super(Character.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "name", "nationNo", "command", "commandTotal", "agent", "agentTotal", "emmisary", "emmisaryTotal", "mage", "mageTotal", "stealth", "stealthTotal", "challenge" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

}
