import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;

// Classe que representa um no
class Node {
    //public LinkedList<Node> adj;
    public boolean visited;
    //public int distance;
    Point point;

    Node(Point p) {
        //this.adj = new LinkedList<Node>();
        this.point = p;
    }
}

public class Graph {
    public LinkedList<Node> nodes;
    public LinkedList<Point> nearest;
    public LinkedList<LinkedList<Point>> candidates;
    public int[] numberIntersections;
    public LinkedList<Point> bestUntilNowPerimeter;
    int lowestPerimeterIndex, lowestPerimeter = (int) Math.pow(10, 9);

    public Graph(int n) {
        nodes = new LinkedList<>();
        nearest = new LinkedList<>();
        numberIntersections = new int[n];
        Arrays.fill(numberIntersections, 0);
        lowestPerimeterIndex = n - 1;
    }

    public void addPoint(Point p) {
        Node n = new Node(p);
        nodes.addLast(n);
    }

    /**
     * @param n   - number of points
     * @param i   - index
     * @param min - minimum range
     * @param max - max range
     */
    public void graphRandom(int n, int i, int min, int max) {
        if (i == n) return;

        int x = (int) (Math.random() * (max - min + 1) + min);
        int y = (int) (Math.random() * (max - min + 1) + min);

        Point p = new Point(x, y);

        // check if already in
        if (!contains(p)) {
            addPoint(p);
            ++i;
        }
        graphRandom(n, i, min, max);
    }

    public void toExchange(LinkedList<Point> list, int option) {
        candidates = new LinkedList<>();
        int a, c;

        for (int i = 0; i < list.size() - 1; ++i) {
            for (int j = i + 2; j < list.size() - 1; ++j) {
                a = i;
                c = j;

                if (!(list.get(a).equals(list.get(c + 1)))) {
                    if (doIntersect(list, a, c)) {
                        checkCase(list, a, c);
                    }
                }
            }
        }
    }

    public void nearest() {
        int randA = (int) (Math.random() * (getSize()));
        Node a = nodes.get(randA);

        findNearest(a);
    }

    private int getSize() {
        return nodes.size();
    }

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are colinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    private int orientation(Point p, Point q, Point r) {
        // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
        // for details of below formula.
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0; // colinear

        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    // Given three colinear points p, q, r, the function checks if
    // point q lies on line segment 'pr'
    private boolean onSegment(Point p, Point q, Point r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    // The main function that returns true if line segment 'p1q1'
    // and 'p2q2' intersect.
    private boolean doIntersect(LinkedList<Point> list, int s1, int s2) {
        Point a = list.get(s1);
        Point b = list.get(s1 + 1);
        Point c = list.get(s2);
        Point d = list.get(s2 + 1);

        // Find the four orientations needed for general and
        // special cases
        int o1 = orientation(a, b, c);
        int o2 = orientation(a, b, d);
        int o3 = orientation(c, d, a);
        int o4 = orientation(c, d, b);
        if (o1 != o2 && o3 != o4) return true;
        if (o1 == 0 && onSegment(a, c, b)) return true;
        if (o2 == 0 && onSegment(a, d, b)) return true;
        if (o3 == 0 && onSegment(c, a, d)) return true;
        return o4 == 0 && onSegment(c, b, d);// Doesn't fall in any of the above cases
    }

    private void checkCase(LinkedList<Point> list, int a, int c) {
        int perimeterFirst = 0;
        int perimeterSecond = 0;

        int tmp_a = a;
        int tmp_c = c;
        a = Math.min(tmp_a, tmp_c);
        c = Math.max(tmp_a, tmp_c);

        int b = a + 1;
        int d = c + 1;
        Point point_a = list.get(a);
        Point point_b = list.get(b);
        Point point_c = list.get(c);
        Point point_d = list.get(d);

        /* FIRST OPTION */
        // A B C D => A C B D
        LinkedList<Point> candidate = new LinkedList<>();
        for (int i = 0; i < a; ++i) {
            candidate.addLast(list.get(i));
        }
        candidate.addLast(point_a);
        for (int i = c; i >= b; --i) {
            candidate.addLast(list.get(i));
        }
        candidate.addLast(point_d);

        for (int i = d + 1; i < list.size(); ++i) {
            candidate.addLast(list.get(i));
        }

        perimeterFirst = checkPerimeter(true, point_a, point_b, point_c, point_d);

        /* SECOND OPTION */
        // A B C D => A D C B
        LinkedList<Point> candidate2 = new LinkedList<>();

        for (int i = 0; i <= a; ++i) {
            candidate2.addLast(list.get(i));
        }
        candidate2.addLast(point_d);

        for (int i = c - 1; i > b; --i) {
            candidate2.addLast(list.get(i));
        }
        candidate2.addLast(point_c);
        candidate2.addLast(point_b);

        for (int i = d + 1; i < list.size(); ++i) {
            candidate2.addLast(list.get(i));
        }

        if (d == list.size() - 1) {
            candidate2.addLast(list.getFirst());
            candidate2.removeFirst();
        }

        perimeterSecond = checkPerimeter(false, point_a, point_b, point_c, point_d);

        if (perimeterFirst < perimeterSecond) {
            if (!candidates.contains(candidate)) {
                candidates.addLast(candidate);
            }
        } else {
            if (!candidates.contains(candidate2)) {
                candidates.addLast(candidate2);
            }
        }
    }

    private int checkPerimeter(boolean first, Point a, Point b, Point c, Point d) {
        // get out
        double distanceA1 = a.distance(b);
        double distanceA2 = c.distance(d);
        double distanceA3, distanceA4;

        if (first) {
            // get in
            distanceA3 = a.distance(c);
            distanceA4 = b.distance(d);
        } else {
            // get in
            distanceA3 = a.distance(d);
            distanceA4 = c.distance(b);
        }

        return  (int) (Math.pow(distanceA3 + distanceA4, 2) - Math.pow(distanceA1 + distanceA2, 2));
    }

    public boolean contains(Point givenPoint) {
        int i = 0;

        if (nodes.size() == 0) return false;

        while (i < nodes.size()) {
            if (nodes.get(i).point.equals(givenPoint)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public void findNearest(Node a) {
        nearest.addLast(a.point);
        a.visited = true;
        Node next = null;

        int i = 0;
        int nextCandidate = 0;
        int flag = 0;
        double distance = 0;

        while (i < nodes.size()) {
            Node T = nodes.get(i);
            if (!T.equals(a)) {                           //para nao pegar nele próprio
                if (!T.visited) {                           //ainda nao foi visitado
                    if (flag == 0) {
                        flag = 1;
                        nextCandidate = i;
                        distance = a.point.distance(T.point);
                        next = nodes.get(i);
                    } else {
                        if (a.point.distance(T.point) < distance) {
                            distance = a.point.distance(T.point);
                            next = nodes.get(i);
                            nextCandidate = i;
                        }
                    }
                }
            }
            ++i;
        }

        if (flag == 1) {
            findNearest(next);
        } else {
            nearest.addLast(nearest.get(0));
        }
    }

    public String printNearest() {
        StringBuilder s = new StringBuilder();
        for (Point p : nearest) {
            s.append("(").append((int) p.getX()).append(",").append((int) p.getY()).append(")");
        }

        return s.toString();
    }

    public void printCandidates() {
        for (LinkedList<Point> L : candidates) {
            for (Point p : L) {
                System.out.print("(" + (int) p.getX() + "," + (int) p.getY() + ")");
            }
            System.out.println("\n");
        }
    }

    public void printLeastPerimeter() {
        for (LinkedList<Point> L : candidates) {
            if (L.equals(candidates.get(lowestPerimeterIndex))) {
                for (Point p : L) {
                    System.out.print("(" + (int) p.getX() + "," + (int) p.getY() + ")");
                }
                System.out.println("\n");
            }
        }
    }
}