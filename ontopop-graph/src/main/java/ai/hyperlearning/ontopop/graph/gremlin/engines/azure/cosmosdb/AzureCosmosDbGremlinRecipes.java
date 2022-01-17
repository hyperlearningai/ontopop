package ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinRecipes;

/**
 * Azure CosmosDB-specific Gremlin Recipes
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class AzureCosmosDbGremlinRecipes {
    
    private static final int LIMIT = 1000;
    
    private AzureCosmosDbGremlinRecipes() {
        throw new IllegalStateException("Azure CosmosDB Gremlin Recipes "
                + "utility class cannot be instantiated.");
    }
    
    /**************************************************************************
     * VERTEX-CENTRIC GREMLIN RECIPES
     *************************************************************************/
    
    /**
     * Delete all vertices
     * @return
     */
    
    public static String deleteVertices() {
        return "g.V()"
                + ".limit(" + LIMIT + ")"
                + ".drop()";
    }
    
    /**
     * Delete all vertices with a given property key/value pair
     * @param propertyKey
     * @param propertyValue
     * @return
     */
    
    public static String deleteVertices(String propertyKey, 
            Object propertyValue) {
        return "g.V()"
                + ".has('" + propertyKey + "', " 
                    + GremlinRecipes.resolveHasPropertyValue(propertyValue) + ")"
                + ".limit(" + LIMIT + ")"
                + ".drop()";
    }
    
    /**************************************************************************
     * EDGE-CENTRIC GREMLIN RECIPES
     *************************************************************************/
    
    /**
     * Delete all edges
     * @return
     */
    
    public static String deleteEdges() {
        return "g.E()"
                + ".limit(" + LIMIT + ")"
                + ".drop()";
    }
    
    /**
     * Delete all edges with a given property key/value pair
     * @param propertyKey
     * @param propertyValue
     * @return
     */
    
    public static String deleteEdges(String propertyKey, 
            Object propertyValue) {
        String hasPropertyValue = propertyValue instanceof String ? 
                "'" + propertyValue + "'" : propertyValue.toString(); 
        return "g.E()"
                + ".has('" + propertyKey + "', " + hasPropertyValue + ")"
                + ".limit(" + LIMIT + ")"
                + ".drop()";
    }

}
