package com.starterkit.imageviewer.controller;

import java.io.File;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;

/**
 * Binding converting the given {@link File} property to a string.
 * 
 * @author Leszek
 */
class FileToStringBinding extends StringBinding {

    private ObjectProperty<File> fileProperty;

    /**
     * Constructs a new instance.
     * 
     * @param fileProperty
     *            property providing the {@link File} value
     */
    public FileToStringBinding(ObjectProperty<File> fileProperty) {
        this.fileProperty = fileProperty;
        bind(fileProperty);
    }

    @Override
    protected String computeValue() {
        return fileProperty.get() == null ? "" : fileProperty.get().getAbsolutePath();
    }

}
