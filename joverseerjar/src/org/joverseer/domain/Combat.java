package org.joverseer.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Stores the narrations for a given combat. Note that there may be more than one narrations
 * in the case that multiple nations participated. Narrations are stored in a hashtable
 * indexed by nation number.
 * 
 * @author Marios Skounakis
 *
 */
public class Combat implements IHasMapLocation, Serializable {
    private static final long serialVersionUID = 9195835736979973465L;
    int x;
    int y;
    
    HashMap<Integer, String> narrations = new HashMap<Integer, String>();
    
    
    @Override
	public int getX() {
        return this.x;
    }
    @Override
	public int getY() {
        return this.y;
    }
    
    public void setHexNo(int hexNo) {
        this.x = hexNo / 100;
        this.y = hexNo % 100;
    }
    
    public int getHexNo() {
        return this.x * 100 + this.y;
    }

    public HashMap<Integer, String> getNarrations() {
        return this.narrations;
    }
    
    public String getNarrationForNation(int nationNo) {
        return this.narrations.get(nationNo);
    }
    
    public void addNarration(int nationNo, String narration) {
        this.narrations.put(nationNo, narration);
    }
    
    public String getFirstNarration() {
    	for (int i=0; i<26; i++) {
    		String r = getNarrationForNation(i);
    		if (r != null) return r;
    	}
    	return null;
    }
    
    public int getFirstNarrationNation() {
    	for (int i=0; i<26; i++) {
    		String r = getNarrationForNation(i);
    		if (r != null) return i;
    	}
    	return -1;
    }
}
