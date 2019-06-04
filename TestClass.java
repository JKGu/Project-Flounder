import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class TestClass {

    public static void startNew(String workingDirectory, int populationSize, int generationNum, int elitism, int mainColorNum, int dotDimensionInPixels, int blockDimensionInDots, int imageDimensionInBlocks){
        FileManager fm = new FileManager(workingDirectory);
        fm.cleanAllDirectory();
            System.out.println("Working directory configured...");
        fm.loadSamples();   
            System.out.println("Raw samples loaded...");
        fm.generateCroppedFiles(fm.originalFiles.size()*50, dotDimensionInPixels*blockDimensionInDots*imageDimensionInBlocks, dotDimensionInPixels*blockDimensionInDots*imageDimensionInBlocks);
            System.out.println("Training data ready...");
        Population p = new Population(populationSize, mainColorNum, imageDimensionInBlocks, imageDimensionInBlocks, blockDimensionInDots);    
            System.out.println("Population initiated...\n[Genes size="+(p.population.iterator().next().genes.length()/1024)+"Bytes][Population size="+(p.population.iterator().next().genes.length()/1024)*populationSize+"Bytes]\nEvolution started...");
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        for(int genNum=1; genNum<=generationNum; genNum++){
            System.out.println("\tGeneration "+genNum+"...");
            Evolution.evolve(p, elitism, genNum, fm);
            p.generationNumTag = genNum;
            Individual record = p.getBestIndividual();
            fm.writeFitnessData(record.label1);
            if(genNum%10==0){
                fm.backupXMLFile(p, genNum, dotDimensionInPixels);
                BufferedImage img = record.getImage(blockDimensionInDots);
                try {
                    ImageIO.write(img, "bmp", new File(workingDirectory+"/Generated/"+genNum+".bmp"));
                } catch (IOException e) {
                }
            }
            if(genNum==1){
                    elapsedTime = new Date().getTime()-startTime;
                    long millis = elapsedTime;
                    String hms1 = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                    millis = elapsedTime*populationSize;
                    String hms2 = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    System.out.println("[Estimated time per evolution:"+hms1+"][Estimated total time for "+generationNum+" evolutions:"+hms2+"]");
            }
        }
    }

    public static void resume(String workingDirectory, int generationNum, int elitism){
        FileManager fm = new FileManager(workingDirectory);
            System.out.println("Working directory configured...");
        fm.loadSamples();   
            System.out.println("Raw samples loaded...");
        fm.loadCroppedFiles();
            System.out.println("Training data loaded...");
        Population p = fm.resumeFromBackup();
            System.out.println("Evolution resumed from previous population: \n[Generation:"+p.generationNumTag+"][Genes size="+(p.population.iterator().next().genes.length()/1024)+"Bytes][Population size="+(p.population.iterator().next().genes.length()/1024)*p.size+"Bytes]");

        for(int genNum=p.generationNumTag; genNum<generationNum; genNum++){
            System.out.println("Working on generation"+genNum+"...");
            Evolution.evolve(p, elitism, genNum, fm);
            p.generationNumTag = genNum;
            Individual record = p.getBestIndividual();
            fm.writeFitnessData(record.label1);
            if(genNum%10==0){
                fm.backupXMLFile(p, genNum, p.dotSizeInPixels);
                BufferedImage img = record.getImage(p.numOfDots);
                try {
                     ImageIO.write(img, "bmp", new File(workingDirectory+"/Generated/"+genNum+".bmp"));
                } catch (IOException e) {
                 }
            }
        }
    }
    public static void main(String[] args) {
        //startNew("C://Users/steve/Desktop/New folder/Path2", 100, 1000, 10, 7, 5, 4, 5);
        resume("C://Users/steve/Desktop/New folder/Path2", 1000, 10);
        System.out.println("DONE.");
    }
}