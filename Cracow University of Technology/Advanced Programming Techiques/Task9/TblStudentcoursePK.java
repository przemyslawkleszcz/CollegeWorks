package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Embeddable
public class TblStudentcoursePK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STUDENTID")
    private int studentid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COURSEID")
    private int courseid;

    /**
     */
    public TblStudentcoursePK() {
    }

    /**
     * @param studentid student id
     * @param courseid course id
     */
    public TblStudentcoursePK(int studentid, int courseid) {
        this.studentid = studentid;
        this.courseid = courseid;
    }

    /**
     * @return student id
     */
    public int getStudentid() {
        return studentid;
    }

    /**
     * @param studentid student id
     */
    public void setStudentid(int studentid) {
        this.studentid = studentid;
    }

    /**
     * @return course id
     */
    public int getCourseid() {
        return courseid;
    }

    /**
     * @param courseid course id
     */
    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += studentid;
        hash += courseid;
        return hash;
    }

    /**
     * @param object comparing object
     * @return true if equals otherwise false
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TblStudentcoursePK)) {
            return false;
        }
        TblStudentcoursePK other = (TblStudentcoursePK) object;
        if (this.studentid != other.studentid) {
            return false;
        }
        if (this.courseid != other.courseid) {
            return false;
        }
        return true;
    }

    /**
     * @return to string message
     */
    @Override
    public String toString() {
        return "entities.TblStudentcoursePK[ studentid=" 
                + studentid + ", courseid=" + courseid + " ]";
    }   
}
