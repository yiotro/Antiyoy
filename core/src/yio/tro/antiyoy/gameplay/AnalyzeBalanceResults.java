package yio.tro.antiyoy.gameplay;

import yio.tro.antiyoy.YioGdxGame;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class AnalyzeBalanceResults {

    private ArrayList<String> results, distributions;
    private final int N = 5; // number of fractions


    public static void main(String[] args) {
        AnalyzeBalanceResults analyzeBalanceResults = new AnalyzeBalanceResults();
        analyzeBalanceResults.analyze();
    }


    private void analyze() {
        String balanceResults = "[ 156 154 159 177 154 ] - [ 207 183 217 199 194 ] - [ 221 193 181 211 194 ] - [ 198 190 211 198 203 ] - [ 202 204 209 207 178 ] - [ 204 176 203 209 208 ]";
        YioGdxGame.say("\nAnalyzing balance results:");
        YioGdxGame.say(balanceResults);

        createResults(balanceResults);
        createDistributions();

        showDistributionStuff();
        YioGdxGame.say("");

        showGeneralResult();

        YioGdxGame.say("");
        showNormalizedResults();
    }


    private void showNormalizedResults() {
        YioGdxGame.say("Normalized:");
        for (int i = 0; i < results.size(); i++) {
            System.out.print(getNormalizedResult(results.get(i)));
            if (i != results.size() - 1) System.out.print(" - ");
        }
        YioGdxGame.say("");
    }


    private String getNormalizedResult(String result) {
        int array[] = getArray(result);
        double average = getAverageValueInResult(result);
        double norm[] = new double[N];
        for (int i = 0; i < norm.length; i++) {
            norm[i] = array[i] / average;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (int i = 0; i < norm.length; i++) {
            stringBuffer.append(trimDoubleString(Double.toString(norm[i])) + " ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }


    private void showGeneralResult() {
        YioGdxGame.say("General: " + getGeneralResult() + " - " + getDistributionFromResult(getGeneralResult()));
    }


    private String getGeneralResult() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        int general[] = new int[N];

        for (int i = 0; i < general.length; i++) {
            general[i] = 0;
        }

        for (String result : results) {
            int array[] = getArray(result);
            for (int i = 0; i < array.length; i++) {
                general[i] += array[i];
            }
        }

        for (int i = 0; i < general.length; i++) {
            stringBuffer.append(general[i] + " ");
        }

        stringBuffer.append("]");
        return stringBuffer.toString();
    }


    private double getAverageValueInResult(String result) {
        int array[] = getArray(result);
        double average = 0;
        for (int i = 0; i < array.length; i++) {
            average += array[i];
        }
        average /= array.length;
        return average;
    }


    private void showDistributionStuff() {
        for (int i = 0; i < distributions.size(); i++) {
            if (i != 0) System.out.print(" - ");
            System.out.print(distributions.get(i));
        }
        YioGdxGame.say(" = " + getGeneralDistribution());
    }


    private double[] getDistributionDoubles() {
        double array[] = new double[distributions.size()];
        for (int i = 0; i < distributions.size(); i++) {
            array[i] = Double.valueOf(distributions.get(i));
        }
        return array;
    }


    private String getGeneralDistribution() {
        double doubles[] = getDistributionDoubles();
        double sum = 0;
        for (int i = 0; i < doubles.length; i++) {
            sum += doubles[i];
        }
        sum /= doubles.length;
        String sumString = Double.toString(sum);
        return trimDoubleString(sumString);
    }


    private void createResults(String balanceResults) {
        StringTokenizer balanceResultsTokenizer = new StringTokenizer(balanceResults, "-");
        results = new ArrayList<String>();
        while (balanceResultsTokenizer.hasMoreTokens()) {
            String token = balanceResultsTokenizer.nextToken();
            if (token.charAt(0) == ' ') token = token.substring(1, token.length());
            if (token.charAt(token.length() - 1) == ' ') token = token.substring(0, token.length() - 1);
            results.add(token);
        }
    }


    private String trimDoubleString(String str) {
        if (str.length() > 4) return str.substring(0, 4);
        else return str;
    }


    String getDistributionFromResult(String result) {
        int balanceIndicator[] = getArray(result);
        double D = 0;
        int max = balanceIndicator[0], min = balanceIndicator[0];
        for (int i = 0; i < balanceIndicator.length; i++) {
            if (balanceIndicator[i] > max) max = balanceIndicator[i];
            if (balanceIndicator[i] < min) min = balanceIndicator[i];
        }
        if (max > 0) {
            D = 1d - (double) min / (double) max;
        }
        String dStr = Double.toString(D);
        return trimDoubleString(dStr);
    }


    private void createDistributions() {
        distributions = new ArrayList<String>();
        for (String result : results) {
            distributions.add(getDistributionFromResult(result));
        }
    }


    private int[] getArray(String resultString) {
        int array[] = new int[N];

        StringTokenizer tokenizer = new StringTokenizer(resultString, " ");
        ArrayList<String> integers = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            integers.add(token);
        }

        // removing '[' and ']'
        integers.remove(0);
        integers.remove(integers.size() - 1);

        for (int i = 0; i < N; i++) {
            array[i] = Integer.valueOf(integers.get(i));
        }

        return array;
    }
}
