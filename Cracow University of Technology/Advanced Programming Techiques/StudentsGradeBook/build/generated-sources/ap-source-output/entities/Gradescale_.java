package entities;

import entities.Grade;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-12-13T17:47:14")
@StaticMetamodel(Gradescale.class)
public class Gradescale_ { 

    public static volatile CollectionAttribute<Gradescale, Grade> gradeCollection;
    public static volatile SingularAttribute<Gradescale, String> name;
    public static volatile SingularAttribute<Gradescale, Integer> id;
    public static volatile SingularAttribute<Gradescale, Integer> value;

}