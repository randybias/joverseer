package org.joverseer.support.readers.newXml;

import org.joverseer.support.Container;

public class TurnInfo {
	Container characters;
	Container popCentres;
	Container armies;
	Container hiddenArtifacts;
	Container nonHiddenArtifacts;
	Container hexes;
	
	int turnNo = -1;
    int nationNo = -1;
    String date;
    String season;
    String nationName;
    boolean seasonChanging;
	
	public Container getCharacters() {
		return characters;
	}

	public void setCharacters(Container characters) {
		this.characters = characters;
	}

	public Container getArmies() {
		return armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container getPopCentres() {
		return popCentres;
	}

	public void setPopCentres(Container popCentres) {
		this.popCentres = popCentres;
	}

	

	public Container getHiddenArtifacts() {
		return hiddenArtifacts;
	}

	public void setHiddenArtifacts(Container hiddenArtifacts) {
		this.hiddenArtifacts = hiddenArtifacts;
	}

	public Container getNonHiddenArtifacts() {
		return nonHiddenArtifacts;
	}

	public void setNonHiddenArtifacts(Container nonHhiddenArtifacts) {
		this.nonHiddenArtifacts = nonHhiddenArtifacts;
	}

	public Container getHexes() {
		return hexes;
	}

	public void setHexes(Container hexes) {
		this.hexes = hexes;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNationName() {
		return nationName;
	}

	public void setNationName(String nationName) {
		this.nationName = nationName;
	}

	public int getNationNo() {
		return nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public boolean getSeasonChanging() {
		return seasonChanging;
	}

	public void setSeasonChanging(boolean seasonChanging) {
		this.seasonChanging = seasonChanging;
	}

	public int getTurnNo() {
		return turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}
	
	
	
	
}
