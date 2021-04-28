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
 * Ontology Vocabulary (e.g. synonyms) Data Source Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = {"ai.hyperlearning.ontology.services.jpa.repositories.vocabulary"}, 
        entityManagerFactoryRef = "ontologyVocabularyEntityManagerFactory",
        transactionManagerRef = "ontologyVocabularyTransactionManager")
public class OntologyVocabularyDataSourceConfiguration {
	
	@Bean(name = "ontologyVocabularyDataSource")
	@ConfigurationProperties(prefix = "spring.datasource-ontology-vocabulary")
	public DataSource ontologyVocabularyDataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "ontologyVocabularyEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean ontologyVocabularyEntityManagerFactory(
			EntityManagerFactoryBuilder ontologyVocabularyEntityManagerFactoryBuilder, 
			@Qualifier("ontologyVocabularyDataSource") DataSource ontologyVocabularyDataSource) {
		
		Map<String, String> primaryJpaProperties = new HashMap<String, String>();
		primaryJpaProperties.put("hibernate.hbm2ddl.auto", "create");
		return ontologyVocabularyEntityManagerFactoryBuilder
				.dataSource(ontologyVocabularyDataSource)
				.packages("ai.hyperlearning.ontology.services.model.vocabulary")
				.persistenceUnit("vocabulary")
				.properties(primaryJpaProperties)
				.build();
		
	}
	
    @Bean(name = "ontologyVocabularyTransactionManager")
	public PlatformTransactionManager ontologyVocabularyTransactionManager(
			@Qualifier("ontologyVocabularyEntityManagerFactory") EntityManagerFactory ontologyVocabularyEntityManagerFactory) {
		return new JpaTransactionManager(ontologyVocabularyEntityManagerFactory);
	}

}
