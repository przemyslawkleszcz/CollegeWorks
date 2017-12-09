/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class Levenshtein {
    /**
     * @param testString input string
     * @param line checked input
     * @return levensthein distance
     */
    public int getDistance(CharSequence testString, CharSequence line) {
        int testStringLength = testString.length();
        int lineLength = line.length();

        if (testStringLength == 0)
            return lineLength;
        else if (lineLength == 0)
            return testStringLength;

        if (testStringLength > lineLength) {
            final CharSequence tmp = testString;
            testString = line;
            line = tmp;
            testStringLength = lineLength;
            lineLength = line.length();
        }

        int[] p = compare(testString, line, testStringLength, lineLength);
        return p[testStringLength];
    }

    private int[] compare(CharSequence testString, CharSequence line, int lowerLength, int higherLength) {
        int[] previousCostArray = new int[lowerLength + 1];
        int[] currentCostArray = new int[lowerLength + 1];
        int[] bufferArray;

        int i;
        int j;

        char t_j;
        int cost;
        for (i = 0; i <= lowerLength; i++)
            previousCostArray[i] = i;

        for (j = 1; j <= higherLength; j++) {
            t_j = line.charAt(j - 1);
            currentCostArray[0] = j;

            for (i = 1; i <= lowerLength; i++) {
                cost = testString.charAt(i - 1) == t_j ? 0 : 1;
                int innerMin = Math.min(currentCostArray[i - 1] + 1, previousCostArray[i] + 1);
                currentCostArray[i] = Math.min(innerMin, previousCostArray[i - 1] + cost);
            }

            bufferArray = previousCostArray;
            previousCostArray = currentCostArray;
            currentCostArray = bufferArray;
        }

        return previousCostArray;
    }
}
