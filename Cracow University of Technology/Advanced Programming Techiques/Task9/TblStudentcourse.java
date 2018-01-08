package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Entity
@Table(name = "TBL_STUDENTCOURSE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "marksFun",
            query = "SELECT "
            + "sxx "
            + "FROM "
            + "TblStudentcourse sxx "
            + "WHERE "
            + "sxx.tblStudentcoursePK.courseid = :przedmiot "
            + "and sxx.mark > 50 "
            + "and sxx.tblCourses.coursesem = sxx.tblStudents.semester "
            + "ORDER BY "
            + "sxx.mark")})

@NamedNativeQuery(name = "markFun", query = "SELECT sc.courseId, sc.mark FROM  "
        + "Tbl_StudentCourse sc  "
        + "join Tbl_Students s on s.Id = sc.studentId  "
        + "join Tbl_Courses c on c.Id = sc.courseId  "
        + "where c.courseName = ? "
        + "and s.firstName = ? "
        + "and s.lastName = ?")

public class TblStudentcourse implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * tblStudentcoursePK reference
     */
    @EmbeddedId
    protected TblStudentcoursePK tblStudentcoursePK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MARK")
    private int mark;
    @JoinColumn(name = "COURSEID", referencedColumnName = "ID",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TblCourses tblCourses;
    @JoinColumn(name = "STUDENTID", referencedColumnName = "ID",
            insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TblStudents tblStudents;

    /**
     */
    public TblStudentcourse() {
    }

    /**
     * @param tblStudentcoursePK tblStudentcoursePK reference
     */
    public TblStudentcourse(TblStudentcoursePK tblStudentcoursePK) {
        this.tblStudentcoursePK = tblStudentcoursePK;
    }

    /**
     * @param tblStudentcoursePK tblStudentcoursePK reference
     * @param mark mark
     */
    public TblStudentcourse(TblStudentcoursePK tblStudentcoursePK, int mark) {
        this.tblStudentcoursePK = tblStudentcoursePK;
        this.mark = mark;
    }

    /**
     * @param studentid student id
     * @param courseid course id
     */
    public TblStudentcourse(int studentid, int courseid) {
        this.tblStudentcoursePK = new TblStudentcoursePK(studentid, courseid);
    }

    /**
     * @return TblStudentcoursePK reference
     */
    public TblStudentcoursePK getTblStudentcoursePK() {
        return tblStudentcoursePK;
    }

    /**
     * @param tblStudentcoursePK TblStudentcoursePK reference
     */
    public void setTblStudentcoursePK(TblStudentcoursePK tblStudentcoursePK) {
        this.tblStudentcoursePK = tblStudentcoursePK;
    }

    /**
     * @return mark
     */
    public int getMark() {
        return mark;
    }

    /**
     * @param mark mark
     */
    public void setMark(int mark) {
        this.mark = mark;
    }

    /**
     * @return TblCourses reference
     */
    public TblCourses getTblCourses() {
        return tblCourses;
    }

    /**
     * @param tblCourses TblCourses reference
     */
    public void setTblCourses(TblCourses tblCourses) {
        this.tblCourses = tblCourses;
    }

    /**
     * @return TblStudents reference
     */
    public TblStudents getTblStudents() {
        return tblStudents;
    }

    /**
     * @param tblStudents TblStudents reference
     */
    public void setTblStudents(TblStudents tblStudents) {
        this.tblStudents = tblStudents;
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tblStudentcoursePK != null 
                ? tblStudentcoursePK.hashCode() 
                : 0);
        return hash;
    }

    /**
     * @param object comparing object
     * @return true if equals otherwise false
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TblStudentcourse)) {
            return false;
        }
        TblStudentcourse other = (TblStudentcourse) object;
        if ((this.tblStudentcoursePK == null 
                && other.tblStudentcoursePK != null) || 
                (this.tblStudentcoursePK != null &&
                !this.tblStudentcoursePK.equals(other.tblStudentcoursePK))) {
            return false;
        }
        return true;
    }

    /**
     * @return to string message
     */
    @Override
    public String toString() {
        return "entities.TblStudentcourse[ tblStudentcoursePK="
                + tblStudentcoursePK + " ]";
    }

}
