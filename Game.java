import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Game extends Application{
	
	//Label title;
	//Color c = Color.rgb(244, 217, 66); //gold color
	//int x = 0;
	//int y = 0;
	int val = 0;
	private StackPane root;
	private List<Node> cars = new ArrayList<>(); //nodes of cars
	private List<Node> logs = new ArrayList<>(); //nodes of logs
	private ImageView person; //character
	private Scene scene;
	//ImageView bcar;
	private AnimationTimer timer;
	private boolean CRASH = false;
	private boolean WIN = false;
	private boolean SWAP = true;
	
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
    	//Image blueCar = new Image("blueCar.png");//Grabbing the image from bin, setting it to a variable
    	//Image greenCar = new Image("GreenCar.png");
    	//Image yellowCar = new Image("YellowCar.png");
    	//Image redCar = new Image("RedCar.png");
    	Image[] images = new Image[4];
    	images[0] = new Image("blueCar.png");
    	images[1] = new Image("GreenCar.png");
    	images[2] = new Image("YellowCar.png");
    	images[3] = new Image("RedCar.png");
    	
    	
    	if(val==4){val = 0;} //going through array to get different color cars
    	
        ImageView nCar = new ImageView();
        nCar.setImage(images[val]); //Creating a way to view an image - ImageView
        val++; //resetting array if over
        //System.out.println("Val=" + val);
        //nCar.setImage(newCar); //Setting the image to ImageView so it can be viewer
        nCar.setFitWidth(100); //resizes image
        nCar.setPreserveRatio(true); //preserves ratio
        nCar.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        nCar.setCache(true); //improves performance
        
        int[] y = {-30, 20};
		int i = 0;
		if(SWAP == true){
			i = 1;
			//SWAP = false;
		}
		else{
			i=0;
			//SWAP = true;
		}
	
        
        //nCar.setTranslateY((int)(Math.random() * -55));
        nCar.setTranslateY(y[i]); //need to find good bounds for this
        nCar.setTranslateX(110);
        root.getChildren().add(nCar);
        //System.out.println("Spawn car method");
        return nCar;
    	
    }
	
	
	private ImageView spawnLog(){
		Image newLog = new Image("log.png");
		ImageView log = new ImageView();
		log.setImage(newLog);
		log.setFitWidth(100);
		log.setPreserveRatio(true);
		log.setSmooth(true);
		log.setCache(true);
		
		int[] y = {140, 100};
		int i = 0;
		if(SWAP == true){
			i = 1;
			SWAP = false;
		}
		else{
			i=0;
			SWAP = true;
		}
		
		log.setTranslateY(y[i]);
		log.setTranslateX(110);
		root.getChildren().add(log);
		return log;
	}
	
	private void onUpdate(){
		for (Node car: cars){
			car.setTranslateX(car.getTranslateX() - Math.random()*10);
		}
		
		if (Math.random() < 0.015){
			cars.add(spawnCar());
			CRASH = true;
			checkCarCollide(this.person);
			//System.out.println("update method");
		}
		else{
			CRASH = false;
		}
		
		for (Node log: logs){
			log.setTranslateX(log.getTranslateX() - Math.random()*12);
		}
		
		if (Math.random() < .02){
			logs.add(spawnLog());
		}
			
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
		if(CRASH && (person.getTranslateY()>= 110 && person.getTranslateY()<=180)){
			//System.out.println(CRASH);
			person.setTranslateX(125);
			person.setTranslateY(350);
			CRASH = false;
		}
		//nCar.setTranslateY(-30); //need to find good bounds for this
        //nCar.setTranslateX(110);
		
		
		//return collide;
	}
	
	public boolean checkWin(ImageView person, HBox buildings){
		boolean w = false;
		for(int i=0; i<buildings.getChildren().size(); i++){
			if(person.intersects(buildings.getBoundsInParent())){
				w = true;
			}
		}
		
		return w;	
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
	
	
	
	@Override
	public void start(Stage game) throws Exception{
		cars.clear();
		logs.clear();
		
		root = new StackPane();
        root.setId("pane");
        scene = new Scene(root, 300, 400);
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

        Image gperson = new Image("bridgewaterGal.png"); //Grabbing the image from bin, setting it to a variable
        person = new ImageView(); //Creating a way to view an image - ImageView
        person.setImage(gperson); //Setting the image to ImageView so it can be viewer
        person.setFitWidth(50); //resizes image
        person.setPreserveRatio(true); //preserves ratio
        person.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        person.setCache(true); //improves performance
        person.setTranslateX(125);
        person.setTranslateY(350);
        
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
        
        //Ernie
        Image ernie = new Image("Ernie.png");
        ImageView ernieView = new ImageView();
        ernieView.setImage(ernie);	
        
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
        
        //All Images ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

        HBox buildingHBox = new HBox(28); //Spacing of 28 between images
        buildingHBox.getChildren().add(niningerView);
        buildingHBox.getChildren().add(memorialView);
        buildingHBox.getChildren().add(mcKinneyView);
        buildingHBox.getChildren().add(floryView);
        //Missing 5th building
        root.getChildren().add(buildingHBox);
        
        //Adding Ernie to map
        VBox vBox1 = new VBox();
        vBox1.setPadding(new Insets(235,0,0,0)); //Padding so they don't sit at y = 0
        vBox1.getChildren().add(ernieView);
        
        //Adding BC_Coin to map
        VBox vBox2 = new VBox();
        vBox2.setPadding(new Insets(120,0,0,0)); 
        vBox2.getChildren().add(coinView);
		
        //Box to use as a grid for Ernie and coin
        HBox spacingHBox = new HBox(190);
        spacingHBox.getChildren().add(vBox1);
        spacingHBox.getChildren().add(vBox2);
        root.getChildren().add(spacingHBox);
	
        
        
        //this works!!!
        game.getScene().setOnKeyPressed(event -> {
        	switch (event.getCode()){
        	case W:
        	case UP:
        		person.setTranslateY(person.getTranslateY() - 10);
        		checkCarCollide(person);
        		break;
        	case S:
        	case DOWN:
        		person.setTranslateY(person.getTranslateY() + 10);
        		checkCarCollide(person);
        		break;
        	case A:
        	case LEFT:
        		person.setTranslateX(person.getTranslateX() - 10);
        		checkCarCollide(person);
        		break;
        	case D:
        	case RIGHT:
        		person.setTranslateX(person.getTranslateX() + 10);
        		checkCarCollide(person);
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
