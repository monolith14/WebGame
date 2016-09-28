package com.webgame.main;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//обект двубой с методи за резултат, подават се 2 отбора ...
public class Game {
	private Playstyle teamA, teamB;
	private Integer totalAttack, goalA, goalB, secondOfGame, ballPosition, ballDirection, minute;
	private ArrayList<String> gameComment;
	private Random r = new Random();

	public Game() {
		
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

	public Integer getSecondOfGame() {
		return secondOfGame;
	}

	public void setSecondOfGame(Integer secondOfGame) {
		this.secondOfGame = secondOfGame;
	}

	public Integer getBallPosition() {
		return ballPosition;
	}

	public void setBallPosition(Integer ballPosition) {
		this.ballPosition = ballPosition;
	}

	public Integer getBallDirection() {
		return ballDirection;
	}

	public void setBallDirection(Integer ballDirection) {
		this.ballDirection = ballDirection;
	}

	public ArrayList<String> getGameComment() {
		return gameComment;
	}

	public void setGameComment(ArrayList<String> gameComment) {
		this.gameComment = gameComment;
	}

	// start game

	private ArrayList<String> startGame(Playstyle t1, Playstyle t2) {
		ArrayList<String> result = new ArrayList<String>();
		Integer tmpInt;
		// първоначално определяне на посока на топката

		ballDirection = r.nextInt(2);

		// първоначална позиция на топката
		// 16 отбор А,17 отбор Б,събираме 16 с посоката(0 или 1) за да се
		// определи кой играе
		ballPosition = 16 + ballDirection;

		// събития за всяка минута от мача
		// добавя се в листа [минута:събитие,минута:събитие]

		for (minute = 1; minute < 91; minute++) {
			//random събития за една минута
			tmpInt = r.nextInt(7)+7;
			for(int y = 0;y<tmpInt;y++){
				switch(passOrShoot(ballPosition)){
				case 0:
					pass(ballPosition);
					break;
				case 1:
					
				}
			}

		}
		return result;

	}

	// Определя дали ще има удар към вратата, подаване или дрибъл
	// като се задава позицията на която се намира топката
	private Integer passOrShoot(Integer x) {
		Integer result;
		Integer pass, shoot, drible, tmpInt;
		switch (x) {
		case 0:
		case 1:
			pass = 95;
			shoot = 1;
			drible = 4;
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			pass = 74;
			shoot = 2;
			drible = 24;
			break;
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
			pass = 60;
			shoot = 3;
			drible = 37;
			break;
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
			pass = 30;
			shoot = 60;
			drible = 10;
			break;
		default:
			pass = 33;
			shoot = 33;
			drible = 34;
			break;

		}

		tmpInt = r.nextInt(100) + 1;
		if (tmpInt <= pass) {
			result = 0;
		} else if (tmpInt > pass && tmpInt <= (pass + shoot)) {
			result = 1;
		} else if (tmpInt > (pass + shoot) && tmpInt <= 100) {
			result = 2;
		} else {
			result = 3;
		}
		return result;
	}

	// подаване на топката - определяме в коя посока ще е подаването/задаваме
	// позицията на топката = х/
	private Integer pass(Integer x) {
		Integer result, tempInt, tempInt2, passToGk, passToDf, passToMd, passToFw;
		// 3 масива с позициите от схемата/защита, център, нападение/
		// след което се определя към коя линия ще е подаването и се избира
		// произволна позиция от линията
		int[] arrDf = { 2, 4, 6, 8, 10 };
		int[] arrMd = { 12, 14, 16, 18, 20 };
		int[] arrFw = { 22, 24, 26, 28, 30 };

		// проверка за вратар, позиции 0 и 1, задават се вероятност за подаване
		// към всяка от линиите/към защитник 35%, център 45%, нападател 20%/
		// след което се избира произволна позиция от масива
		if (x < 2) {
			passToDf = 40;
			passToMd = 50;
			passToFw = 10;
			tempInt = r.nextInt(100)+1;
			if (tempInt <= passToDf) {
				tempInt2 = r.nextInt(arrDf.length);
				result = x + arrDf[tempInt2];
			} else if (tempInt > passToDf && tempInt <= (passToDf + passToMd)) {
				tempInt2 = r.nextInt(arrMd.length);
				result = x + arrMd[tempInt2];
			} else {
				tempInt2 = r.nextInt(arrFw.length);
				result = x + arrFw[tempInt2];
			}

		}
		// проверка за защитник
		else if (x > 1 && x < 12) {
			passToGk = 10;
			passToDf = 30;
			passToMd = 50;
			passToFw = 10;
			tempInt = r.nextInt(100)+1;
			if (tempInt <= passToGk) {
				result = x;
			} else if (tempInt > passToGk && tempInt <= (passToGk + passToDf)) {
				tempInt2 = r.nextInt(arrDf.length);
				while (arrDf[tempInt2] != x) {
					tempInt2 = r.nextInt(arrDf.length);
				}
				result = x + arrDf[tempInt2];
			} else if (tempInt > (passToGk + passToDf) && tempInt <= (passToGk + passToDf + passToMd)) {
				tempInt2 = r.nextInt(arrMd.length);
				result = x + arrMd[tempInt2];
			} else {
				tempInt2 = r.nextInt(arrFw.length);
				result = x + arrFw[tempInt2];
			}
		}
		// проверка за център
		else if (x > 12 && x < 22) {
			passToGk = 2;
			passToDf = 18;
			passToMd = 45;
			passToFw = 35;
			tempInt = r.nextInt(100)+1;
			if (tempInt <= passToGk) {
				result = x;
			} else if (tempInt > passToGk && tempInt <= (passToGk + passToDf)) {
				tempInt2 = r.nextInt(arrDf.length);
				result = x + arrDf[tempInt2];
			} else if (tempInt > (passToGk + passToDf) && tempInt <= (passToGk + passToDf + passToMd)) {
				tempInt2 = r.nextInt(arrMd.length);
				while (arrMd[tempInt2] != x) {
					tempInt2 = r.nextInt(arrMd.length);
				}
				result = x + arrMd[tempInt2];
			} else {
				tempInt2 = r.nextInt(arrFw.length);
				result = x + arrFw[tempInt2];
			}
		}
		// проверка за нападател
		else {
			passToGk = 1;
			passToDf = 4;
			passToMd = 25;
			passToFw = 70;
			tempInt = r.nextInt(100)+1;
			if (tempInt <= passToGk) {
				result = x;
			} else if (tempInt > passToGk && tempInt <= (passToGk + passToDf)) {
				tempInt2 = r.nextInt(arrDf.length);
				result = x + arrDf[tempInt2];
			} else if (tempInt > (passToGk + passToDf) && tempInt <= (passToGk + passToDf + passToMd)) {
				tempInt2 = r.nextInt(arrMd.length);
				result = x + arrMd[tempInt2];
			} else {
				tempInt2 = r.nextInt(arrFw.length);
				while (arrFw[tempInt2] != x) {
					tempInt2 = r.nextInt(arrFw.length);
				}
				result = x + arrFw[tempInt2];
			}
		}
		return result;
	}
	// край на подаване

}
