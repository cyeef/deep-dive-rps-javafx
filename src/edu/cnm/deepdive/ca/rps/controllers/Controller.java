package edu.cnm.deepdive.ca.rps.controllers;

import edu.cnm.deepdive.ca.rps.models.Terrain;
import edu.cnm.deepdive.ca.rps.models.Terrain.Neighborhood;
import edu.cnm.deepdive.ca.rps.util.Constants;
import edu.cnm.deepdive.ca.rps.views.TerrainView;
import edu.cnm.deepdive.ca.rps.views.Timer;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

public class Controller {


  @FXML
  TerrainView terrainView;
  @FXML
  Slider mixingSlider;
  @FXML
  Slider speedSlider;
  @FXML
  ChoiceBox neighborhoodChoice;
  @FXML
  Button runButton;
  @FXML
  Button stopButton;
  @FXML
  Button resetButton;

  private ResourceBundle bundle;
  private Terrain.Neighborhood neighborhood = Terrain.DEFAULT_NEIGHBORHOOD;
  private Terrain.Neighborhood[] neighborhoodChoices;
  private Timer timer;
  private boolean running = false;
  private Terrain terrain;
  private int runnerThreadRest = Constants.DEFAULT_RUNNER_THREAD_REST;

  @FXML
  private void initialize() {
    speedSlider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
          Number newValue) {
        runnerThreadRest = (int) Math.round(Constants.THREAD_INTERVAL / newValue.doubleValue());

      }
    });
      mixingSlider.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue,
          Number newValue) {
        terrain.setMixing(newValue.intValue());

      }
    });
    timer = new Timer(terrainView);
    terrain = new Terrain();
    terrain.setSize(Constants.MODEL_SIZE);
    resetModel();

  }

  @FXML
  private void runModel() {
    System.out.println("Start button clicked!");
    runButton.setDisable(true);
    stopButton.setDisable(false);
    resetButton.setDisable(true);
    timer.start();
    setRunning(true);
    new Runnner().start();
  }

  @FXML
  private void stopModel() {
    runButton.setDisable(false);
    stopButton.setDisable(true);
    resetButton.setDisable(false);
    setRunning(false);
    timer.stop();


  }

  @FXML
  private void resetModel() {
    terrain.initialize();
    terrainView.setSource(terrain.getSnapshot());
    terrainView.draw();
  }
   @FXML
   private void changeNeighborhood() {
    int index = neighborhoodChoice.getItems().indexOf(neighborhoodChoice.getValue());
    terrain.setNeighborhood(neighborhoodChoices[index]);
   }

  @FXML
  private void controlModel(ActionEvent event) {
    System.out.println(((Button) event.getSource()).getText());

  }

  public ResourceBundle getBundle() {
    return bundle;
  }

  public void setBundle(ResourceBundle bundle) {
    this.bundle = bundle;
    String neighborhoodChoices = bundle.getString(Constants.NEIGHBORHOOD_CHOICES_KEY);
    String choices[] = neighborhoodChoices.split("\\|");
    this.neighborhoodChoices = new Neighborhood[choices.length];
    for (int i = 0; i < choices.length; i++) {
      String choice = choices[i].split("@")[0];
      this.neighborhoodChoices[i] = Terrain.Neighborhood.valueOf(choices[i].split("@")[1]);
      neighborhoodChoice.getItems().add(choice);
      if (this.neighborhoodChoices[i] == Terrain.DEFAULT_NEIGHBORHOOD) {
        neighborhoodChoice.setValue(choice);
      }
    }
  }

  private synchronized boolean isRunning() {
    return running;
  }

  private synchronized void setRunning(boolean running) {
    this.running = running;
  }

  private class Runnner extends Thread {

    @Override
    public void run() {
      while (isRunning()){
        terrain.step();
        terrainView.setSource(terrain.getSnapshot());
        try {
          Thread.sleep(runnerThreadRest);
        } catch (InterruptedException ex) {
          // Do nothing.
        }
      }
    }
  }

}











