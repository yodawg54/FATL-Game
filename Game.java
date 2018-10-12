import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import java.io.FileInputStream;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class Game extends Application{
	
	Label title;
	Button btn;
	Color c = Color.rgb(244, 217, 66); //gold color
	//Background value = new Background();
	
	//http://tutorials.jenkov.com/javafx/button.html
	//FileInputStream input = new FileInputStream("images/blueCar.png");
	
	public static void main (String args[]){
		launch(args);
	}
	
	@Override
	public void start(Stage game){
		
		title = new Label("Bridgewater's First Game - FATL!");
		btn = new Button("Click it!");
		
		
		
		game.setTitle("FATL Game");
        Group root = new Group();
        Scene gScene = new Scene(root);
        game.setScene(gScene);
        
        //couldn't quite get this to work
        /*btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
		        btn.setBackground();
			}
        });*/
        
        //Canvas canvas = new Canvas(600, 400);
        root.getChildren().addAll(title, btn);
        //GraphicsContext gc = canvas.getGraphicsContext2D();
        //Image car = new Image("car.png");  //probably use for background
        //gc.drawImage(car, 20, 50);
        //Image bg = new Image("background.png");

        //Animation Timer for the game play
        final long startTime = System.nanoTime();
        new AnimationTimer(){
        	public void handle(long currentTime){
        	double t = currentTime - startTime/1000000000.0;
        	double x = 232+128*Math.cos(t);
        	double y = 232+128*Math.sin(t);
        	//background clears the canvas
        	//gc.drawImage(bg);
        }
    }.start();
    
    game.show(); //last thing in this method

	}
}
