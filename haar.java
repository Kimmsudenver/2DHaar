package haar;

import java.awt.GridLayout;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class haar extends JFrame {
 public static BufferedImage img=null,Rimg=null,Iimg=null;
 public static JFrame window;
 public static JLabel label1,label2,label3,label4;
 public static JPanel panel1,panel2;
 public static int timesr=0,timesc=0;
 
    public static void main(String arg[])
     { window=new JFrame("2D Haar Compression");
     //window.setSize(1000,1000);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setDefaultLookAndFeelDecorated(true);
      window.setLayout(new GridLayout(2,1));
      File fl=null;
      try{
    	  JFileChooser choose = new JFileChooser();
			int result=choose.showOpenDialog(null);
			if(result==JFileChooser.APPROVE_OPTION)
			 fl=new File(choose.getSelectedFile().getPath());
      img=ImageIO.read(fl);
      label1= new JLabel();
      panel1=new JPanel();
      panel2=new JPanel();
         label1.setIcon(new ImageIcon(img));
       ColorConvertOp gray=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
         gray.filter(img, img);
         label2=new JLabel("grey Image");
         label2.setIcon(new ImageIcon(img));
         double [][] data=initial(img);
         System.out.println("rows: "+data.length +"columns: "+data[0].length);
      double[][] compressData=compress(data);
      Rimg=createImg(compressData);
      label3=new JLabel("Intermediate Image");
      label3.setIcon(new ImageIcon(Rimg));
      Iimg=createImg(inverse(compressData));
      label4=new JLabel("inverse");
      label4.setIcon(new ImageIcon(Iimg));
    //  	panel1.add(label1);
         panel1.add(label2);
         panel2.add(label3);
         panel2.add(label4);
         window.add(panel1);
         window.add(panel2);
         window.pack();
         window.setVisible(true);
         
         //test with small matrix, result in console to see the actual number 
         double[][] test=new double[4][4];
         int b=0;
        timesr=timesc=2;
         System.out.println(Math.log(4)/Math.log(2.0)+" ");
         for(int i=0;i<4;i++)
         {
          for(int j=0;j<4;j++){
           test[i][j]=b++;
          System.out.print(test[i][j]+" ");}
          System.out.println("\n");
         }
         test=compress(test);
         System.out.println("rows:"+test.length);
         for(int i=0;i<test.length;i++)
         {
          for(int j=0;j<test[0].length;j++)           
           System.out.print(test[i][j]+" ");
          System.out.println("\n");
         }
         test=inverse(test);
         System.out.println("cols: "+test.length+"rows:" +test[0].length);
         for(int i=0;i<test.length;i++)
         {
          for(int j=0;j<test[0].length;j++)           
          System.out.print(test[i][j]+" ");
          System.out.println("\n");
         }         
     }
     catch(Exception e){
      e.printStackTrace();
     }
}
     
    //This function take an image and return gray pixel value of that image in a 2D array of Double
   public static double[][] initial(BufferedImage m)
     {
      int w=m.getWidth();
      int h=m.getHeight();   
      double[][] pixel=new double[w][h];
      for (int x=0;x<w;x++)
       {
        for(int y=0;y<h;y++)
        {
         pixel[x][y]=m.getRGB(x,y)&0xFF;
         System.out.print(pixel[x][y]+" ");
        }
        
       }
      System.out.println("done");
     // BufferedImage test= createImg(pixel); this is for debugging
  // JLabel testlabel=new JLabel("test");
  // testlabel.setIcon(new ImageIcon(test));
  // panel.add(testlabel);
      return pixel;
     }
     

   //This function re-create image from an array of pixel
public static BufferedImage createImg(double[][] data){
 int w=data.length;
 int h=data[0].length; 
 BufferedImage im= new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
 WritableRaster raster=im.getRaster();
 for(int i=0;i<w;i++){
  for(int j=0;j<h;j++){   
   double value=data[i][j];
   raster.setSample(i, j, 0, data[i][j]);
  }
 }
 im.setData(raster);
 return im;
}

//This function perform forward 2D Haar compression on input array
public static double[][] compress(double[][] orig){
 int w=orig[0].length;
 int h=orig.length;
 double temp[]=new double[w];
 double[][] result=new double[1000][1000];
 int time;
 //rows 
 for(int i=0;i<h;i++){
  int l=w;
  do{int k=0;
    for(int j=0;j<l-1;j+=2)//averaging
    	temp[k++]=(orig[i][j]+orig[i][j+1])/2;
    for(int j=0;j<l-1;j+=2)
    	temp[k++]=(orig[i][j]-orig[i][j+1])/2;//difference
    for(int j=0;j<w;j++)
    	orig[i][j]=temp[j];
   l=l/2;   
   
  }
  while(l!=w/4);//small 4 times instead of l!=1 which is the smallest( hard to see the picture)
  
 }
 
 temp=new double[h];
  //column transformation
  for(int i=0;i<w;i++){
   int l=h;
   do{
    int k=0;
    //time=0;
    for(int j=0;j<l-1;j+=2)
     temp[k++]=(orig[j][i]+orig[j+1][i])/2;
    for(int j=0;j<l-1;j+=2)
     temp[k++]=(orig[j][i] - orig[j+1][i])/2;
    for(int j=0;j<h;j++)
     orig[j][i]=temp[j];
    l=l/2;   
   }
   while(l!=h/4);   
 } 
 
 return orig;
}

//This function perform inverse 2D Haar compression on input array
public static double[][] inverse(double[][] orig){
 int w=orig[0].length;
 int h=orig.length;
 int[][] result=new int[1000][1000];
 double[] temp=new double[h+w];
 //column transformation
 for(int i=0;i<w;i++){
  int l=h/4;  
  do{
   int k=0;
  // int m=l;
   for(int j=0;j<l;j++){
    temp[k++]=orig[j][i]+orig[j+l][i];
    temp[k++]=orig[j][i]-orig[j+l][i];
   }
   for(int j=0;j<k;j++)
    orig[j][i]=temp[j];
   l*=2;
  }
  while(l<=h/2);
}

  temp=new double[h+w];
 //rows transformation
 for(int i=0;i<h;i++){
  int l=w/4;
  //System.out.println("timer: "+l);
  do{
   int k=0;  
   for(int j=0;j<l;j++){
    temp[k++]=orig[i][j]+orig[i][j+l];
    temp[k++]=orig[i][j]-orig[i][j+l];
   }
   for(int j=0;j<k;j++)
    orig[i][j]=temp[j];
   l*=2;
  }
  while(l<=w/2);
 }
 
 return orig;
}


     
}
     

