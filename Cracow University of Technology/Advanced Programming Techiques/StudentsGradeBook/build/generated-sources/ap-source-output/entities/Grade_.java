package entities;

import entities.Gradescale;
import entities.Student;
import entities.Subject;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-13T17:47:14")
@StaticMetamodel(Grade.class)
public class Grade_ { 

    public static volatile SingularAttribute<Grade, Date> date;
    public static volatile SingularAttribute<Grade, Gradescale> idGradescale;
    public static volatile SingularAttribute<Grade, Subject> idSubject;
    public static volatile SingularAttribute<Grade, Integer> id;
    public static volatile SingularAttribute<Grade, Student> idStudent;

}