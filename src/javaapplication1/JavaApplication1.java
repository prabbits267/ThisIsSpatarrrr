/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author endin
 */
public class JavaApplication1 {

    /**
     * @param args the command line arguments
     */
    public static double[] quatProd(double[] a, double[] b) {
        double lm, p1, p2, p3;
        double temp[] = new double[4];

        lm = a[0];
        p1 = a[1];
        p2 = a[2];
        p3 = a[3];

        temp[0] = lm * b[0] - p1 * b[1] - p2 * b[2] - p3 * b[3];
        temp[1] = p1 * b[0] + lm * b[1] - p3 * b[2] + p2 * b[3];
        temp[2] = p2 * b[0] + p3 * b[1] + lm * b[2] - p1 * b[3];
        temp[3] = p3 * b[0] - p2 * b[1] + p1 * b[2] + lm * b[3];

        return temp;
    }

    public static double[] quatRot(double x, double y, double z, double qw, double qx, double qy, double qz) {
        double vector[] = new double[4];
        double quaternion[] = new double[4];
        double invQuat[] = new double[4];
        double ret[] = new double[4];
        double temp[] = new double[3];

        vector[0] = 0;
        vector[1] = x;
        vector[2] = y;
        vector[3] = z;
        
        quaternion[0] = qw;
        quaternion[1] = -qx;
        quaternion[2] = -qy;
        quaternion[3] = -qz;

        invQuat[0] = qw;
        invQuat[1] = qx;
        invQuat[2] = qy;
        invQuat[3] = qz;

        ret = quatProd(invQuat, vector);

        ret = quatProd(ret, quaternion);

        temp[0] = ret[1];
        temp[1] = ret[2];
        temp[2] = ret[3];

        return temp;
    }

    public static ArrayList<Double> filtFilt(ArrayList<Double> input, boolean high, boolean isAbs) {
//        double a, b1, b2;
        ArrayList<Double> a = new ArrayList<>();
        ArrayList<Double> b = new ArrayList<>();
        if (high) {
            a.add(0.1);
            a.add(-0.999937170120766);
            b.add(0.999968585060383);
            b.add(-0.999968585060383);

        } else {
            a.add(0.1);
            a.add(-0.726542528005361);
            b.add(0.136728735997320);
            b.add(0.136728735997320);
        }
        
        ArrayList<Double> filter = Filtfilt.doFiltfilt(b, a, input);

        if (isAbs) {
            for(int i=0; i < filter.size(); i++){
                filter.set(i, filter.get(i));
            }
            return filter;
        } else {
            return filter;
        }
    }

    public static double[][] getPosition(List<Double> AccX, List<Double> AccY,
            List<Double> AccZ, List<Double> Qx, List<Double> Qy, List<Double> Qz, List<Double> Qw) {

        int len = AccX.size();
        double vel[][] = new double[len][3];

        List<Double> acc_mag = new ArrayList<Double>();
        double samplePeriod = 2. / 100;

        double mean_x = 0.0, mean_y = 0.0, mean_z = 0.0;
        double accX_drift = 0.0, accY_drift = 0.0, accZ_drift = 0.0;

        for (int i = 0; i < len; i++) {
            acc_mag.add(sqrt(AccX.get(i) * AccX.get(i) + AccY.get(i) * AccY.get(i) + AccZ.get(i) * AccZ.get(i)));
            // calculate mean of acceleration
            mean_x += AccX.get(i);
            mean_y += AccY.get(i);
            mean_z += AccZ.get(i);
            // pc.printf("mean: %f %f %f\r\n", mean_x, mean_y, mean_z);
        }

        accX_drift = mean_x / len;
        accY_drift = mean_y / len;
        accZ_drift = mean_z / len;

        List<Double> temp = new ArrayList<>();
        List<Double> acc_magFilt1 = new ArrayList<>();
        List<Double> acc_magFilt2 = new ArrayList<>();
        List<Double> accX_filt = new ArrayList<>();
        List<Double> accY_filt = new ArrayList<>();
        List<Double> accZ_filt = new ArrayList<>();

//            List<Integer> stationary = new ArrayList<Integer>();
        int[] stationary = new int[len];

        temp.add(0.0);
        acc_magFilt1.add(0.0);
        acc_magFilt2.add(0.0);
        accX_filt.add(0.0);
        accY_filt.add(0.0);
        accZ_filt.add(0.0);

//        for (int i = 1; i < len; i++) {
//            temp.add(filter(acc_mag.get(i), acc_mag.get(i - 1), temp.get(i - 1), true));
//            acc_magFilt1.add(abs(filter(temp.get(i), temp.get(i - 1), acc_magFilt1.get(i - 1), true)));
//        }
        acc_magFilt1 = filtFilt((ArrayList<Double>) acc_mag, true, true);

        stationary[0] = 1;
//        
//        temp.add(filter(acc_magFilt1.get(i), acc_magFilt1.get(i - 1), temp.get(i - 1), false));
//        acc_magFilt2.add(filter(temp.get(i), temp.get(i - 1), acc_magFilt2.get(i - 1), false));

        acc_magFilt2 = filtFilt((ArrayList<Double>) acc_magFilt1, false, false);

        for (int i = 1; i < len; i++) {
            // check startionary using threshold
            if (acc_magFilt2.get(i) < 0.07) {
                stationary[i] = 1;
            } else {
                stationary[i] = 0;
            }
        }

        List<Double> tempX = new ArrayList<>();
        tempX.add(0.0);

//        for (int i = 1; i < len; i++) {
//            tempX.add(filter(AccX.get(i), AccX.get(i - 1), tempX.get(i - 1), false));
//            accX_filt.add(filter(tempX.get(i), tempX.get(i - 1), accX_filt.get(i - 1), false));
//        }
        
        accX_filt = filtFilt((ArrayList<Double>) AccX, false, false);

        List<Double> tempY = new ArrayList<Double>();
        tempY.add(0.0);

//        for (int i = 1; i < len; i++) {
//            tempY.add(filter(AccY.get(i), AccY.get(i - 1), tempY.get(i - 1), false));
//            accY_filt.add(filter(tempY.get(i), tempY.get(i - 1), accY_filt.get(i - 1), false));
//        }
        accY_filt = filtFilt((ArrayList<Double>) AccY, false, false);

        List<Double> tempZ = new ArrayList<Double>();
        tempY.add(0.0);

//        for (int i = 1; i < len; i++) {
//
////            insertArray(&tempZ, filter(AccZ.array[i], AccZ.array[i - 1], tempZ.array[i - 1], false));
////            insertArray(&accZ_filt, filter(tempZ.array[i], tempZ.array[i - 1], accZ_filt.array[i - 1], false));
//            tempZ.add(filter(AccZ.get(i), AccZ.get(i - 1), AccZ.get(i - 1), false));
//            accZ_filt.add(filter(tempZ.get(i), tempZ.get(i - 1), accZ_filt.get(i - 1), false));
//        }
        accZ_filt = filtFilt((ArrayList<Double>) AccZ, false, false);

//        double accX_fix_drift[len], accY_fix_drift[len], accZ_fix_drift[len];
//        accX_fix_drift[0] = accY_fix_drift[0] = accZ_fix_drift[0] = 0;
        double accX_fix_drift[] = new double[len], accY_fix_drift[] = new double[len], accZ_fix_drift[] = new double[len];
        accX_fix_drift[0] = accY_fix_drift[0] = accZ_fix_drift[0] = 0;

        for (int i = 1; i < len; i++) {
//            accX_fix_drift[i] = accX_filt.array[i] - accX_drift;
//            accY_fix_drift[i] = accY_filt.array[i] - accY_drift;
//            accZ_fix_drift[i] = accZ_filt.array[i] - accZ_drift;

            accX_fix_drift[i] = accX_filt.get(i) - accX_drift;
            accY_fix_drift[i] = accY_filt.get(i) - accY_drift;
            accZ_fix_drift[i] = accZ_filt.get(i) - accZ_drift;
        }

        double[][] acc_d = new double[len][3];
        double temp_acc[] = new double[3];

        for (int i = 0; i < len; i++) {
            temp_acc = quatRot(accX_fix_drift[i], accY_fix_drift[i], accZ_fix_drift[i], (double) Qw.get(i), (double) Qx.get(i), (double) Qy.get(i), (double) Qz.get(i));
//            memcpy(acc_d[i], temp_acc, 3 * sizeof(double));
            acc_d[i] = temp_acc;
            // pc.printf("acc: %f - temp: %f \r\n", acc_d[i][0], temp_acc[0]);
            // pc.printf("acc: %f - temp: %f \r\n", acc_d[i][1], temp_acc[1]);
            // pc.printf("acc: %f - temp: %f \r\n", acc_d[i][2], temp_acc[2]);
        }

        vel[0][0] = vel[0][1] = vel[0][2] = 0;
        vel[len - 1][0] = vel[len - 1][1] = vel[len - 1][2] = 0;

        for (int i = 1; i < len - 1; i++) {
            if (stationary[i] == 1) {
                vel[i][0] = vel[i][1] = vel[i][2] = 0;
            } else {
                vel[i][0] = vel[i - 1][0] + acc_d[i][0] * samplePeriod;
                vel[i][1] = vel[i - 1][1] + acc_d[i][1] * samplePeriod;
                vel[i][2] = vel[i - 1][2] + acc_d[i][2] * samplePeriod;
            }
            // pc.printf("vel [%d]: %f %f %f \r\n", i, vel[i][0], vel[i][1], vel[i][2]);
        }

        List<Integer> stationaryStart = new ArrayList<>();
        List<Integer> stationaryEnd = new ArrayList<>();

        for (int i = 0; i < len - 2; i++) {
            if (stationary[i + 1] - stationary[i] == -1) {
                stationaryStart.add(i);
            } else if (stationary[i + 1] - stationary[i] == 1) {
                stationaryEnd.add(i);
            } else {
                continue;
            }
        }
        // used : so61 luong phan tu trong mang
        if (stationaryEnd.size() > stationaryStart.size()) {
            if (stationaryEnd.size() > 1) {
                stationaryEnd.remove(0);
            }
        } else if (stationaryEnd.size() < stationaryStart.size() || stationaryEnd.size() == 0) {
            stationaryEnd.add(len - 2);
        }

        if (stationaryStart.size() == 0) {
            stationaryStart.add(10);
        }

        double driftRate[] = new double[3];
        double velDrift[][] = new double[len][3];
        double range = 0, j = 0;

        for (int i = 0; i < stationaryEnd.size(); i++) {
            range = stationaryEnd.get(i) - stationaryStart.get(i);
            
            driftRate[0] = vel[(int) stationaryEnd.get(i) - 1][0] / (range);
            driftRate[1] = vel[(int) stationaryEnd.get(i) - 1][1] / (range);
            driftRate[2] = vel[(int) stationaryEnd.get(i) - 1][2] / (range);

            // calculate drift values
            for (j = 0; j < range; j++) {
                velDrift[stationaryStart.get(i) + (int) j][0] = j * driftRate[0];
                velDrift[stationaryStart.get(i) + (int) j][1] = j * driftRate[1];
                velDrift[stationaryStart.get(i) + (int) j][2] = j * driftRate[2];
                // pc.printf("velDrift[%d]: %f %f %f \r\n", (int)stationaryStart.array[i] + j, velDrift[(int)stationaryStart.array[i] + j][0], velDrift[(int)stationaryStart.array[i] + j][1], velDrift[(int)stationaryStart.array[i] + j][2]);
            }
        }

        for (int i = 0; i < len; i++) {
            vel[i][0] -= velDrift[i][0];
            vel[i][1] -= velDrift[i][1];
            vel[i][2] -= velDrift[i][2];
            // pc.printf("velfix drift [%d]: %f %f %f\r\n", i, vel[i][0], vel[i][1], vel[i][2]);
        }

        double pos[][] = new double[2][3];
        pos[0][0] = pos[0][1] = pos[0][2] = 0;
        pos[1][0] = pos[1][1] = pos[1][2] = 0;

        for (int i = 1; i < len - 1; i++) {
            pos[1][0] = pos[0][0] + vel[i][0] * samplePeriod;
            pos[1][1] = pos[0][1] + vel[i][1] * samplePeriod;
            pos[1][2] = pos[0][2] + vel[i][2] * samplePeriod;

//            System.out.println(pos[1][0] + " " + pos[1][1] + "" + pos[1][2] + " \r\n");

            pos[0][0] = pos[1][0];
            pos[0][1] = pos[1][1];
            pos[0][2] = pos[1][2];
        }

        return pos;

    }
    
    public static void main(String[] args) {
//        getPosition(List<Double> AccX, List<Double> AccY,
//            List<Double> AccZ, List<Double> Qx, List<Double> Qy, List<Double> Qz, List<Double> Qw)
        
        List<Double> AccX = new ArrayList<>(), AccY = new ArrayList<>(), AccZ = new ArrayList<>(),
                Qx = new ArrayList<>(), Qy = new ArrayList<>(), Qz = new ArrayList<>(), Qw = new ArrayList<>();
        
        AccX.add(-54.2);
        AccX.add(-54.2);
        AccX.add(-54.2);
        AccX.add(-54.2);
        AccX.add(-54.2);
        AccX.add(-4.2);
        
        AccY.add(-5.2);
        AccY.add(-5.2);
        AccY.add(-5.2);
        AccY.add(-5.2);
        AccY.add(-5.2);
        AccY.add(-5.2);
        
        AccZ.add(5.2);
        AccZ.add(5.2);
        AccZ.add(5.2);
        AccZ.add(5.2);
        AccZ.add(5.2);
        AccZ.add(5.2);
        
        Qx.add(51.2);
        Qx.add(51.2);
        Qx.add(51.2);
        Qx.add(51.2);
        Qx.add(51.2);
        Qx.add(51.2);
        
        Qy.add(51.2);
        Qy.add(51.2);
        Qy.add(51.2);
        Qy.add(51.2);
        Qy.add(51.2);
        Qy.add(51.2);
        
        Qz.add(0.1);
        Qz.add(0.1);
        Qz.add(0.1);
        Qz.add(0.1);
        Qz.add(0.1);
        Qz.add(0.1);
        
        Qw.add(0.1);
        Qw.add(0.1);
        Qw.add(0.1);
        Qw.add(0.1);
        Qw.add(0.1);
        Qw.add(0.1);
        
        
        double [][]pos = getPosition(AccX, AccY, AccZ, Qx, Qy, Qz, Qw);
        
        
        
    }

}
