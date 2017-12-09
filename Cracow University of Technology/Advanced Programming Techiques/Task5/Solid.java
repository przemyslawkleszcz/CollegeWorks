import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.ejb.Stateless;
import java.util.Comparator;

/**
 * @author Przemysław Kleszcz
 * @version 1.0
 */
@Stateless
public class Solid implements ISolidRemote {

    public static class Point {
        double x;
        double y;
        double z;

        public Point(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    protected enum Turn {CLOCKWISE_TURN, COUNTER_CLOCKWISE_TURN, COLLINEAR_TURN}

    /**
     * @param points coordinate system points
     * @return area of the hull
     */
    public double calculate(List<Point> points)
            throws IllegalArgumentException {

        List<Point> sortedSet = new ArrayList<>(getSortedSet(points));
        Stack<Point> pointsHeap = new Stack<>();
        pointsHeap.push(sortedSet.get(0));
        pointsHeap.push(sortedSet.get(1));

        for (int i = 2; i < sortedSet.size(); i++) {
            Point startPart = sortedSet.get(i);
            Point middlePart = pointsHeap.pop();
            Point endPart = pointsHeap.peek();

            Turn turn = getDirection(endPart, middlePart, startPart);

            switch (turn) {
                case COUNTER_CLOCKWISE_TURN:
                    pointsHeap.push(middlePart);
                    pointsHeap.push(startPart);
                    break;
                case CLOCKWISE_TURN:
                    i--;
                    break;
                case COLLINEAR_TURN:
                    pointsHeap.push(startPart);
                    break;
            }
        }

        pointsHeap.push(sortedSet.get(0));
        return calculateArea(new ArrayList<>(pointsHeap));
    }

    private double calculateArea(List<Solid.Point> points) {
        double sum_but_no_result = 0;
        for (int i = 0; i < (points.size() - 1); i++) {
            sum_but_no_result += points.get(i).x * points.get(i + 1).z - points.get(i).z * points.get(i + 1).x;
        }

        sum_but_no_result += points.get(points.size() - 1).x * points.get(0).z - points.get(points.size() - 1).z * points.get(0).x;
        return Math.abs(sum_but_no_result) / 2.0;
    }

    private static Point getSmallestPoint(List<Point> points) {
        Point smallest = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point temp = points.get(i);
            if (temp.z < smallest.z || (temp.z == smallest.z && temp.x < smallest.x))
                smallest = temp;
        }

        return smallest;
    }

    private static Set<Point> getSortedSet(List<Point> points) {
        final Point smallest = getSmallestPoint(points);
        TreeSet<Point> set = new TreeSet<>(new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {

                if (a == b || a.equals(b))
                    return 0;

                double thA = Math.atan2(a.z - smallest.z, a.x - smallest.x);
                double thB = Math.atan2(b.z - smallest.z, b.x - smallest.x);

                if (thA < thB)
                    return -1;
                else if (thA > thB)
                    return 1;
                else {
                    double distanceA = Math.sqrt(((smallest.x - a.x) * (smallest.x - a.x)) +
                            ((smallest.z - a.z) * (smallest.z - a.z)));
                    double distanceB = Math.sqrt(((smallest.x - b.x) * (smallest.x - b.x)) +
                            ((smallest.z - b.z) * (smallest.z - b.z)));

                    if (distanceA < distanceB)
                        return -1;
                    else
                        return 1;
                }
            }
        });

        set.addAll(points);
        return set;
    }

    private static Turn getDirection(Point a, Point b, Point c) {
        double crossProduct = ((b.x - a.x) * (c.z - a.z)) -
                ((b.z - a.z) * (c.x - a.x));

        if (crossProduct > 0)
            return Turn.COUNTER_CLOCKWISE_TURN;
        else if (crossProduct < 0)
            return Turn.CLOCKWISE_TURN;
        else
            return Turn.COLLINEAR_TURN;
    }
}
