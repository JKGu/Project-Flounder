import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FileManager {
    private String path;
    protected ArrayList<String> originalFiles;
    protected ArrayList<String> croppedFiles;
    private FileWriter writer;

    public FileManager(String workingPath) {
        this.path = workingPath;
        File dir1 = new File(path + "/Original");
        dir1.mkdir();
        File dir2 = new File(path + "/Cropped");
        dir2.mkdir();
        File dir3 = new File(path + "/Generated");
        dir3.mkdir();
        File dir4 = new File(path + "/Data");
        dir4.mkdir();
        this.originalFiles = new ArrayList<String>();
        this.croppedFiles = new ArrayList<String>();
        // -----------WRITING FITNESS DATA
        File fitnessData = new File(path + "/Data/FitnessData.txt");
        try {
            fitnessData.createNewFile();
        } catch (IOException e) {
            System.out.println("ERROR:" + e.toString());
            e.printStackTrace();
        }
        try {
            writer = new FileWriter(path + "/Data/FitnessData.txt");
        } catch (IOException e) {
            System.out.println("ERROR:" + e.toString());
            e.printStackTrace();
        }
    }

    public void loadSamples(){
        File dir = new File(path+"/Original");
        File[] directoryListing = dir.listFiles();
        for(File f: directoryListing){
            originalFiles.add(f.getAbsolutePath());
        }
    }

    public void loadCroppedFiles(){
        File dir = new File(path+"/Cropped");
        File[] directoryListing = dir.listFiles();
        for(File f: directoryListing){
            croppedFiles.add(f.getAbsolutePath());
        }
    }

    public void generateCroppedFiles(int num, int w, int h){
        for(int i=0; i<num; i++){
            int r = (int)(Math.random()*(originalFiles.size()));
            try {
                BufferedImage b = ImageIO.read(new File(originalFiles.get(r)));
                int x = (int)(Math.random()*b.getWidth());
                int y = (int)(Math.random()*b.getHeight());
                if(x+w<b.getWidth()-1&&y+h<b.getHeight()-1){
                    BufferedImage tmp = b.getSubimage(x, y, w, h);
                    try {
                        ImageIO.write(tmp, "bmp", new File(path+"/Cropped/"+Integer.toString(i,16)+".bmp"));
                        this.croppedFiles.add(path+"/Cropped/"+Integer.toString(i,16)+".bmp");
                    } catch (IOException e) {
                    }
                }else{
                    if(i!=0) i--;
                }
            } catch (IOException e) {
                if(i!=0) i--;
            }
        }
    }

    public void writeFitnessData(int x){
        try {
            writer.write(x+"\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("ERROR:"+e.toString());e.printStackTrace();
        }
    }

    private void cleanDirectory(File file){  
       String[] myFiles;    
           if(file.isDirectory()){
               myFiles = file.list();
               for (int i=0; i<myFiles.length; i++) {
                   File myFile = new File(file, myFiles[i]); 
                   myFile.delete();
               }
            }
    }

    public void cleanAllDirectory(){
        File dir2 = new File(path+"/Cropped");
        File dir3 = new File(path+"/Generated");
        cleanDirectory(dir2);
        cleanDirectory(dir3);
    }

    public void backupXMLFile(Population p, int generationCount, int dotDimensionInPixels){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("flounder");
            doc.appendChild(rootElement);
            // basic elements
            Element basics = doc.createElement("basics");
            rootElement.appendChild(basics);
                basics.setAttribute("workingDirectory", this.path);
                basics.setAttribute("populationSize", Integer.toString(p.size));
                basics.setAttribute("generationCount", Integer.toString(generationCount) );
                Individual sample = p.population.iterator().next();
                basics.setAttribute("mainColorNum", Integer.toString(sample.mainColorNum));
                basics.setAttribute("dotDimensionInPixels", Integer.toString(dotDimensionInPixels));
                basics.setAttribute("blockDimensionInDots", Integer.toString(sample.dots*dotDimensionInPixels));
                basics.setAttribute("imageDimensionInBlocks", Integer.toString(sample.height));
            //Population
            Element population = doc.createElement("population");
            rootElement.appendChild(population);
            HashSet<Individual> list = p.population;
            for(Individual i: list){
                Element individual = doc.createElement("individual");
                population.appendChild(individual);
                    individual.setAttribute("genes", i.genes);
            }
    
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(this.path+"/Data/BackupData.xml"));
            transformer.transform(source, result);
          } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
          } catch (TransformerException tfe) {
            tfe.printStackTrace();
          }
        
    }

    public Population resumeFromBackup(){
        try {
            File fXmlFile = new File(this.path+"/Data/BackupData.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList nList1 = doc.getElementsByTagName("basics");
            Node nNode1 = nList1.item(0);
            Element eElement = (Element) nNode1;

            int populationSize = Integer.parseInt(eElement.getAttribute("populationSize"));
            int generationCount = Integer.parseInt(eElement.getAttribute("generationCount"));
            int mainColorNum = Integer.parseInt(eElement.getAttribute("mainColorNum"));
            int dotDimensionInPixels = Integer.parseInt((eElement.getAttribute("dotDimensionInPixels")));
            int blockDimensionInDots = Integer.parseInt(eElement.getAttribute("blockDimensionInDots"));
            int imageDimensionInBlocks = Integer.parseInt(eElement.getAttribute("imageDimensionInBlocks"));

            Population p = new Population(populationSize, mainColorNum, imageDimensionInBlocks, imageDimensionInBlocks, blockDimensionInDots, false);
            p.generationNumTag = generationCount;
            p.dotSizeInPixels = dotDimensionInPixels;
            
            NodeList nList = doc.getElementsByTagName("individual");
            int length = nList.getLength();
            for (int temp = 0; temp < length; temp++) {
                
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        
                    Element eElement1 = (Element) nNode;
                    Individual i = new Individual(mainColorNum, imageDimensionInBlocks, imageDimensionInBlocks, blockDimensionInDots);
                    i.genes = eElement1.getAttribute("genes");
                    i.dots = blockDimensionInDots;
                    i.height = imageDimensionInBlocks;
                    i.width = imageDimensionInBlocks;
                    i.mainColorNum = mainColorNum;
                    p.putIndividual(i);
                }
            }

            return p;
            } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error "+e.toString());
            return null;
            }
        
    }


}