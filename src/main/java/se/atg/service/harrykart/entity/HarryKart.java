package se.atg.service.harrykart.entity;

import java.util.ArrayList;

/**
 * Class representation of a HarryKart within a number of loops,Participant
 * List,Loops list
 */

public class HarryKart {
	int numberOfLoops;
	ArrayList<Participant> startList;
	ArrayList<Loop> powerUps;

	public int getNumberOfLoops() {
		return numberOfLoops;
	}

	public ArrayList<Loop> getPowerUps() {
		return powerUps;
	}

	public ArrayList<Participant> getStartList() {
		return startList;
	}

}
