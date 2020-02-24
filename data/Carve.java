import java.io.*;
import java.awt.image.*;
import javax.imageio.*;


class Carve{
	public static void main(String args[])throws Exception{
		BufferedImage img=ImageIO.read(new File("carve_in.png"));
		double map[][]=new double[128][128*6];
		double data[][]=new double[128][128*6];
		int S=128;
		int index0[]=new int[S*6],index1[]=new int[S*6];
		for(int y=0;y<6*S;y++){
			int i0=-1,i1=-1;
			for(int x=0;x<S;x++){
				int c=(img.getRGB(x,y)>>24)&0xff;
				map[x][y]=(double)c/0xff;
				if(c!=0){
					if(i0<0)i0=x;
					i1=x;
				}
			}
			if(i0<0)i0=0;
			index0[y]=i0;index1[y]=i1+1;
		}
		
		for(int i=0;i<100;i++){
			for(int y=0;y<6*S;y++)for(int x=index0[y];x<index1[y];x++){
				double sum=data[x+1][y]+data[x-1][y]+data[x][y-1]+data[x][y+1];
				data[x][y]=(sum-1)/4*map[x][y];
			}
		}
		double max=0;
		for(int y=0;y<6*S;y++)for(int x=index0[y];x<index1[y];x++){
			if(max>data[x][y])max=data[x][y];
		}
		for(int y=0;y<6*S;y++)for(int x=0;x<S;x++){
			img.setRGB(x,y,0xff000000|(0x10101*(int)(0xff*data[x][y]/max)));
		}
		ImageIO.write(img,"png",new File("carve_out.png"));
	}
}