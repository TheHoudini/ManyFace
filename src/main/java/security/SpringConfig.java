package security;

import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.AbstractDriverBasedDataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import java.util.Properties;

import dao.AccountDAO;
import dao.DialogDAO;
import dao.UserDAO;



@Configuration
@EnableTransactionManagement
@EnableWebMvc
public class SpringConfig {

    @Bean
    @Inject
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        AbstractDriverBasedDataSource ds = (AbstractDriverBasedDataSource) dataSource;
        Properties prop = ds.getConnectionProperties();
        Boolean isTest = Boolean.parseBoolean(prop.getProperty("isTest"));

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(new String[] { "dbmapping" });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties(isTest));


        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties(Boolean isTest) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        if(isTest.booleanValue()) {
            properties.setProperty("javax.persistence.schema-generation.database.action", "drop-and-create");
        }else{
            System.out.println("NO DROP (");
        }
        return properties;
    }



    @Bean
    public AccountDAO accountDAO(){
        return new AccountDAO();
    }

    @Bean
    public UserDAO userDAO(){
        return new UserDAO();
    }

    @Bean
    public DialogDAO dialogDAO(){
        return new DialogDAO();
    }

    @Bean
    public TokenAuthManager tokenAuthManager(){
        return new TokenAuthManager(accountDAO());
    }

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter("/api/v1/**", tokenAuthManager());
    }


}
