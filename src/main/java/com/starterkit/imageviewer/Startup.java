package com.starterkit.imageviewer;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Application entry point.
 *
 * @author Leszek
 */
@ComponentScan
public class Startup extends Application {

    private static final int INITIAL_WINDOW_WIDTH = 800;
    private static final int INITIAL_WINDOW_HEIGHT = 600;

    public static void main(String[] args) {
        Application.launch(args);
    }

    private AbstractApplicationContext context;

    @Override
    public void init() throws Exception {
        this.context = new AnnotationConfigApplicationContext(Startup.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /*
         * Set the default locale based on the '--lang' startup argument.
         */
        String langCode = getParameters().getNamed().get("lang");
        if (langCode != null && !langCode.isEmpty()) {
            Locale.setDefault(Locale.forLanguageTag(langCode));
        }

        FXMLLoader loader = new FXMLLoader( //
                getClass().getResource("/com/starterkit/imageviewer/view/main.fxml"), //
                ResourceBundle.getBundle("com/starterkit/imageviewer/bundle/base"), //
                new JavaFXBuilderFactory(), //
                clazz -> context.getBean(clazz) //
        );

        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/starterkit/imageviewer/css/standard.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle(loader.getResources().getString("imageViewer"));
        primaryStage.setMinWidth(INITIAL_WINDOW_WIDTH);
        primaryStage.setMinHeight(INITIAL_WINDOW_HEIGHT);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("AppIcon.png")));

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        this.context.close();
    }

}
