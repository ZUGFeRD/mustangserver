package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;

import java.util.concurrent.atomic.AtomicLong;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Optional;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "World")
public class WorldResource {
	private final String template;
	private final String defaultName;
	private final AtomicLong counter;

	public WorldResource(String template, String defaultName) {
		this.template = template;
		this.defaultName = defaultName;
		this.counter = new AtomicLong();
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {

		java.nio.file.Path outputPath = FileSystems.getDefault().getPath("/tmp", "my.txt");
		try {
			Files.copy(fileInputStream, outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = "File can be downloaded from the following location : ";

		return Response.status(200).entity(output).build();

	}

	@GET
	@Path("/download")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile() {
		InputStream is;
		try {
			is = new FileInputStream("pom.xml");
	        return Response.ok(is)
	                .header(HttpHeaders.CONTENT_DISPOSITION, 
	                        "attachment; filename=\"file.txt\"")
	                .build();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	@POST
	@Path("/convert")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response convertFile(@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {

		java.nio.file.Path outputPath = FileSystems.getDefault().getPath("/tmp", "my.txt");
		try {
			Files.copy(fileInputStream, outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		InputStream is;
		try {
			is = new FileInputStream("/tmp/my.txt");
	        return Response.ok(is)
	                .header(HttpHeaders.CONTENT_DISPOSITION, 
	                        "attachment; filename=\"file.txt\"")
	                .build();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	}

	
	@GET
	@Timed
	public World sayHello(@QueryParam("name") Optional<String> name) {
		final String value = String.format(template, name.orElse(defaultName));
		return new World(counter.incrementAndGet(), value);
	}
}