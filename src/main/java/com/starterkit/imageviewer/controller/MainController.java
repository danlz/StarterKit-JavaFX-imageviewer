package com.starterkit.imageviewer.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.starterkit.imageviewer.dataprovider.DataProvider;
import com.starterkit.imageviewer.model.Model;
import com.starterkit.imageviewer.model.ModelImage;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

/**
 * Controller for the main window.
 * 
 * @author Leszek
 */
@Component
public class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class);

    private static final int MINI_IMAGE_WIDTH = 30;
    private static final int MINI_IMAGE_HEIGHT = 30;
    private static final int SLIDESHOW_DELAY = 3;
    private static final double MIN_ZOOM_FACTOR = 0.2;
    private static final double MAX_ZOOM_FACTOR = 3.0;
    private static final double ZOOM_FACTOR_STEP = 0.1;

    @Autowired
    private DataProvider dataProvider;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Pane rootPane;

    @FXML
    private TextField selectedDirectoryField;

    @FXML
    private ListView<ModelImage> imageList;

    @FXML
    private ImageView mainImageView;

    @FXML
    private Button previousButton;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button nextButton;

    @FXML
    private VBox loadingIndicatorPane;

    private Model model = new Model();

    private DirectoryChooser dirChooser = new DirectoryChooser();

    private Alert noFilesDialog;

    private Alert loadErrorDialog;

    private Service<List<ModelImage>> listImagesService = new Service<List<ModelImage>>() {

        @Override
        protected Task<List<ModelImage>> createTask() {
            return new Task<List<ModelImage>>() {

                @Override
                protected List<ModelImage> call() throws Exception {
                    List<File> images = dataProvider.listImages(model.getSelectedDirectory());
                    List<ModelImage> modelImages = images.stream().map(imageFile -> {
                        Image image = null;
                        try {
                            InputStream is = dataProvider.loadImage(imageFile);
                            // scaling takes some time
                            image = new Image(is, MINI_IMAGE_WIDTH, MINI_IMAGE_HEIGHT, true, true);
                        } catch (IOException e) {
                            // ignore the error
                            LOG.error("Could not load file: " + imageFile, e);
                        }

                        return new ModelImage(imageFile, image);
                    }).collect(Collectors.toList());

                    return modelImages;
                }

                @Override
                protected void succeeded() {
                    model.setImages(getValue());
                    if (getValue().isEmpty()) {
                        noFilesDialog.showAndWait();
                    } else {
                        imageList.getSelectionModel().selectFirst();
                        imageList.requestFocus();
                    }
                }

                @Override
                protected void failed() {
                    LOG.error("Could not list images", getException());
                }
            };
        }
    };

    private Service<Image> loadImageService = new Service<Image>() {

        @Override
        protected Task<Image> createTask() {
            return new Task<Image>() {

                @Override
                protected Image call() throws Exception {
                    InputStream is = dataProvider.loadImage(imageList.getSelectionModel().getSelectedItem().getFile());
                    return new Image(is);
                }

                @Override
                protected void succeeded() {
                    mainImageView.setImage(getValue());
                }

                @Override
                protected void failed() {
                    LOG.error("Could not load image", getException());
                    loadErrorDialog.showAndWait();
                }
            };
        }
    };

    private ScheduledService<Void> slideshowService = new ScheduledService<Void>() {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    // we are not doing anything here
                    return null;
                }

                @Override
                protected void succeeded() {
                    nextAction();
                }
            };
        }
    };

    public MainController() {
        slideshowService.setDelay(Duration.seconds(SLIDESHOW_DELAY));
        slideshowService.setPeriod(Duration.seconds(SLIDESHOW_DELAY));
    }

    @FXML
    private void initialize() {
        selectedDirectoryField.textProperty().bind(new FileToStringBinding(model.selectedDirectory()));
        imageList.setCellFactory(listView -> new ListCell<ModelImage>() {

            {
                setContentDisplay(ContentDisplay.LEFT);
                setGraphic(new ImageView());
            }

            @Override
            protected void updateItem(ModelImage item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    ((ImageView) getGraphic()).setImage(null);
                } else {
                    setText(item.getFile().getName());
                    ((ImageView) getGraphic()).setImage(item.getImage());
                }
            }
        });
        imageList.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends ModelImage> observable, ModelImage oldValue, ModelImage newValue) -> {
                    if (newValue == null) {
                        mainImageView.setImage(null);
                    } else {
                        loadImageService.restart();
                    }
                });
        imageList.itemsProperty().bind(model.imagesProperty());

        mainImageView.fitWidthProperty().bind(new ZoomedImageSizeDoubleBinding(mainImageView.imageProperty(), model.zoomFactor()) {

            @Override
            protected double getSize(Image image) {
                return image.getWidth();
            }
        });
        mainImageView.fitHeightProperty().bind(new ZoomedImageSizeDoubleBinding(mainImageView.imageProperty(), model.zoomFactor()) {

            @Override
            protected double getSize(Image image) {
                return image.getHeight();
            }
        });

        BooleanBinding disableControlButtonsBinding = model.selectedDirectory().isNull().or(model.imagesProperty().emptyProperty());
        previousButton.disableProperty().bind(disableControlButtonsBinding);
        startButton.disableProperty().bind(disableControlButtonsBinding);
        stopButton.disableProperty().bind(disableControlButtonsBinding);
        nextButton.disableProperty().bind(disableControlButtonsBinding);

        startButton.visibleProperty().bind(slideshowService.runningProperty().not());
        stopButton.visibleProperty().bind(slideshowService.runningProperty());

        noFilesDialog = new Alert(AlertType.INFORMATION, resources.getString("noImagesFoundMessage"), ButtonType.OK);
        noFilesDialog.setTitle(resources.getString("imageViewer"));
        noFilesDialog.setHeaderText(resources.getString("information"));

        loadErrorDialog = new Alert(AlertType.ERROR, resources.getString("loadErrorMessage"), ButtonType.OK);
        loadErrorDialog.setTitle(resources.getString("imageViewer"));
        loadErrorDialog.setHeaderText(resources.getString("error"));

        loadingIndicatorPane.visibleProperty().bind(listImagesService.runningProperty());
    }

    @FXML
    public void chooseDirectoryAction() {
        stopAction();
        if (model.getSelectedDirectory() == null) {
            dirChooser.setInitialDirectory(FileUtils.getUserDirectory());
        } else {
            dirChooser.setInitialDirectory(model.getSelectedDirectory());
        }

        File imagesDir = dirChooser.showDialog(rootPane.getScene().getWindow());
        model.setSelectedDirectory(imagesDir);
        model.imagesProperty().clear();
        if (imagesDir == null) {
            LOG.info("No directory selected");
        } else {
            LOG.info("Selected directory is " + imagesDir);

            listImagesService.restart();
        }
    }

    @FXML
    public void previousAction() {
        LOG.info("Previous image");
        if (imageList.getSelectionModel().getSelectedIndex() == 0) {
            imageList.getSelectionModel().selectLast();
        } else {
            imageList.getSelectionModel().selectPrevious();
        }
        imageList.scrollTo(imageList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void nextAction() {
        LOG.info("Next image");
        if (imageList.getSelectionModel().getSelectedIndex() == (imageList.getItems().size() - 1)) {
            imageList.getSelectionModel().selectFirst();
        } else {
            imageList.getSelectionModel().selectNext();
        }
        imageList.scrollTo(imageList.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void startAction() {
        LOG.info("Slideshow start");
        slideshowService.restart();
    }

    @FXML
    public void stopAction() {
        LOG.info("Slideshow stop");
        slideshowService.cancel();
    }

    @FXML
    public void scrollAction(ScrollEvent event) {
        double zoomFactor = model.getZoomFactor();
        zoomFactor += ZOOM_FACTOR_STEP * (event.getDeltaY() / event.getMultiplierY());
        if (zoomFactor < MIN_ZOOM_FACTOR) {
            zoomFactor = MIN_ZOOM_FACTOR;
        } else if (zoomFactor > MAX_ZOOM_FACTOR) {
            zoomFactor = MAX_ZOOM_FACTOR;
        }
        model.setZoomFactor(zoomFactor);
        // consume the event so that the scroll pane does not get scrolled
        event.consume();
        LOG.info("Zoom factor is " + zoomFactor);
    }

}
