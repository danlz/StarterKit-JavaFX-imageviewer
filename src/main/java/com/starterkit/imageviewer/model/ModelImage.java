package com.starterkit.imageviewer.model;

import java.io.File;

import javafx.scene.image.Image;

/**
 * Image displayed in the list.
 * 
 * @author Leszek
 */
public class ModelImage {

    private File file;
    private Image image;

    public ModelImage(File file, Image image) {
        this.file = file;
        this.image = image;
    }

    public File getFile() {
        return file;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ModelImage [file=" + file + "]";
    }

}
