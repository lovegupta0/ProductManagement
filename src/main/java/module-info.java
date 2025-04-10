module ProductManagement {
    requires java.base; // This is always required.
    requires org.hibernate.orm.core; // For Hibernate ORM
    requires org.postgresql.jdbc; // For PostgreSQL JDBC
    requires jjwt.api; // For JWT functionality
    requires jakarta.persistence;


    exports org.lg.Model; // Example export for model classes (e.g., Product, Inventory)
    exports org.lg.Repository; // Example export for service classes
    exports org.lg.UUID;
    exports org.lg.common;

    opens org.lg.UUID to org.hibernate.orm.core;
    opens org.lg.Model to org.hibernate.orm.core;


}