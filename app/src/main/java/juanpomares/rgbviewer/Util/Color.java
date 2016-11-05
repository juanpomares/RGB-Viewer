package juanpomares.rgbviewer.Util;

public class Color
{
    private float ValueR=0, ValueG=0, ValueB=0, ValueA=255;

    public Color(){}

    public Color(int _R, int _G, int _B, int _A)           { setValueR(_R); setValueG(_G); setValueB(_B); setValueA(_A);}
    public Color(int _R, int _G, int _B)                   { setValueR(_R); setValueG(_G); setValueB(_B);}

    public Color(float _R, float _G, float _B, float _A)    { setValueR(_R); setValueG(_G); setValueB(_B); setValueA(_A);}
    public Color(float _R, float _G, float _B)             { setValueR(_R); setValueG(_G); setValueB(_B);}

    public float getValueR(){return ValueR;}
    public float getValueG(){return ValueG;}
    public float getValueB(){return ValueB;}
    public float getValueA(){return ValueA;}


    public void setValueR(float _R){ValueR=Math.max(0, Math.min(1, _R));}
    public void setValueG(float _G){ValueG=Math.max(0, Math.min(1, _G));}
    public void setValueB(float _B){ValueB=Math.max(0, Math.min(1, _B));}
    public void setValueA(float _A){ValueA=Math.max(0, Math.min(1, _A));}

    public void setValueR(int _R){setValueR(_R/255f);}
    public void setValueG(int _G){setValueG(_G/255f);}
    public void setValueB(int _B){setValueB(_B/255f);}
    public void setValueA(int _A){setValueA(_A/255f);}

    public static  Color REDCOLOR=new Color(255, 0, 0);
    public static  Color GREENCOLOR=new Color(0, 255, 0);
    public static  Color BLUECOLOR=new Color(0, 0, 255);
    public static  Color WHITECOLOR=new Color(255, 255, 255);
    public static  Color BLACKCOLOR=new Color(0, 0, 0);
}
