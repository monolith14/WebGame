package com.webgame.main;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Player {
	private int id;
	private String name;
	private int age;
	private int s1;
	private int s2;
	private int s3;
	private int s4;
	private int tallent;
	private int teamId;
	private int position;
	private int condition;
	private int primePosition;
	private int money;
	private int playNumber;

	public Player() {

	}

	public Player(int id, String name) {
		this.id = id;
		this.name = name;

	}

	public int getPlayNumber() {
		return playNumber;
	}

	public void setPlayNumber(int playNumber) {
		this.playNumber = playNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getS1() {
		return s1;
	}

	public void setS1(int s1) {
		this.s1 = s1;
	}

	public int getS2() {
		return s2;
	}

	public void setS2(int s2) {
		this.s2 = s2;
	}

	public int getS3() {
		return s3;
	}

	public void setS3(int s3) {
		this.s3 = s3;
	}

	public int getS4() {
		return s4;
	}

	public void setS4(int s4) {
		this.s4 = s4;
	}

	public int getTallent() {
		return tallent;
	}

	public void setTallent(int tallent) {
		this.tallent = tallent;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public int getPrimePosition() {
		return primePosition;
	}

	public void setPrimePosition(int primePosition) {
		this.primePosition = primePosition;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

}
