package com.starterkit.imageviewer.controller;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;

/**
 * Base class for bindings calculating image size based on zoom factor. This binding can handle the {@null} value for image.
 * 
 * @author Leszek
 */
abstract class ZoomedImageSizeDoubleBinding extends DoubleBinding {

    private ObjectProperty<Image> image;
    private DoubleProperty zoomFactor;

    /**
     * Constructs a new instance.
     * 
     * @param imageView
     *            image view
     * @param zoomFactor
     *            zoom factor
     */
    public ZoomedImageSizeDoubleBinding(ObjectProperty<Image> image, DoubleProperty zoomFactor) {
        this.image = image;
        this.zoomFactor = zoomFactor;
        // we have to recalculate the size when image is changed
        bind(image);
        bind(zoomFactor);
    }

    @Override
    protected double computeValue() {
        if (image.get() == null) {
            return 0;
        }

        return zoomFactor.get() * getSize(image.get());
    }

    /**
     * Gets the original size of the given image. This method typically calls {@link Image#getWidth()} or {@link Image#getHeight()}.
     * 
     * @param image
     *            the image
     * @return the size
     */
    protected abstract double getSize(Image image);

}
