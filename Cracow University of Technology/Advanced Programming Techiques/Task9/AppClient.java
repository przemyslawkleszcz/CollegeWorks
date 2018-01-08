import entities.TblStudentcourse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
public class AppClient {
    static class FileData {
        private final String mFirstName;
        private final String mLastName;
        private final String mSubject;

        public FileData(String firstName, String lastName, String subject) {
            mFirstName = firstName;
            mLastName = lastName;
            mSubject = subject;
        }
    }

    static class Marks {
        private final Integer mGivenMark;
        private final List<TblStudentcourse> mOtherMarks;

        public Marks(Integer givenMark, List<TblStudentcourse> otherMarks) {
            mGivenMark = givenMark;
            mOtherMarks = otherMarks;
        }
    }

    /**
     * Application entry point
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            FileData data = readFromFile(args[0]);
            EntityManager em = createEntityManager();
            Marks marks = getMarks(em, data);
            Double median = getMedian(marks.mOtherMarks);
            Double result = (marks.mGivenMark / median) * 100. - 100.;
            System.out.println("Wynik : " 
                    + String.format(Locale.US, "%.0f", result) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileData readFromFile(String path) {
        try {
            FileReader fileReader = new FileReader(new File(path));
            try (BufferedReader reader = new BufferedReader(fileReader)) {
                String subject = reader.readLine();
                String fullName = reader.readLine();
                String[] names = fullName.split("\\s+");
                String firstName = names[0];
                String lastName = names[1];
                return new FileData(firstName, lastName, subject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static EntityManager createEntityManager() {
        EntityManager em = Persistence
                .createEntityManagerFactory("myPersistence")
                .createEntityManager();

        return em;
    }

    private static Marks getMarks(EntityManager em, FileData data) {
        @SuppressWarnings("unchecked")
        List<Object[]> wyn = em.createNamedQuery("markFun")
                .setParameter(1, data.mSubject)
                .setParameter(2, data.mFirstName)
                .setParameter(3, data.mLastName)
                .getResultList();

        int courseId = Integer.parseInt(wyn.get(0)[0].toString());
        int givenMark = Integer.parseInt(wyn.get(0)[1].toString());
        List<TblStudentcourse> otherMarks = em.createNamedQuery("marksFun",
                TblStudentcourse.class)
                .setParameter("przedmiot", courseId)
                .getResultList();

        return new Marks(givenMark, otherMarks);
    }

    private static Double getMedian(List<TblStudentcourse> marks) {
        Double median;
        if (marks.size() % 2 == 0) 
        median = ((double) marks.get(marks.size() / 2).getMark()
                    + (double) marks.get(marks.size() / 2 - 1).getMark()) / 2.0;
        else 
            median = (double) marks.get(marks.size() / 2).getMark();

        return median;
    }
}
