import java.util.List;
import javax.ejb.Remote;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Remote
public interface ISolidRemote {
    /**
     * @param points coordinate system points
     * @return area of the hull
     */
    double calculate(List<Solid.Point> points);
}
