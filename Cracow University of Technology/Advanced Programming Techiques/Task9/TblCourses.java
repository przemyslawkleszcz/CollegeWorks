package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Przemyslaw Kleszcz
 * @version 1.0
 */
@Entity
@Table(name = "TBL_COURSES")
@XmlRootElement
public class TblCourses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "COURSENAME")
    private String coursename;
    @Size(max = 250)
    @Column(name = "COURSEDESCR")
    private String coursedescr;
    @Column(name = "COURSEHOURS")
    private Integer coursehours;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COURSESEM")
    private int coursesem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tblCourses")
    private Collection<TblStudentcourse> tblStudentcourseCollection;

    /**
     */
    public TblCourses() {
    }

    /**
     * @param id id
     */
    public TblCourses(Integer id) {
        this.id = id;
    }

    /**
     * @param id id
     * @param coursename course name
     * @param coursesem course semester
     */
    public TblCourses(Integer id, String coursename, int coursesem) {
        this.id = id;
        this.coursename = coursename;
        this.coursesem = coursesem;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return course name
     */
    public String getCoursename() {
        return coursename;
    }

    /**
     * @param coursename course name
     */
    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    /**
     * @return course description
     */
    public String getCoursedescr() {
        return coursedescr;
    }

    /**
     * @param coursedescr course description
     */
    public void setCoursedescr(String coursedescr) {
        this.coursedescr = coursedescr;
    }

    /**
     * @return course hours
     */
    public Integer getCoursehours() {
        return coursehours;
    }

    /**
     * @param coursehours course hours
     */
    public void setCoursehours(Integer coursehours) {
        this.coursehours = coursehours;
    }

    /**
     * @return course semester
     */
    public int getCoursesem() {
        return coursesem;
    }

    /**
     * @param coursesem course semester
     */
    public void setCoursesem(int coursesem) {
        this.coursesem = coursesem;
    }

    /**
     * @return student course collection
     */
    @XmlTransient
    public Collection<TblStudentcourse> getTblStudentcourseCollection() {
        return tblStudentcourseCollection;
    }

    /**
     * @param tblStudentcourseCollection student course collection
     */
    public void setTblStudentcourseCollection(Collection<TblStudentcourse> 
            tblStudentcourseCollection) {
        this.tblStudentcourseCollection = tblStudentcourseCollection;
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     * @param object comparing object
     * @return true if equals otherwise false
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TblCourses)) {
            return false;
        }
        TblCourses other = (TblCourses) object;
        if ((this.id == null && other.id != null) || 
                (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return to string message
     */
    @Override
    public String toString() {
        return "entities.TblCourses[ id=" + id + " ]";
    }
}
