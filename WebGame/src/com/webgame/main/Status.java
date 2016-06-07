package com.webgame.main;
/*
 *  клас за генериране на обект с настройките за играта:
 *  текущ кръг, мет. време ..
 */
public class Status {
	private Integer round, weather, createPlayers, distributePlayers, createProgram;
	
	public Status(){
		
	}

	public Integer getRound() {
		return round;
	}

	public void setRound(Integer round) {
		this.round = round;
	}

	public Integer getWeather() {
		return weather;
	}

	public void setWeather(Integer weather) {
		this.weather = weather;
	}

	public Integer getCreatePlayers() {
		return createPlayers;
	}

	public void setCreatePlayers(Integer createPlayers) {
		this.createPlayers = createPlayers;
	}

	public Integer getDistributePlayers() {
		return distributePlayers;
	}

	public void setDistributePlayers(Integer distributePlayers) {
		this.distributePlayers = distributePlayers;
	}

	public Integer getCreateProgram() {
		return createProgram;
	}

	public void setCreateProgram(Integer createProgram) {
		this.createProgram = createProgram;
	}
	
	

}
