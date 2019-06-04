import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Individual{
    /*
    Encoding:
    n*[main color code]+i*j*(n)*[percentage]
    | color part       |  pattern part        |
    length:
    n*(24)+i*j*n*(3)
        (16 dots each block)
    */
    protected String genes;
    protected int mainColorNum;
    protected int width;//in blocks
    protected int height;//in blocks
    protected int dots;
    public int label1;

    public Individual(int mainColorNum, int i, int j, int numOfDots){
        this.mainColorNum=mainColorNum;
        int length = mainColorNum*24+i*j*(mainColorNum)*3;
        this.genes = new String();
        this.width = i; this.height = j;
        this.dots = numOfDots;

        StringBuilder sbuf = new StringBuilder();
            for (int k=0; k<length; k++) {
                int r = (int)(Math.random()*2);
                if(r==0)  sbuf.append("0");
                else  sbuf.append("1");
        }
        this.genes = sbuf.toString();

        this.label1=0;
    }

    public Individual individualWithSameSettings(){
        return new Individual(this.mainColorNum, this.width, this.height, this.dots);
    }

    public BufferedImage getImage(int dotSize){
        BufferedImage image = new BufferedImage(this.width*dots*dotSize, this.height*dots*dotSize, BufferedImage.TYPE_3BYTE_BGR);
        ArrayList<String> blockList = getVectorOfBlocks();
        for(int i=0;i<width; i++){
            for(int j=0; j<height; j++){//look at a block
                String block = blockList.get(i*width+j);
                for(int m=0; m<dots; m++){//look at a dot
                    for(int n=0; n<dots; n++){
                        String color = pickColor(getListOfMainColors(), getVectorOfPercentages(block));
                        for(int k =0; k<dotSize; k++){//look at a pixel
                            for(int l=0; l<dotSize; l++){
                                image.setRGB(i*dots*dotSize+m*dotSize+k, j*dots*dotSize+n*dotSize+l, Integer.parseInt(color, 16));
                            }
                        }
                    }
                }
            }
        }
        return image;
    }

    public ArrayList<String> getListOfMainColors(){
        ArrayList<String> output = new ArrayList<String>();
        int stringPtr = 0;
        for(int i=0; i<mainColorNum; i++){
            String color = genes.substring(stringPtr, stringPtr+24);
            int decimal = Integer.parseInt(color,2);
            String hex = Integer.toString(decimal, 16);
            output.add(hex);
            stringPtr+=24;
        }
        return output;
    }

    public int getTotalBlockCount(){
        return width*height;
    }
    public int getTotalDotCount(){
        return width*height*dots*dots;
    }
    public final int findDotSize(int widthInPixels){
        return widthInPixels/(width*dots);
    }
    protected ArrayList<String> getVectorOfBlocks(){  //return a list of blocks
        ArrayList<String> output = new ArrayList<String>();
        String tmp = this.genes.substring(mainColorNum*24);
        int ptr = 0;
        for(int i = 0; i<width; i++){
            for(int j=0; j<height; j++){
                output.add(tmp.substring(ptr, ptr+(mainColorNum)*3));
                ptr+=(mainColorNum)*3;
            }
        }
        return output;
    }
    protected ArrayList<Integer> getVectorOfPercentages(String block){
        ArrayList<Integer> output = new ArrayList<Integer>();
        int t =0;
        int ptr =0;
        for(int i=0; i<(mainColorNum); i++){
            String tmp =block.substring(ptr, ptr+3);
            output.add(Integer.parseInt(tmp, 2));
            if(output.get(i)<0) output.set(i, output.get(i)*-1);
            t+=output.get(i);
            ptr+=3;
        }
        for(Integer i:output){
            i= i*10/t;
        }
        return output;
    }
    private String pickColor(ArrayList<String> mainColorList, ArrayList<Integer> percentageList){
        int num = (int)(Math.random()*10);
        int accumulator = 0;
        int index=0;
        while(accumulator<num){
            if(index==mainColorNum-1) break;
            accumulator+=percentageList.get(index);
            index++;
        }
        String output = mainColorList.get(index);
        return output;
    }

    public String toString(){
        String output = new String();
        output+="Number of main colors:"+mainColorNum+" [";
        output+=this.getListOfMainColors().toString();
        output+=" ] Genes length="+genes.length();
        return output;
    }

}