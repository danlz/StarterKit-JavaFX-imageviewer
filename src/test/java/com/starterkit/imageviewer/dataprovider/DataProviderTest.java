package com.starterkit.imageviewer.dataprovider;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.starterkit.imageviewer.dataprovider.impl.DataProviderImpl;

/**
 * Tests for {@link DataProvider}.
 * 
 * @author Leszek
 */
public class DataProviderTest {

    private DataProvider testee = new DataProviderImpl();

    @Test
    public void testEmptyDirectory() throws Exception {
        List<File> result = testee.listImages(new File("./src/test/resources/empty-directory"));

        Assert.assertThat(result, Matchers.empty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDirectoryWithImages() throws Exception {
        List<File> result = testee.listImages(new File("./src/test/resources/directory-with-images"));

        assertThat(result,
                containsInAnyOrder(//
                        hasProperty("name", equalTo("image1.png")), //
                        hasProperty("name", equalTo("image2.jpg")) //
                ));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListFile() throws Exception {
        testee.listImages(new File("./src/test/resources/not-an-image"));
    }

}
