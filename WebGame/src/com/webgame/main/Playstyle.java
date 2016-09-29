package com.webgame.main;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Playstyle {
	private String Name;
	private Player gk;
	private Player df1;
	private Player df2;
	private Player df3;
	private Player df4;
	private Player df5;
	private Player md1;
	private Player md2;
	private Player md3;
	private Player md4;
	private Player md5;
	private Player fw1;
	private Player fw2;
	private Player fw3;
	private Player fw4;
	private Player fw5;
	private Player r1;
	private Player r2;
	private Player r3;
	private Player r4;
	private Player r5;
	private Player r6;
	private Player r7;
	private int df;
	private int md;
	private int fw;
	private Integer attack, defence, speed, technic, condition;
	private ArrayList<String> dfList,mdList,fwList;
	
	
	public ArrayList<String> getDfList() {
		return dfList;
	}


	public void setDfList(ArrayList<String> dfList) {
		this.dfList = dfList;
	}


	public ArrayList<String> getMdList() {
		return mdList;
	}


	public void setMdList(ArrayList<String> mdList) {
		this.mdList = mdList;
	}


	public ArrayList<String> getFwList() {
		return fwList;
	}


	public void setFwList(ArrayList<String> fwList) {
		this.fwList = fwList;
	}


	public Integer getAttack() {
		return attack;
	}


	public void setAttack(Integer attack) {
		this.attack = attack;
	}


	public Integer getDefence() {
		return defence;
	}


	public void setDefence(Integer defence) {
		this.defence = defence;
	}


	public Integer getSpeed() {
		return speed;
	}


	public void setSpeed(Integer speed) {
		this.speed = speed;
	}


	public Integer getTechnic() {
		return technic;
	}


	public void setTechnic(Integer technic) {
		this.technic = technic;
	}


	public Playstyle(){
		

	}


	public Player getGk() {
		return gk;
	}


	public void setGk(Player gk) {
		this.gk = gk;
	}


	public Player getDf1() {
		return df1;
	}


	public void setDf1(Player df1) {
		this.df1 = df1;
	}


	public Player getDf2() {
		return df2;
	}


	public void setDf2(Player df2) {
		this.df2 = df2;
	}


	public Player getDf3() {
		return df3;
	}


	public void setDf3(Player df3) {
		this.df3 = df3;
	}


	public Player getDf4() {
		return df4;
	}


	public void setDf4(Player df4) {
		this.df4 = df4;
	}


	public Player getDf5() {
		return df5;
	}


	public void setDf5(Player df5) {
		this.df5 = df5;
	}


	public Player getMd1() {
		return md1;
	}


	public void setMd1(Player md1) {
		this.md1 = md1;
	}


	public Player getMd2() {
		return md2;
	}


	public void setMd2(Player md2) {
		this.md2 = md2;
	}


	public Player getMd3() {
		return md3;
	}


	public void setMd3(Player md3) {
		this.md3 = md3;
	}


	public Player getMd4() {
		return md4;
	}


	public void setMd4(Player md4) {
		this.md4 = md4;
	}


	public Player getMd5() {
		return md5;
	}


	public void setMd5(Player md5) {
		this.md5 = md5;
	}


	public Player getFw1() {
		return fw1;
	}


	public void setFw1(Player fw1) {
		this.fw1 = fw1;
	}


	public Player getFw2() {
		return fw2;
	}


	public void setFw2(Player fw2) {
		this.fw2 = fw2;
	}


	public Player getFw3() {
		return fw3;
	}


	public void setFw3(Player fw3) {
		this.fw3 = fw3;
	}


	public Player getFw4() {
		return fw4;
	}


	public void setFw4(Player fw4) {
		this.fw4 = fw4;
	}


	public Player getFw5() {
		return fw5;
	}


	public void setFw5(Player fw5) {
		this.fw5 = fw5;
	}


	public Player getR1() {
		return r1;
	}


	public void setR1(Player r1) {
		this.r1 = r1;
	}


	public Player getR2() {
		return r2;
	}


	public void setR2(Player r2) {
		this.r2 = r2;
	}


	public Player getR3() {
		return r3;
	}


	public void setR3(Player r3) {
		this.r3 = r3;
	}


	public Player getR4() {
		return r4;
	}


	public void setR4(Player r4) {
		this.r4 = r4;
	}


	public Player getR5() {
		return r5;
	}


	public void setR5(Player r5) {
		this.r5 = r5;
	}


	public Player getR6() {
		return r6;
	}


	public void setR6(Player r6) {
		this.r6 = r6;
	}


	public Player getR7() {
		return r7;
	}


	public void setR7(Player r7) {
		this.r7 = r7;
	}


	public int getDf() {
		return df;
	}


	public void setDf(int df) {
		this.df = df;
	}


	public int getMd() {
		return md;
	}


	public void setMd(int md) {
		this.md = md;
	}


	public int getFw() {
		return fw;
	}


	public void setFw(int fw) {
		this.fw = fw;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public Integer getCondition() {
		return condition;
	}


	public void setCondition(Integer condition) {
		this.condition = condition;
	}
	
	

}
