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
@Table(name = "TBL_STUDENTS")
@XmlRootElement
public class TblStudents implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Size(max = 50)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Size(max = 50)
    @Column(name = "LASTNAME")
    private String lastname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SEMESTER")
    private int semester;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tblStudents")
    private Collection<TblStudentcourse> tblStudentcourseCollection;

    /**
     */
    public TblStudents() {
    }

    /**
     * @param id id
     */
    public TblStudents(Integer id) {
        this.id = id;
    }

    /**
     * @param id id
     * @param semester semester
     */
    public TblStudents(Integer id, int semester) {
        this.id = id;
        this.semester = semester;
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
     * @return first name
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname first name
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return last name
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname last name
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * @param semester semester
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * @return TblStudentcourse collection
     */
    @XmlTransient
    public Collection<TblStudentcourse> getTblStudentcourseCollection() {
        return tblStudentcourseCollection;
    }

    /**
     * @param tblStudentcourseCollection TblStudentcourse collection
     */
    public void setTblStudentcourseCollection(
            Collection<TblStudentcourse> tblStudentcourseCollection) {
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
        if (!(object instanceof TblStudents)) {
            return false;
        }
        TblStudents other = (TblStudents) object;
        if ((this.id == null && other.id != null) 
                || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return to string message
     */
    @Override
    public String toString() {
        return "entities.TblStudents[ id=" + id + " ]";
    }   
}
