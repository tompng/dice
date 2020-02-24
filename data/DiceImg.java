import java.io.*;
import java.awt.image.*;
import javax.imageio.*;


class DiceImg{
	public static void main(String args[])throws Exception{
		int S=128;
		BufferedImage carve=ImageIO.read(new File("carve_out.png"));
		BufferedImage pattern=ImageIO.read(new File("pattern.png"));
		double carveData[][]=new double[S][6*S];
		for(int x=0;x<S;x++)for(int y=0;y<6*S;y++){
			carveData[x][y]=carve.getRGB(x,y)&0xff;
		}
		double carveNormal[][][]=new double[S][6*S][3];
		for(int x=1;x<S-1;x++)for(int y=1;y<6*S-1;y++){
			double dx=carveData[x+1][y]-carveData[x-1][y];
			double dy=carveData[x][y+1]-carveData[x][y-1];
			double dz=128;
			double dr=Math.sqrt(dx*dx+dy*dy+dz*dz);
			carveNormal[x][y][0]=dx/dr;
			carveNormal[x][y][1]=dy/dr;
			carveNormal[x][y][2]=dz/dr;
			if(dr==dz)carveNormal[x][y][2]=0;
		}
		
		double D=1./8;
		double normal[][][]=new double[S][S][3];
		for(int ix=0;ix<S;ix++)for(int iy=0;iy<S;iy++){
			double x=(ix+0.5)/S,y=(iy+0.5)/S;
			double dx=(x<D?x-D:x>1-D?x-1+D:0)/D;
			double dy=(y<D?y-D:y>1-D?y-1+D:0)/D;
			double dz=1;
			double dr=Math.sqrt(dx*dx+dy*dy+dz*dz);
			dx/=dr;dy/=dr;dz/=dr;
			normal[ix][iy][0]=dx;
			normal[ix][iy][1]=dy;
			normal[ix][iy][2]=dz;
		}
		int L=2;
		double coords[][][]=new double[2*L+2][2*L+2][3];
		for(int i=0;i<=2*L+1;i++)for(int j=0;j<=2*L+1;j++){
			double x=i<=L?D*i/L:1-D+D*(i-L-1)/L;
			double y=j<=L?D*j/L:1-D+D*(j-L-1)/L;
			double x0=x<0.5?D:1-D;
			double y0=y<0.5?D:1-D;
			double dx=x-x0,dy=y-y0,dz=D;
			double dr=Math.sqrt(dx*dx+dy*dy+dz*dz);
			coords[i][j][0]=2*(x0+D*dx/dr)-1;
			coords[i][j][1]=2*(y0+D*dy/dr)-1;
			coords[i][j][2]=1+2*(D*dz/dr-D);
		}
		
		
		BufferedImage nimg=new BufferedImage(4*S,4*S,BufferedImage.TYPE_INT_ARGB);
		BufferedImage timg=new BufferedImage(4*S,4*S,BufferedImage.TYPE_INT_ARGB);
		PrintWriter vout=new PrintWriter(new FileOutputStream(new File("dice_vertex.js")));
		PrintWriter tout=new PrintWriter(new FileOutputStream(new File("dice_texcoord.js")));
		vout.println("dice_vertex=[");
		tout.println("dice_texcoord=[");
		for(int i=0;i<6;i++){
			int ix0,iy0;
			if(i<4){ix0=S*i;iy0=S/2;}
			else{ix0=S/2+2*S*(i-4);iy0=2*S+S/2;}
			DiceImg rotator=new DiceImg(i);
			int carveArr[]={0,1,5,4,2,3};
			int carveIndex=S*carveArr[i];
			for(int x=0;x<S;x++)for(int y=0;y<S;y++){
				double[]v=normal[x][y];
				double[]v2=carveNormal[x][y+carveIndex];
				if(v2[2]!=0)v=v2;
				
				double dx=rotator.rotateX(v);
				double dy=rotator.rotateY(v);
				double dz=rotator.rotateZ(v);
				nimg.setRGB(ix0+x,iy0+y,((int)(127+127*dx+0.5)*0x10000+(int)(127+127*dy+0.5)*0x100+(int)(127+127*dz+0.5))|0xff000000);
				timg.setRGB(ix0+x,iy0+y,pattern.getRGB(x,y+carveIndex)|0xff000000);
			}
			for(int x=0;x<2*L+1;x++)for(int y=0;y<2*L+1;y++){
				double tx=x<=L?D*x/L:1-D+D*(x-L-1)/L;
				double ty=y<=L?D*y/L:1-D+D*(y-L-1)/L;
				double tx2=x+1<=L?D*(x+1)/L:1-D+D*(x+1-L-1)/L;
				double ty2=y+1<=L?D*(y+1)/L:1-D+D*(y+1-L-1)/L;
				vout.println(
					rotator.rotateX(coords[x][y])+","+rotator.rotateY(coords[x][y])+","+rotator.rotateZ(coords[x][y])+",\t"+
					rotator.rotateX(coords[x+1][y])+","+rotator.rotateY(coords[x+1][y])+","+rotator.rotateZ(coords[x+1][y])+",\t"+
					rotator.rotateX(coords[x+1][y+1])+","+rotator.rotateY(coords[x+1][y+1])+","+rotator.rotateZ(coords[x+1][y+1])+","
				);
				vout.println(
					rotator.rotateX(coords[x][y])+","+rotator.rotateY(coords[x][y])+","+rotator.rotateZ(coords[x][y])+",\t"+
					rotator.rotateX(coords[x+1][y+1])+","+rotator.rotateY(coords[x+1][y+1])+","+rotator.rotateZ(coords[x+1][y+1])+",\t"+
					rotator.rotateX(coords[x][y+1])+","+rotator.rotateY(coords[x][y+1])+","+rotator.rotateZ(coords[x][y+1])+","
				);
				vout.println();
				tout.println(
					(tx/4+ix0/4.0/S)+","+(ty/4+iy0/4.0/S)+",\t"+
					(tx2/4+ix0/4.0/S)+","+(ty/4+iy0/4.0/S)+",\t"+
					(tx2/4+ix0/4.0/S)+","+(ty2/4+iy0/4.0/S)+","
				);
				tout.println(
					(tx/4+ix0/4.0/S)+","+(ty/4+iy0/4.0/S)+",\t"+
					(tx2/4+ix0/4.0/S)+","+(ty2/4+iy0/4.0/S)+",\t"+
					(tx/4+ix0/4.0/S)+","+(ty2/4+iy0/4.0/S)+","
				);
				tout.println();
			}
		}
		vout.println("]");
		tout.println("]");
		vout.close();
		tout.close();
		ImageIO.write(nimg,"png",new File("normal_.png"));
		ImageIO.write(timg,"png",new File("texture.png"));
	}
	int type;
	DiceImg(int type){this.type=type;}
	double rotateX(double[]vector){
		switch(type){
			case 0:return vector[0];
			case 1:return vector[2];
			case 2:return -vector[0];
			case 3:return -vector[2];
			case 4:return vector[0];
			case 5:return vector[0];
		}
		return 0;
	}
	double rotateY(double[]vector){
		switch(type){
			case 0:return vector[1];
			case 1:return vector[1];
			case 2:return vector[1];
			case 3:return vector[1];
			case 4:return vector[2];
			case 5:return -vector[2];
		}
		return 0;
	}
	double rotateZ(double[]vector){
		switch(type){
			case 0:return vector[2];
			case 1:return -vector[0];
			case 2:return -vector[2];
			case 3:return vector[0];
			case 4:return -vector[1];
			case 5:return vector[1];
		}
		return 0;
	}
}