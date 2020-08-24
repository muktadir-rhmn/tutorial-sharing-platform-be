package tsp.be.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tsp.be.config.pojos.DatabaseConfiguration;
import tsp.be.config.pojos.JWTConfiguration;
import tsp.be.config.pojos.RedisConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigurationManager {
    private final static String configurationFileDirectory = "src/main/resources/configurations/";
   public static DatabaseConfiguration getDatabaseConfiguration() {
        String fileName = "db_config.json";
       return (DatabaseConfiguration) readConfigurationFromFile(configurationFileDirectory + fileName, DatabaseConfiguration.class);
   }

   public static JWTConfiguration getJWTConfiguration() {
       String fileName = "jwt_config.json";
       return (JWTConfiguration) readConfigurationFromFile(configurationFileDirectory + fileName, JWTConfiguration.class);
   }

    public static RedisConfiguration getRedisConfiguration() {
        String fileName = "redis_config.json";
        return (RedisConfiguration) readConfigurationFromFile(configurationFileDirectory + fileName, RedisConfiguration.class);
    }

   private static Object readConfigurationFromFile(String filePath, Class outputClass) {
       try {
           FileInputStream is = new FileInputStream(filePath);
           ObjectMapper mapper = new ObjectMapper();
           return mapper.readValue(is, outputClass);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       } catch (JsonParseException e) {
           e.printStackTrace();
       } catch (JsonMappingException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return null;
   }
}
