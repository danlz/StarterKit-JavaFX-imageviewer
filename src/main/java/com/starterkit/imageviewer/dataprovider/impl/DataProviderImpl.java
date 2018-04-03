package com.starterkit.imageviewer.dataprovider.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.starterkit.imageviewer.dataprovider.DataProvider;

/**
 * {@link DataProvider} implementation.
 * 
 * @author Leszek
 */
@Component
public class DataProviderImpl implements DataProvider {

    private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

    private static final Collection<String> EXTENSIONS = Collections.unmodifiableCollection(//
            Arrays.asList(new String[] { "png", "jpg", "jpeg", "bmp", "gif" }));

    @Override
    public List<File> listImages(File directoryPath) throws IOException {
        if (directoryPath == null) {
            throw new IllegalArgumentException("directoryPath must not be null");
        }
        if (!directoryPath.isDirectory()) {
            throw new IllegalArgumentException("directoryPath must denote a directory");
        }

        LOG.info("Listing images in " + directoryPath);

        List<File> images = Arrays.asList(directoryPath.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                String extension = FilenameUtils.getExtension(pathname.getName()).toLowerCase();
                return pathname.isFile() && EXTENSIONS.contains(extension);
            }

        }));
        if (images == null) {
            throw new IOException("Could not list images in " + directoryPath);
        }

        LOG.info(images.size() + " images found");

        return images;
    }

    @Override
    public InputStream loadImage(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        LOG.info("Loading image from " + file);

        // we load the file explicitly here to keep the time consuming operation in the data provider
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
    }

}
