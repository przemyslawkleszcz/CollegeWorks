import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/Cone"}, name = "servlet124624")
public class Cone extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static ArrayList<Defect> defects = new ArrayList<>();
    private static ConeSettings _coneSettings;
    private int _defectAmount = 0;
    private int _nonDefectAmount = 0;

    class Defect {
        private double _x;
        private double _y;
        private double _z;
        private double _r;

        public Defect(double x, double y, double z, double r) {
            this._x = x;
            this._y = y;
            this._z = z;
            this._r = r;
        }
    }

    class ConeSettings {
        private double _ra;
        private double _rb;
        private double _h;
        private double _c;
        private double _g;
        private double _coneVolume;

        public ConeSettings(double ra, double rb, double h, double c, double g) {
            _ra = ra;
            _rb = rb;
            _h = h;
            _c = c;
            _g = g;
            _coneVolume = (1.0 / 3) * Math.PI * h * (Math.pow(ra, 2) + (ra * rb) + Math.pow(rb, 2));
        }
    }

    private boolean checkSpot(double x, double y, double z, Defect defect) {
        double x2 = (x - defect._x) * (x - defect._x);
        double y2 = (y - defect._y) * (y - defect._y);
        double z2 = (z - defect._z) * (z - defect._z);
        return x2 + y2 + z2 <= defect._r * defect._r;
    }

    private double assumeMaxRay(double z) {
        double hBeforeCut = (_coneSettings._h * _coneSettings._ra) / (_coneSettings._ra - _coneSettings._rb);
        return ((hBeforeCut - z) * (_coneSettings._ra) / hBeforeCut);
    }

    private boolean createSpotAndCheck() {
        double z = Math.random() * _coneSettings._h;
        double maxRay = assumeMaxRay(z);
        double x = Math.cos(Math.random() * Math.PI) * maxRay;
        double y = Math.sin(Math.random() * Math.PI) * maxRay;

        for (Defect defect : defects) {
            if (checkSpot(x, y, z, defect))
                return true;
        }

        return false;
    }

    private double getConeMass() {
        int randomSpotsAmount = 1000000;
        for (int i = 0; i < randomSpotsAmount; i++) {
            if (createSpotAndCheck())
                _defectAmount++;
            else
                _nonDefectAmount++;
        }

        double defectsVolume = _defectAmount / (double) randomSpotsAmount * _coneSettings._coneVolume;
        double nonDefectsVolume = _nonDefectAmount / (double) randomSpotsAmount * _coneSettings._coneVolume;
        return nonDefectsVolume * _coneSettings._c + defectsVolume * _coneSettings._g;
    }

    private void reset() {
        defects = new ArrayList<>();
        _coneSettings = null;
        _defectAmount = 0;
        _nonDefectAmount = 0;
    }

    private Double getParameter(HttpServletRequest request, String parameter) {
        return Double.parseDouble(request.getParameter(parameter));
    }

    private void fillConeSettings(HttpServletRequest request) {
        double ra = getParameter(request, "ra");
        double rb = getParameter(request, "rb");
        double h = getParameter(request, "h");
        double c = getParameter(request, "c");
        double g = getParameter(request, "g");
        _coneSettings = new ConeSettings(ra, rb, h, c, g);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        fillConeSettings(request);
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        try {
            out.print(new Formatter(Locale.US).format("%.3f", getConeMass()));
        } finally {
            out.close();
        }

        reset();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sR = request.getParameter("r");
        String sX = request.getParameter("x");
        String sY = request.getParameter("y");
        String sZ = request.getParameter("z");
        if (sR != null && sX != null && sY != null && sZ != null) {
            double r = Double.parseDouble(sR);
            double x = Double.parseDouble(sX);
            double y = Double.parseDouble(sY);
            double z = Double.parseDouble(sZ);
            defects.add(new Defect(x, y, z, r));
        }
    }
}


