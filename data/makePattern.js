document.body.innerHTML="<canvas width=128 height=768>"
canvas=document.body.firstChild
canvas.style.background="silver";
g=canvas.getContext("2d")
var num;
function arc(x,y,r){
	g.beginPath();
	g.arc(64+64*x,128*(num-1)+64+64*y,64*r,0,Math.PI*2,false);
	g.fill();
}

r=0.18;
num=1;
g.fillStyle="red";
arc(0,0,0.3);
num=2;
arc(-0.4,-0.4,r);
arc(0.4,0.4,r);

g.fillStyle="black";

num=3;
arc(-0.45,-0.45,r);
arc(0,0,r);
arc(0.45,0.45,r);

num=4;
arc(-0.4,-0.4,r);
arc(0.4,-0.4,r);
arc(-0.4,0.4,r);
arc(0.4,0.4,r);

num=5;
arc(-0.45,-0.45,r);
arc(0.45,-0.45,r);
arc(-0.45,0.45,r);
arc(0.45,0.45,r);
arc(0,0,r);

num=6

arc(-0.4,-0.45,r);
arc(0.4,-0.45,r);
arc(-0.4,0,r);
arc(0.4,0,r);
arc(-0.4,0.45,r);
arc(0.4,0.45,r);

