package org.joverseer.ui.listviews;

import org.joverseer.ui.domain.OwnedArtifact;
import org.springframework.context.MessageSource;

/**
 * Table model for Owned Artifacts
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class OwnedArtifactsTableModel extends ItemTableModel {
	public static final int iHexNo=4;
	public OwnedArtifactsTableModel(MessageSource messageSource) {
		super(OwnedArtifact.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "number", "name", "nationNo", "owner", "hexNo", "power1", "power2" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, Integer.class, String.class, String.class };
	}

}
