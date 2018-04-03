package com.starterkit.imageviewer.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Tests for {@link FileToStringBinding}.
 * 
 * @author Leszek
 */
public class FileToStringBindingTest {

    @Test
    public void testNullFile() {
        ObjectProperty<File> property = new SimpleObjectProperty<>();
        property.set(null);

        FileToStringBinding binding = new FileToStringBinding(property);

        String result = binding.computeValue();

        assertThat(result, isEmptyString());
    }

    @Test
    public void testFile() {
        File file = new File("./src/test/resources/empty-directory");

        ObjectProperty<File> property = new SimpleObjectProperty<>();
        property.set(file);

        FileToStringBinding binding = new FileToStringBinding(property);

        String result = binding.computeValue();

        assertThat(result, equalTo(file.getAbsolutePath()));
    }

}
