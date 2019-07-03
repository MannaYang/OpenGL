attribute vec4 vPosition;
attribute vec2 vCoordinate;
uniform mat4 vMatrix;

varying vec2 aCoordinate;
varying vec4 aPosition;
varying vec4 gPosition;

void main(){
    gl_Position=vMatrix*vPosition;
    aPosition=vPosition;
    aCoordinate=vCoordinate;
    gPosition=vMatrix*vPosition;
}