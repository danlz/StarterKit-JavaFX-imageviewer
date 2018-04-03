package com.starterkit.imageviewer.model;

import java.io.File;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

/**
 * The model.
 * 
 * @author Leszek
 */
public class Model {

    private final ObjectProperty<File> selectedDirectory = new SimpleObjectProperty<>();
    private final ListProperty<ModelImage> images = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

    public ObjectProperty<File> selectedDirectory() {
        return selectedDirectory;
    }

    public File getSelectedDirectory() {
        return selectedDirectory.get();
    }

    public void setSelectedDirectory(File selectedDirectory) {
        this.selectedDirectory.set(selectedDirectory);
    }

    public ListProperty<ModelImage> imagesProperty() {
        return images;
    }

    public List<ModelImage> getImages() {
        return images.get();
    }

    public void setImages(List<ModelImage> images) {
        this.images.setAll(images);
    }

    public DoubleProperty zoomFactor() {
        return zoomFactor;
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor.set(zoomFactor);
    }

    @Override
    public String toString() {
        return "Model [selectedDirectory=" + selectedDirectory + ", images=" + images + ", zoomFactor=" + zoomFactor + "]";
    }

}
