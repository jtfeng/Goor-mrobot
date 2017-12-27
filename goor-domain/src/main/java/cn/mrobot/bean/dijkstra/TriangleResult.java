package cn.mrobot.bean.dijkstra;

/**
 * @author Created by chay on 2017/12/25.
 * 顶点P，与线段A,B组成三角形
 * P在线段AB的投影为C点
 *
 * 计算点到线段的距离。
 * 与到直线距离区别是线段是有长度的，所以要考虑点到线段的投影是不是在线段上，如果是，则取到直线的距离，如果不是，则取点到最近的端点的距离。
 * r=(AP向量*AB向量)/(AB向量模)^2=AP向量的模/AB向量的模*cosΘ,Θ为AP向量与AB向量的夹角
 * 向量AP在向量AB上的投影:向量AC = r*AB向量
 *
 * P(x0,y0)到线段A(x1,y1),B(x2,y2)的距离为:
 * r<=0,AP向量的模
 * r>=1,BP向量的模
 * 其他,CP向量的模，C是P在AB向量上的投影点
 */
public class TriangleResult {
    /**
     * AB向量的模
     */
    double absAB;
    /**
     * AP向量的模
     */
    double absAP;
    /**
     * BP向量的模
     */
    double absBP;
    /**
     * P到直线AB的距离
     */
    double absPC;
    /**
     * r AP向量的模/AB向量的模*cosΘ,Θ为AP向量与AB向量的夹角，有正负
     */
    double r;
    /**
     * AP向量在AB向量的投影长度=r * AB向量的模,有正负
     */
    double shadowAC;

    public double getAbsAB() {
        return absAB;
    }

    public void setAbsAB(double absAB) {
        this.absAB = absAB;
    }

    public double getAbsAP() {
        return absAP;
    }

    public void setAbsAP(double absAP) {
        this.absAP = absAP;
    }

    public double getAbsBP() {
        return absBP;
    }

    public void setAbsBP(double absBP) {
        this.absBP = absBP;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getShadowAC() {
        return shadowAC;
    }

    public void setShadowAC(double shadowAC) {
        this.shadowAC = shadowAC;
    }

    public double getAbsPC() {
        return absPC;
    }

    public void setAbsPC(double absPC) {
        this.absPC = absPC;
    }
}
