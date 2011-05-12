package org.pocketcampus.plugin.positioning.utils;

/**
 * Author : Jama package
 * 
 * modified by Tarek
 * 
 * */

import java.io.Serializable;


public class LUDecomposition
    implements Serializable {

    public LUDecomposition(Matrix matrix) {
        LU = matrix.getArrayCopy();
        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();
        piv = new int[m];
        for(int i = 0; i < m; i++)
            piv[i] = i;

        pivsign = 1;
        double ad1[] = new double[m];
        for(int j = 0; j < n; j++) {
            for(int k = 0; k < m; k++)
                ad1[k] = LU[k][j];

            for(int l = 0; l < m; l++) {
                double ad[] = LU[l];
                int j1 = Math.min(l, j);
                double d = 0.0D;
                for(int k2 = 0; k2 < j1; k2++)
                    d += ad[k2] * ad1[k2];

                ad[j] = ad1[l] -= d;
            }

            int i1 = j;
            for(int k1 = j + 1; k1 < m; k1++)
                if(Math.abs(ad1[k1]) > Math.abs(ad1[i1]))
                    i1 = k1;

            if(i1 != j) {
                for(int l1 = 0; l1 < n; l1++) {
                    double d1 = LU[i1][l1];
                    LU[i1][l1] = LU[j][l1];
                    LU[j][l1] = d1;
                }

                int i2 = piv[i1];
                piv[i1] = piv[j];
                piv[j] = i2;
                pivsign = -pivsign;
            }
            if(!((j < m) & (LU[j][j] != 0.0D)))
                continue;
            for(int j2 = j + 1; j2 < m; j2++)
                LU[j2][j] /= LU[j][j];

        }

    }

    public boolean isNonsingular() {
        for(int i = 0; i < n; i++)
            if(LU[i][i] == 0.0D)
                return false;

        return true;
    }

    public Matrix getL() {
        Matrix matrix = new Matrix(m, n);
        double ad[][] = matrix.getArray();
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {
                if(i > j) {
                    ad[i][j] = LU[i][j];
                    continue;
                }
                if(i == j)
                    ad[i][j] = 1.0D;
                else
                    ad[i][j] = 0.0D;
            }

        }

        return matrix;
    }

    public Matrix getU() {
        Matrix matrix = new Matrix(n, n);
        double ad[][] = matrix.getArray();
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++)
                if(i <= j)
                    ad[i][j] = LU[i][j];
                else
                    ad[i][j] = 0.0D;

        }

        return matrix;
    }

    public int[] getPivot() {
        int ai[] = new int[m];
        for(int i = 0; i < m; i++)
            ai[i] = piv[i];

        return ai;
    }

    public double[] getDoublePivot() {
        double ad[] = new double[m];
        for(int i = 0; i < m; i++)
            ad[i] = piv[i];

        return ad;
    }

    public double det() {
        if(m != n)
            throw new IllegalArgumentException("Matrix must be square.");
        double d = pivsign;
        for(int i = 0; i < n; i++)
            d *= LU[i][i];

        return d;
    }

    public Matrix solve(Matrix matrix) {
        if(matrix.getRowDimension() != m)
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        if(!isNonsingular())
            throw new RuntimeException("Matrix is singular.");
        int i = matrix.getColumnDimension();
        Matrix matrix1 = matrix.getMatrix(piv, 0, i - 1);
        double ad[][] = matrix1.getArray();
        for(int j = 0; j < n; j++) {
            for(int l = j + 1; l < n; l++) {
                for(int k1 = 0; k1 < i; k1++)
                    ad[l][k1] -= ad[j][k1] * LU[l][j];

            }

        }

        for(int k = n - 1; k >= 0; k--) {
            for(int i1 = 0; i1 < i; i1++)
                ad[k][i1] /= LU[k][k];

            for(int j1 = 0; j1 < k; j1++) {
                for(int l1 = 0; l1 < i; l1++)
                    ad[j1][l1] -= ad[k][l1] * LU[j1][k];

            }

        }

        return matrix1;
    }

    private double LU[][];
    private int m;
    private int n;
    private int pivsign;
    private int piv[];
}
