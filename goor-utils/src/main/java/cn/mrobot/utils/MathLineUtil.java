package cn.mrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author Created by xiaoni on 2017/12/28.
 * 直线相关数学工具
 */
public class MathLineUtil {
    private final static Logger logger = LoggerFactory.getLogger(MathLineUtil.class);

    /**
     * * 已知两点的坐便(x1, y1); (x2, y2)
     另外一个点的坐标是(x0, y0);
     求(x0, y0)到经过(x1, y1); (x2, y2)直线的距离。
     直线方程中
     A = y2 - y1,
     B = x1- x2,
     C = x2 * y1 - x1 * y2;
     点的直线的距离公式为:
     double d = (fabs((y2 - y1) * x0 +(x1 - x2) * y0 + ((x2 * y1) -(x1 * y2)))) / (sqrt(pow(y2 - y1, 2) + pow(x1 - x2, 2)));
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calPointToLineDistance(double x0, double y0,
                                                double x1, double y1,
                                                double x2, double y2) {
        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2 * y1 - x1 * y2;
        double d = (Math.abs(A * x0 +B * y0 + C)) / (Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)));
        logger.info("####################" +
                "计算点(" + x0 + "," + y0 + ")到直线上的点(" + x1 + "," + y1 + "),(" + x2 + "," + y2 +")的距离为" + d );
        return d;
    }

    /**
     * 计算点到线段的距离。
     * 与到直线距离区别是线段是有长度的，所以要考虑点到线段的投影是不是在线段上，如果是，则取到直线的距离，如果不是，则取点到最近的端点的距离。
     * r=(AP向量*AB向量)/(AB向量模)^2=AP向量的模/AB向量的模*cosΘ,Θ为AP向量与AB向量的夹角
     * 向量AP在向量AB上的投影:向量AC = r*AB向量
     *
     * P(x0,y0)到线段A(x1,y1),B(x2,y2)的距离为:
     * r<=0,AP向量的模
     * r>=1,BP向量的模
     * 其他,CP向量的模，C是P在AB向量上的投影点
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calPointToSegmentDistance(double x0, double y0,
                                                   double x1, double y1,
                                                   double x2, double y2) {
        //计算r的分子
        double cross = (x2 - x1) * (x0 - x1) + (y2 - y1) * (y0 - y1);
        //分子小于等于0，说明r<=0
        if (cross <= 0) {
            //返回AP向量的模
            return calPointToPointDistance(x0, y0, x1, y1);
        }

        //计算r的分母，AB向量模的平方
        double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (cross >= d2) {
            //如果r>=1,则返回BP向量的模
            return calPointToPointDistance(x0, y0, x2, y2);
        }

        //其他情况，则返回CP向量的膜
        double r = cross / d2;
        double px = x1 + (x2 - x1) * r;
        double py = y1 + (y2 - y1) * r;
        return calPointToPointDistance(x0, y0, px, py);
    }

    /**
     * 计算两点间距离
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @return
     */
    public static double calPointToPointDistance(double x0, double y0,
                                                 double x1,double y1) {
        double d = Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        logger.info("####################" +
                "计算点(" + x0 + "," + y0 + ")到点(" + x1 + "," + y1 + ")的距离为" + d );
        return d;
    }

    /**
     * 根据小数位数四舍五入double数
     * @param x
     * @param scale
     * @return
     */
    public static double roundDoubleByScale(double x, int scale) {
        BigDecimal b =  new  BigDecimal(x);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 根据小数位数截取double数，注意不是四舍五入
     * 接近负无穷大的舍入模式。
     * @param x
     * @param scale
     * @return
     */
    public static double floorDoubleByScale(double x, int scale) {
        BigDecimal b =  new  BigDecimal(x);
        return b.setScale(scale, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * 根据小数位数截取double数，注意不是四舍五入
     * 接近正无穷大的舍入模式。
     * @param x
     * @param scale
     * @return
     */
    public static double ceilingDoubleByScale(double x, int scale) {
        BigDecimal b =  new  BigDecimal(x);
        return b.setScale(scale, BigDecimal.ROUND_CEILING).doubleValue();
    }

    /**
     * 把double转换成Long,四舍五入
     * @param x
     * @return
     */
    public static Long doubleToLongRoundHalfUp(double x) {
        return new BigDecimal(x + "").setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
    }

    /**
     * 如果x >=0,则用向负无穷大的方向截取，如果x<0，则用向正无穷大的方向截取
     * @param x
     * @param scale
     * @return
     */
    public static double cutDoubleByScale(double x, int scale) {
        //如果x >=0,则用向负无穷大的方向截取，如果x<0，则用向正无穷大的方向截取
        return x >= 0 ?MathLineUtil.floorDoubleByScale(x, scale)
                :
                MathLineUtil.ceilingDoubleByScale(x, scale);
    }

}
