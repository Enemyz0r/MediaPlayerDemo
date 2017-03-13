package application;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainController implements Initializable{

	private MediaPlayer mp;
	private ChangeListener<Duration> progressChangeListener;
	private Media me;
	@FXML private MediaView mv;
	@FXML Slider volumeSlider;
	@FXML Slider timeSlider;
	@FXML ProgressBar progressBar;
	@FXML HBox Hbox;
	@FXML Label label;
	@FXML Label label2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

// set JFileChooser and JButton to select media

	   JFileChooser fc = new JFileChooser();
	   JButton open = new JButton();
	   fc.setCurrentDirectory(new File("src/media"));
	   fc.setDialogTitle("Choose Media");
	   fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	   if(fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){

	   }

	   String choice = fc.getSelectedFile().toString();
	   System.out.println("You chose: "+choice);

// set mediaPlayer

		String path = new File(choice).getAbsolutePath();
		me = new Media(new File(path).toURI().toString());
		mp = new MediaPlayer(me);
		mv.setMediaPlayer(mp);
		//mp.setAutoPlay(true);
		DoubleProperty width = mv.fitWidthProperty();
		DoubleProperty height = mv.fitHeightProperty();
		width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mv.sceneProperty(), "height"));
		volumeSlider.setValue(mp.getVolume() *  100);
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		HBox.setHgrow(volumeSlider,Priority.ALWAYS);

		timeSlider.setValueChanging(true);
		bindToTime();



//volumeSlider Listener for changing sound value

		volumeSlider.valueProperty().addListener(new InvalidationListener(){
			@Override
			public void invalidated(Observable observable){
		    mp.setVolume(volumeSlider.getValue() / 100);
			}
		});

// timeSlider setting duration of media file being played

		timeSlider.valueProperty().addListener(new InvalidationListener(){
			@Override
			public void invalidated(Observable observable){

			timeSlider.setMax(me.getDuration().toSeconds());
			}
	});

// media player property that moves timeSlider progressively
	    mp.currentTimeProperty().addListener(new ChangeListener<Duration>(){
				@Override
				public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration current) {
					// TODO Auto-generated method stub
				timeSlider.setValue(current.toSeconds());
				}
				});

// ability to drag the timeSlider
		    timeSlider.setOnMouseDragged(new EventHandler<MouseEvent>(){
		    	@Override
		    	public void handle(MouseEvent mouseEvent){
		    		mp.seek(Duration.seconds(timeSlider.getValue()));
		    	}

		    });

// ability to click on timeSlider
		    timeSlider.setOnMousePressed(new EventHandler<MouseEvent>(){
		    	@Override
		    	public void handle(MouseEvent mouseEvent){
		    		mp.seek(Duration.seconds(timeSlider.getValue()));
		    	}

		    });

// ability to move timeSlider by pressing the arrow keys (left and right)
		    timeSlider.setOnKeyPressed(new EventHandler <KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					// TODO Auto-generated method stub
				if(event.getCode() == KeyCode.LEFT){
					mp.seek(mp.getCurrentTime().divide(1.2));
				}else if(event.getCode() == KeyCode.RIGHT){
					mp.seek(mp.getCurrentTime().multiply(1.2));
				}
				}

		    });

// initializing the progressBar and setting its progress based on media file progression
		progressBar.setProgress(0);

	    progressChangeListener = new ChangeListener<Duration>() {
	      @Override public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
	        progressBar.setProgress(1.0 * mp.getCurrentTime().toMillis() / mp.getTotalDuration().toMillis());
	      }
	    };

	    mp.currentTimeProperty().addListener(progressChangeListener);
	}

// media buttons methods

	public void play(ActionEvent event){
		mp.play();
	}
	public void pause(ActionEvent event){
		mp.pause();
	}
	public void fast(ActionEvent event){
		mp.setRate(1.5);
	}
	public void normal(ActionEvent event){
		mp.setRate(1);
	}
	public void slow(ActionEvent event){
		mp.setRate(.5);
	}
	public void fastForward(ActionEvent event){
		mp.seek(mp.getCurrentTime().multiply(1.5));
	}
	public void reverse(ActionEvent event){
		mp.seek(mp.getCurrentTime().divide(1.5));
	}
	public void reload(ActionEvent event){
		mp.seek(mp.getStartTime());
		mp.play();
	}
	public void start(ActionEvent event){
		mp.seek(mp.getStartTime());
		mp.stop();
	}

// method that calculates the elapsed and remaining time of the media file
	private void bindToTime() {
		  Timeline timeline = new Timeline(
		    new KeyFrame(Duration.seconds(0),
		      new EventHandler<ActionEvent>() {
		        @Override public void handle(ActionEvent actionEvent) {
		          //Calendar time = Calendar.getInstance();
		          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
		          label.setText(simpleDateFormat.format(mp.getCurrentTime().toMillis()));
		          label2.setText(simpleDateFormat.format(mp.getTotalDuration().toMillis() - mp.getCurrentTime().toMillis()));
		        }
		      }
		    ),
		    new KeyFrame(Duration.seconds(1))
		  );
		  timeline.setCycleCount(Animation.INDEFINITE);
		  timeline.play();
		 }

	}