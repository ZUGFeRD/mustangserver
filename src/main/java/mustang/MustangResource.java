package mustang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mustangproject.ZUGFeRD.ZUGFeRDConformanceLevel;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporter;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA1Factory;
import org.mustangproject.ZUGFeRD.ZUGFeRDImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.jersey.params.IntParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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
	@ApiOperation(value="Extracts XML from a zf/fx PDF",notes="Input PDF must be ZUGFeRD or Factur-X")
	public String extractFile(@ApiParam(required=true,value="Input ZUGFeRD/Factur-X file") @FormDataParam("file") final InputStream fileInputStream,
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
	@ApiOperation(value="Combine PDF file and custom XML to zf/fx PDF",notes="Input PDF must be PDF/A-1, output PDF will be a ZUGFeRD/Factur-X PDF/A-3 file called invoice.pdf")
	public Response combineFile(@ApiParam(required=true,value="Input PDF/A-1 file") @FormDataParam("file") final InputStream fileInputStream,
			@FormDataParam("file") final FormDataContentDisposition contentDispositionHeader, 
			@ApiParam(required=true, value="The zf/fx XML to add to the file") @FormDataParam("xml") String XML, 
			@ApiParam(required=true, defaultValue="zf", value="zf|fx:zf for ZUGFeRD, fx for Factur-X") @FormDataParam("format") String format,
			@ApiParam(required=true, defaultValue="2", value="version, i.e. fx 1 or zf 1 or 2") @FormDataParam("version") int version,
			@ApiParam(required=true, defaultValue="EN16931", value="Profile: BASIC|COMFORT|EXTENDED for zf1, MINIMUM|BASICWL|BASIC|CIUS|EN16931|EXTENDED for zf2/fx1") @FormDataParam("profile") String profile
			) {

		ZUGFeRDExporter ze;
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		
		try {
			logger.debug("Converting to PDF/A-3u");
			
			if ((version<1)||(version>2)) {
				// this should be checked with annotations but I did not get it to work
				throw new IllegalArgumentException("invalid version");
			}

			// this should be restricted to the enum with annotations but I did not get it to work
			ZUGFeRDConformanceLevel prof=ZUGFeRDConformanceLevel.valueOf(profile);
			/*
			 * Add .setZUGFeRDVersion and .setZUGFeRDConformanceLevel in the next lines to
			 * set the ZUGFeRD version respective profile of the XML you are inserting.
			 */
			ze = new ZUGFeRDExporterFromA1Factory().setProducer("Mustang API")
					.setCreator(System.getProperty("user.name"))
					.setZUGFeRDVersion(version)
					.setZUGFeRDConformanceLevel(prof)
					.load(fileInputStream);
			logger.debug("Attaching ZUGFeRD-Data");
			if (format.equalsIgnoreCase("fx")) {
				ze.setFacturX();
			}
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