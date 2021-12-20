module com.thuan.carambola {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.hibernate.orm.core;
    requires java.sql;
    requires java.persistence;

    requires spring.context;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.core;
    requires spring.tx;
    requires spring.data.jpa;

    requires org.apache.logging.log4j;
    requires org.slf4j;

    requires lombok;
    requires spring.orm;
    requires spring.jdbc;
    requires com.zaxxer.hikari;
    requires spring.data.commons;
    requires com.jfoenix;
    requires org.apache.commons.lang3;
    requires jasperreports;
    requires org.apache.commons.collections4;

    exports com.thuan.carambola;
    opens com.thuan.carambola to spring.core;
    exports com.thuan.carambola.controller to spring.beans, spring.context;
    opens com.thuan.carambola.controller to javafx.fxml, spring.core;
    opens com.thuan.carambola.config to spring.core;
    exports com.thuan.carambola.repositoryprimary to javafx.fxml, spring.beans, spring.context;
    exports com.thuan.carambola.repositorygeneral to javafx.fxml, spring.beans, spring.context;
    exports com.thuan.carambola.config to javafx.fxml, spring.beans, spring.context;
    opens com.thuan.carambola.entityprimary to javafx.base, org.hibernate.orm.core, spring.core;
    opens com.thuan.carambola.entitygeneral to javafx.base, org.hibernate.orm.core, spring.core;
    exports com.thuan.carambola.entitygeneral to spring.beans;
    exports com.thuan.carambola.recovery;
    opens com.thuan.carambola.recovery to spring.core;
    exports com.thuan.carambola.Service;
    opens com.thuan.carambola.Service to spring.core;
}