package org.pocketcampus.plugin.positioning.utils;

/**
 * Author : Jama package
 * 
 * modified by Tarek
 * 
 * */

import java.io.Serializable;


public class CholeskyDecomposition
    implements Serializable {

    public CholeskyDecomposition(Matrix matrix) {
        double ad[][] = matrix.getArray();
        n = matrix.getRowDimension();
        L = new double[n][n];
        isspd = matrix.getColumnDimension() == n;
        for(int i = 0; i < n; i++) {
            double ad1[] = L[i];
            double d = 0.0D;
            for(int j = 0; j < i; j++) {
                double ad2[] = L[j];
                double d1 = 0.0D;
                for(int l = 0; l < j; l++)
                    d1 += ad2[l] * ad1[l];

                ad1[j] = d1 = (ad[i][j] - d1) / L[j][j];
                d += d1 * d1;
                isspd = isspd & (ad[j][i] == ad[i][j]);
            }

            d = ad[i][i] - d;
            isspd = isspd & (d > 0.0D);
            L[i][i] = Math.sqrt(Math.max(d, 0.0D));
            for(int k = i + 1; k < n; k++)
                L[i][k] = 0.0D;

        }

    }

    public boolean isSPD() {
        return isspd;
    }

    public Matrix getL() {
        return new Matrix(L, n, n);
    }

    public Matrix solve(Matrix matrix) {
        if(matrix.getRowDimension() != n)
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        if(!isspd)
            throw new RuntimeException("Matrix is not symmetric positive definite.");
        double ad[][] = matrix.getArrayCopy();
        int i = matrix.getColumnDimension();
        for(int j = 0; j < n; j++) {
            for(int l = 0; l < i; l++) {
                for(int j1 = 0; j1 < j; j1++)
                    ad[j][l] -= ad[j1][l] * L[j][j1];

                ad[j][l] /= L[j][j];
            }

        }

        for(int k = n - 1; k >= 0; k--) {
            for(int i1 = 0; i1 < i; i1++) {
                for(int k1 = k + 1; k1 < n; k1++)
                    ad[k][i1] -= ad[k1][i1] * L[k1][k];

                ad[k][i1] /= L[k][k];
            }

        }

        return new Matrix(ad, n, i);
    }

    private double L[][];
    private int n;
    private boolean isspd;
}
