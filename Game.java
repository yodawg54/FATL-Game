import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.security.util.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//For midi playing
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;

import java.math.*;

public class Game extends Application{
	
	//Label title;
	//Color c = Color.rgb(244, 217, 66); //gold color
	//int x = 0;
	//int y = 0;
	private final boolean DEBUG = true;
	private StackPane root;
	private List<Node> cars = new ArrayList<>(); //nodes of cars
	private List<Node> logs = new ArrayList<>(); //nodes of logs
	private ImageView person; //character
	private Scene scene;
	private AnimationTimer timer;
	//private boolean CRASH = true;
	//private boolean WIN = true;
	//private boolean SWAP = true;
	private boolean moveUp = false;
	private boolean moveDown = false;
	private boolean moveRight = false;
	private boolean moveLeft = false;
	private boolean gameOver = false;
	private boolean lifeOne = true;
	private ImageView lifeOneView;
	private boolean lifeTwo = true;
	private boolean wrappingUp = false;
	boolean playerPlaying = false; // For stamp sound effect
	boolean scoresUp = false;
	private ImageView lifeTwoView;
	private ImageView timeBar;
	private ImageView scoreBoard;
	int schoolDay = 0;
	int moveCount = 0;
	private int level = 0;
	int gameState = 1; //0 = idle, 1 = playing, 2 = dead
	int deathAnimation = 0;
	Image death;
	int carSpeed = 5;
	boolean logHalfMovement = true;
	
	//time loops since last image spawn
	final int CAR_SPAWN_LOOPS = 20;
	int lane4 = 0;
	int lane5 = 0;
	int lane6 = 0;
	int lane7 = 0;
	
	//Clock
	final double TIME_BAR_X_SCALE = 2.3;
	final double TIME_BAR_Y_SCALE = .25;
	private boolean clockSoundRunning = false;
	
	//Hbox
	HBox hbox1 = new HBox();
	HBox hbox2 = new HBox();
	
	//Sound
	MediaPlayer fightSongPlayer;
	MediaPlayer clockPlayer;
	MediaPlayer stampPlayer;
	
	//Bridgewater Gal
	Image gperson;
	
	//Grades
	Image[] grades;
	ImageView bowman;
	ImageView mckinney;
	ImageView ninninger;
	ImageView flory;
	ImageView memorial;
	ImageView finalGrade;
	int bowmanScore = 0;
	int mckinneyScore = 0;
	int ninningerScore = 0;
	int floryScore = 0;
	int memorialScore = 0;
	
	//Grade Positions
	final int BOWMAN_ORIGINAL_X = 510;
	final int BOWMAN_ORIGINAL_Y = 70;
	final int FLORY_ORIGINAL_X = 395;
	final int FLORY_ORIGINAL_Y = 70;
	final int MCKINNEY_ORIGINAL_X = 280;
	final int MCKINNEY_ORIGINAL_Y = 70;
	final int MEMORIAL_ORIGINAL_X = 150;
	final int MEMORIAL_ORIGINAL_Y = 70;
	final int NINNINGER_ORIGINAL_X = 40;
	final int NINNINGER_ORIGINAL_Y = 70;
	
	final int BOWMAN_NEW_X = 445;
	final int BOWMAN_NEW_Y = 465;
	final int FLORY_NEW_X = 495;
	final int FLORY_NEW_Y = 405;
	final int MCKINNEY_NEW_X = 470;
	final int MCKINNEY_NEW_Y = 330;
	final int MEMORIAL_NEW_X = 480;
	final int MEMORIAL_NEW_Y = 370;
	final int NINNINGER_NEW_X = 475;
	final int NINNINGER_NEW_Y = 505;
	
	final int FINAL_GRADE_X = 450;
	final int FINAL_GRADE_Y = 550;
	
	final int MOVE_INCREMENTS = 1;
	final int PRECISION = 1;
	
	// player lanes, must be divisible by MOVE_INCREMENT
	final int START_LANE = 740;
	final int RIVER_ONE = 680;
	final int RIVER_TWO = 640;
	final int RIVER_THREE = 600;
	final int RIVER_FOUR = 550;
	final int RIVER_ROAD_MEDIAN = 500;
	final int ROAD_ONE_ONE = 450;
	final int ROAD_ONE_TWO = 410;
	final int ROAD_ONE_THREE = 360;
	final int ROAD_ONE_FOUR = 320;
	final int ROAD_ROAD_MEDIAN = 270;
	final int ROAD_TWO_ONE = 210;
	final int ROAD_TWO_TWO = 170;
	final int ROAD_TWO_THREE = 130;
	final int ROAD_TWO_FOUR = 90;
	final int END_LANE = 50;
	
	//how far the player moves in one update loop
	final int MOVE_INCREMENT = 10;
	
	//Car lanes
	final int CAR_LANE_ONE = 60;
	final int CAR_LANE_TWO = -40;
	final int CAR_LANE_THREE = -180;
	final int CAR_LANE_FOUR = -270;
	
	//http://tutorials.jenkov.com/javafx/button.html
	//FileInputStream input = new FileInputStream("images/blueCar.png");
	
	public static void main (String args[]){
		launch(args);
	}
	
	/*
	private Parent createContent(){
        timer = new AnimationTimer(){
        	@Override
        	public void handle(long now){
        		onUpdate();
        	}
        };
        timer.start();
        return root;
	
	}*/
	
	
	private ImageView spawnCar(){
    	Image[] images = new Image[8];
    	images[0] = new Image("blueCar.png");
    	images[1] = new Image("GreenCar.png");
    	images[2] = new Image("YellowCar.png");
    	images[3] = new Image("RedCar.png");
    	images[4] = new Image("blueCarFlipped.png");
    	images[5] = new Image("GreenCarFlipped.png");
    	images[6] = new Image("YellowCarFlipped.png");
    	images[7] = new Image("RedCarFlipped.png");
    	
    	Random rand = new java.util.Random();
    	
        ImageView nCar = new ImageView();
        int color = rand.nextInt(4);
        nCar.setImage(images[color]); //Creating a way to view an image - ImageView
        nCar.setFitWidth(100); //resizes image
        nCar.setPreserveRatio(true); //preserves ratio
        nCar.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        nCar.setCache(true); //improves performance
        
        int lane = rand.nextInt(4);
        
        if(lane == 0) {
        	if (lane4 > CAR_SPAWN_LOOPS) {
        		nCar.setTranslateY(CAR_LANE_FOUR);
        		nCar.setTranslateX(500);
        		lane4 = 0;
        	}
        	else {
        		nCar.setTranslateY(END_LANE + 500);
        	}
        }
        else if(lane == 1) {
        	if (lane5 > CAR_SPAWN_LOOPS) {
	        	nCar.setTranslateY(CAR_LANE_THREE);
	        	nCar.setTranslateX(-500);
	        	nCar.setImage(images[color + 4]);
	        	lane5 = 0;
        	}
        	else {
        		nCar.setTranslateY(END_LANE + 500);
        	}
        }
        else if(lane == 2) {
        	if (lane6 > CAR_SPAWN_LOOPS) {
	        	nCar.setTranslateY(CAR_LANE_TWO);
	        	nCar.setTranslateX(500);
	        	lane6 = 0;
        	}
        	else {
        		nCar.setTranslateY(END_LANE + 500);
        	}
        }
        else {
        	if (lane7 > CAR_SPAWN_LOOPS) {
	        	nCar.setTranslateY(CAR_LANE_ONE);
	        	nCar.setTranslateX(-500);
	        	nCar.setImage(images[color + 4]);
	        	lane7 = 0;
        	}
        	else {
        		nCar.setTranslateY(END_LANE + 500);
        	}
        }
        
        //Car Horn
        if(rand.nextInt(5) < 1) {
        	playRandomCarHorn();
        }
        
        root.getChildren().add(nCar);
        hbox1.toFront();
        hbox2.toFront();
        return nCar;
    }
	
	private void playRandomCarHorn() {
		double rand = Math.random();
		if (rand < 0.5) {
			Media carHorn = new Media(new File("carHorn.mp3").toURI().toString());
	        MediaPlayer carHornPlayer = new MediaPlayer(carHorn);
	        carHornPlayer.setVolume(.3);
	        carHornPlayer.play();
		}
		else {
			Media carHorn1 = new Media(new File("carHorn1.mp3").toURI().toString());
	        MediaPlayer carHornPlayer1 = new MediaPlayer(carHorn1);
	        carHornPlayer1.setVolume(.3);
	        carHornPlayer1.play();
		}
	}
	
	private ImageView spawnLog(){
		Random rand = new java.util.Random();
		Image newLog;
		newLog = new Image("log.png");
		ImageView log = new ImageView();
		log.setImage(newLog);
		log.setFitWidth(100);
		log.setPreserveRatio(true);
		log.setSmooth(true);
		log.setCache(true);
		//Blend blend = new Blend(BlendMode.SRC_OVER);
		//blend.setTopInput(log.getEffect());
		//person.setEffect(blend);
		
		int lane = rand.nextInt(10);
        
        if(lane < 3) {
        	log.setTranslateY(180);
        }
        else if(lane < 5) {
        	log.setTranslateY(225);
        }
        else if(lane < 7) {
        	log.setTranslateY(270);
        }
        else {
        	log.setTranslateY(315);
        }
        
        log.setTranslateX(500);
		
		root.getChildren().add(log);
		hbox1.toFront();
		hbox2.toFront();
		return log;
	}
	
	private void onUpdate(){
		//Life Counter update
		if (!lifeOne) {
			lifeOneView.setVisible(false);
			if (!lifeTwo) {
				lifeTwoView.setVisible(false);
			}
			else {
				lifeTwoView.setVisible(true);
			}
		}
		else {
			lifeOneView.setVisible(true);
		}
		
		//Death animation
		if(gameState == 2) {
			if(deathAnimation % 50 < 25) {
				person.setImage(death);
			}
			else {
				person.setImage(gperson);
			}
			deathAnimation++;
			if (deathAnimation > 200) {
				deathAnimation = 0;
				gameState = 1;
				person.setImage(gperson);
				sendPlayerToBeginning(person, false);
				if (gameOver) {
					gameState = 0;
					schoolDay = 0;
				}
			}
		}
		
		//Round won
		if(bowmanScore != 0 && mckinneyScore != 0 && floryScore != 0 && memorialScore != 0 && ninningerScore != 0) {
			if (gameState != 0) {
				scoreBoard.setVisible(true);
				scoreBoard.setTranslateX(175);
				scoreBoard.setTranslateY(-400);
				gameState = 0;
			}
			if (scoreBoard.getTranslateY() < 300) {
				scoreBoard.setTranslateY(scoreBoard.getTranslateY() + 3);
			}
			else if (!playerPlaying) {
				playerPlaying = true;
				stampPlayer.play();
			}
			else if (!scoresUp) {
				if(stampPlayer.getCurrentTime().lessThan(Duration.seconds(1.638))) {
					mckinney.setTranslateX(MCKINNEY_NEW_X);
					mckinney.setTranslateY(MCKINNEY_NEW_Y);
				}
				else if (stampPlayer.getCurrentTime().lessThan(Duration.seconds(3.403))) {
					memorial.setTranslateX(MEMORIAL_NEW_X);
					memorial.setTranslateY(MEMORIAL_NEW_Y);
				}
				else if(stampPlayer.getCurrentTime().lessThan(Duration.seconds(5.131))) {
					flory.setTranslateX(FLORY_NEW_X);
					flory.setTranslateY(FLORY_NEW_Y);
				}
				else if(stampPlayer.getCurrentTime().lessThan(Duration.seconds(6.731))) {
					bowman.setTranslateX(BOWMAN_NEW_X);
					bowman.setTranslateY(BOWMAN_NEW_Y);
				}
				else {
					ninninger.setTranslateX(NINNINGER_NEW_X);
					ninninger.setTranslateY(NINNINGER_NEW_Y);
					scoresUp = true;
				}
			}
			else if (stampPlayer.getCurrentTime().greaterThan(Duration.seconds(7)) && scoresUp) {
				stampPlayer.seek(Duration.ZERO);
				stampPlayer.setStopTime(Duration.seconds(4));
				stampPlayer.play();
				int finalScore = (bowmanScore + ninningerScore + floryScore + memorialScore + mckinneyScore) / 5;
				finalGrade.setImage(grades[finalScore - 1]);
				finalGrade.setVisible(true);
				wrappingUp = true;
			}
			else if (wrappingUp && stampPlayer.getCurrentTime().greaterThan(Duration.seconds(1))) {
				scoresUp = false;
				stampPlayer.seek(Duration.ZERO);
				stampPlayer.stop();
				wrappingUp = false;
				increaseDifficulty();
				scoreBoard.setVisible(false);
				setGradesToOriginalPosition();
				finalGrade.setVisible(false);
				playerPlaying = false;
				scoresUp = false;
				moveUp = false;
				stampPlayer.setStopTime(stampPlayer.getMedia().getDuration());
				gameState = 1;
				schoolDay++;
			}
		}
		
		//Time bar update
		if (gameState == 1) {
			if (timeBar.getScaleX() > 0) {
				timeBar.setScaleX(timeBar.getScaleX() - .001);
			}
			if (timeBar.getScaleX() < TIME_BAR_X_SCALE * .25) {
				if (!clockSoundRunning) {
					clockPlayer.seek(Duration.ZERO);
					clockPlayer.setVolume(1);
					clockPlayer.play();
					clockSoundRunning = true;
				}
			}
			if (timeBar.getScaleX() <= 0) {
				sendPlayerToBeginning(person, true);
			}
		}
		
		//Car update
		if (gameState != 2) {
			for (Node car: cars){
				if (car.getTranslateY() == CAR_LANE_ONE || car.getTranslateY() == CAR_LANE_THREE) {
					car.setTranslateX(car.getTranslateX() + carSpeed + (schoolDay * .5));
				}
				else {
					car.setTranslateX(car.getTranslateX() - carSpeed - (schoolDay * .5)); //school day increases car speed
				}
			}
			
			if (Math.random() < 0.015 + (.0025 * schoolDay)){ //later school days increase car spawn frequency
				cars.add(spawnCar());
				checkCarCollide(this.person);
				//System.out.println("update method");
			}
			
			for (Node log: logs){
				if (log.getTranslateY() == 180) {
					log.setTranslateX(log.getTranslateX() - 1 - (schoolDay * .5));
				}
				else if (log.getTranslateY() == 225) {
					if (logHalfMovement) {
						log.setTranslateX(log.getTranslateX() - 1 - (schoolDay * .5));
					}
				}
				else if (log.getTranslateY() == 270) {
					log.setTranslateX(log.getTranslateX() - 1 - (schoolDay * .5));
				}
				else {
					if (logHalfMovement) {
						log.setTranslateX(log.getTranslateX() - 1 - (schoolDay * .5));
					}
				}
			}
		}
		
		//Person smooth movement
		//Left and Right
		if (gameState == 1) {
			if (moveCount < 5) {
				if (moveLeft) {
					person.setTranslateX(person.getTranslateX() - MOVE_INCREMENT);
					moveCount++;
				}
				if (moveRight) {
					person.setTranslateX(person.getTranslateX() + MOVE_INCREMENT);
					moveCount++;
				}
			}
			else {
				moveCount = 0;
				moveLeft = false;
				moveRight = false;
			}
			
			//Up and Down, lane bound movement
			if (moveUp) {
				moveUp = !movePlayerUp(person);
			}
			if (moveDown) {
				moveDown = !movePlayerDown(person);
			}
		}
		
		//Update time since last spawn
		if (gameState != 2) {
			lane4++;
			lane5++;
			lane6++;
			lane7++;
		}
		
		//Check for win
		checkWin(person, fightSongPlayer);
		
		//If person is on log, move them
		//Reminder, change these hard-coded values
		if(person.getTranslateY() == RIVER_ONE) {
			if (logHalfMovement) {
				person.setTranslateX(person.getTranslateX() - 1 - (schoolDay * .5));
			}
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_TWO) {
			person.setTranslateX(person.getTranslateX() - 1 - (schoolDay * .5));
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_THREE) {
			if (logHalfMovement) {
				person.setTranslateX(person.getTranslateX() - 1 - (schoolDay * .5));
			}
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_FOUR) {
			person.setTranslateX(person.getTranslateX() - 1 - (schoolDay * .5));
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		logHalfMovement = !logHalfMovement;
		
		if (checkCarCollisions(person, cars)) {
			sendPlayerToBeginning(person, true);
		}
		
		if (Math.random() < .02 + (.005 * schoolDay)){ //Logs spawn more frequently as the game progresses
			logs.add(spawnLog());
		}
	}
	
	public boolean checkCarCollisions(ImageView person, List<Node> Cars) {
		if (DEBUG || gameState != 1) {
			return false;
		}
		for(Node object: Cars) {
			if (person.getTranslateX() > object.getTranslateX() + 220 && person.getTranslateX() < object.getTranslateX() + 330) {
				if (person.getTranslateY() < object.getTranslateY() + 430 && person.getTranslateY() > object.getTranslateY() + 320) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkLogCollision(ImageView person, List<Node> Logs) {
		if (DEBUG) {
			return true;
		}
		for(Node object: Logs) {
			if (person.getTranslateX() > object.getTranslateX() + 220 && person.getTranslateX() < object.getTranslateX() + 330) {
				if (person.getTranslateY() < object.getTranslateY() + 410 && person.getTranslateY() > object.getTranslateY() + 350) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void checkCarCollide(Node person){
		/*boolean collide=false;
		System.out.println(cars.size());
		
		for(int i=0;i<cars.size();i++){
			//checks the bounds for both the person and every car
			if(person.getBoundsInParent().intersects(cars.get(i).getBoundsInParent())){
				collide=true;
			}
		}*/
		//System.out.println(CRASH);
		//System.out.println("person x val: " + person.getTranslateX());
		//System.out.println("person Y val: " + person.getTranslateY());
		//if(CRASH && (person.getTranslateY()>= 110 && person.getTranslateY()<=180)){
			//System.out.println(CRASH);
			//person.setTranslateX(125);
			//person.setTranslateY(350);
			//CRASH = false;
		//}
		//nCar.setTranslateY(-30); //need to find good bounds for this
        //nCar.setTranslateX(110);
		
		
		//return collide;
	}
	
	// A win is defined as reaching the top of the play field
	public void checkWin(ImageView person, MediaPlayer player){
		if (person.getTranslateY() == END_LANE) {
			//Ninnenger
			if (person.getTranslateX() < 100) {
				if (ninningerScore != 0) {
					sendPlayerToBeginning(person, true);
				}
				else {
					ninningerScore = determineScore();
					ninninger.setImage(grades[ninningerScore - 1]);
					ninninger.setVisible(true);
					sendPlayerToBeginning(person, false);
					player.seek(Duration.ZERO); // Resets the media player
					player.play();
				}
			}
			//Memorial
			else if (person.getTranslateX() < 200) {
				if (memorialScore != 0) {
					sendPlayerToBeginning(person, true);
				}
				else {
					memorialScore = determineScore();
					memorial.setImage(grades[memorialScore - 1]);
					memorial.setVisible(true);
					sendPlayerToBeginning(person, false);
					player.seek(Duration.ZERO); // Resets the media player
					player.play();
				}
			}
			//McKinney
			else if (person.getTranslateX() < 300) {
				if (mckinneyScore != 0) {
					sendPlayerToBeginning(person, true);
				}
				else {
					mckinneyScore = determineScore();
					mckinney.setImage(grades[mckinneyScore - 1]);
					mckinney.setVisible(true);
					sendPlayerToBeginning(person, false);
					player.seek(Duration.ZERO); // Resets the media player
					player.play();
				}
			}
			//Flory
			else if (person.getTranslateX() < 450) {
				if (floryScore != 0) {
					sendPlayerToBeginning(person, true);
				}
				else {
					floryScore = determineScore();
					flory.setImage(grades[floryScore - 1]);
					flory.setVisible(true);
					sendPlayerToBeginning(person, false);
					player.seek(Duration.ZERO); // Resets the media player
					player.play();
				}
			}
			//Bowman
			else {
				if (bowmanScore != 0) {
					sendPlayerToBeginning(person, true);
				}
				else {
					bowmanScore = determineScore();
					bowman.setImage(grades[bowmanScore - 1]);
					bowman.setVisible(true);
					sendPlayerToBeginning(person, false);
					player.seek(Duration.ZERO); // Resets the media player
					player.play();
				}
			}
		}
	}
	
	public int determineScore() {
		if (timeBar.getScaleX() > TIME_BAR_X_SCALE * .8) {
			return 1;
		}
		if (timeBar.getScaleX() > TIME_BAR_X_SCALE * .6) {
			return 2;
		}
		if (timeBar.getScaleX() > TIME_BAR_X_SCALE * .4) {
			return 3;
		}
		if (timeBar.getScaleX() > TIME_BAR_X_SCALE * .2) {
			return 4;
		}
		return 5;
	}
	
	public boolean aboutEqual(double number1, int number2, int precision) {
		double difference = Math.abs(number1 - number2);
		if (difference > precision) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public void sendPlayerToBeginning(ImageView person, boolean died) {
		moveUp = false;
		if (died && gameState == 1) {
			gameState = 2;
			if (lifeOne) {
				lifeOne = false;
			}
			else if (lifeTwo) {
				lifeTwo = false;
			}
			else {
				gameOver = true;
			}
		}
		if (gameState != 2) {
			person.setTranslateX(275);
			person.setTranslateY(START_LANE);
			timeBar.setScaleX(TIME_BAR_X_SCALE);
		}
	}
	
	//movement up function, returns true when movement is done
	public boolean movePlayerUp(ImageView person) {
		if (person.getTranslateY() > RIVER_ONE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > RIVER_TWO) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > RIVER_THREE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > RIVER_FOUR) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > RIVER_ROAD_MEDIAN) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_ROAD_MEDIAN) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_ONE_ONE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_ONE_TWO) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_ONE_THREE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_ONE_FOUR) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_ROAD_MEDIAN) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ROAD_MEDIAN) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_TWO_ONE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_TWO_TWO) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_TWO_THREE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > ROAD_TWO_FOUR) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() > END_LANE) {
			person.setTranslateY(person.getTranslateY() - MOVE_INCREMENT);
			if (person.getTranslateY() == END_LANE) {
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	public boolean movePlayerDown(ImageView person) {
		if (person.getTranslateY() < ROAD_TWO_FOUR) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_TWO_THREE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_TWO_TWO) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_TWO_ONE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_TWO_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_ROAD_MEDIAN) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ROAD_MEDIAN) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_ONE_FOUR) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_ONE_THREE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_ONE_TWO) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < ROAD_ONE_ONE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == ROAD_ONE_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < RIVER_ROAD_MEDIAN) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_ROAD_MEDIAN) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < RIVER_FOUR) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_FOUR) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < RIVER_THREE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_THREE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < RIVER_TWO) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_TWO) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < RIVER_ONE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == RIVER_ONE) {
				return true;
			}
			else {
				return false;
			}
		}
		if (person.getTranslateY() < START_LANE) {
			person.setTranslateY(person.getTranslateY() + MOVE_INCREMENT);
			if (person.getTranslateY() == START_LANE) {
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}
	/*private void checkState(){
		for (Node car: cars){
			if(car.getBoundsInParent().intersects(person.getBoundsInParent())){
				person.setTranslateX(0);
				person.setTranslateY(500);
			}
		}
		
		if(person.getTranslateY() <= 0){
			timer.stop();
			String win = "YOU WIN";
			HBox hbox = new HBox();
			hbox.setTranslateX(300);
			hbox.setTranslateY(250);
			root.getChildren().add(hbox);
			
			for(int i=0; i<win.toCharArray().length; i++){
				char letter = win.charAt(i);
				Text text = new Text(String.valueOf(letter));
				text.setFont(Font.font(25));
				text.setOpacity(0);
				hbox.getChildren().add(text);
				FadeTransition ft = new FadeTransition(Duration.seconds(0.75), text);
				ft.setToValue(1);
				ft.setDelay(Duration.seconds(i*.15));
				ft.play();
			}
		}
	}*/
	
	public void randomizeCoin(ImageView coin) {
		
	}
	
	public void increaseDifficulty() {
		
	}
	
	public void setGradesToOriginalPosition() {
		bowman.setVisible(false);
		mckinney.setVisible(false);
		flory.setVisible(false);
		ninninger.setVisible(false);
		memorial.setVisible(false);
		
		bowmanScore = 0;
		mckinneyScore = 0;
		floryScore = 0;
		ninningerScore = 0;
		memorialScore = 0;
		
		bowman.setTranslateX(BOWMAN_ORIGINAL_X);
		bowman.setTranslateY(BOWMAN_ORIGINAL_Y);
		mckinney.setTranslateX(MCKINNEY_ORIGINAL_X);
		mckinney.setTranslateY(MCKINNEY_ORIGINAL_Y);
		flory.setTranslateX(FLORY_ORIGINAL_X);
		flory.setTranslateY(FLORY_ORIGINAL_Y);
		ninninger.setTranslateX(NINNINGER_ORIGINAL_X);
		ninninger.setTranslateY(NINNINGER_ORIGINAL_Y);
		memorial.setTranslateX(MEMORIAL_ORIGINAL_X);
		memorial.setTranslateY(MEMORIAL_ORIGINAL_Y);
	}
	
	@Override
	public void start(Stage game) throws Exception{
		cars.clear();
		logs.clear();
		
		root = new StackPane();
        root.setId("pane");
        scene = new Scene(root, 600, 800);
        scene.getStylesheets().addAll(this.getClass().getResource("myCSS.css").toExternalForm());
		game.setScene(scene);
        game.setTitle("The Official Bridgewater Video Game - FATL");
        
        timer = new AnimationTimer(){
        	@Override
        	public void handle(long now){
        		onUpdate();
        	}
        };
        timer.start();
        
      //TimeBar
        Image time = new Image("timeBar.png");
        timeBar = new ImageView();
        timeBar.setImage(time);
        timeBar.setTranslateX(175);
        timeBar.setTranslateY(-380);
        timeBar.setScaleX(TIME_BAR_X_SCALE);
        timeBar.setScaleY(TIME_BAR_Y_SCALE);
        root.getChildren().add(timeBar);
        
        //Person
        gperson = new Image("bridgewaterGal.png"); //Grabbing the image from bin, setting it to a variable
        person = new ImageView(); //Creating a way to view an image - ImageView
        person.setImage(gperson); //Setting the image to ImageView so it can be viewer
        person.setFitWidth(50); //resizes image
        person.setPreserveRatio(true); //preserves ratio
        person.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        person.setCache(true); //improves performance
        hbox1.getChildren().add(person); //Adding the image view as a child to the box
        sendPlayerToBeginning(person, false);
        
        //Bridgewater Guy
        //Image bridgewaterGuy = new Image("BridgewaterGuy.png"); //Grabbing the image from bin, setting it to a variable
        //ImageView bGuyView = new ImageView(); 					//Creating a way to view an image - ImageView
        //bGuyView.setImage(bridgewaterGuy);						//Setting the image to ImageView so it can be viewed
        
        //Bridgewater Gal
        //Image bridgewaterGal = new Image("BridgewaterGal.png");
        //ImageView bGalView = new ImageView();
        //bGalView.setImage(bridgewaterGal);	
        
        //Alligator
        Image alligator = new Image("Alligator.png");
        ImageView alligatorView = new ImageView();
        alligatorView.setImage(alligator);	
        
        //Log
        //Image log = new Image("log.png");
        //ImageView logView = new ImageView();
        //logView.setImage(log);	
        
        //BC Coin
        Image coin = new Image("BC_Coin.png");
        ImageView coinView = new ImageView();
        coinView.setImage(coin);		
        
        //Green Car
        //Image gCar = new Image("GreenCar.png");
        //ImageView gCarView = new ImageView();
        //gCarView.setImage(gCar);	
        
        //Red Car
        //Image rCar = new Image("RedCar.png");
        //ImageView rCarView = new ImageView();
        //rCarView.setImage(rCar);	
        
        //Blue Car
        //Image bCar = new Image("BlueCar.png");
        //ImageView bCarView = new ImageView();
        //bCarView.setImage(bCar);	
        
        //Yellow Car
        //Image yCar = new Image("YellowCar.png");
        //ImageView yCarView = new ImageView();
        //yCarView.setImage(yCar);	
        
        //Score Board
        scoreBoard = new ImageView(new Image("transcript.png"));
        scoreBoard.setScaleX(3);
        scoreBoard.setScaleY(3);
        root.getChildren().add(scoreBoard);
        scoreBoard.setVisible(false);
        hbox1.getChildren().add(scoreBoard);
        root.getChildren().add(hbox1);
        
        //Flory
        flory = new ImageView();
        flory.setScaleX(3);
        flory.setScaleY(3);
        flory.setTranslateX(FLORY_ORIGINAL_X);
        flory.setTranslateY(FLORY_ORIGINAL_Y);
        flory.setSmooth(false);
        hbox2.getChildren().add(flory);
        
        //McKinney
        mckinney = new ImageView();
        mckinney.setScaleX(3);
        mckinney.setScaleY(3);
        mckinney.setTranslateX(MCKINNEY_ORIGINAL_X);
        mckinney.setTranslateY(MCKINNEY_ORIGINAL_Y);
        mckinney.setSmooth(false);
        hbox2.getChildren().add(mckinney);
        
        //Memorial
        memorial = new ImageView();
        memorial.setScaleX(3);
        memorial.setScaleY(3);
        memorial.setTranslateX(MEMORIAL_ORIGINAL_X);
        memorial.setTranslateY(MEMORIAL_ORIGINAL_Y);
        memorial.setSmooth(false);
        hbox2.getChildren().add(memorial);
        
        //Ninninger
        ninninger = new ImageView();
        ninninger.setScaleX(3);
        ninninger.setScaleY(3);
        ninninger.setTranslateX(NINNINGER_ORIGINAL_X);
        ninninger.setTranslateY(NINNINGER_ORIGINAL_Y);
        ninninger.setSmooth(false);
        hbox2.getChildren().add(ninninger);
        
        //Bowman
        bowman = new ImageView();
        bowman.setScaleX(3);
        bowman.setScaleY(3);
        bowman.setTranslateX(BOWMAN_ORIGINAL_X);
        bowman.setTranslateY(BOWMAN_ORIGINAL_Y);
        bowman.setSmooth(false);
        hbox2.getChildren().add(bowman);
        root.getChildren().add(hbox2);
        
        //Final Grade
        finalGrade = new ImageView();
        finalGrade.setScaleX(3);
        finalGrade.setScaleY(3);
        finalGrade.setTranslateX(FINAL_GRADE_X);
        finalGrade.setTranslateY(FINAL_GRADE_Y);
        finalGrade.setSmooth(false);
        hbox2.getChildren().add(finalGrade);
        
        //Letter grades
        Image gradeA = new Image("letterGradeA.png");
        Image gradeB = new Image("letterGradeB.png");
        Image gradeC = new Image("letterGradeC.png");
        Image gradeD = new Image("letterGradeD.png");
        Image gradeF = new Image("letterGradeF.png");
        grades = new Image[] {gradeA, gradeB, gradeC, gradeD, gradeF};
        
        //Death
        death = new Image("bones.png");
        
        //LifeTwo
        Image ernie = new Image("Ernie.png");
        lifeTwoView = new ImageView();
        lifeTwoView.setImage(ernie);
        lifeTwoView.setTranslateX(-210);
        lifeTwoView.setTranslateY(-385);
        root.getChildren().add(lifeTwoView);
        
        //LifeOne
        lifeOneView = new ImageView();
        lifeOneView.setImage(ernie);
        lifeOneView.setTranslateX(-180);
        lifeOneView.setTranslateY(-385);
        root.getChildren().add(lifeOneView);
        
        //All Images ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        
        //Adding Ernie to map
        //VBox vBox1 = new VBox();
        //vBox1.setPadding(new Insets(235,0,0,0)); //Padding so they don't sit at y = 0
        //vBox1.getChildren().add(ernieView);
        
        //Adding BC_Coin to map
        //VBox vBox2 = new VBox();
        //vBox2.setPadding(new Insets(120,0,0,0)); 
        //vBox2.getChildren().add(coinView);
		
        //Box to use as a grid for Ernie and coin
        //HBox spacingHBox = new HBox(190);
        //spacingHBox.getChildren().add(vBox1);
        //spacingHBox.getChildren().add(vBox2);
        //root.getChildren().add(spacingHBox);
        
        //Sounds~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        Media fightSong = new Media(new File("BCFightSong.mp3").toURI().toString());
        fightSongPlayer = new MediaPlayer(fightSong);
        
        Media clock = new Media(new File("clock.mp3").toURI().toString());
        clockPlayer = new MediaPlayer(clock);
        
        Media stamp = new Media(new File("stamp.mp3").toURI().toString());
        stampPlayer = new MediaPlayer(stamp);
        stampPlayer.setVolume(.5);
        
        //this works!!!
        
        game.getScene().setOnKeyPressed(event -> {
        	switch (event.getCode()){
        	case W:
        	case UP:
    			moveUp = true;
        		break;
        	case S:
        	case DOWN:
        		moveDown = true;
        		break;
        	case A:
        	case LEFT:
        		if (person.getTranslateX() > 0) {
        			moveLeft = true;
        		}
        		break;
        	case D:
        	case RIGHT:
        		if (person.getTranslateX() < 540) {
        			moveRight = true;
        		}
			default:
				break;
        	}
        });
        
        onUpdate();
        checkCarCollide(person);
        //checkState();
        //createContent();
        //WIN = checkWin(person, buildingHBox);
        //if (WIN == true){
        	//game.close();
        //}
        

		//bcar.setX(x);
		//bcar.setY(y);
        
        game.show();

	}
	
	
}
