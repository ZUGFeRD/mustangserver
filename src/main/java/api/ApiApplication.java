package api;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import mustang.MustangResource;

public class ApiApplication extends Application<ApiConfiguration> {
    public static void main(String[] args) throws Exception {
        new ApiApplication().run(args);
    }

    @Override
    public String getName() {
        return "Mustang API";
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
    	  final MustangResource resource = new MustangResource();

  	    environment.jersey().register(MultiPartFeature.class);
    		    environment.jersey().register(resource);
    }

}