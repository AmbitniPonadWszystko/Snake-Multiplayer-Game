package content;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginWindow {
    
    //private Image image;
    private Stage primaryStage;
    
    public LoginWindow(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public GameConfiguration getConfiguration() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Witaj! Zaloguj się najpierw:");
        
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        
        //image = new Image(getClass().getResourceAsStream("resources/bg.png"));
        String image = LoginWindow.class.getResource("resources/bg.png").toExternalForm();
        dialog.getDialogPane().setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-repeat: repeat;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField playerName = new TextField();
        TextField ipAdress = new TextField();
        
        
        Label loginLabel = new Label("Login:");
        loginLabel.setStyle("-fx-text-fill: white;" + "-fx-font-weight: bold;");
        grid.add(loginLabel, 0, 0);
        grid.add(playerName, 1, 0);
        Label ipLabel = new Label("Adres IP:");
        ipLabel.setStyle("-fx-text-fill: white;" + "-fx-font-weight: bold;");
        grid.add(ipLabel, 0, 1);
        grid.add(ipAdress, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
                
        dialog.setOnCloseRequest((DialogEvent e) -> {
            ButtonType result = dialog.getResult();
            if (result == loginButtonType){
                String playerNameToString = playerName.getText();
                String ipToString = ipAdress.getText();

                Pattern pattern = Pattern.compile("[a-zA-Z0-9\\-_~.]{1,10}");
                Matcher matcher = pattern.matcher(playerNameToString);
                boolean playerNameMatches = matcher.matches();    

                Pattern patternIP = Pattern.compile("([0-9]{1,2}|1[0-9]{2}|2([0-4][0-9]|5[0-5]))\\.([0-9]{1,2}|1[0-9]{2}|2([0-4][0-9]|5[0-5]))\\.([0-9]{1,2}|1[0-9]{2}|2([0-4][0-9]|5[0-5]))\\.([0-9]{1,2}|1[0-9]{2}|2([0-4][0-9]|5[0-5]))");
                Matcher matcherIP = patternIP.matcher(ipToString);
                boolean ipMatches = matcherIP.matches(); 
                
                    
                if(!playerNameMatches){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Niepoprawny login");
                    alert.showAndWait();
                    e.consume();
                }
                else if(!ipMatches){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Niepoprawny adres IP");
                    alert.showAndWait();
                    e.consume();
                }
            }
        });
        
        dialog.showAndWait();

        if (dialog.getResult() == ButtonType.CANCEL){
            return null;
        }
        
        List<String> choices = new ArrayList<>();
        choices.add("czerwony");
        choices.add("różowy");
        choices.add("niebieski");
        choices.add("żółty");

        ChoiceDialog<String> colors = new ChoiceDialog<>("niebieski", choices);
        colors.getDialogPane().setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-repeat: repeat;");
        colors.setTitle("Teraz wybierz kolor:");
        colors.setHeaderText("Wybierz kolor z listy");
        
        
        colors.setOnCloseRequest((DialogEvent e) -> {
            // Spytać serwer czy kolor zajety
            //colors.getItems().remove(0);
            //e.consume();
            
            // Tak - e.consume() i podpierdolic z listy
            // Nie - spoko
        });
        
        Optional<String> result = colors.showAndWait();
            
        GameConfiguration config = new GameConfiguration();
        config.playerName = playerName.getText();
        config.ip = ipAdress.getText();
        config.color = colors.getSelectedItem();
        return config;
    } 
    
    boolean colorAvailable(String serverIp, String color){
        
        return true;
    }
    
    class GameConfiguration {
        public String playerName;
        public String ip;
        public String color;
    }
}


