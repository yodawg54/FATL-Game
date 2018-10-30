import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Test extends Application{
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage myStage) throws Exception {
		StackPane root = new StackPane();
        root.setId("pane");
        Scene scene = new Scene(root, 300, 400);
        scene.getStylesheets().addAll(this.getClass().getResource("myCSS.css").toExternalForm());

        Image bridgewaterGuy = new Image("bridgewaterGuy.png"); //Grabbing the image from bin, setting it to a variable
        ImageView bGuyView = new ImageView(); //Creating a way to view an image - ImageView
        bGuyView.setImage(bridgewaterGuy); //Setting the image to ImageView so it can be viewed

        bGuyView.setFitWidth(100); //resizes image
        bGuyView.setPreserveRatio(true); //preserves ratio
        bGuyView.setSmooth(true); //Better quality (true) vs better performance (false - default) [probably want better perf., so delete this line later]
        bGuyView.setCache(true); //improves performance

        HBox hbox1 = new HBox(); //A Horizontal Box (Basically a row for grouping)
        hbox1.getChildren().add(bGuyView); //Adding the image view as a child to the box
        root.getChildren().add(hbox1); //Needed to actually see on scene, adding the box as a child to the root


        myStage.setScene(scene);
        myStage.setTitle("The Official Bridgewater Video Game - FATL");
        myStage.show();

	}

}