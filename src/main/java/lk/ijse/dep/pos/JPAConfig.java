package lk.ijse.dep.pos;

import lk.ijse.dep.crypto.DEPCrypt;
import net.sf.jasperreports.data.hibernate.HibernateDataAdapter;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.awt.*;

@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@Configuration
public class JPAConfig {

    @Autowired
    private Environment env;
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds, JpaVendorAdapter jpaAdapter)
    {
        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setDataSource(ds);
        lcemfb.setJpaVendorAdapter(jpaAdapter);
        lcemfb.setPackagesToScan("lk.ijse.dep.pos.entity");
        return lcemfb;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds= new DriverManagerDataSource();
        // ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setDriverClassName(env.getRequiredProperty("javax.persistence.jdbc.driver"));
        //ds.setUrl("jdbc:mysql://localhost:3306/DEP4JPAPOS1?createDatabaseIfNotExist=true");
        ds.setUrl(env.getRequiredProperty("javax.persistence.jdbc.url"));
        //ds.setUsername("root");
        ds.setUsername(DEPCrypt.decode(env.getRequiredProperty("javax.persistence.jdbc.user"),"dep4"));
        //ds.setPassword("123");
        ds.setPassword(DEPCrypt.decode(env.getRequiredProperty("javax.persistence.jdbc.password"),"dep4"));
        return ds;

    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter()
    {
        HibernateJpaVendorAdapter jpaAdapter = new HibernateJpaVendorAdapter();
        jpaAdapter.setDatabase(Database.MYSQL);
        jpaAdapter.setDatabasePlatform(env.getRequiredProperty("hibernate.dialect"));
        jpaAdapter.setShowSql(env.getRequiredProperty("hibernate.show_sql",Boolean.class));
        jpaAdapter.setGenerateDdl(env.getRequiredProperty("hibernate.hbm2ddl.auto").equals("update")?true:false);
        return jpaAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf)
    {
        return new JpaTransactionManager(emf);
    }
}

