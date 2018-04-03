package com.starterkit.imageviewer.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.junit.Test;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

/**
 * Tests for {@link ZoomedImageSizeDoubleBinding}.
 * 
 * @author Leszek
 */
public class ZoomedImageSizeDoubleBindingTest {

    @Test
    public void testNullImage() {
        ObjectProperty<Image> image = new SimpleObjectProperty<>();
        DoubleProperty zoomFactor = new SimpleDoubleProperty(5);

        ZoomedImageSizeDoubleBinding binding = new ZoomedImageSizeDoubleBinding(image, zoomFactor) {

            @Override
            protected double getSize(Image image) {
                return 500;
            }
        };

        double result = binding.computeValue();

        assertThat(result, equalTo(0.0));
    }

    @Test
    public void testWithImage() {
        // image height is 100px
        InputStream is = getClass().getResourceAsStream("/directory-with-images/image1.png");
        ObjectProperty<Image> image = new SimpleObjectProperty<>(new Image(is));
        DoubleProperty zoomFactor = new SimpleDoubleProperty(2);

        ZoomedImageSizeDoubleBinding binding = new ZoomedImageSizeDoubleBinding(image, zoomFactor) {

            @Override
            protected double getSize(Image image) {
                return image.getHeight();
            }
        };

        double result = binding.computeValue();

        assertThat(result, equalTo(200.0));
    }

}
