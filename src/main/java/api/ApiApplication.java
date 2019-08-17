package api;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ApiApplication extends Application<ApiConfiguration> {
    public static void main(String[] args) throws Exception {
        new ApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<ApiConfiguration> bootstrap) {

	    bootstrap.addBundle(new SwaggerBundle<ApiConfiguration>() {
	        @Override
	        protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ApiConfiguration configuration) {
	            return configuration.swaggerBundleConfiguration;
	        }
	    });
    }

    @Override
    public void run(ApiConfiguration configuration,
                    Environment environment) {
    	  final WorldResource resource = new WorldResource(
    		        configuration.getTemplate(),
    		        configuration.getDefaultName()
    		    );

  	    environment.jersey().register(MultiPartFeature.class);
    		    environment.jersey().register(resource);
    }

}