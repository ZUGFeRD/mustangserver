package mustang;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporter;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA1Factory;
import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;

@Path("/mustang")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Mustang")
public class MustangResource {

	Logger logger = LoggerFactory.getLogger(MustangResource.class.getName());

	public MustangResource() {
	}

	@POST
	@Path("/extract")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_XML)
	public String extractFile(@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader) {

		ZUGFeRDImporter zi;
		logger.debug("Reading...");
		zi = new ZUGFeRDImporter(fileInputStream);
		return zi.getUTF8();
	}


	@POST
	@Path("/combine")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response combineFile(@FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader, @FormDataParam("xml") String XML) {

		ZUGFeRDExporter ze;
   	    
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		
		try {
			logger.debug("Converting to PDF/A-3u");

			/*
			 * Add .setZUGFeRDVersion and .setZUGFeRDConformanceLevel in the next lines to
			 * set the ZUGFeRD version respective profile of the XML you are inserting.
			 */
			ze = new ZUGFeRDExporterFromA1Factory().setProducer("Mustang API")
					.setCreator(System.getProperty("user.name")).load(fileInputStream);
			logger.debug("Attaching ZUGFeRD-Data");
			ze.setZUGFeRDXMLData(XML.getBytes("UTF-8"));
			logger.debug("Writing ZUGFeRD-PDF");
	            
			ze.export(output);
		} catch (IOException e) {
			e.printStackTrace();
		}    
		byte[] bytes = output.toByteArray();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		
		return Response.ok(inputStream)
	                .header(HttpHeaders.CONTENT_DISPOSITION, 
	                        "attachment; filename=\"invoice.pdf\"")
	                .build();
		
	}

}