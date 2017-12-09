import javax.ejb.Stateless;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Stateless
public class NprimeBean implements NprimeRemote {

    /**
     * @param n natural number
     * @return next natural number which satisfies condition
     */
    @Override
    public int prime(int n) {
        boolean isPost = n > 0;
        n = Math.abs(n);

        while (!isPrimeAndConditionSatisfied(n))
            if (isPost)
                n++;
            else
                n--;

        return n;
    }

    private boolean isPrimeAndConditionSatisfied(int n) {
        int i = 2;

        double sqrtN = Math.sqrt(n);
        while (i <= sqrtN) {
            if (n % i == 0)
                return false;

            i++;
        }

        return (n - 3) % 4 == 0;
    }
}