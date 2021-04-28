package ai.hyperlearning.ontology.services.jpa.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Ontology UI (e.g. style management) Data Source Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = {"ai.hyperlearning.ontology.services.jpa.repositories.ui"}, 
        entityManagerFactoryRef = "ontologyUIEntityManagerFactory",
        transactionManagerRef = "ontologyUITransactionManager")
public class OntologyUIDataSourceConfiguration {
	
	@Bean(name = "ontologyUIDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-ontology-ui")
	public DataSource ontologyUIDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "ontologyUIEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean ontologyUIEntityManagerFactory(
			EntityManagerFactoryBuilder ontologyUIEntityManagerFactoryBuilder, 
			@Qualifier("ontologyUIDataSource") DataSource ontologyUIDataSource) {
		
		Map<String, String> primaryJpaProperties = new HashMap<String, String>();
		primaryJpaProperties.put("hibernate.hbm2ddl.auto", "create");
		return ontologyUIEntityManagerFactoryBuilder
				.dataSource(ontologyUIDataSource)
				.packages("ai.hyperlearning.ontology.services.model.ui")
				.persistenceUnit("styling")
				.properties(primaryJpaProperties)
				.build();
		
	}
	
    @Bean(name = "ontologyUITransactionManager")
	public PlatformTransactionManager ontologyUITransactionManager(
			@Qualifier("ontologyUIEntityManagerFactory") EntityManagerFactory ontologyUIEntityManagerFactory) {
		return new JpaTransactionManager(ontologyUIEntityManagerFactory);
	}

}
