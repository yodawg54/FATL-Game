import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Game extends Application{
	
	Label title;
	Color c = Color.rgb(244, 217, 66); //gold color
	int x = 0;
	int y = 0;
	private StackPane root;
	private List<Node> cars = new ArrayList<>();
	private Scene scene;
	ImageView person;
	private AnimationTimer timer;
	
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
    	Image newCar = new Image("blueCar.png"); //Grabbing the image from bin, setting it to a variable
        ImageView nCar = new ImageView(); //Creating a way to view an image - ImageView
        nCar.setImage(newCar); //Setting the image to ImageView so it can be viewer
        nCar.setFitWidth(100); //resizes image
        nCar.setPreserveRatio(true); //preserves ratio
        nCar.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        nCar.setCache(true); //improves performance
        
        //nCar.setTranslateY((int)(Math.random() * -55));
        
        //Code to determine which lane the car gets spawned in
        double carPlacement = Math.random();
        if (carPlacement < .25) {
        	nCar.setTranslateY(-110);
            nCar.setTranslateX(110);
        }
        else if (carPlacement < .5) {
        	nCar.setTranslateY(-30);
            nCar.setTranslateX(110);
        }
        else if (carPlacement < .75) {
        	nCar.setTranslateY(-155);
            nCar.setTranslateX(110);
        }
        else {
        	nCar.setTranslateY(22);
            nCar.setTranslateX(110);
        }
        root.getChildren().add(nCar);
        return nCar;
    	
    }
	
	private void sendToBeginning(ImageView toMove) {
		toMove.setTranslateY(350);
		toMove.setTranslateX(100);
	}
	
	private void onUpdate(){
		
		for (Node car: cars)
			car.setTranslateX(car.getTranslateX() - Math.random() * 10);
		
		if (Math.random() < 0.015){
			cars.add(spawnCar());
		}
			
	}
	
	
	private void checkState(){
		for (Node car: cars){
			if(car.getBoundsInParent().intersects(person.getBoundsInParent())){
				sendToBeginning(person);
				//person.setTranslateX(0);
				//person.setTranslateY(500);
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
	}
	
	
	@Override
	public void start(Stage game) throws Exception{
		
		//title = new Label("Bridgewater's First Game - FATL!");
		//btn = new Button("Click it!");
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
        
        boolean genderSelected = true; // DEBUG, will be replaced in character select menu
        Image galImage = new Image("bridgewaterGal.png");
        Image guyImage = new Image("bridgewaterGuy.png");
        Image chosenImage;
        if (genderSelected == true) {
        	chosenImage = guyImage;
        }
        else {
        	chosenImage = galImage;
        }
        person = new ImageView(); //Creating a way to view an image - ImageView
        person.setImage(chosenImage); //Setting the image to ImageView so it can be viewer
        person.setFitWidth(75); //resizes image
        person.setPreserveRatio(true); //preserves ratio
        person.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        person.setCache(true); //improves performance
        
        HBox hbox1 = new HBox(); //A Horizontal Box (Basically a row for grouping)
        hbox1.getChildren().add(person); //Adding the image view as a child to the box
        root.getChildren().add(hbox1); //Needed to actually see on scene, adding the box as a child to the root
	
        
        
        //this works!!!
        game.getScene().setOnKeyPressed(event -> {
        	switch (event.getCode()){
        	case R:
        		sendToBeginning(person); //For testing, R = "reset"
        		break;
        	case W:
        	case UP:
        		if (person.getTranslateY() >= 0) {
        			person.setTranslateY(person.getTranslateY() - 80);
        		}
        		break;
        	case S:
        	case DOWN:
        		if (person.getTranslateY() < 700) {
        			person.setTranslateY(person.getTranslateY() + 80);
        		}
        		break;
        	case A:
        	case LEFT:
        		if (person.getTranslateX() >= 0) {
        			person.setTranslateX(person.getTranslateX() - 80);
        		}
        		break;
        	case D:
        	case RIGHT:
        		if (person.getTranslateX() < 510) {
        			person.setTranslateX(person.getTranslateX() + 80);
        		}
        	}
        });
        
        

		//bcar.setX(x);
		//bcar.setY(y);
        
        game.show();

	}
	
	
}
