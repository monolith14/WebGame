package com.webgame.main;
//обект двубой с методи за резултат, подават се 2 отбора ...
public class Game {
	private Playstyle teamA, teamB;
	private Integer totalAttack, goalAttack, goalA, goalB, attackPriority, machType, minute, teamACond, teamBCond; 
	

	public Game(){
		
	}

	public Playstyle getTeamA() {
		return teamA;
	}


	public void setTeamA(Playstyle teamA) {
		this.teamA = teamA;
	}


	public Playstyle getTeamB() {
		return teamB;
	}


	public void setTeamB(Playstyle teamB) {
		this.teamB = teamB;
	}


	public Integer getTotalAttack() {
		return totalAttack;
	}


	public void setTotalAttack(Integer totalAttack) {
		this.totalAttack = totalAttack;
	}


	public Integer getGoalAttack() {
		return goalAttack;
	}


	public void setGoalAttack(Integer goalAttack) {
		this.goalAttack = goalAttack;
	}


	public Integer getGoalA() {
		return goalA;
	}


	public void setGoalA(Integer goalA) {
		this.goalA = goalA;
	}


	public Integer getGoalB() {
		return goalB;
	}


	public void setGoalB(Integer goalB) {
		this.goalB = goalB;
	}


	public Integer getAttackPriority() {
		return attackPriority;
	}


	public void setAttackPriority(Integer attackPriority) {
		this.attackPriority = attackPriority;
	}


	public Integer getMachType() {
		return machType;
	}


	public void setMachType(Integer machType) {
		this.machType = machType;
	}


	public Integer getMinute() {
		return minute;
	}


	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public Integer getTeamACond() {
		return teamACond;
	}

	public void setTeamACond(Integer teamACond) {
		this.teamACond = teamACond;
	}

	public Integer getTeamBCond() {
		return teamBCond;
	}

	public void setTeamBCond(Integer teamBCond) {
		this.teamBCond = teamBCond;
	}

	

}
