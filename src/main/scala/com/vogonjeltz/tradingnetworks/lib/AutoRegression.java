package com.vogonjeltz.tradingnetworks.lib;

import Jama.*;

/**
 * Created by Freddie on 26/04/2017.
 */
public class AutoRegression {

    public static double[] calculateARCoefficients(double[] inputseries, int degree, boolean removeMean) throws Exception{
        double[] w = null;
        if(removeMean){
            w = removeMean(inputseries);
        }
        else{
            w = inputseries;
        }

        return calcLeastSquare(w,degree);
        //return calcMaxEntropy(w,degree);
    }

    public static double[] calculateEstimation(double[] inputseries, double[] arCoefficients, boolean removeMean){
        int length = inputseries.length;

        double[] w = null;
        if(removeMean){
            w = removeMean(inputseries);
        }
        else{
            w = inputseries;
        }

        int order = arCoefficients.length;

        double[] estimation = new double[length];
        for(int i=order; i<length; i++){
            double est = 0.0;
            for(int j=0;j<order;j++){
                est += arCoefficients[j]*w[i-(j+1)];
            }
            estimation[i] = est;
        }
        return estimation;
    }

    private static double[] removeMean(double[] inputseries){

        int length = inputseries.length;

        //calculate the mean of the timeseries and substract it from the sample values
        double[] w = new double[length];
        double mean =0.0;
        for (int t=0;t<length;t++){
            mean += inputseries[t];
        }

        mean /= (double)length;

        for (int t=0;t<length;t++){
            w[t] = inputseries[t] - mean;
        }
        return w;
    }

    private static double[] calcLeastSquare(double[] inputseries, int degree) throws Exception{

        int length = inputseries.length;

        double ar[] = null;
        double[] coef = new double[degree];
        double[][] mat = new double[degree][degree];

        //create a symetric matrix of covariance values for the past timeseries elements
        //and a vector with covariances between the past timeseries elements and the timeseries element to estimate.
        //start at "degree"-th sampel and repeat this for the length of the timeseries
        for(int i=degree-1;i<length-1;i++) {
            for (int j=0;j<degree;j++) {
                coef[j] += inputseries[i+1]*inputseries[i-j];
                for (int k=j;k<degree;k++){ //start with k=j due to symmetry of the matrix...
                    mat[j][k] += inputseries[i-j]*inputseries[i-k];
                }
            }
        }

        //calculate the mean values for the matrix and the coefficients vector according to the length of the timeseries
        for (int i=0;i<degree;i++) {
            coef[i] /= (length - degree);
            for (int j=i;j<degree;j++) {
                mat[i][j] /= (length - degree);
                mat[j][i] = mat[i][j]; //use the symmetry of the matrix
            }
        }

        Matrix matrix = new Matrix(mat);
        Matrix coefficients = new Jama.Matrix(degree,1);
        for(int i=0;i<degree;i++){
            coefficients.set(i, 0, coef[i]);
        }

        //solve the equation "matrix * X = coefficients", where x is the solution vector with the AR-coeffcients
        try {
            ar = matrix.solve(coefficients).getRowPackedCopy();
        }
        catch(RuntimeException e){
            System.out.println("Matrix is singular");
        }
        return ar;
    }

    /**
     * Maximum-Entropie-Methode von Burg
     * @param inputseries
     * @param degree
     * @return
     */
    private static double[] calcMaxEntropy(double[] inputseries, int degree){

        int length = inputseries.length;
        double[] per = new double[length+1];
        double[] pef = new double[length+1];
        double[] h = new double[degree+1];
        double[] g = new double[degree+2];
        double[] coef = new double[degree];
        double[][] ar = new double[degree+1][degree+1];

        double t1,t2;
        int n;

        for (n=1;n<=degree;n++) {

            double sn = 0.0;
            double sd = 0.0;
            int j;
            int jj = length - n;

            for (j=0;j<jj;j++) {
                t1 = inputseries[j+n] + pef[j];
                t2 = inputseries[j] + per[j];
                sn -= 2.0 * t1 * t2;
                sd += (t1 * t1) + (t2 * t2);
            }

            g[n] = sn / sd;
            t1 = g[n];

            if (n != 1) {
                for (j=1;j<n;j++){
                    h [j] = g [j] + t1 * g [n - j];
                }
                for (j=1;j<n;j++){
                    g[j] = h[j];
                }
                jj--;
            }

            for (j=0;j<jj;j++) {
                per [j] += t1 * pef [j] + t1 * inputseries [j + n];
                pef [j] = pef [j + 1] + t1 * per [j + 1] + t1 * inputseries [j + 1];
            }

            for (j = 0; j < n; j++)
                ar [n][j] = g [j + 1];
        }

        for (int i=0;i<degree;i++)
            coef[i] = -ar[degree][i];

        return coef;
    }
}
