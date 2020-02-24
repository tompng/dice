import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
class FillImage{
	public static void main(String args[])throws Exception{
		BufferedImage img=ImageIO.read(new File(args[0]));
		int W=img.getWidth(),H=img.getHeight();
		BufferedImage img2=new BufferedImage(W,H,BufferedImage.TYPE_INT_ARGB);
		for(int n=0;n<20;n++){
			for(int x=0;x<W;x++)for(int y=0;y<H;y++){
				int c=img.getRGB(x,y);
				if((c>>24)==0){
					int c2;
					if(x>0&&((c2=img.getRGB(x-1,y))>>24)!=0)c=c2;
					else if(x<W-1&&((c2=img.getRGB(x+1,y))>>24)!=0)c=c2;
					else if(y>0&&((c2=img.getRGB(x,y-1))>>24)!=0)c=c2;
					else if(y<H-1&&((c2=img.getRGB(x,y+1))>>24)!=0)c=c2;
				}
				img2.setRGB(x,y,c);
			}
			BufferedImage tmp=img;img=img2;img2=tmp;
		}
		
		BufferedImage out=new BufferedImage(W,H,BufferedImage.TYPE_INT_RGB);
		for(int x=0;x<W;x++)for(int y=0;y<H;y++)out.setRGB(x,y,img.getRGB(x,y));
		ImageIO.write(out,"png",new File(args[1]));
	}
}