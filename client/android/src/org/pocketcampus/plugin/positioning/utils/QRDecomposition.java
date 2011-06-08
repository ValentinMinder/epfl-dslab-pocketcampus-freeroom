package org.pocketcampus.plugin.positioning.utils;


/**
 * Author : Jama package * modified by Tarek
 * 
 * */


import java.io.Serializable;



public class QRDecomposition
    implements Serializable {

    public QRDecomposition(Matrix matrix) {
        QR = matrix.getArrayCopy();
        m = matrix.getRowDimension();
        n = matrix.getColumnDimension();
        Rdiag = new double[n];
        for(int i = 0; i < n; i++) {
            double d = 0.0D;
            for(int j = i; j < m; j++)
                d = Maths.hypot(d, QR[j][i]);

            if(d != 0.0D) {
                if(QR[i][i] < 0.0D)
                    d = -d;
                for(int k = i; k < m; k++)
                    QR[k][i] /= d;

                QR[i][i]++;
                for(int l = i + 1; l < n; l++) {
                    double d1 = 0.0D;
                    for(int i1 = i; i1 < m; i1++)
                        d1 += QR[i1][i] * QR[i1][l];

                    d1 = -d1 / QR[i][i];
                    for(int j1 = i; j1 < m; j1++)
                        QR[j1][l] += d1 * QR[j1][i];

                }

            }
            Rdiag[i] = -d;
        }

    }

    public boolean isFullRank() {
        for(int i = 0; i < n; i++)
            if(Rdiag[i] == 0.0D)
                return false;

        return true;
    }

    public Matrix getH() {
        Matrix matrix = new Matrix(m, n);
        double ad[][] = matrix.getArray();
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++)
                if(i >= j)
                    ad[i][j] = QR[i][j];
                else
                    ad[i][j] = 0.0D;

        }

        return matrix;
    }

    public Matrix getR() {
        Matrix matrix = new Matrix(n, n);
        double ad[][] = matrix.getArray();
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(i < j) {
                    ad[i][j] = QR[i][j];
                    continue;
                }
                if(i == j)
                    ad[i][j] = Rdiag[i];
                else
                    ad[i][j] = 0.0D;
            }

        }

        return matrix;
    }

    public Matrix getQ() {
        Matrix matrix = new Matrix(m, n);
        double ad[][] = matrix.getArray();
        for(int i = n - 1; i >= 0; i--) {
            for(int j = 0; j < m; j++)
                ad[j][i] = 0.0D;

            ad[i][i] = 1.0D;
            for(int k = i; k < n; k++) {
                if(QR[i][i] == 0.0D)
                    continue;
                double d = 0.0D;
                for(int l = i; l < m; l++)
                    d += QR[l][i] * ad[l][k];

                d = -d / QR[i][i];
                for(int i1 = i; i1 < m; i1++)
                    ad[i1][k] += d * QR[i1][i];

            }

        }

        return matrix;
    }

    public Matrix solve(Matrix matrix) {
        if(matrix.getRowDimension() != m)
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        if(!isFullRank())
            throw new RuntimeException("Matrix is rank deficient.");
        int i = matrix.getColumnDimension();
        double ad[][] = matrix.getArrayCopy();
        for(int j = 0; j < n; j++) {
            for(int l = 0; l < i; l++) {
                double d = 0.0D;
                for(int l1 = j; l1 < m; l1++)
                    d += QR[l1][j] * ad[l1][l];

                d = -d / QR[j][j];
                for(int i2 = j; i2 < m; i2++)
                    ad[i2][l] += d * QR[i2][j];

            }

        }

        for(int k = n - 1; k >= 0; k--) {
            for(int i1 = 0; i1 < i; i1++)
                ad[k][i1] /= Rdiag[k];

            for(int j1 = 0; j1 < k; j1++) {
                for(int k1 = 0; k1 < i; k1++)
                    ad[j1][k1] -= ad[k][k1] * QR[j1][k];

            }

        }

        return (new Matrix(ad, n, i)).getMatrix(0, n - 1, 0, i - 1);
    }

    private double QR[][];
    private int m;
    private int n;
    private double Rdiag[];
}
