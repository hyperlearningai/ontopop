package ai.hyperlearning.ontology.services.utils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Global ontology framework properties
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
@PropertySource("classpath:ontology-framework.properties")
@ConfigurationProperties
@Validated
public class GlobalProperties {
	
	@Value("${ontology.framework.version}")
	@NotEmpty
	private String ontologyFrameworkVersion;
	
	@Value("${graph.db.engine}")
	@NotEmpty
	private String graphDbEngine;
	
	@Value("${graph.db.configuration.filename}")
	@NotEmpty
	private String graphDbConfigurationFilename;
	
	@Value("${graph.db.apps.loader.mode}")
	@NotEmpty
	private String graphDbAppsLoaderMode;
	
	@Value("${graph.db.apps.loader.ontology.owl.filename}")
	@NotEmpty
	private String graphDbAppsLoaderOntologyOwlFilename;
	
	@Value("${graph.db.apps.loader.load.data.catalogue}")
	@NotNull
	private boolean graphDbAppsLoaderLoadDataCatalogue;
	
	@Value("${graph.db.apps.loader.data.catalogue.filename}")
	private String graphDbAppsLoaderDataCatalogueFilename;
	
	@Value("${graph.db.apps.loader.data.catalogue.parser.class}")
	private String graphDbAppsLoaderDataCatalogueParseClass;
	
	@Value("${graph.db.apps.loader.load.system.catalogue}")
	@NotNull
	private boolean graphDbAppsLoaderLoadSystemCatalogue;
	
	@Value("${graph.db.apps.loader.system.catalogue.filename}")
	private String graphDbAppsLoaderSystemCatalogueFilename;
	
	@Value("${graph.db.apps.loader.system.catalogue.parser.class}")
	private String graphDbAppsLoaderSystemCatalogueParseClass;
	
	@Value("${search.engine}")
	@NotEmpty
	private String searchEngine;
	
	@Value("${ontology.api.all.http.port}")
	@NotNull
	@Positive
	private int ontologyApiAllHttpPort;
	
	@Value("${ontology.api.auth.http.port}")
	@NotNull
	@Positive
	private int ontologyApiAuthHttpPort;
	
	@Value("${ontology.api.collaboration.http.port}")
	@NotNull
	@Positive
	private int ontologyApiCollaborationHttpPort;
	
	@Value("${ontology.api.graph.http.port}")
	@NotNull
	@Positive
	private int ontologyApiGraphHttpPort;
	
	@Value("${ontology.api.ontology.http.port}")
	@NotNull
	@Positive
	private int ontologyApiOntologyHttpPort;
	
	@Value("${ontology.api.ui.http.port}")
	@NotNull
	@Positive
	private int ontologyApiUiHttpPort;
	
	@Value("${ontology.auth.provider}")
	@NotEmpty
	private String ontologyAuthProvider;
	
	@Value("${spring.ldap.embedded.url}")
	private String springLdapEmbeddedUrl;
	
	@Value("${spring.ldap.embedded.base-dn}")
	private String springLdapEmbeddedBaseDn;
	
	@Value("${spring.ldap.embedded.user-dn}")
	private String springLdapEmbeddedUserDn;
	
	public String getOntologyFrameworkVersion() {
		return ontologyFrameworkVersion;
	}

	public void setOntologyFrameworkVersion(String ontologyFrameworkVersion) {
		this.ontologyFrameworkVersion = ontologyFrameworkVersion;
	}

	public String getGraphDbEngine() {
		return graphDbEngine;
	}

	public void setGraphDbEngine(String graphDbEngine) {
		this.graphDbEngine = graphDbEngine;
	}

	public String getGraphDbConfigurationFilename() {
		return graphDbConfigurationFilename;
	}

	public void setGraphDbConfigurationFilename(
			String graphDbConfigurationFilename) {
		this.graphDbConfigurationFilename = graphDbConfigurationFilename;
	}

	public String getGraphDbAppsLoaderMode() {
		return graphDbAppsLoaderMode;
	}

	public void setGraphDbAppsLoaderMode(String graphDbAppsLoaderMode) {
		this.graphDbAppsLoaderMode = graphDbAppsLoaderMode;
	}

	public String getGraphDbAppsLoaderOntologyOwlFilename() {
		return graphDbAppsLoaderOntologyOwlFilename;
	}

	public void setGraphDbAppsLoaderOntologyOwlFilename(
			String graphDbAppsLoaderOntologyOwlFilename) {
		this.graphDbAppsLoaderOntologyOwlFilename = 
				graphDbAppsLoaderOntologyOwlFilename;
	}

	public boolean isGraphDbAppsLoaderLoadDataCatalogue() {
		return graphDbAppsLoaderLoadDataCatalogue;
	}

	public void setGraphDbAppsLoaderLoadDataCatalogue(
			boolean graphDbAppsLoaderLoadDataCatalogue) {
		this.graphDbAppsLoaderLoadDataCatalogue = 
				graphDbAppsLoaderLoadDataCatalogue;
	}

	public String getGraphDbAppsLoaderDataCatalogueFilename() {
		return graphDbAppsLoaderDataCatalogueFilename;
	}

	public void setGraphDbAppsLoaderDataCatalogueFilename(
			String graphDbAppsLoaderDataCatalogueFilename) {
		this.graphDbAppsLoaderDataCatalogueFilename = 
				graphDbAppsLoaderDataCatalogueFilename;
	}
	
	public String getGraphDbAppsLoaderDataCatalogueParseClass() {
		return graphDbAppsLoaderDataCatalogueParseClass;
	}

	public void setGraphDbAppsLoaderDataCatalogueParseClass(
			String graphDbAppsLoaderDataCatalogueParseClass) {
		this.graphDbAppsLoaderDataCatalogueParseClass = 
				graphDbAppsLoaderDataCatalogueParseClass;
	}
	
	public boolean isGraphDbAppsLoaderLoadSystemCatalogue() {
		return graphDbAppsLoaderLoadSystemCatalogue;
	}

	public void setGraphDbAppsLoaderLoadSystemCatalogue(
			boolean graphDbAppsLoaderLoadSystemCatalogue) {
		this.graphDbAppsLoaderLoadSystemCatalogue = 
				graphDbAppsLoaderLoadSystemCatalogue;
	}

	public String getGraphDbAppsLoaderSystemCatalogueFilename() {
		return graphDbAppsLoaderSystemCatalogueFilename;
	}

	public void setGraphDbAppsLoaderSystemCatalogueFilename(
			String graphDbAppsLoaderSystemCatalogueFilename) {
		this.graphDbAppsLoaderSystemCatalogueFilename = 
				graphDbAppsLoaderSystemCatalogueFilename;
	}
	
	public String getGraphDbAppsLoaderSystemCatalogueParseClass() {
		return graphDbAppsLoaderSystemCatalogueParseClass;
	}

	public void setGraphDbAppsLoaderSystemCatalogueParseClass(
			String graphDbAppsLoaderSystemCatalogueParseClass) {
		this.graphDbAppsLoaderSystemCatalogueParseClass = 
				graphDbAppsLoaderSystemCatalogueParseClass;
	}
	
	public String getSearchEngine() {
		return searchEngine;
	}

	public void setSearchEngine(String searchEngine) {
		this.searchEngine = searchEngine;
	}

	public int getOntologyApiAllHttpPort() {
		return ontologyApiAllHttpPort;
	}

	public void setOntologyApiAllHttpPort(int ontologyApiAllHttpPort) {
		this.ontologyApiAllHttpPort = ontologyApiAllHttpPort;
	}

	public int getOntologyApiAuthHttpPort() {
		return ontologyApiAuthHttpPort;
	}

	public void setOntologyApiAuthHttpPort(int ontologyApiAuthHttpPort) {
		this.ontologyApiAuthHttpPort = ontologyApiAuthHttpPort;
	}

	public int getOntologyApiCollaborationHttpPort() {
		return ontologyApiCollaborationHttpPort;
	}

	public void setOntologyApiCollaborationHttpPort(int ontologyApiCollaborationHttpPort) {
		this.ontologyApiCollaborationHttpPort = ontologyApiCollaborationHttpPort;
	}

	public int getOntologyApiGraphHttpPort() {
		return ontologyApiGraphHttpPort;
	}

	public void setOntologyApiGraphHttpPort(int ontologyApiGraphHttpPort) {
		this.ontologyApiGraphHttpPort = ontologyApiGraphHttpPort;
	}

	public int getOntologyApiOntologyHttpPort() {
		return ontologyApiOntologyHttpPort;
	}

	public void setOntologyApiOntologyHttpPort(int ontologyApiOntologyHttpPort) {
		this.ontologyApiOntologyHttpPort = ontologyApiOntologyHttpPort;
	}

	public int getOntologyApiUiHttpPort() {
		return ontologyApiUiHttpPort;
	}

	public void setOntologyApiUiHttpPort(int ontologyApiUiHttpPort) {
		this.ontologyApiUiHttpPort = ontologyApiUiHttpPort;
	}

	public String getOntologyAuthProvider() {
		return ontologyAuthProvider;
	}

	public void setOntologyAuthProvider(String ontologyAuthProvider) {
		this.ontologyAuthProvider = ontologyAuthProvider;
	}

	public String getSpringLdapEmbeddedUrl() {
		return springLdapEmbeddedUrl;
	}

	public void setSpringLdapEmbeddedUrl(String springLdapEmbeddedUrl) {
		this.springLdapEmbeddedUrl = springLdapEmbeddedUrl;
	}

	public String getSpringLdapEmbeddedBaseDn() {
		return springLdapEmbeddedBaseDn;
	}

	public void setSpringLdapEmbeddedBaseDn(String springLdapEmbeddedBaseDn) {
		this.springLdapEmbeddedBaseDn = springLdapEmbeddedBaseDn;
	}

	public String getSpringLdapEmbeddedUserDn() {
		return springLdapEmbeddedUserDn;
	}

	public void setSpringLdapEmbeddedUserDn(String springLdapEmbeddedUserDn) {
		this.springLdapEmbeddedUserDn = springLdapEmbeddedUserDn;
	}

	@Override
	public String toString() {
		return "GlobalProperties ["
				+ "ontologyFrameworkVersion=" 
					+ ontologyFrameworkVersion + ", "
				+ "graphdbEngine=" 
					+ graphDbEngine + ", "
				+ "graphDbConfigurationFilename=" 
					+ graphDbConfigurationFilename + ", "
				+ "searchEngine="
					+ searchEngine
				+ "]";
	}

}
