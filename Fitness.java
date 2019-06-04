import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Fitness {
       
    public abstract int getFitness(Individual i, FileManager fm);

    protected final static int awardAesthetics(Individual i, FileManager fm){
        int mixedBlockCount = 0;
        BufferedImage tmp =null;
        try {
            tmp = ImageIO.read(new File(fm.croppedFiles.get(0)));
            int dotSize = i.findDotSize(tmp.getWidth());
            BufferedImage pattern = i.getImage(dotSize);
            int blockSize = dotSize*i.dots;
            for(int m=0, width=pattern.getWidth(); m<width; m+=blockSize){
                for(int n=0, height=pattern.getHeight(); n<height; n+=blockSize){
    
                    int note = pattern.getRGB(m, n);
                    loopCheckingABlock:
                    for(int j=0; j<blockSize; j++){
                        for(int k=0; k<blockSize; k++){
                            if(note!=pattern.getRGB(m+j, n+k)){
                                mixedBlockCount++;
                                break loopCheckingABlock;
                            }
                        }
                    }
                }
            }
            int mixedRate = mixedBlockCount*10000/i.getTotalBlockCount();
            return 10000-mixedRate+1;
        } catch (IOException e) {
            return 1;
        }

    }
}