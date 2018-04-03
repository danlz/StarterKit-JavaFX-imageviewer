package com.starterkit.imageviewer.dataprovider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Provides image data.
 * 
 * @author Leszek
 */
public interface DataProvider {

    /**
     * Lists images in the specified directory.
     * 
     * @param directoryPath
     *            directory path
     * @return list of image files
     * @throws IOException
     *             if images cannot be listed
     */
    List<File> listImages(File directoryPath) throws IOException;

    /**
     * Loads given image.
     * 
     * @param file
     *            image file
     * @return {@link InputStream} containing image bytes
     * @throws IOException
     *             if image cannot be loaded
     */
    InputStream loadImage(File file) throws IOException;
}
