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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Game extends Application{
	
	//Label title;
	//Color c = Color.rgb(244, 217, 66); //gold color
	//int x = 0;
	//int y = 0;
	private final boolean DEBUG = false;
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
	private boolean lifeOne = true;
	private ImageView lifeOneView;
	private boolean lifeTwo = true;
	private ImageView lifeTwoView;
	private ImageView timeBar;
	int moveCount = 0;
	int lives;
	int gameState;
	int carSpeed = 5;
	boolean logHalfMovement = true;
	
	final double TIME_BAR_X_SCALE = 2.3;
	final double TIME_BAR_Y_SCALE = .25;
	private boolean clockSoundRunning = false;
	
	//Sound
	MediaPlayer fightSongPlayer;
	MediaPlayer clockPlayer;
	
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
	final int CAR_LANE_ONE = 70;
	final int CAR_LANE_TWO = -30;
	final int CAR_LANE_THREE = -170;
	final int CAR_LANE_FOUR = -260;
	
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
        	nCar.setTranslateY(CAR_LANE_FOUR);
        	nCar.setTranslateX(500);
        }
        else if(lane == 1) {
        	nCar.setTranslateY(CAR_LANE_THREE);
        	nCar.setTranslateX(-500);
        	nCar.setImage(images[color + 4]);
        }
        else if(lane == 2) {
        	nCar.setTranslateY(CAR_LANE_TWO);
        	nCar.setTranslateX(500);
        }
        else {
        	nCar.setTranslateY(CAR_LANE_ONE);
        	nCar.setTranslateX(-500);
        	nCar.setImage(images[color + 4]);
        }
        
        //Car Horn
        if(rand.nextInt(5) < 1) {
        	playRandomCarHorn();
        }
        
        root.getChildren().add(nCar);
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
		if (rand.nextInt(10) < 9) {
			newLog = new Image("log.png");
		}
		else {
			newLog = new Image("Alligator.png");
		}
		ImageView log = new ImageView();
		log.setImage(newLog);
		log.setFitWidth(100);
		log.setPreserveRatio(true);
		log.setSmooth(true);
		log.setCache(true);
		
		int lane = rand.nextInt(10);
        
        if(lane < 4) {
        	log.setTranslateY(180);
        }
        else if(lane < 6) {
        	log.setTranslateY(225);
        }
        else if(lane < 9) {
        	log.setTranslateY(270);
        }
        else {
        	log.setTranslateY(315);
        }
        log.setTranslateX(500);
		
		root.getChildren().add(log);
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
		
		//Time bar update
		if (timeBar.getScaleX() > 0) {
			timeBar.setScaleX(timeBar.getScaleX() - .002);
		}
		if (timeBar.getScaleX() < TIME_BAR_X_SCALE * .25) {
			if (!clockSoundRunning) {
				clockPlayer.seek(Duration.ZERO);
				clockPlayer.play();
				clockSoundRunning = true;
			}
		}
		else if (timeBar.getScaleX() <= 0) {
			sendPlayerToBeginning(person, true);
		}
		
		//Car update
		for (Node car: cars){
			if (car.getTranslateY() == CAR_LANE_ONE || car.getTranslateY() == CAR_LANE_THREE) {
				car.setTranslateX(car.getTranslateX() + carSpeed);
			}
			else {
				car.setTranslateX(car.getTranslateX() - carSpeed);
			}
		}
		
		if (Math.random() < 0.015){
			cars.add(spawnCar());
			checkCarCollide(this.person);
			//System.out.println("update method");
		}
		
		for (Node log: logs){
			if (log.getTranslateY() == 180) {
				log.setTranslateX(log.getTranslateX() - 1);
			}
			else if (log.getTranslateY() == 225) {
				if (logHalfMovement) {
					log.setTranslateX(log.getTranslateX() - 1);
				}
			}
			else if (log.getTranslateY() == 270) {
				log.setTranslateX(log.getTranslateX() - 1);
			}
			else {
				if (logHalfMovement) {
					log.setTranslateX(log.getTranslateX() - 1);
				}
			}
		}
		
		//Person smooth movement
		//Left and Right
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
		
		//Check for win
		checkWin(person, fightSongPlayer);
		
		//If person is on log, move them
		//Reminder, change these hard-coded values
		if(person.getTranslateY() == RIVER_ONE) {
			if (logHalfMovement) {
				person.setTranslateX(person.getTranslateX() - 1);
			}
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_TWO) {
			person.setTranslateX(person.getTranslateX() - 1);
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_THREE) {
			if (logHalfMovement) {
				person.setTranslateX(person.getTranslateX() - 1);
			}
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		if(person.getTranslateY() == RIVER_FOUR) {
			person.setTranslateX(person.getTranslateX() - 1);
			if (!checkLogCollision(person, logs)) {
				sendPlayerToBeginning(person, true);
			}
		}
		logHalfMovement = !logHalfMovement;
		
		if (checkCarCollisions(person, cars)) {
			sendPlayerToBeginning(person, true);
		}
		
		if (Math.random() < .02){
			logs.add(spawnLog());
		}
	}
	
	public boolean checkCarCollisions(ImageView person, List<Node> Cars) {
		if (DEBUG) {
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
			sendPlayerToBeginning(person, false);
			player.seek(Duration.ZERO); // Resets the media player
			player.play();
		}
	}
	
	public void sendPlayerToBeginning(ImageView person, boolean died) {
		moveUp = false;
		if (died) {
			if (lifeOne) {
				lifeOne = false;
			}
			else if (lifeTwo) {
				lifeTwo = false;
			}
			else {
				gameOver();
			}
		}
		person.setTranslateX(275);
		person.setTranslateY(START_LANE);
		timeBar.setScaleX(TIME_BAR_X_SCALE);
	}
	
	//Displays gameover screen then sets game state to idle
	public void gameOver() {
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

        Image gperson = new Image("bridgewaterGal.png"); //Grabbing the image from bin, setting it to a variable
        person = new ImageView(); //Creating a way to view an image - ImageView
        person.setImage(gperson); //Setting the image to ImageView so it can be viewer
        person.setFitWidth(50); //resizes image
        person.setPreserveRatio(true); //preserves ratio
        person.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        person.setCache(true); //improves performance
        sendPlayerToBeginning(person, false);
        
        HBox hbox1 = new HBox(); //A Horizontal Box (Basically a row for grouping)
        hbox1.getChildren().add(person); //Adding the image view as a child to the box
        root.getChildren().add(hbox1); //Needed to actually see on scene, adding the box as a child to the root
        
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
        
        //Flory
        Image flory = new Image("Flory.png");
        ImageView floryView = new ImageView();
        floryView.setImage(flory);	
        
        //McKinney
        Image mcKinney = new Image("McKinney.png");
        ImageView mcKinneyView = new ImageView();
        mcKinneyView.setImage(mcKinney);	
        
        //Memorial
        Image memorial = new Image("Memorial.png");
        ImageView memorialView = new ImageView();
        memorialView.setImage(memorial);	
        
        //Nininger
        Image nininger = new Image("Nininger.png");
        ImageView niningerView = new ImageView();
        niningerView.setImage(nininger);	
        
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
        
        //All Images ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

        HBox buildingHBox = new HBox(75); //Spacing of 75 between images
        buildingHBox.getChildren().add(niningerView);
        buildingHBox.getChildren().add(memorialView);
        buildingHBox.getChildren().add(mcKinneyView);
        buildingHBox.getChildren().add(floryView);
        //Missing 5th building
        root.getChildren().add(buildingHBox);
        buildingHBox.setTranslateY(END_LANE - 15);
        
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
