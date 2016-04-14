package de.soeiner.mental;

/**
 * Created by Malte on 13.04.2016.
 */

public class GaussElimination {

    public static double[] lsolve(double[][] A, double[] b) {
        int N  = b.length;

        for (int p = 0; p < N; p++) {

            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double   t    = b[p]; b[p] = b[max]; b[max] = t;

            for (int i = p + 1; i < N; i++) {
                double faktor = A[i][p] / A[p][p];
                b[i] -= faktor * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= faktor * A[p][j];
                }
            }
        }

        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double summe = 0.0;
            for (int j = i + 1; j < N; j++) {
                summe += A[i][j] * x[j];
            }
            x[i] = (b[i] - summe) / A[i][i];
        }
        return x;
    }
}
