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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Ontology Collaboration (e.g. note management) Data Source Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = {"ai.hyperlearning.ontology.services.jpa.repositories.collaboration"}, 
        entityManagerFactoryRef = "ontologyCollaborationEntityManagerFactory",
        transactionManagerRef = "ontologyCollaborationTransactionManager")
public class OntologyCollaborationDataSourceConfiguration {
	
	@Primary
	@Bean(name = "ontologyCollaborationDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-ontology-collaboration")
	public DataSource ontologyCollaborationDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Primary
	@Bean(name = "ontologyCollaborationEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean ontologyCollaborationEntityManagerFactory(
			EntityManagerFactoryBuilder ontologyCollaborationEntityManagerFactoryBuilder, 
			@Qualifier("ontologyCollaborationDataSource") DataSource ontologyCollaborationDataSource) {
		
		Map<String, String> primaryJpaProperties = new HashMap<String, String>();
		primaryJpaProperties.put("hibernate.hbm2ddl.auto", "create");
		return ontologyCollaborationEntityManagerFactoryBuilder
				.dataSource(ontologyCollaborationDataSource)
				.packages("ai.hyperlearning.ontology.services.model.collaboration")
				.persistenceUnit("note")
				.properties(primaryJpaProperties)
				.build();
		
	}
	
	@Primary
    @Bean(name = "ontologyCollaborationTransactionManager")
	public PlatformTransactionManager ontologyCollaborationTransactionManager(
			@Qualifier("ontologyCollaborationEntityManagerFactory") EntityManagerFactory ontologyCollaborationEntityManagerFactory) {
		return new JpaTransactionManager(ontologyCollaborationEntityManagerFactory);
	}
	
}
