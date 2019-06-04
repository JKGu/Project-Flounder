import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FitnessAuto extends Fitness {

    @Override
    public int getFitness(Individual i, FileManager im) {
        int output=0;
        for(int k=0, size=im.croppedFiles.size(); k<size; k++){
            int s =  calSimilarity(i, im.croppedFiles.get(k));
            output+=s*s*s*awardAesthetics(i, im);
        }
        output= output/im.croppedFiles.size();
        i.label1=output;

        return output;
    }

    private int calSimilarity(Individual i, String file) {//need to make sure cropped image got same size with testing individuals
        BufferedImage img = null;
        double x=0;
        
        try {
            img = ImageIO.read(new File(file));
            int dotSize = i.findDotSize(img.getWidth());
            BufferedImage pattern = i.getImage(dotSize);
            int marginW = (int) (img.getWidth() * 0.15);
            int marginH = (int) (img.getWidth() * 0.15);
            for(int m=0, width=img.getWidth(); m<width; m++){
                for(int n=0, height=img.getHeight(); n<height; n++){
                    Color imgC = new Color(img.getRGB(m, n));
                    Color patternC = new Color(pattern.getRGB(m, n));
                    double difference = Math.sqrt(Math.pow(imgC.getRed()-patternC.getRed(), 2)+Math.pow(imgC.getBlue()-patternC.getBlue(), 2)+Math.pow(imgC.getGreen()-patternC.getGreen(), 2));
                    if(m<marginW||m>img.getWidth()-marginW||n<marginH||n>img.getHeight()-marginH){
                        x+=1/(1+difference);
                    }else{
                        x+=1/(1+difference/10);
                    }
                }
            }
            x=x/(img.getWidth()*img.getHeight());//average 
            //x=x*(1+(10000-x));if(x>10000) x=10000;//curve
        } catch (IOException e) {
        }
        return (int)(1000000*x);
    }

    
}