package juanpomares.rgbviewer.Util;


public class Point3D
{
    private Color color=Color.WHITECOLOR;

    private float ValueX =0, ValueY =0, ValueZ =0;

    public Point3D(){}
    public Point3D(float _x, float _y, float _z)              {ValueX =_x; ValueY =_y; ValueZ =_z;}
    public Point3D(float _x, float _y, float _z, Color _color){ValueX =_x; ValueY =_y; ValueZ =_z; color=_color;}


    public float getX()  {return ValueX;}
    public float getY()  {return ValueY;}
    public float getZ()  {return ValueZ;}
    public Color getColor()  {return color;}

    public void setX(float _x)  {ValueX =_x;}
    public void setY(float _y)  {ValueY =_y;}
    public void setZ(float _z)  {ValueZ =_z;}
    public void setColor(Color _color){color=_color;}

}
