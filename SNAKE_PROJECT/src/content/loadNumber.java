/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package content;

import static content.Snake.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Ada
 */
public class loadNumber extends Task<Integer>{
    private final int number;
    private Image three;
    private Image two;
    private Image one;
    

    public loadNumber(int number) {
        this.number = number;
        //init Images
        this.three=new Image(getClass().getResourceAsStream("resources/3.png"));
        this.two=new Image(getClass().getResourceAsStream("resources/2.png"));
        this.one=new Image(getClass().getResourceAsStream("resources/1.png"));
    }

    
    @Override
    protected Integer call() throws Exception {
        //wait 1s
        Thread.sleep(1000);
        //change label
        Platform.runLater(new Runnable() {
                @Override
                public void run() {
        switch (number){
            case 0:
                second.setGraphic(null);                
                setStarted(true);                
                break;
            case 1:               
                second.setGraphic(new ImageView(one)); 
                break;
            case 2:
                second.setGraphic(new ImageView(two));
                    break;
            case 3:
                second.setGraphic(new ImageView(three));
                    break;
            default:
                    break;
        }        
        }});
        
        
        
        
        
        return 0;
        }
    
}
