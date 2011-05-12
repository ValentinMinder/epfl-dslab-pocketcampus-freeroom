package org.pocketcampus.plugin.positioning.utils;

/**
 * Author : Jama package
 * 
 * modified by Tarek
 * 
 * */

import java.io.Serializable;


public class SingularValueDecomposition
    implements Serializable {

    public SingularValueDecomposition(Matrix matrix) {
        double ad[][] = matrix.getArrayCopy();
        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();
        int i = Math.min(m, n);
        s = new double[Math.min(m + 1, n)];
        U = new double[m][i];
        V = new double[n][n];
        double ad1[] = new double[n];
        double ad2[] = new double[m];
        boolean flag = true;
        boolean flag1 = true;
        int j = Math.min(m - 1, n);
        int k = Math.max(0, Math.min(n - 2, m));
        for(int l = 0; l < Math.max(j, k); l++) {
            if(l < j) {
                s[l] = 0.0D;
                for(int j1 = l; j1 < m; j1++)
                    s[l] = Maths.hypot(s[l], ad[j1][l]);

                if(s[l] != 0.0D) {
                    if(ad[l][l] < 0.0D)
                        s[l] = -s[l];
                    for(int k1 = l; k1 < m; k1++)
                        ad[k1][l] /= s[l];

                    ad[l][l]++;
                }
                s[l] = -s[l];
            }
            for(int l1 = l + 1; l1 < n; l1++) {
                if((l < j) & (s[l] != 0.0D)) {
                    double d = 0.0D;
                    for(int i7 = l; i7 < m; i7++)
                        d += ad[i7][l] * ad[i7][l1];

                    d = -d / ad[l][l];
                    for(int j7 = l; j7 < m; j7++)
                        ad[j7][l1] += d * ad[j7][l];

                }
                ad1[l1] = ad[l][l1];
            }

            if(flag & (l < j)) {
                for(int i2 = l; i2 < m; i2++)
                    U[i2][l] = ad[i2][l];

            }
            if(l >= k)
                continue;
            ad1[l] = 0.0D;
            for(int j2 = l + 1; j2 < n; j2++)
                ad1[l] = Maths.hypot(ad1[l], ad1[j2]);

            if(ad1[l] != 0.0D) {
                if(ad1[l + 1] < 0.0D)
                    ad1[l] = -ad1[l];
                for(int k2 = l + 1; k2 < n; k2++)
                    ad1[k2] /= ad1[l];

                ad1[l + 1]++;
            }
            ad1[l] = -ad1[l];
            if((l + 1 < m) & (ad1[l] != 0.0D)) {
                for(int l2 = l + 1; l2 < m; l2++)
                    ad2[l2] = 0.0D;

                for(int i3 = l + 1; i3 < n; i3++) {
                    for(int l4 = l + 1; l4 < m; l4++)
                        ad2[l4] += ad1[i3] * ad[l4][i3];

                }

                for(int j3 = l + 1; j3 < n; j3++) {
                    double d1 = -ad1[j3] / ad1[l + 1];
                    for(int k7 = l + 1; k7 < m; k7++)
                        ad[k7][j3] += d1 * ad2[k7];

                }

            }
            if(!flag1)
                continue;
            for(int k3 = l + 1; k3 < n; k3++)
                V[k3][l] = ad1[k3];

        }

        int i1 = Math.min(n, m + 1);
        if(j < n)
            s[j] = ad[j][j];
        if(m < i1)
            s[i1 - 1] = 0.0D;
        if(k + 1 < i1)
            ad1[k] = ad[k][i1 - 1];
        ad1[i1 - 1] = 0.0D;
        if(flag) {
            for(int l3 = j; l3 < i; l3++) {
                for(int i5 = 0; i5 < m; i5++)
                    U[i5][l3] = 0.0D;

                U[l3][l3] = 1.0D;
            }

            for(int i4 = j - 1; i4 >= 0; i4--) {
                if(s[i4] != 0.0D) {
                    for(int j5 = i4 + 1; j5 < i; j5++) {
                        double d2 = 0.0D;
                        for(int l7 = i4; l7 < m; l7++)
                            d2 += U[l7][i4] * U[l7][j5];

                        d2 = -d2 / U[i4][i4];
                        for(int i8 = i4; i8 < m; i8++)
                            U[i8][j5] += d2 * U[i8][i4];

                    }

                    for(int k5 = i4; k5 < m; k5++)
                        U[k5][i4] = -U[k5][i4];

                    U[i4][i4] = 1.0D + U[i4][i4];
                    for(int l5 = 0; l5 < i4 - 1; l5++)
                        U[l5][i4] = 0.0D;

                    continue;
                }
                for(int i6 = 0; i6 < m; i6++)
                    U[i6][i4] = 0.0D;

                U[i4][i4] = 1.0D;
            }

        }
        if(flag1) {
            for(int j4 = n - 1; j4 >= 0; j4--) {
                if((j4 < k) & (ad1[j4] != 0.0D)) {
                    for(int j6 = j4 + 1; j6 < i; j6++) {
                        double d3 = 0.0D;
                        for(int j8 = j4 + 1; j8 < n; j8++)
                            d3 += V[j8][j4] * V[j8][j6];

                        d3 = -d3 / V[j4 + 1][j4];
                        for(int k8 = j4 + 1; k8 < n; k8++)
                            V[k8][j6] += d3 * V[k8][j4];

                    }

                }
                for(int k6 = 0; k6 < n; k6++)
                    V[k6][j4] = 0.0D;

                V[j4][j4] = 1.0D;
            }

        }
        int k4 = i1 - 1;
        int l6 = 0;
        double d4 = Math.pow(2D, -52D);
        double d5 = Math.pow(2D, -966D);
        do {
            if(i1 <= 0)
                break;
            int l8 = i1 - 2;
            do {
                if(l8 < -1 || l8 == -1)
                    break;
                if(Math.abs(ad1[l8]) <= d5 + d4 * (Math.abs(s[l8]) + Math.abs(s[l8 + 1]))) {
                    ad1[l8] = 0.0D;
                    break;
                }
                l8--;
            } while(true);
            byte byte0;
            if(l8 == i1 - 2) {
                byte0 = 4;
            } else {
                int i9 = i1 - 1;
                do {
                    if(i9 < l8 || i9 == l8)
                        break;
                    double d12 = (i9 == i1 ? 0.0D : Math.abs(ad1[i9])) + (i9 == l8 + 1 ? 0.0D : Math.abs(ad1[i9 - 1]));
                    if(Math.abs(s[i9]) <= d5 + d4 * d12) {
                        s[i9] = 0.0D;
                        break;
                    }
                    i9--;
                } while(true);
                if(i9 == l8)
                    byte0 = 3;
                else
                if(i9 == i1 - 1) {
                    byte0 = 1;
                } else {
                    byte0 = 2;
                    l8 = i9;
                }
            }
            l8++;
            switch(byte0) {
            case 1: // '\001'
                double d6 = ad1[i1 - 2];
                ad1[i1 - 2] = 0.0D;
                int k9 = i1 - 2;
                while(k9 >= l8)  {
                    double d14 = Maths.hypot(s[k9], d6);
                    double d19 = s[k9] / d14;
                    double d22 = d6 / d14;
                    s[k9] = d14;
                    if(k9 != l8) {
                        d6 = -d22 * ad1[k9 - 1];
                        ad1[k9 - 1] = d19 * ad1[k9 - 1];
                    }
                    if(flag1) {
                        for(int k10 = 0; k10 < n; k10++) {
                            double d15 = d19 * V[k10][k9] + d22 * V[k10][i1 - 1];
                            V[k10][i1 - 1] = -d22 * V[k10][k9] + d19 * V[k10][i1 - 1];
                            V[k10][k9] = d15;
                        }

                    }
                    k9--;
                }
                break;

            case 2: // '\002'
                double d7 = ad1[l8 - 1];
                ad1[l8 - 1] = 0.0D;
                int l9 = l8;
                while(l9 < i1)  {
                    double d16 = Maths.hypot(s[l9], d7);
                    double d20 = s[l9] / d16;
                    double d23 = d7 / d16;
                    s[l9] = d16;
                    d7 = -d23 * ad1[l9];
                    ad1[l9] = d20 * ad1[l9];
                    if(flag) {
                        for(int l10 = 0; l10 < m; l10++) {
                            double d17 = d20 * U[l10][l9] + d23 * U[l10][l8 - 1];
                            U[l10][l8 - 1] = -d23 * U[l10][l9] + d20 * U[l10][l8 - 1];
                            U[l10][l9] = d17;
                        }

                    }
                    l9++;
                }
                break;

            case 3: // '\003'
                double d8 = Math.max(Math.max(Math.max(Math.max(Math.abs(s[i1 - 1]), Math.abs(s[i1 - 2])), Math.abs(ad1[i1 - 2])), Math.abs(s[l8])), Math.abs(ad1[l8]));
                double d13 = s[i1 - 1] / d8;
                double d18 = s[i1 - 2] / d8;
                double d21 = ad1[i1 - 2] / d8;
                double d24 = s[l8] / d8;
                double d25 = ad1[l8] / d8;
                double d26 = ((d18 + d13) * (d18 - d13) + d21 * d21) / 2D;
                double d27 = d13 * d21 * (d13 * d21);
                double d28 = 0.0D;
                if((d26 != 0.0D) | (d27 != 0.0D)) {
                    d28 = Math.sqrt(d26 * d26 + d27);
                    if(d26 < 0.0D)
                        d28 = -d28;
                    d28 = d27 / (d26 + d28);
                }
                double d29 = (d24 + d13) * (d24 - d13) + d28;
                double d30 = d24 * d25;
                for(int i11 = l8; i11 < i1 - 1; i11++) {
                    double d31 = Maths.hypot(d29, d30);
                    double d33 = d29 / d31;
                    double d34 = d30 / d31;
                    if(i11 != l8)
                        ad1[i11 - 1] = d31;
                    d29 = d33 * s[i11] + d34 * ad1[i11];
                    ad1[i11] = d33 * ad1[i11] - d34 * s[i11];
                    d30 = d34 * s[i11 + 1];
                    s[i11 + 1] = d33 * s[i11 + 1];
                    if(flag1) {
                        for(int j11 = 0; j11 < n; j11++) {
                            d31 = d33 * V[j11][i11] + d34 * V[j11][i11 + 1];
                            V[j11][i11 + 1] = -d34 * V[j11][i11] + d33 * V[j11][i11 + 1];
                            V[j11][i11] = d31;
                        }

                    }
                    d31 = Maths.hypot(d29, d30);
                    d33 = d29 / d31;
                    d34 = d30 / d31;
                    s[i11] = d31;
                    d29 = d33 * ad1[i11] + d34 * s[i11 + 1];
                    s[i11 + 1] = -d34 * ad1[i11] + d33 * s[i11 + 1];
                    d30 = d34 * ad1[i11 + 1];
                    ad1[i11 + 1] = d33 * ad1[i11 + 1];
                    if(!flag || i11 >= m - 1)
                        continue;
                    for(int k11 = 0; k11 < m; k11++) {
                        double d32 = d33 * U[k11][i11] + d34 * U[k11][i11 + 1];
                        U[k11][i11 + 1] = -d34 * U[k11][i11] + d33 * U[k11][i11 + 1];
                        U[k11][i11] = d32;
                    }

                }

                ad1[i1 - 2] = d29;
                l6++;
                break;

            case 4: // '\004'
                if(s[l8] <= 0.0D) {
                    s[l8] = s[l8] >= 0.0D ? 0.0D : -s[l8];
                    if(flag1) {
                        for(int j9 = 0; j9 <= k4; j9++)
                            V[j9][l8] = -V[j9][l8];

                    }
                }
                for(; l8 < k4 && s[l8] < s[l8 + 1]; l8++) {
                    double d9 = s[l8];
                    s[l8] = s[l8 + 1];
                    s[l8 + 1] = d9;
                    if(flag1 && l8 < n - 1) {
                        for(int i10 = 0; i10 < n; i10++) {
                            double d10 = V[i10][l8 + 1];
                            V[i10][l8 + 1] = V[i10][l8];
                            V[i10][l8] = d10;
                        }

                    }
                    if(!flag || l8 >= m - 1)
                        continue;
                    for(int j10 = 0; j10 < m; j10++) {
                        double d11 = U[j10][l8 + 1];
                        U[j10][l8 + 1] = U[j10][l8];
                        U[j10][l8] = d11;
                    }

                }

                l6 = 0;
                i1--;
                break;
            }
        } while(true);
    }

    public Matrix getU() {
        return new Matrix(U, m, Math.min(m + 1, n));
    }

    public Matrix getV() {
        return new Matrix(V, n, n);
    }

    public double[] getSingularValues() {
        return s;
    }

    public Matrix getS() {
        Matrix matrix = new Matrix(n, n);
        double ad[][] = matrix.getArray();
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++)
                ad[i][j] = 0.0D;

            ad[i][i] = s[i];
        }

        return matrix;
    }

    public double norm2() {
        return s[0];
    }

    public double cond() {
        return s[0] / s[Math.min(m, n) - 1];
    }

    public int rank() {
        double d = Math.pow(2D, -52D);
        double d1 = (double)Math.max(m, n) * s[0] * d;
        int i = 0;
        for(int j = 0; j < s.length; j++)
            if(s[j] > d1)
                i++;

        return i;
    }

    private double U[][];
    private double V[][];
    private double s[];
    private int m;
    private int n;
}
