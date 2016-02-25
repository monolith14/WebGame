package com.webgame.main;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Team {
	private int id;
	private int uid;
	private String name;
	private int stat1;
	private int stat2;
	private int stat3;
	private int stat4;
	private int extra;
	private int played;
	private int wons;
	private int draws;
	private int loss;
	private int goals;
	private int points;
	
	
	public int getPlayed() {
		return played;
	}

	public void setPlayed(int played) {
		this.played = played;
	}

	public int getWons() {
		return wons;
	}

	public void setWons(int wons) {
		this.wons = wons;
	}

	public int getDraws() {
		return draws;
	}

	public void setDraws(int draws) {
		this.draws = draws;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public int getGoals() {
		return goals;
	}

	public void setGoals(int goals) {
		this.goals = goals;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Team(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStat1() {
		return stat1;
	}

	public void setStat1(int stat1) {
		this.stat1 = stat1;
	}

	public int getStat2() {
		return stat2;
	}

	public void setStat2(int stat2) {
		this.stat2 = stat2;
	}

	public int getStat3() {
		return stat3;
	}

	public void setStat3(int stat3) {
		this.stat3 = stat3;
	}

	public int getStat4() {
		return stat4;
	}

	public void setStat4(int stat4) {
		this.stat4 = stat4;
	}

	public int getStat() {
		return extra;
	}

	public void setStat(int stat) {
		this.extra = stat;
	}
	
	

}
